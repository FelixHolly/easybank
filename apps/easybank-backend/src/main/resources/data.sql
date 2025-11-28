-- -------------------------
-- USERS  (AUTO ID)
-- -------------------------
INSERT INTO users (
  name, email, create_dt
) VALUES
    ('Alice Adams',   'alice.adams@example.com',   '2025-10-01'),
    ('Bob Brown',     'bob.brown@example.com',     '2025-10-02'),
    ('Charlie Carter','charlie.carter@example.com','2025-10-03'),
    ('Diana Dawson',  'diana.dawson@example.com',  '2025-10-04'),
    ('Admin User',    'admin@example.com',         '2025-10-05');

-- -------------------------
-- ACCOUNTS
-- -------------------------
INSERT INTO accounts (
  user_id, account_number, account_type, branch_address, create_dt
) VALUES
    (1, 10000001, 'SAVINGS',  '123 Main St, Las Vegas, NV',  '2025-10-01'),
    (2, 10000002, 'CHECKING', '456 Desert Ave, Henderson, NV','2025-10-02'),
    (3, 10000003, 'CREDIT',   '789 Palm Blvd, Summerlin, NV','2025-10-03'),
    (1, 10000004, 'SAVINGS',  '123 Main St, Las Vegas, NV',  '2025-10-04'),
    (4, 10000005, 'CHECKING', '555 Sunset Rd, Las Vegas, NV','2025-10-05'),
    -- Diana Dawson additional accounts
    --(4, 10000006, 'SAVINGS',  '555 Sunset Rd, Las Vegas, NV','2025-10-06'),
    --(4, 10000007, 'CREDIT',   '555 Sunset Rd, Las Vegas, NV','2025-10-07')
    ;

