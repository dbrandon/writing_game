'use strict';

import angular, { IHttpService, IIntervalService, IScope } from 'angular';
import { SessionTracker } from './app';
import '../style/ctrl.css';

import { ErrorDialog } from './errorDialog';

export let writingBoard = () => {
  return {
    template: require('./writingBoard.html').default,
    controller: 'WritingBoardCtrl',
    controllerAs: 'ctrl'
  }
};

class WriterStatus {
  author : string;
  publicId : string;
  typing : boolean;
  finished : boolean;
  expired : boolean;
  reportedAfk : boolean;

  letter : string;

  updateStatus(status : WriterStatus) {
    if(this.author != status.author) {
      this.author = status.author;
      this.letter = this.author.charAt(0).toUpperCase();
    }
    this.publicId = status.publicId;
    if(this.typing != status.typing) {
      this.typing = status.typing;
    }
    if(this.finished != status.finished) {
      this.finished = status.finished;
    }
    if(this.expired != status.expired) {
      this.expired = status.expired;
    }
    if(this.reportedAfk != status.reportedAfk) {
      this.reportedAfk = status.reportedAfk;
    }
  }
}

interface RoomStatus {
  errorMessage : string;
  roomName : string;
  writerStatusList : WriterStatus[];
  episodeDone : boolean;
  prevVisibleFragment : string;
  workingHiddenFragment : string;
  workingVisibleFragment : string;
  fragmentDone : boolean;

  roundNumber : number;
  maxRound : number;
  roundTimerRunning : boolean;
  roundTimeRemaining : number;

  episodeTimerRunning : boolean;
  episodeTimeRemaining : number;
}

class FragmentStatus {
  hiddenText : string;
  visibleText : string;
  done :boolean;

  constructor() {}

  copyStatus(status : RoomStatus) {
    this.hiddenText = status.workingHiddenFragment;
    this.visibleText = status.workingVisibleFragment;
    this.done = status.fragmentDone;
  }
}

interface BasicResponse {
  success : boolean;
  message : string;
}

export class WritingBoardCtrl {
  private $scope : IScope;
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

  private finalStories: any;
  private curStory: any;

  private statusQuery : angular.IPromise<any>;
  private displayingQueryFailure : boolean;

  constructor($scope : IScope, $http : IHttpService, 
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
      console.log('response: ' + JSON.stringify(resp));
      if(!resp.success) {
        new ErrorDialog(this.$uibModal, 'Error Running Command', resp.message).open();
      }
    })
    .catch((err) => {
      console.log('something went bad: ' + err);
    })
  }

  queryFinalStories() {
    this.$http({
      method: 'GET',
      url: '/rest/Rooms/finishedStories/' + this.SessionTracker.getSessionId()
    }).then((resp) => {
      this.updateFinals(resp.data);
    }, (err) => {
      console.log('failed to load final stories: ' + err);
    })
  }

  queryStatus(updateAll : boolean) {
    this.$http({
      method: 'GET',
      url: '/rest/Rooms/status/' + this.SessionTracker.getSessionId()
    }).then((resp) => {
      let status : RoomStatus = resp.data as RoomStatus;

      console.log('got response: ' + JSON.stringify(status));
      if(status.errorMessage) {
        this.handleQueryFailure(status.errorMessage);
      }
      else {
        this.updateDisplay(status, updateAll);
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

  submitFragment() {
    var update = {
      sessionId: this.SessionTracker.getSessionId(),
      hiddenText: this.frag.hiddenText,
      visibleText: this.frag.visibleText,
      finished: this.frag.done
    }

    this.$http({
      method: 'PUT',
      url: '/rest/Rooms/fragment',
      data: update
    }).then((resp) => {
      console.log('got update response: ' + JSON.stringify(resp.data));
      this.updateDisplay(resp.data as RoomStatus, true);
    }, (err) => {
      console.log('error updating: ' + err);
    })
  }

  viewStory(story : any) {
    console.log('view story for ' + story.authorName);
    this.curStory = story;
  }

  updateDisplay(status : RoomStatus, updateAll : boolean) {
    let rebuildWriterStatus = false;
    let writerList = status.writerStatusList;

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

    for(let i = 0; i < writerList.length; i++) {
      if(rebuildWriterStatus) {
        this.status.writerStatusList.push(new WriterStatus());
      }
      this.status.writerStatusList[i].updateStatus(writerList[i]);
    }

    this.state = status.episodeDone ? 'REVIEW' : 'WRITING';

    this.roundTimeRemaining = status.roundTimerRunning ?
      (status.roundTimeRemaining / 1000).toFixed(0) : '0';

    this.episodeTimeRemaining = status.episodeTimerRunning ?
      (status.episodeTimeRemaining / 1000).toFixed(0) : '0';

    if(status.episodeDone != this.wasEpisodeDone) {
      this.wasEpisodeDone = status.episodeDone;
      if(status.episodeDone) {
        this.queryFinalStories();
      }
    }
  }

  updateFinals(data : any) {
    this.finalStories = data;
    console.log('got finals: ' + JSON.stringify(this.finalStories));
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