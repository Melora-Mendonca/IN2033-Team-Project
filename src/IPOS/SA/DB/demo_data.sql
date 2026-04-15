-- IPOS-SA Demo Data — Based on Sample Data PDF v1.1
-- Run this script to wipe and reload the database for the demo.
-- IMPORTANT: Keep one default admin login before running, or run as-is.

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE `recordpayment`;
TRUNCATE TABLE `overduepayment`;
TRUNCATE TABLE `stockdelivery`;
TRUNCATE TABLE `monthlydiscount`;
TRUNCATE TABLE `orderitem`;
TRUNCATE TABLE `invoice`;
TRUNCATE TABLE `order`;
TRUNCATE TABLE `commercial_applications`;
TRUNCATE TABLE `merchant`;
TRUNCATE TABLE `catalogue`;
TRUNCATE TABLE `userlogin`;

SET FOREIGN_KEY_CHECKS = 1;

-- ─── STAFF LOGINS ────────────────────────────────────────────────────────────
-- Passwords hashed with SHA-256

INSERT INTO `userlogin` (username, password_hash, first_Name, sur_Name, email, role, is_Active, created_at) VALUES
('Sysdba',     '868f871e6d78f4d0751d3e2a07f851ba87f7a27c6c2a828fb7ae9e6de12e1b3f', 'System',    'Admin',      'sysdba@infopharma.com',     'Administrator',          1, '2026-01-01'),
('manager',    'bfa481bc4a52d1b84d94a80c6df26cab5b8dcd1e3b489f4d5d90cb6dacaef5c6', 'Operations', 'Manager',   'manager@infopharma.com',    'Director of Operations', 1, '2026-01-01'),
('accountant', 'c2fdc4571ab3b13a74b5a4c4e7da6eff90a8d7e5ee9ece5658a3c209a79d4b45', 'Senior',    'Accountant', 'accountant@infopharma.com', 'Senior Accountant',      1, '2026-01-01'),
('clerk',      '820b6a32652a01f5859d0d8fa7313b3a62cb34a62928895319f23d19b1fbf37a', 'Accounts',  'Clerk',      'clerk@infopharma.com',      'Accountant',             1, '2026-01-01'),
('warehouse1', '9daf98ada23c69b85b3fbbe25be82cebe387ebbacc85c02b97d92b85de0b0571', 'Warehouse', 'One',        'warehouse1@infopharma.com', 'Warehouse Employee',     1, '2026-01-01'),
('warehouse2', 'af4ff5ae3af0ccbed3fe5fb869e83a3cedc90dd32ced4fbc88f7c51d934cdd29', 'Warehouse', 'Two',        'warehouse2@infopharma.com', 'Warehouse Employee',     1, '2026-01-01'),
('delivery',   '98a9db6d605813b2c9ca3b531d21a89da897feae61efe378d89368d2dbbec85c', 'Delivery',  'Staff',      'delivery@infopharma.com',   'Delivery Employee',      1, '2026-01-01');

-- ─── CATALOGUE ───────────────────────────────────────────────────────────────
-- Item IDs, descriptions, stock levels and minimums from InfoPharma catalogue

