

export class UserPrefs {
  font : string;
  name : string;
  sessionId : string;

  constructor(sessionId : string) {
    this.sessionId = sessionId;
  }
}

export interface FinishedFragment {
  author : string;
  font : string;
  hiddenText : string;
  visibleText : string;
}

export interface FinishedStory {
  author : string;
  font : string;
  fragmentList : FinishedFragment[];
}

export interface FinishedStoryResponse {
  success : boolean;
  message : string;
  story : FinishedStory;
}

export class WriterStatus {
  author: string;
  publicId: string;
  typing: boolean;
  finished: boolean;
  expired: boolean;
  reportedAfk: boolean;
  myStatus: boolean;
  roomLeader: boolean;
  font: string;

  letter: string;

  updateStatus(status: WriterStatus) {
    if (this.author != status.author) {
      this.author = status.author;
      this.letter = this.author.charAt(0).toUpperCase();
    }
    this.publicId = status.publicId;
    if (this.myStatus != status.myStatus) {
      this.myStatus = status.myStatus;
    }
    if (this.typing != status.typing) {
      this.typing = status.typing;
    }
    if (this.finished != status.finished) {
      this.finished = status.finished;
    }
    if (this.expired != status.expired) {
      this.expired = status.expired;
    }
    if (this.reportedAfk != status.reportedAfk) {
      this.reportedAfk = status.reportedAfk;
    }
    if (this.roomLeader != status.roomLeader) {
      this.roomLeader = status.roomLeader;
    }
    if(this.font != status.font) {
      this.font = status.font;
    }
  }
}

export interface RoomStatusResponse {
  message : string;
  status : RoomStatus;
  success : boolean;
}

export interface RoomStatus {
  roomName: string;
  writerStatusList: WriterStatus[];
  episodeDone: boolean;

  prevAuthorPublicId: string;
  prevVisibleFragment: string;
  prevAuthorStatus: WriterStatus;

  workingHiddenFragment: string;
  workingVisibleFragment: string;
  fragmentDone: boolean;

  roundNumber: number;
  maxRounds: number;
  roundTimerRunning: boolean;
  roundTimeRemaining: number;

  episodeTimerRunning: boolean;
  episodeTimeRemaining: number;

  selectedStoryAuthorId : string;
}

export class FragmentStatus {
  hiddenText: string;
  visibleText: string;
  done: boolean;

  constructor() { }

  copyStatus(status: RoomStatus) {
    this.hiddenText = status.workingHiddenFragment;
    this.visibleText = status.workingVisibleFragment;
    this.done = status.fragmentDone;
  }
}

export interface BasicResponse {
  success: boolean;
  message: string;
}
