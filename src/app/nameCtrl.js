'use strict';

export let namePrompt = () => {
  return {
    template: require('./namePrompt.html'),
    controller: 'NameCtrl',
    controllerAs: 'ctrl'
  }
};

export class NameCtrl {
  constructor(SessionTracker, $scope) {
    this.SessionTracker = SessionTracker;
    this.$scope = $scope;
  }

  keypress(e) {
    if(e.keyCode === 13) {
      this.updateName();
    }
  }

  updateName() {
    if(this.newName != null && this.newName.length > 0) {
      this.SessionTracker.requestNewSession(this.newName)
        .then(() => {
          this.SessionTracker.setGameState('INROOM')
        })
        .catch((err) => {
          console.log('fuck: ' + err);
        });
    }
    else {
      console.log("name too short");
    }
  }
}