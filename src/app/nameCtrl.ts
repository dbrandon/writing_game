'use strict';

import { IIntervalService, IPromise, IScope, ITimeoutService } from "angular";
import { SessionTracker } from './app';

export let namePrompt = () => {
  return {
    template: require('./namePrompt.html').default,
    controller: 'NameCtrl',
    controllerAs: 'ctrl'
  }
};

export class NameCtrl {
  private SessionTracker: SessionTracker;
  private $scope : IScope;
  private newName : string;
  private lastErrorMessage : string;
  private $interval: IIntervalService;

  constructor(SessionTracker : SessionTracker, $scope : IScope, $interval :IIntervalService) {
    this.SessionTracker = SessionTracker;
    this.$scope = $scope;
    this.$interval = $interval;
  }

  keypress(e : any) {
    if(e.keyCode === 13) {
      this.updateName();
    }
  }

  private clearPromise : IPromise<any>;

  private setError(error : string) {
    this.$interval(() => {
      if(this.clearPromise != null) {
        this.$interval.cancel(this.clearPromise);
        this.clearPromise = null;
      }
      this.lastErrorMessage = error;

      this.clearPromise = this.$interval(this.clearError.bind(this), 3000, 1);
    }, 1, 1);
  }

  private clearError() {
    this.lastErrorMessage = null;
  }

  updateName() {
    if(this.newName != null && this.newName.length > 0) {
      this.SessionTracker.requestNewSession(this.newName)
        .then(() => {
          this.SessionTracker.setGameState('INROOM')
        })
        .catch((err : any) => {
          this.setError(err);
        });
    }
    else {
      this.setError("Name was too short");
    }
  }
}