INSERT INTO `catalogue` (item_id, description, package_Type, unit, unit_per_pack, package_cost, availability, minimum_stock_level, is_active) VALUES
('100 00001', 'Paracetamol',           'box',    'Caps', 20,  0.10,  10345, 300, 1),
('100 00002', 'Aspirin',               'box',    'Caps', 20,  0.50,  12453, 500, 1),
('100 00003', 'Analgin',               'box',    'Caps', 10,  1.20,  4235,  200, 1),
('100 00004', 'Celebrex, caps 100 mg', 'box',    'Caps', 10,  10.00, 3420,  200, 1),
('100 00005', 'Celebrex, caps 200 mg', 'box',    'caps', 10,  18.50, 1450,  150, 1),
('100 00006', 'Retin-A Tretin, 30 g',  'box',    'caps', 20,  25.00, 2013,  200, 1),
('100 00007', 'Lipitor TB, 20 mg',     'box',    'caps', 30,  15.50, 1562,  200, 1),
('100 00008', 'Claritin CR, 60g',      'box',    'caps', 20,  19.50, 2540,  200, 1),
('200 00004', 'Iodine tincture',       'bottle', 'ml',   100, 0.30,  2213,  200, 1),
('200 00005', 'Rhynol',                'bottle', 'ml',   200, 2.50,  1908,  300, 1),
('300 00001', 'Ospen',                 'box',    'caps', 20,  10.50, 809,   200, 1),
('300 00002', 'Amopen',               'box',    'caps', 30,  15.00, 1340,  300, 1),
('400 00001', 'Vitamin C',             'box',    'caps', 30,  1.20,  3258,  300, 1),
('400 00002', 'Vitamin B12',           'box',    'caps', 30,  1.30,  2673,  300, 1);

-- ─── MERCHANTS ───────────────────────────────────────────────────────────────
-- ACC0001 CityPharmacy  — fixed 3% discount, £10,000 credit, paid in full 15 March
-- ACC0002 Cosymed Ltd   — variable discount (0/<£1k, 1%/£1k-£2k, 2%/£2k+), £5,000 credit, paid in full 15 March
-- ACC0003 HelloPharmacy — variable discount (0/<£1k, 1%/£1k-£2k, 3%/£2k+), £5,000 credit, balance outstanding

INSERT INTO `merchant` (merchant_id, company_name, business_type, registration_number, email, phone, fax, address, credit_limit, outstanding_balance, account_status, discount_type, fixed_discount_rate, flexible_discount_rate, registration_date, is_Active, last_payment_date, username, password_hash) VALUES
('ACC0001', 'CityPharmacy',  'Pharmacy', 'REG-CITY-001',   'city@citypharmacy.com',   '0207 040 8000', NULL, 'Northampton Square, London EC1V 0HB', 10000.00, 0.00,    'normal', 'fixed',    3.00, 0.00, '2026-01-01', 1, '2026-03-15', 'city',    'd4716687e8f6c89654bc8c507f98e12fec9b15d80c05311a45b09d049b2e2c58'),
('ACC0002', 'Cosymed Ltd',   'Pharmacy', 'REG-COSY-002',   'cosymed@cosymed.com',     '0207 321 8001', NULL, '25, Bond Street, London WC1V 8LS',   5000.00,  0.00,    'normal', 'flexible', 0.00, 2.00, '2026-01-01', 1, '2026-03-15', 'cosymed', '41595a90649af8dbfbf44b8ce776650c00a88eb72fcd03bc68eaf03dbb4f317d'),
('ACC0003', 'HelloPharmacy', 'Pharmacy', 'REG-HELLO-003',  'hello@hellopharmacy.com', '0207 321 8002', NULL, '12, Bond Street, London WC1V 9NS',   5000.00,  1455.00, 'normal', 'flexible', 0.00, 3.00, '2026-01-01', 1, '2026-03-05', 'hello',   'e244f187f696561d5fd7e00f618e7ba641dc52e3c137380f6fa23a854b773aac');

-- ─── ORDERS ──────────────────────────────────────────────────────────────────
-- All 6 scenario orders, all delivered as per sample data

