import angular from 'angular';

import {namePrompt, NameCtrl} from './nameCtrl';
import {rooms, RoomCtrl} from './roomList';
import {writingBoard, WritingBoardCtrl} from './writingBoard';

import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.min.css'
import '../style/app.css';
import '../style/board.css';

let app = () => {
  return {
    template: require('./app.html'),
    controller: 'AppCtrl',
    controllerAs: 'app'
  }
};

class AppCtrl {
  constructor(SessionTracker, $scope) {
    this.$scope = $scope;
    this.gameState = 'LOAD';
    this.showExtra = false;
    console.log('loaded app control');

    SessionTracker.setAppCtrl(this);
  }

  setGameState(state) {
    this.gameState = state;
    if(!this.$scope.$$phase) {
      this.$scope.$apply();
    }
    console.log('state is now ' + this.gameState);
  }
}

const COOKIE_NAME = 'writingGameSessionId';

class SessionTracker {
  constructor($http, $cookies) {
    this.$http = $http;
    this.$cookies = $cookies;

    this.loadSessionId();

    console.log('Instantiated the session tracker');
  }

  setAppCtrl(appCtrl) {
    this.appCtrl = appCtrl;
  }

  loadSessionId() {
    let self = this;
    let id = self.$cookies.get(COOKIE_NAME);
    console.log('Id is ' + id);
    if (id == null) {
      this.requestNewSession();
    }
    else {
      this.requestCurrentSession(id);
    }
  }

  requestNewSession(playerName, roomName) {
    var request = {
      name: playerName,
      roomName: roomName || 'Brandonses'
    }

    return new Promise((resolve, reject) => {
      this.$http({
        method: 'PUT',
        url: '/rest/Rooms/joinOrCreate',
        data: request
      }).then((resp) => {
        console.log('got a session: ' + JSON.stringify(resp.data));
        if(!resp.data.success) {
          reject('Failed to request session: ' + resp.data.message);
        }
        else if(resp.data.info == null) {
          reject('Failed to request session (did not receive a session identifier)');
        }
        else {
          this.$cookies.put(COOKIE_NAME, resp.data.info.sessionId);
          this.setSessionInfo(resp.data.info);
          resolve();
        }
      }, (err) => {
        console.log('Failed: ' + JSON.stringify(err));
        reject('Failed to request session (error requesting session from server)')
      })
    });
  }

  requestCurrentSession(id) {
    console.log('request current session with ID=[' + id + ']')
    this.$http({
      method: 'GET',
      url: '/rest/Rooms/lookup/' + id
    }).then((resp) => {
      console.log('got my session info: ' + JSON.stringify(resp.data));
      if(resp.data.info == null || !resp.data.success) {
        this.appCtrl.setGameState('GETNAME')
      }
      else {
        this.setSessionInfo(resp.data.info);
        this.appCtrl.setGameState('INROOM')
      }
    }, (err) => {
      console.log('Failed: ' + JSON.stringify(err));
    })
  }

  getSessionId() {
    return this.sessionInfo.sessionId;
  }

  setSessionInfo(info) {
    this.sessionInfo = info;
  }

  setGameState(state) {
    this.appCtrl.setGameState(state);
  }
}

const MODULE_NAME = 'app';

angular.module(MODULE_NAME, [require('angular-cookies')])
  .service('SessionTracker', ['$http', '$cookies', SessionTracker])
  .directive('app', app)
  .controller('AppCtrl', ['SessionTracker', '$scope', AppCtrl])
  .directive('namePrompt', namePrompt)
  .controller('NameCtrl', ['SessionTracker', '$scope', NameCtrl])
  .directive('roomList', rooms)
  .controller('RoomCtrl', RoomCtrl)
  .directive('writingBoard', writingBoard)
  .controller('WritingBoardCtrl', ['$scope', '$http', '$interval', 'SessionTracker', WritingBoardCtrl]);

export default MODULE_NAME;