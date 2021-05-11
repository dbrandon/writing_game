
export class GameState {
  private _stateName : string;

  static LOAD = new GameState('LOAD');
  static GET_NAME =new GameState('GET_NAME');
  static IN_ROOM = new GameState('IN_ROOM');

  private constructor(stateName : string) {
    this._stateName = stateName;
  }

  public get stateName() {
    return this._stateName;
  }
}