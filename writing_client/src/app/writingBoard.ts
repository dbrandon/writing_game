'use strict';

import angular, { IHttpService, IIntervalService, IScope, reloadWithDebugInfo } from 'angular';
import { SessionTracker } from './app';
import { BasicResponse, FinishedStory, FinishedStoryResponse, FragmentStatus, RoomStatus, RoomStatusResponse, UserPrefs, WriterStatus } from './api';
import '../style/ctrl.css';

import { ErrorDialog } from './errorDialog';
import { PromptDialog } from './promptDialog';

export let writingBoard = () => {
  return {
    template: require('./writingBoard.html').default,
    controller: 'WritingBoardCtrl',
    controllerAs: 'ctrl'
  }
};

// let helper = require('./helper')

interface WritingBoardScope extends IScope {
  curFont : string;
  lastRound : boolean;

  autoSaveRunning : boolean;
}

export class WritingBoardCtrl {
  private $scope: WritingBoardScope;
  private $http : IHttpService;
  private $interval : IIntervalService;
  private $uibModal : angular.ui.bootstrap.IModalService;
  private SessionTracker : SessionTracker;
  private state : string;
  private wasEpisodeDone : boolean;
  private frag = new FragmentStatus;

  private status: RoomStatus;

  private roundTimeRemaining: string;
  private episodeTimeRemaining: string;

  private curStory : FinishedStory;

  private autoSubmitTimer : angular.IPromise<any>;

  private statusQuery : angular.IPromise<any>;
  private displayingQueryFailure : boolean;

  private amLeader : boolean;

  private selectedWriter : WriterStatus;

  fontList = [
    'ArchitectsDaughter',
//    'BrushScriptMT',
    'CaveatVariable',
    'CoveredByYourGrace',
    'DancingScript',
//    'DefaultFont',
    'Devonshire',
    'HomemadeApple',
    'IndieFlower',
    'LiuJianMaoCao',
//    'Monaco',
    'MysteryQuest',
//    'NewTegomin',
    'Pangolin',
    'PeaEmilysPen',
    'PeaPinkPeaches',
    'PressStart2P',
    'PrincessSofia',
    'ReenieBeanie',
    'Rye',
    'Sacramento',
    'ShadowsIntoLight',
//    'TitilliumWeb',
    'VT323',
    'WalterTurncoat',
  ]

  constructor($scope: WritingBoardScope, $http : IHttpService,
              $interval : IIntervalService, 
              $uibModal : angular.ui.bootstrap.IModalService,
              SessionTracker : SessionTracker) {
    this.$scope = $scope;
    this.$http = $http;
    this.$uibModal = $uibModal;
    this.$interval = $interval;
    this.SessionTracker = SessionTracker;
    this.state = 'LOADING';
    this.wasEpisodeDone = false;

    this.$scope.curFont = 'DefaultFont';

    this.statusQuery = $interval(() => {this.queryStatus(false);}, 2000);
    $scope.$on('$destroy', () => {
      this.cancelStatusQuery();
    });

    this.queryStatus(true);
  }

  private cancelStatusQuery() {
    if(this.statusQuery) {
      this.$interval.cancel(this.statusQuery);
      this.statusQuery = null;
    }
  }

  authorCommand(writer : WriterStatus, command : string) {
    this.$http({
      method: 'PUT',
      url: '/rest/Rooms/command',
      data: {
        sessionId : this.SessionTracker.getSessionId(),
        authorPublicId : writer.publicId,
        command: command
      }
    }).then((rawresp) => {
      let resp = rawresp.data as BasicResponse;
      if(!resp.success) {
        new ErrorDialog(this.$uibModal, 'Error Running Command', resp.message).open();
      }
    })
    .catch((err) => {
      console.log('something went bad: ' + err);
    })
  }

  changeName() {
    this.SessionTracker.changeName();
  }

  changeFont(font : string) {
    let prefs = new UserPrefs(this.SessionTracker.getSessionId());

    prefs.font = font;
    this.$http({
      method: 'PUT',
      url: '/rest/Rooms/updatePrefs',
      data: prefs
    })
    .then(raw => {
      let resp = raw.data as BasicResponse;
      if(!resp.success) {
        new ErrorDialog(this.$uibModal, 'Error', 'Failed to update font: ' + resp.message).open();
      }
      else{
        this.$scope.curFont = font;
      }
    })
    .catch(err => {
      new ErrorDialog(this.$uibModal, 'Failure', 'Failed to set font: ' + err);
    })
  }

