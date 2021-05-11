import angular, { IHttpService, IScope } from 'angular';
const ngCookies = require('angular-cookies');
const uiBootstrap = require('ui-bootstrap4');

import {namePrompt, NameCtrl} from './nameCtrl';
//import {rooms, RoomCtrl} from './roomList';
import {writingBoard, WritingBoardCtrl} from './writingBoard';
import {GameState} from './gameState';

import { ErrorDialog } from './errorDialog';
import { PromptDialog } from './promptDialog';
import { BasicResponse, UserPrefs } from './api';

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
  private gameState : GameState;

  constructor(SessionTracker : SessionTracker, $scope : IScope) {
    this.$scope = $scope;
    this.gameState = GameState.LOAD;
    console.log('loaded app control');

    SessionTracker.setAppCtrl(this);
  }

  setGameState(state : GameState) {
    this.gameState = state;
    if(!this.$scope.$$phase) {
      this.$scope.$apply();
    }
    console.log('state is now ' + this.gameState.stateName);
  }

  getGameState(): GameState {
    return this.gameState;
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
  private $uibModal : angular.ui.bootstrap.IModalService;

  constructor($http : IHttpService, $cookies : angular.cookies.ICookiesService, $uibModal : angular.ui.bootstrap.IModalService) {
    this.$http = $http;
    this.$cookies = $cookies;
    this.$uibModal = $uibModal;
    console.log('Instantiated the session tracker');
  }

  setAppCtrl(appCtrl : AppCtrl) {
    if(this.appCtrl != appCtrl) {
      console.log('got new appctrl');
      this.appCtrl = appCtrl;
      if(appCtrl.getGameState() == GameState.LOAD) {
        console.log('need to load app ctrl')
        this.loadSessionId()
      }
    }
  }

  changeName() {
    new PromptDialog(this.$uibModal, 'Change Name', 'Enter your new name', this.sessionInfo.name)
      .open()
      .then((name : string) => {
        if(name != this.sessionInfo.name) {
          let prefs = new UserPrefs(this.sessionInfo.sessionId);

          prefs.name = name;
          this.$http({
            method: 'PUT',
            url: '/rest/Rooms/updatePrefs',
            data: prefs
          }).then(raw => {
            let resp = raw.data as BasicResponse;

            if(resp.success) {
              this.sessionInfo.name = name;
            }
            else {
              new ErrorDialog(this.$uibModal, 'Error Changing Name', 'Failed to change name: '+ resp.message).open();
            }
          }).catch(err => {
            console.log('Update error: ' + err);
            new ErrorDialog(this.$uibModal, 'Error Changing Name', 'Failed to change name: ' + err);
          })
        }
      })
      .catch(() => { console.log('no action'); })
  }

  reloadSession() {
    this.setGameState(GameState.LOAD);
    this.loadSessionId()
  }

  loadSessionId() {
    let self = this;
    let id = self.$cookies.get(COOKIE_NAME);
    console.log('Id is ' + id);
    if (id == null) {
      console.log('No session ID available; request name and start new session')
      this.appCtrl.setGameState(GameState.GET_NAME);
    }
    else {
      this.requestCurrentSession(id)
        .catch(err => {
          console.log('Error requestiong session: ' + err);
          this.appCtrl.setGameState(GameState.GET_NAME);
        })
    }
  }

  requestNewSession(playerName? : string, roomName? : string) {
    var request = {
      name: playerName,
      roomName: roomName || 'Brandonses'
    }

    console.log('requesting a new session: ' + JSON.stringify(request))

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
    return new Promise((resolve, reject) => {
      this.$http({
        method: 'GET',
        url: '/rest/Rooms/lookup/' + id
      }).then((rawresp) => {
        let resp = rawresp.data as SessionInfoResponse;
        console.log('got my session info: ' + JSON.stringify(resp));
        if(resp.info == null || !resp.success) {
          reject('Failed to request current session')
          this.appCtrl.setGameState(GameState.GET_NAME)
        }
        else {
          this.setSessionInfo(resp.info);
          this.appCtrl.setGameState(GameState.IN_ROOM)
          resolve('success');
        }
      }, (err) => {
        reject(err);
        console.log('Failed: ' + JSON.stringify(err));
        this.appCtrl.setGameState(GameState.GET_NAME);
      })
    });
  }

  getSessionId() {
    return this.sessionInfo.sessionId;
  }

  setSessionInfo(info : SessionInfo) {
    this.sessionInfo = info;
  }

  setGameState(state : GameState) {
    this.appCtrl.setGameState(state);
  }
}

const MODULE_NAME = 'app';

export let writingGameModule = angular.module(MODULE_NAME, [ngCookies, uiBootstrap])
  .service('SessionTracker', ['$http', '$cookies', '$uibModal', SessionTracker])
  .directive('app', app)
  .controller('AppCtrl', ['SessionTracker', '$scope', AppCtrl])
  .directive('namePrompt', namePrompt)
  .controller('NameCtrl', ['SessionTracker', '$scope', '$interval', NameCtrl])
  // .directive('roomList', rooms)
  // .controller('RoomCtrl', RoomCtrl)
  .directive('writingBoard', writingBoard)
  .controller('WritingBoardCtrl', ['$scope', '$http', '$interval', '$uibModal', 'SessionTracker', WritingBoardCtrl]);

export default MODULE_NAME;