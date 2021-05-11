'use strict';

export let rooms = () => {
  return {
    template: require('./roomList.html'),
    controller: 'RoomCtrl',
    controllerAs: 'ctrl'
  }
}

export class RoomCtrl {
  constructor($http, $scope, SessionTracker) {
    this.$http = $http;
    this.$scope = $scope;
    this.SessionTracker = SessionTracker;
    this.rooms = []

    this.$http({
      method: 'GET',
      url: '/rest/Rooms/list'
    }).then((resp) => {
      console.log('got a response: ' + JSON.stringify(resp.data));
      this.rooms = resp.data;
    }, (err) => {
      console.log('failed: ' + JSON.stringify(err));
    });

    let self = this;
    SessionTracker.getSessionLoadPromise().then((info) => {
      console.log('loaded session, my name is ' + info.name)
      self.setName(info.name);
    })
  }

  setName(name) {
    this.displayName = name;
    if(!this.$scope.$$phase) {
      this.$scope.$apply();
    }
  }

  createRoom() {
    let name = this.newRoomName;
    console.log('create a room named [' + this.newRoomName + ']');

    this.$http({
      method: 'PUT',
      url: '/rest/Rooms/create',
      data: name
    }).then((resp) => {
      console.log('response: ' + resp.data);
    }, (err) => {
      console.log('failed: ' + JSON.stringify(err));
    });
  }

  joinRoom(roomName) {
    console.log('join room [' + roomName + '] as [' + this.displayName + ']')
    this.SessionTracker.joinRoom(roomName, this.displayName).then(() => {
      console.log('joined a room!');
    }, (err) => {
      console.log('failed to join: ' + err)
    })
  }
}