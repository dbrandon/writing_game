import angular, { IScope } from "angular";

const page = require('./errorDialog.html').default;

interface ErrorDialogScope extends IScope {
  title : string;
  message : string;
  handleDismiss():void;
}

export class ErrorDialog {
  private $uibModal : angular.ui.bootstrap.IModalService;
  private title : string;
  private message : string;
  private dialog : angular.ui.bootstrap.IModalInstanceService;

  constructor($uibModal : angular.ui.bootstrap.IModalService, title : string, message : string) {
    this.$uibModal = $uibModal;
    this.title = title;
    this.message = message;
  }

  open() {
    return new Promise((resolve) => {
      this.dialog = this.$uibModal.open({
        template: page,
        controller: ['$scope', this.controller.bind(this)]
      });

      this.dialog.result.then(() => {
        resolve(null);
      }, () => {
        resolve(null);
      })
    })
  }

  private controller($scope : ErrorDialogScope) {
    $scope.title = this.title;
    $scope.message = this.message;

    $scope.handleDismiss = () => {
      this.dialog.close();
    }
  }
}