INSERT INTO `order` (order_id, order_date, status, dispatched_by, dispatched_date, courier_name, courier_ref_no, expected_delivery_date, total_amount, discount_applied, final_amount, merchant_id) VALUES
-- Scenario 1: CityPharmacy, 20 Feb 2026, delivered 23 Feb, InfoPharma courier, 3% discount
('ORD001', '2026-02-20', 'delivered', 'InfoPharma Courier', '2026-02-22', 'InfoPharma Courier', 'IP-2026-001', '2026-02-23', 508.60, 15.26, 493.34, 'ACC0001'),
-- Scenario 2: Cosymed, 25 Feb 2026, delivered 26 Feb, DHL, 0% discount (total < £1000)
('ORD002', '2026-02-25', 'delivered', 'Warehouse Staff',    '2026-02-25', 'DHL',                'DHL-2026-001','2026-02-26', 376.00, 0.00,  376.00, 'ACC0002'),
-- Scenario 3: HelloPharmacy, 25 Feb 2026, delivered 27 Feb, DHL, 0% discount
('ORD003', '2026-02-25', 'delivered', 'Warehouse Staff',    '2026-02-26', 'DHL',                'DHL-2026-002','2026-02-27', 259.10, 0.00,  259.10, 'ACC0003'),
-- Scenario 4: Cosymed, 10 March 2026, delivered 12 March, InfoPharma courier, 0% discount
('ORD004', '2026-03-10', 'delivered', 'InfoPharma Courier', '2026-03-11', 'InfoPharma Courier', 'IP-2026-002', '2026-03-12', 430.00, 0.00,  430.00, 'ACC0002'),
-- Scenario 5: HelloPharmacy, 25 March 2026, delivered 27 March, InfoPharma courier, 0% discount
('ORD005', '2026-03-25', 'delivered', 'InfoPharma Courier', '2026-03-26', 'InfoPharma Courier', 'IP-2026-003', '2026-03-27', 877.50, 0.00,  877.50, 'ACC0003'),
-- Scenario 6: HelloPharmacy, 1 April 2026, delivered 3 April, InfoPharma courier, 0% discount
('ORD006', '2026-04-01', 'delivered', 'InfoPharma Courier', '2026-04-02', 'InfoPharma Courier', 'IP-2026-004', '2026-04-03', 577.50, 0.00,  577.50, 'ACC0003');

-- ─── ORDER ITEMS ─────────────────────────────────────────────────────────────

INSERT INTO `orderitem` (order_id, catalogue_item_id, quantity, unit_price, total_price) VALUES
-- ORD001 CityPharmacy 20 Feb
('ORD001', '100 00001', 10, 0.10,  1.00),
('ORD001', '100 00003', 20, 1.20,  24.00),
('ORD001', '200 00004', 20, 0.30,  3.60),
('ORD001', '200 00005', 10, 2.50,  25.00),
('ORD001', '300 00001', 10, 10.50, 105.00),
('ORD001', '300 00002', 20, 15.00, 300.00),
('ORD001', '400 00001', 20, 1.20,  24.00),
('ORD001', '400 00002', 20, 1.30,  26.00),
-- ORD002 Cosymed 25 Feb
('ORD002', '100 00001', 10, 0.10,  1.00),
('ORD002', '100 00003', 20, 1.20,  24.00),
('ORD002', '200 00005', 10, 2.50,  25.00),
('ORD002', '300 00002', 20, 15.00, 300.00),
('ORD002', '400 00002', 20, 1.30,  26.00),
-- ORD003 HelloPharmacy 25 Feb
('ORD003', '100 00003', 20, 1.20,  24.00),
('ORD003', '200 00004', 20, 0.30,  3.60),
('ORD003', '300 00001',  3, 10.50, 31.50),
('ORD003', '300 00002', 10, 15.00, 150.00),
('ORD003', '400 00001', 20, 1.20,  24.00),
('ORD003', '400 00002', 20, 1.30,  26.00),
-- ORD004 Cosymed 10 March
('ORD004', '200 00005', 10, 2.50,  25.00),
('ORD004', '300 00001', 10, 10.50, 105.00),
('ORD004', '300 00002', 20, 15.00, 300.00),
-- ORD005 HelloPharmacy 25 March
('ORD005', '100 00003', 20, 1.20,  24.00),
('ORD005', '100 00004',  5, 10.00, 50.00),
('ORD005', '100 00005',  5, 18.50, 92.50),
('ORD005', '100 00006',  5, 25.00, 125.00),
('ORD005', '100 00007', 10, 15.50, 155.00),
('ORD005', '300 00001', 10, 10.50, 105.00),
('ORD005', '300 00002', 20, 15.00, 300.00),
('ORD005', '400 00002', 20, 1.30,  26.00),
-- ORD006 HelloPharmacy 1 April
('ORD006', '100 00003', 20, 1.20,  24.00),
('ORD006', '100 00004',  5, 10.00, 50.00),
('ORD006', '100 00005',  5, 18.50, 92.50),
('ORD006', '100 00006',  5, 25.00, 125.00),
('ORD006', '100 00007', 10, 15.50, 155.00),
('ORD006', '300 00001', 10, 10.50, 105.00),
('ORD006', '400 00002', 20, 1.30,  26.00);