  changeMaxRounds() {
    new PromptDialog(this.$uibModal, 'Change Max Rounds', 'Number of rounds', '' + this.status.maxRounds)
      .open()
      .then(newMax => {
        this.$http({
          method: 'GET',
          url: '/rest/Rooms/fragmentLimit/' + this.SessionTracker.getSessionId() + '/' + newMax
        })
        .then(raw => {
          let resp = raw.data as BasicResponse;
          if(!resp.success) {
            new ErrorDialog(this.$uibModal, 'Error', 'Failed to update max rounds: ' + resp.message).open();
          }
        })
        .catch(err => {
          new ErrorDialog(this.$uibModal, 'Error', 'Failed to change max rounds: ' + err).open();
        })
      })
      .catch(() => {});
  }

  selectFinishedStory(writer : WriterStatus) {
    this.$http({
      method: 'GET',
      url: '/rest/Rooms/selectFinishedStory/' + this.SessionTracker.getSessionId() + '/' + writer.publicId
    }).then(raw => {
      let resp = raw.data as BasicResponse;

      if(!resp.success) {
        new ErrorDialog(this.$uibModal, 'Error', 'Failed to select story: ' + resp.message).open();
      }
    }).catch(err => {
      new ErrorDialog(this.$uibModal, 'Error', 'Failure requesting story: ' + err).open();
    })
  }

  loadCurrentFinishedStory() {
    this.$http({
      method: 'GET',
      url: '/rest/Rooms/finishedStory/' + this.SessionTracker.getSessionId()
    }).then(raw => {
      let resp = raw.data as FinishedStoryResponse;

      if(!resp.success) {
        new ErrorDialog(this.$uibModal, 'Error', 'Failed to request finished story: ' + resp.message).open();
      }
      else {
        this.showStory(resp.story);
      }
    }, err => {
      new ErrorDialog(this.$uibModal, 'Failure', 'Failure to load requested story: ' + err);
    })
  }

  queryStatus(updateAll : boolean) {
    this.$http({
      method: 'GET',
      url: '/rest/Rooms/status/' + this.SessionTracker.getSessionId()
    }).then(raw => {
      let resp = raw.data as RoomStatusResponse;

//      console.log('got response: ' + JSON.stringify(resp.status));
      if(!resp.success) {
        this.handleQueryFailure(resp.message);
      }
      else {
        this.updateDisplay(resp.status, updateAll);
      }
    }, (err) => {
      console.log('failed to load room: ' + err);
    });
  }

  private handleQueryFailure(message : string) {
    if(!this.displayingQueryFailure) {
      this.displayingQueryFailure = true;
      this.cancelStatusQuery();
      new ErrorDialog(this.$uibModal, 'Error Getting Status', message).open().then(() => {
        this.SessionTracker.reloadSession();
        this.displayingQueryFailure = false;
      });
    }
  }

  handleDoneChange() {
    this.submitFragment();
  }

  handleTextTyped(event : any, kind : string) {
    if(this.autoSubmitTimer) {
      this.$interval.cancel(this.autoSubmitTimer);
    }
    this.autoSubmitTimer = this.$interval(this.autoSubmitCallback.bind(this), 1500, 1);
  }

  private autoSubmitCallback() {
    this.$interval.cancel(this.autoSubmitTimer);
    this.autoSubmitTimer = null;

    this.$scope.autoSaveRunning = true;
    this.submitFragment()
      .then(() => this.$scope.autoSaveRunning = false)
      .catch(() => this.$scope.autoSaveRunning = false);
  }

  submitFragment() {
    var update = {
      sessionId: this.SessionTracker.getSessionId(),
      hiddenText: this.frag.hiddenText,
      visibleText: this.frag.visibleText,
      finished: this.frag.done
    }

    return this.$http({
      method: 'PUT',
      url: '/rest/Rooms/fragment',
      data: update
    }).then(raw  => {
      let resp = raw.data as RoomStatusResponse;

      console.log('got update response: ' + JSON.stringify(resp));
      if(!resp.success) {
        new ErrorDialog(this.$uibModal, 'Error Updating', 'Error applying update: ' + resp.message).open();
      }
      else {
        this.updateDisplay(resp.status, true);
      }
    }, (err) => {
      console.log('error updating: ' + err);
      new ErrorDialog(this.$uibModal, 'Error Updating', 'Error applying update: ' + err).open();
    })
  }

