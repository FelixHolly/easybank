/**
 * Financial Models
 * Models for loans, cards, accounts, and transactions
 */

export interface Loan {
  loanNumber: number;
  userId: number;
  startDt: string;
  loanType: string;
  totalLoan: number;
  amountPaid: number;
  outstandingAmount: number;
  createDt: string;
}

export interface Card {
  cardId: number;
  userId: number;
  cardNumber: string;
  cardType: string;
  totalLimit: number;
  amountUsed: number;
  availableAmount: number;
  createDt: string;
}

export interface Account {
  userId: number;
  accountNumber: number;
  accountType: string;
  branchAddress: string;
  createDt: string;
}

export interface AccountTransaction {
  transactionId: string;
  accountNumber: number;
  userId: number;
  transactionDt: string;
  transactionSummary: string;
  transactionType: string;
  transactionAmt: number;
  closingBalance: number;
  createDt: string;
}
