import angular, { IScope } from "angular"

const page = require('./menuDialog.html').default

export interface MenuOption<VALUE_TYPE> {
  label: string,
  value: VALUE_TYPE
}

interface MenuDialogScope<VALUE_TYPE> extends IScope {
  title: string;
  prompt: string;
  options: MenuOption<VALUE_TYPE>[];

  itemSelected(option: MenuOption<VALUE_TYPE>): void;
  handleDismiss(): void;
}

export class MenuDialog<VALUE_TYPE> {
  private $uibModal : angular.ui.bootstrap.IModalService;
  private dialog: angular.ui.bootstrap.IModalInstanceService;
  private title: string;
  private prompt: string;
  private options: MenuOption<VALUE_TYPE>[];
  private selection: MenuOption<VALUE_TYPE>;

  constructor($uibModal: angular.ui.bootstrap.IModalService, title: string,
    prompt: string, options: MenuOption<VALUE_TYPE>[]) {
    this.$uibModal = $uibModal;
    this.title = title;
    this.prompt = prompt;
    this.options = options;
  }

  open() {
    return new Promise<MenuOption<VALUE_TYPE>>((resolve, reject) => {
      this.dialog = this.$uibModal.open({
        template: page,
        controller: [ '$scope', this.controller.bind(this)]
      });

      this.dialog.result.then(() => {
        resolve(this.selection);
      }, () => { resolve(null) });
    })
  }

  private controller($scope: MenuDialogScope<VALUE_TYPE>) {
    $scope.title = this.title;
    $scope.prompt = this.prompt;
    $scope.options = this.options;

    $scope.itemSelected = selection => {
      this.selection = selection;
      this.dialog.close();
    }

    $scope.handleDismiss = () => {
      this.dialog.close();
    }
  }
}