-- -------------------------
-- ACCOUNT TRANSACTIONS
-- -------------------------
INSERT INTO account_transactions (
  transaction_id,
  account_number,
  user_id,
  transaction_dt,
  transaction_summary,
  transaction_type,
  transaction_amt,
  closing_balance,
  create_dt
) VALUES
    ('TXN-1001', 10000001, 1, '2025-10-02', 'ATM Withdrawal',      'DEBIT',  200, 4800, '2025-10-02'),
    ('TXN-1002', 10000001, 1, '2025-10-05', 'Salary Credit',       'CREDIT', 1500, 6300, '2025-10-05'),
    ('TXN-1003', 10000002, 2, '2025-10-06', 'Online Purchase',     'DEBIT',  120, 2880, '2025-10-06'),
    ('TXN-1004', 10000002, 2, '2025-10-08', 'Transfer Received',   'CREDIT',  400, 3280, '2025-10-08'),
    ('TXN-1005', 10000003, 3, '2025-10-09', 'Utility Bill Payment','DEBIT',   90, 4910, '2025-10-09'),
    ('TXN-1006', 10000004, 1, '2025-10-10', 'Grocery Payment',     'DEBIT',   60, 6240, '2025-10-10'),
    ('TXN-1007', 10000005, 4, '2025-10-11', 'Cash Deposit',        'CREDIT', 500, 5500, '2025-10-11'),
    -- Diana Dawson additional transactions (24 more for total 25)
    ('TXN-2001', 10000005, 4, '2025-09-01', 'Opening Balance',        'CREDIT', 5000, 5000, '2025-09-01'),
    ('TXN-2002', 10000005, 4, '2025-09-05', 'Salary Deposit',         'CREDIT', 3500, 8500, '2025-09-05'),
    ('TXN-2003', 10000005, 4, '2025-09-07', 'Rent Payment',           'DEBIT',  1200, 7300, '2025-09-07'),
    ('TXN-2004', 10000005, 4, '2025-09-10', 'Grocery Store',          'DEBIT',   150, 7150, '2025-09-10'),
    ('TXN-2005', 10000005, 4, '2025-09-12', 'ATM Withdrawal',         'DEBIT',   200, 6950, '2025-09-12'),
    ('TXN-2006', 10000005, 4, '2025-09-15', 'Electric Bill',          'DEBIT',    85, 6865, '2025-09-15'),
    ('TXN-2007', 10000005, 4, '2025-09-18', 'Online Shopping',        'DEBIT',   220, 6645, '2025-09-18'),
    ('TXN-2008', 10000005, 4, '2025-09-20', 'Transfer to Savings',    'DEBIT',  1000, 5645, '2025-09-20'),
    ('TXN-2009', 10000005, 4, '2025-09-25', 'Restaurant Payment',     'DEBIT',    75, 5570, '2025-09-25'),
    ('TXN-2010', 10000005, 4, '2025-09-28', 'Freelance Income',       'CREDIT',  600, 6170, '2025-09-28'),
    ('TXN-2011', 10000005, 4, '2025-10-01', 'Gas Station',            'DEBIT',    50, 6120, '2025-10-01'),
    ('TXN-2012', 10000005, 4, '2025-10-05', 'Monthly Salary',         'CREDIT', 3500, 9620, '2025-10-05'),
    ('TXN-2013', 10000005, 4, '2025-10-07', 'Rent Payment',           'DEBIT',  1200, 8420, '2025-10-07'),
    ('TXN-2014', 10000005, 4, '2025-10-09', 'Pharmacy Purchase',      'DEBIT',    45, 8375, '2025-10-09'),
    -- Diana Dawson Savings account transactions
    ('TXN-2015', 10000006, 4, '2025-09-20', 'Transfer from Checking', 'CREDIT', 1000, 1000, '2025-09-20'),
    ('TXN-2016', 10000006, 4, '2025-09-25', 'Interest Credit',        'CREDIT',   12, 1012, '2025-09-25'),
    ('TXN-2017', 10000006, 4, '2025-10-01', 'Cash Deposit',           'CREDIT',  500, 1512, '2025-10-01'),
    ('TXN-2018', 10000006, 4, '2025-10-15', 'Investment Transfer',    'CREDIT', 2000, 3512, '2025-10-15'),
    ('TXN-2019', 10000006, 4, '2025-10-20', 'Emergency Withdrawal',   'DEBIT',   500, 3012, '2025-10-20'),
    ('TXN-2020', 10000006, 4, '2025-10-25', 'Interest Credit',        'CREDIT',   15, 3027, '2025-10-25'),
    -- Diana Dawson Credit account transactions
    ('TXN-2021', 10000007, 4, '2025-10-01', 'Credit Card Payment',    'CREDIT',  300, 300, '2025-10-01'),
    ('TXN-2022', 10000007, 4, '2025-10-08', 'Shopping Mall Purchase',  'DEBIT',   180, 120, '2025-10-08'),
    ('TXN-2023', 10000007, 4, '2025-10-12', 'Online Subscription',    'DEBIT',    25,  95, '2025-10-12'),
    ('TXN-2024', 10000007, 4, '2025-10-18', 'Restaurant Dinner',      'DEBIT',    90,   5, '2025-10-18');

-- -------------------------
-- CARDS
-- -------------------------
INSERT INTO cards (
  card_id, user_id, card_number, card_type, total_limit, amount_used, available_amount, create_dt
) VALUES
    (5001, 1, '4111111111111111', 'VISA',       5000, 1200, 3800, '2025-10-01'),
    (5002, 2, '5555555555554444', 'MASTERCARD', 3000,  750, 2250, '2025-10-02'),
    (5003, 3, '6011000990139424', 'DISCOVER',   4000, 1500, 2500, '2025-10-03'),
    (5004, 1, '3530111333300000', 'JCB',        2000,  300, 1700, '2025-10-04'),
    (5005, 4, '4000123412341234', 'VISA',       7000, 2200, 4800, '2025-10-05'),
    -- Diana Dawson additional cards (4 more for total 5)
    (5006, 4, '5425233430109903', 'MASTERCARD', 5000, 3800, 1200, '2025-09-15'),
    (5007, 4, '378282246310005',  'AMEX',       10000, 1500, 8500, '2025-08-20'),
    (5008, 4, '6011111111111117', 'DISCOVER',   6000,  450, 5550, '2025-09-01'),
    (5009, 4, '4012888888881881', 'VISA',       3000, 2700,  300, '2025-07-10');