  viewStory(story : any) {
    console.log('view story for ' + story.authorName);
    this.curStory = story;
  }

  updateDisplay(status : RoomStatus, updateAll : boolean) {
    let rebuildWriterStatus = false;
    let writerList = status.writerStatusList;
    let fontChanged = false;

    this.$scope.lastRound = status.roundNumber >= status.maxRounds;

    if(updateAll || this.status == null || this.status.roundNumber != status.roundNumber) {
      this.status = status;
      this.frag.copyStatus(status);
      rebuildWriterStatus = true;
      this.status.writerStatusList = [];
    }
    else if(this.status.writerStatusList.length != writerList.length) {
      rebuildWriterStatus = true;
      this.status.writerStatusList = [];
    }

    this.status.roundTimerRunning = status.roundTimerRunning;
    this.status.maxRounds = status.maxRounds;
    this.status.episodeTimerRunning = status.episodeTimerRunning;

    for(let i = 0; i < writerList.length; i++) {
      if(rebuildWriterStatus) {
        this.status.writerStatusList.push(new WriterStatus());
      }
      if(this.status.writerStatusList[i].font != writerList[i].font) {
        fontChanged = true;
      }
      this.status.writerStatusList[i].updateStatus(writerList[i]);

      if(this.status.writerStatusList[i].publicId == this.status.prevAuthorPublicId) {
        if(this.status.prevAuthorStatus != this.status.writerStatusList[i]) {
          this.status.prevAuthorStatus = this.status.writerStatusList[i];
        }
      }
    }

    this.status.writerStatusList.forEach(writer => {
      if(writer.myStatus) {
        this.amLeader = writer.roomLeader;
        this.$scope.curFont = writer.font || 'DefaultFont';
      }
    })

    if(updateAll || this.status.selectedStoryAuthorId != status.selectedStoryAuthorId) {
      this.selectedWriter = null;

      if(status.selectedStoryAuthorId) {
        this.status.selectedStoryAuthorId = status.selectedStoryAuthorId;
        console.log('selected author = ' + this.status.selectedStoryAuthorId);
        this.status.writerStatusList.forEach(writer => {
          if(writer.publicId == status.selectedStoryAuthorId) {
            this.selectedWriter = writer;
          }
        });

        console.log('need to request story!!!');
        this.loadCurrentFinishedStory();
      }
      else{
        this.selectedWriter = null;
        this.curStory = null;
      }
    }

    this.state = status.episodeDone ? 'REVIEW' : 'WRITING';

    this.roundTimeRemaining = status.roundTimerRunning ?
      (status.roundTimeRemaining / 1000).toFixed(0) : '0';

    this.episodeTimeRemaining = status.episodeTimerRunning ?
      (status.episodeTimeRemaining / 1000).toFixed(0) : '0';

    if(status.episodeDone != this.wasEpisodeDone) {
      this.wasEpisodeDone = status.episodeDone;
    }
    if(!status.episodeDone) {
      this.curStory = null;
    }
    else if(fontChanged) {
      this.loadCurrentFinishedStory();
    }
  }

  showStory(story : FinishedStory) {
    this.curStory = story;
    console.log('got finals: ' + JSON.stringify(this.curStory));
  }

  nextEpisode() {
    this.$http({
      method: 'GET',
      url: '/rest/Rooms/startNewEpisode/' + this.SessionTracker.getSessionId()
    }).then((resp) => {
      console.log('resp is ' + resp.data);
    }, (err) => {
      console.log('error starting: ' + err);
    });
  }

  cancelNextEpisode() {
    this.$http({
      method: 'GET',
      url: '/rest/Rooms/cancelEpisodeStart/' + this.SessionTracker.getSessionId()
    }).then((resp) => {
      console.log('resp is ' + resp.data);
    }, (err) => {
      console.log('error starting: ' + err);
    });
  }
}