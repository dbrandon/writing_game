import angular, { IHttpService, IScope } from 'angular';
const ngCookies = require('angular-cookies');
const uiBootstrap = require('ui-bootstrap4');

import {namePrompt, NameCtrl} from './nameCtrl';
//import {rooms, RoomCtrl} from './roomList';
import {writingBoard, WritingBoardCtrl} from './writingBoard';

import 'bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css'
import '@fortawesome/fontawesome-free/js/all'
import '../style/app.css';
import '../style/board.css';

let app = () => {
  return {
    template: require('./app.html').default,
    controller: 'AppCtrl',
    controllerAs: 'app'
  }
};

class AppCtrl {
  private $scope : IScope;
  private gameState : string;
  private showExtra : boolean;

  constructor(SessionTracker : SessionTracker, $scope : IScope) {
    this.$scope = $scope;
    this.gameState = 'LOAD';
    this.showExtra = false;
    console.log('loaded app control');

    SessionTracker.setAppCtrl(this);
  }

  setGameState(state : string) {
    this.gameState = state;
    if(!this.$scope.$$phase) {
      this.$scope.$apply();
    }
    console.log('state is now ' + this.gameState);
  }
}

const COOKIE_NAME = 'writingGameSessionId';


class SessionInfo {
  sessionId : string;
  name : string;
  roomName : string;
}

class SessionInfoResponse {
  success : boolean;
  message : string;
  info : SessionInfo;
}

export class SessionTracker {
  private $http : IHttpService;
  private $cookies : angular.cookies.ICookiesService;
  private appCtrl : AppCtrl;
  private sessionInfo : SessionInfo;

  constructor($http : IHttpService, $cookies : angular.cookies.ICookiesService) {
    this.$http = $http;
    this.$cookies = $cookies;

    this.loadSessionId();

    console.log('Instantiated the session tracker');
  }

  setAppCtrl(appCtrl : AppCtrl) {
    this.appCtrl = appCtrl;
  }

  reloadSession() {
    this.setGameState('LOAD');
    this.loadSessionId();
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

  requestNewSession(playerName? : string, roomName? : string) {
    var request = {
      name: playerName,
      roomName: roomName || 'Brandonses'
    }

    return new Promise((resolve, reject) => {
      this.$http({
        method: 'PUT',
        url: '/rest/Rooms/joinOrCreate',
        data: request
      }).then((rawresp) => {
        let resp = rawresp.data as SessionInfoResponse;
        console.log('got a session: ' + JSON.stringify(resp));
        if(!resp.success) {
          reject('Failed to request session: ' + resp.message);
        }
        else if(resp.info == null) {
          reject('Failed to request session (did not receive a session identifier)');
        }
        else {
          this.$cookies.put(COOKIE_NAME, resp.info.sessionId);
          this.setSessionInfo(resp.info);
          resolve(null);
        }
      }, (err) => {
        console.log('Failed: ' + JSON.stringify(err));
        reject('Failed to request session (error requesting session from server)')
      })
    });
  }

  requestCurrentSession(id : string) {
    console.log('request current session with ID=[' + id + ']')
    this.$http({
      method: 'GET',
      url: '/rest/Rooms/lookup/' + id
    }).then((rawresp) => {
      let resp = rawresp.data as SessionInfoResponse;
      console.log('got my session info: ' + JSON.stringify(resp));
      if(resp.info == null || !resp.success) {
        this.appCtrl.setGameState('GETNAME')
      }
      else {
        this.setSessionInfo(resp.info);
        this.appCtrl.setGameState('INROOM')
      }
    }, (err) => {
      console.log('Failed: ' + JSON.stringify(err));
      this.appCtrl.setGameState('GETNAME');
    })
  }

  getSessionId() {
    return this.sessionInfo.sessionId;
  }

  setSessionInfo(info : SessionInfo) {
    this.sessionInfo = info;
  }

  setGameState(state : string) {
    this.appCtrl.setGameState(state);
  }
}

const MODULE_NAME = 'app';

export let writingGameModule = angular.module(MODULE_NAME, [ngCookies, uiBootstrap])
  .service('SessionTracker', ['$http', '$cookies', SessionTracker])
  .directive('app', app)
  .controller('AppCtrl', ['SessionTracker', '$scope', AppCtrl])
  .directive('namePrompt', namePrompt)
  .controller('NameCtrl', ['SessionTracker', '$scope', '$interval', NameCtrl])
  // .directive('roomList', rooms)
  // .controller('RoomCtrl', RoomCtrl)
  .directive('writingBoard', writingBoard)
  .controller('WritingBoardCtrl', ['$scope', '$http', '$interval', '$uibModal', 'SessionTracker', WritingBoardCtrl]);

export default MODULE_NAME;