-- -------------------------
-- CONTACT MESSAGES
-- -------------------------
INSERT INTO contact_messages (
  contact_id, contact_name, contact_email, subject, message, create_dt
) VALUES
    ('C-1001', 'Alice Adams',   'alice.adams@example.com',   'Unable to login',    'I tried resetting my password but still cannot log in.', '2025-10-01'),
    ('C-1002', 'Bob Brown',     'bob.brown@example.com',     'New card request',   'Please issue a replacement card for my expired one.',   '2025-10-02'),
    ('C-1003', 'Charlie Carter','charlie.carter@example.com','Loan inquiry',       'Could you provide details about available loan options?','2025-10-03'),
    ('C-1004', 'Diana Dawson',  'diana.dawson@example.com',  'Account closure',    'I would like to close my secondary checking account.',   '2025-10-04'),
    ('C-1005', 'Ethan Evans',   'ethan.evans@example.com',   'Feedback',           'The new mobile banking UI is great! Keep it up.',        '2025-10-05');

-- -------------------------
-- LOANS  (FIXED: added create_dt col; removed stray comma; MySQL dates)
-- -------------------------
INSERT INTO loans (
  loan_number,
  user_id,
  start_dt,
  loan_type,
  total_loan,
  amount_paid,
  outstanding_amount,
  create_dt
) VALUES
    (7001, 1, '2023-06-15', 'HOME',      250000,  50000, 200000, '2025-10-01'),
    (7002, 2, '2024-01-10', 'AUTO',       20000,   5000,  15000, '2025-10-02'),
    (7003, 3, '2023-12-01', 'EDUCATION',  40000,  10000,  30000, '2025-10-03'),
    (7004, 1, '2024-02-20', 'PERSONAL',   10000,   2500,   7500, '2025-10-04'),
    (7005, 4, '2022-11-05', 'HOME',      300000, 120000, 180000, '2025-10-05'),
    -- Diana Dawson additional loans (2 more for total 3)
    (7006, 4, '2023-08-15', 'AUTO',       35000,  26250,   8750, '2025-10-05'),
    (7007, 4, '2021-09-01', 'EDUCATION',  60000,  55000,   5000, '2025-10-05');

-- -------------------------
-- NOTICE DETAILS
-- -------------------------
INSERT INTO notice_details (
  notice_id,
  notice_summary,
  notice_details,
  notic_beg_dt,
  notic_end_dt,
  create_dt,
  update_dt
) VALUES
    (9001, 'Scheduled Maintenance',
     'Our online banking services will be unavailable on Sunday, November 10th from 2:00 AM to 4:00 AM PST due to scheduled maintenance.',
     '2025-11-08', '2025-11-10', '2025-11-01', '2025-11-03'),
    (9002, 'New Credit Card Features',
     'We have added instant transaction notifications and spending analytics for all credit card users.',
     '2025-10-15', '2025-12-31', '2025-10-10', '2025-10-12'),
    (9003, 'Branch Opening',
     'We are excited to announce the opening of a new EasyBank branch in Henderson, NV.',
     '2025-11-01', '2026-01-01', '2025-10-25', '2025-10-27'),
    (9004, 'Holiday Hours',
     'Our branches will close early at 1:00 PM on December 24th and reopen on December 26th.',
     '2025-12-20', '2025-12-26', '2025-11-15', '2025-11-18'),
    (9005, 'Security Reminder',
     'Please do not share your login credentials or one-time passwords with anyone. EasyBank will never ask for them via email or phone.',
     '2025-09-01', '2025-12-31', '2025-09-01', '2025-10-01');
