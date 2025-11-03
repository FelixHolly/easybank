export interface Notice {
  noticeId: number;
  noticeSummary: string;
  noticeDetails: string;
  noticBegDt: Date;
  noticEndDt: Date;
  createDt?: Date;  // Optional, ignored in JSON from backend
  updateDt?: Date;  // Optional, ignored in JSON from backend
}
