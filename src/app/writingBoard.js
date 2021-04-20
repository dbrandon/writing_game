'use strict';

import '../style/ctrl.css';

export let writingBoard = () => {
  return {
    template: require('./writingBoard.html'),
    controller: 'WritingBoardCtrl',
    controllerAs: 'ctrl'
  }
};

export class WritingBoardCtrl {
  constructor($scope, $http, $interval, SessionTracker) {
    this.$scope = $scope;
    this.$http = $http;
    this.SessionTracker = SessionTracker;
    this.state = 'LOADING';
    this.wasEpisodeDone = false;

    this.frag = {};

    let query = $interval(() => {this.queryStatus(false);}, 2000);
    $scope.$on('$destroy', () => {
      $interval.cancel(query);
    });

    this.queryStatus(true);
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

  queryStatus(updateAll) {
    this.$http({
      method: 'GET',
      url: '/rest/Rooms/status/' + this.SessionTracker.getSessionId()
    }).then((resp) => {
      console.log('got response: ' + JSON.stringify(resp.data));
      this.updateDisplay(resp.data, updateAll);
    }, (err) => {
      console.log('failed to load room: ' + err);
    });
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
      this.updateDisplay(resp.data, true);
    }, (err) => {
      console.log('error updating: ' + err);
    })
  }

  viewStory(story) {
    console.log('view story for ' + story.authorName);
    this.curStory = story;
  }

  updateDisplay(status, updateAll) {
    if(this.status != null && this.status.roundNumber != status.roundNumber) {
      updateAll = true;
    }
    this.status = status;
    if(updateAll) {
      this.frag.hiddenText = status.workingHiddenFragment;
      this.frag.visibleText = status.workingVisibleFragment;
      this.frag.done = status.fragmentDone;
    }

    this.status.writerStatusList.forEach((writer) => {
      writer.letter = writer.author.charAt(0).toUpperCase();
    })

    this.state = status.episodeDone ? 'REVIEW' : 'WRITING';

    this.roundTimeRemaining = status.roundTimerRunning ?
      (status.roundTimeRemaining / 1000).toFixed(0) : 0;

    this.episodeTimeRemaining = status.episodeTimerRunning ?
      (status.episodeTimeRemaining / 1000).toFixed(0) : 0;

    if(status.episodeDone != this.wasEpisodeDone) {
      this.wasEpisodeDone = status.episodeDone;
      if(status.episodeDone) {
        this.queryFinalStories();
      }
    }
  }

  updateFinals(data) {
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