-- ─── INVOICES ────────────────────────────────────────────────────────────────
-- Due date = 30 days after invoice date
-- ORD001/002/003/004 paid; ORD005/006 unpaid (HelloPharmacy)

INSERT INTO `invoice` (invoice_id, invoice_date, due_date, total_amount, amount_paid, status, days_overdue, order_id) VALUES
('INV001', '2026-02-23', '2026-03-25', 493.34, 493.34, 'paid',   0, 'ORD001'),
('INV002', '2026-02-26', '2026-03-28', 376.00, 376.00, 'paid',   0, 'ORD002'),
('INV003', '2026-02-27', '2026-03-29', 259.10, 259.10, 'paid',   0, 'ORD003'),
('INV004', '2026-03-12', '2026-04-11', 430.00, 430.00, 'paid',   0, 'ORD004'),
('INV005', '2026-03-27', '2026-04-26', 877.50, 0.00,   'unpaid', 0, 'ORD005'),
('INV006', '2026-04-03', '2026-05-03', 577.50, 0.00,   'unpaid', 0, 'ORD006');

-- ─── PAYMENTS ────────────────────────────────────────────────────────────────
-- Scenario 8: CityPharmacy paid 15 March via bank transfer
-- Scenario 9: Cosymed paid 15 March via credit card
-- Scenario 7: HelloPharmacy last paid 5 March (cleared Feb balance)

INSERT INTO `recordpayment` (amount, payment_date, payment_method, reference_number, invoice_id, userlogin_user_id) VALUES
(493.34, '2026-03-15', 'bank_transfer', 'BT-CITY-001',  'INV001', 1),
(376.00, '2026-03-15', 'card',          'CC-COSY-001',  'INV002', 1),
(259.10, '2026-03-05', 'bank_transfer', 'BT-HELLO-001', 'INV003', 1),
(430.00, '2026-03-15', 'card',          'CC-COSY-002',  'INV004', 1);

-- ─── COMMERCIAL APPLICATIONS (from PU) ───────────────────────────────────────
-- One pending application to demonstrate the workflow

INSERT INTO `commercial_applications` (company_name, registration_no, business_type, director_name, email, phone, fax, address, application_date, status) VALUES
('Pond Pharmacy', 'UK10003429CompH', 'Pharmacy', 'Pond Director', 'pondPharma@example.com', '0208 000 0001', NULL, '25, High Street, Chislehurst BR7 5BN', '2026-04-10', 'pending');

-- ─── MONTHLY DISCOUNT TRACKING ───────────────────────────────────────────────

INSERT INTO `monthlydiscount` (merchant_id, discount_month, total_orders, discount_earned, discount_rate, discount_applied, discount_paid) VALUES
('ACC0001', '2026-02-01', 508.60, 15.26, 3.00, 1, 1),
('ACC0002', '2026-02-01', 376.00, 0.00,  0.00, 0, 0),
('ACC0002', '2026-03-01', 430.00, 0.00,  0.00, 0, 0),
('ACC0003', '2026-02-01', 259.10, 0.00,  0.00, 0, 0),
('ACC0003', '2026-03-01', 877.50, 0.00,  0.00, 0, 0),
('ACC0003', '2026-04-01', 577.50, 0.00,  0.00, 0, 0);
