-- -------------------------
-- CUSTOMERS  (AUTO ID)
-- -------------------------
INSERT INTO customers (
  name, email, mobile_number, password, create_dt
) VALUES
    ('Alice Adams',   'alice.adams@example.com',   '702-555-0100', '{noop}password', '2025-10-01'),
    ('Bob Brown',     'bob.brown@example.com',     '702-555-0101', '{noop}password', '2025-10-02'),
    ('Charlie Carter','charlie.carter@example.com','702-555-0102', '{noop}password', '2025-10-03'),
    ('Diana Dawson',  'diana.dawson@example.com',  '702-555-0103', '{noop}password', '2025-10-04'),
    ('Admin User',    'admin@example.com',         '702-555-0104', '{noop}adminpass', '2025-10-05');

-- -------------------------
-- CUSTOMER ROLES (many-to-many)
-- -------------------------
INSERT INTO customer_roles (customer_id, role) VALUES
    (1, 'USER'),            -- Alice: USER
    (2, 'USER'),            -- Bob: USER
    (3, 'USER'),            -- Charlie: USER
    (4, 'USER'),            -- Diana: USER
    (4, 'MANAGER'),         -- Diana: also MANAGER (showing multiple roles)
    (5, 'USER'),            -- Admin: USER
    (5, 'ADMIN'),           -- Admin: ADMIN
    (5, 'MANAGER');         -- Admin: also MANAGER (full privileges)

-- -------------------------
-- CUSTOMER AUTHORITIES (Custom/Exception permissions only)
-- -------------------------
-- With RBAC, authorities are automatically granted based on roles via AuthorityService.
-- This table stores ONLY custom/exception authorities beyond role defaults.
--
-- Examples of when to use custom authorities:
-- 1. Temporarily grant extra permission (e.g., USER gets REPORT_GENERATE for audit)
-- 2. Revoke specific permission (future enhancement: negative authorities)
-- 3. Grant permission before role change (e.g., USER testing MANAGER features)
--
-- For demonstration, we'll add a few custom authorities:

-- Alice gets special report access (beyond USER role)
INSERT INTO customer_authorities (customer_id, authority) VALUES
    (1, 'REPORT_GENERATE');

-- Charlie gets temporary card activation rights (beyond USER role)
INSERT INTO customer_authorities (customer_id, authority) VALUES
    (3, 'CARD_ACTIVATE');

-- Note: Bob, Diana, and Admin don't need custom authorities
-- - Bob: Gets all authorities from USER role (via AuthorityService)
-- - Diana: Gets authorities from USER + MANAGER roles (via AuthorityService)
-- - Admin: Gets all authorities from ADMIN role (via AuthorityService)

-- -------------------------
-- ACCOUNTS
-- -------------------------
INSERT INTO accounts (
  customer_id, account_number, account_type, branch_address, create_dt
) VALUES
    (1, 10000001, 'SAVINGS',  '123 Main St, Las Vegas, NV',  '2025-10-01'),
    (2, 10000002, 'CHECKING', '456 Desert Ave, Henderson, NV','2025-10-02'),
    (3, 10000003, 'CREDIT',   '789 Palm Blvd, Summerlin, NV','2025-10-03'),
    (1, 10000004, 'SAVINGS',  '123 Main St, Las Vegas, NV',  '2025-10-04'),
    (4, 10000005, 'CHECKING', '555 Sunset Rd, Las Vegas, NV','2025-10-05');

-- -------------------------
-- ACCOUNT TRANSACTIONS
-- -------------------------
INSERT INTO account_transactions (
  transaction_id,
  account_number,
  customer_id,
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
    ('TXN-1007', 10000005, 4, '2025-10-11', 'Cash Deposit',        'CREDIT', 500, 5500, '2025-10-11');

-- -------------------------
-- CARDS
-- -------------------------
INSERT INTO cards (
  card_id, customer_id, card_number, card_type, total_limit, amount_used, available_amount, create_dt
) VALUES
    (5001, 1, '4111111111111111', 'VISA',       5000, 1200, 3800, '2025-10-01'),
    (5002, 2, '5555555555554444', 'MASTERCARD', 3000,  750, 2250, '2025-10-02'),
    (5003, 3, '6011000990139424', 'DISCOVER',   4000, 1500, 2500, '2025-10-03'),
    (5004, 1, '3530111333300000', 'JCB',        2000,  300, 1700, '2025-10-04'),
    (5005, 4, '4000123412341234', 'VISA',       7000, 2200, 4800, '2025-10-05');

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
  customer_id,
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
    (7005, 4, '2022-11-05', 'HOME',      300000, 120000, 180000, '2025-10-05');

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
