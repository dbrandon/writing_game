
import angular, { IScope } from 'angular';

const page = require('./promptDialog.html').default;

interface PromptDialogScope extends IScope {
  title : string;
  message : string;
  textField : string;

  handleOk():void;
  handleDismiss():void;
  keypress(e:any):void;
}

export class PromptDialog {
  private $uibModal : angular.ui.bootstrap.IModalService;
  private title : string;
  private message : string;
  private textField : string;
  private dialog : angular.ui.bootstrap.IModalInstanceService;

  constructor($uibModal : angular.ui.bootstrap.IModalService, title : string, message : string, textField : string) {
    this.$uibModal =$uibModal;
    this.title = title;
    this.message = message;
    this.textField = textField;
  }

  open() {
    return new Promise((resolve, reject) => {
      this.dialog = this.$uibModal.open({
        template: page,
        controller: [ '$scope', this.controller.bind(this)]
      });

      this.dialog.result.then(() => {
        if(this.textField) {
          resolve(this.textField);
        }
        else {
          reject();
        }
      }, () => { reject(); });
    });
  }

  private controller($scope: PromptDialogScope) {
    $scope.title = this.title;
    $scope.message = this.message;
    $scope.textField = this.textField;

    $scope.handleOk = () => {
      this.textField = $scope.textField;
      this.dialog.close();
    }
    $scope.handleDismiss = () => {
      this.textField = null;
      this.dialog.close();
    }

    $scope.keypress = (e) => {
      if(e.keyCode == 13) {
        $scope.handleOk();
      }
      if(e.keyCode == 27) {
        $scope.handleDismiss();
      }
    }
  }
}