-- ========================================================
-- Military Asset Management System (MAMS) - Seed Data
-- Demo Accounts:
--   admin / Admin@123          (ADMIN)
--   commander / Commander@123  (BASE_COMMANDER, Base 1 - Alpha)
--   logistics / Logistics@123  (LOGISTICS_OFFICER, Base 1 - Alpha)
-- ========================================================

USE mams_db;

-- Clear existing data in reverse FK order
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE audit_logs;
TRUNCATE TABLE inventory_movements;
TRUNCATE TABLE expenditures;
TRUNCATE TABLE assignments;
TRUNCATE TABLE transfers;
TRUNCATE TABLE purchases;
TRUNCATE TABLE users;
TRUNCATE TABLE equipment_types;
TRUNCATE TABLE bases;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Bases
INSERT INTO bases (id, base_code, base_name, location, status, created_at) VALUES
(1, 'BASE-ALPHA', 'Fort Alpha Garrison', 'Northern Sector (Highland)', 'ACTIVE', '2026-01-01 08:00:00'),
(2, 'BASE-BRAVO', 'Camp Bravo Forward Base', 'Eastern Command (Coastal)', 'ACTIVE', '2026-01-01 08:00:00'),
(3, 'BASE-CHARLIE', 'Charlie Logistics Depot', 'Central Logistics Hub', 'ACTIVE', '2026-01-01 08:00:00'),
(4, 'BASE-DELTA', 'Outpost Delta Recon', 'Western Desert Sector', 'ACTIVE', '2026-01-01 08:00:00');

-- 2. Equipment Types
INSERT INTO equipment_types (id, name, category, description, unit, created_at) VALUES
(1, 'Tactical Utility Vehicle (4x4)', 'Vehicle', 'Armored tactical personnel and cargo carrier', 'Units', '2026-01-05 09:00:00'),
(2, 'Rapid Response Armored Recon Vehicle', 'Vehicle', 'High-mobility scout vehicle with integrated comms', 'Units', '2026-01-05 09:00:00'),
(3, '5.56mm Standard Infantry Service Rifle', 'Weapon', 'Standard-issue assault rifle with optical rail', 'Pieces', '2026-01-05 09:00:00'),
(4, '7.62mm Designated Marksman Rifle', 'Weapon', 'Semi-automatic long-range precision marksman rifle', 'Pieces', '2026-01-05 09:00:00'),
(5, '5.56x45mm NATO Ball Ammunition', 'Ammunition', 'Standard ammunition sealed crate (1,000 rds/crate)', 'Boxes', '2026-01-05 09:00:00'),
(6, '7.62x51mm Match Grade Ammunition', 'Ammunition', 'Precision sniper ammunition crate (500 rds/crate)', 'Boxes', '2026-01-05 09:00:00'),
(7, 'Tactical Encrypted VHF/UHF Transceiver', 'Communication Equipment', 'Secure frequency-hopping multi-band radio set', 'Sets', '2026-01-05 09:00:00'),
(8, 'Level IV Ceramic Ballistic Vest', 'Protective Equipment', 'Full-torso modular plate carrier with ceramic inserts', 'Suits', '2026-01-05 09:00:00'),
(9, 'Advanced Ballistic Combat Helmet', 'Protective Equipment', 'Lightweight composite helmet with night-vision mount', 'Pieces', '2026-01-05 09:00:00');

-- 3. Users (Passwords: Admin@123, Commander@123, Logistics@123)
INSERT INTO users (id, username, password, full_name, email, role, base_id, created_at, updated_at) VALUES
(1, 'admin', '$2a$10$kCcIXjOVTmECpZr8Fh9tpOscSZ.esGfNveivpfkmybuJ1pBpbHecS', 'General Marcus Vance', 'admin@mams.mil', 'ADMIN', NULL, '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(2, 'commander', '$2a$10$PVQDIquxLKGJyb5EzomIZeMdFjDLJ6AkCOAeCR9i0YhAnqfeH3.Rm', 'Col. Sarah Jenkins', 'commander.alpha@mams.mil', 'BASE_COMMANDER', 1, '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(3, 'logistics', '$2a$10$m1VmD3vACKxDbPGaEqQkLeY7LkRf7rwz8L5kx.qFUerEK43K/Kf1G', 'Maj. Alex Rivera', 'logistics.alpha@mams.mil', 'LOGISTICS_OFFICER', 1, '2026-01-10 10:00:00', '2026-01-10 10:00:00'),
(4, 'commander_bravo', '$2a$10$PVQDIquxLKGJyb5EzomIZeMdFjDLJ6AkCOAeCR9i0YhAnqfeH3.Rm', 'Col. Robert Chen', 'commander.bravo@mams.mil', 'BASE_COMMANDER', 2, '2026-01-10 10:00:00', '2026-01-10 10:00:00');

-- 4. Purchases (Opening stock in August 2026 + Purchases in September 2026)
-- August Purchases (Will count towards Opening Balance for September filter)
INSERT INTO purchases (id, base_id, equipment_type_id, quantity, purchase_date, reference_number, remarks, created_by, created_at) VALUES
(1, 1, 1, 50, '2026-08-01', 'PUR-2026-0801', 'Initial vehicle fleet acquisition for Fort Alpha', 'admin', '2026-08-01 10:00:00'),
(2, 1, 3, 200, '2026-08-05', 'PUR-2026-0805', 'Standard infantry armament batch 1', 'admin', '2026-08-05 11:30:00'),
(3, 1, 5, 500, '2026-08-10', 'PUR-2026-0810', 'Quarterly ammunition replenishment', 'admin', '2026-08-10 09:15:00'),
(4, 2, 7, 40, '2026-08-15', 'PUR-2026-0815', 'Camp Bravo communications overhaul', 'admin', '2026-08-15 14:00:00'),
(5, 1, 8, 150, '2026-08-20', 'PUR-2026-0820', 'Personal protective body armor batch', 'admin', '2026-08-20 16:45:00'),

-- September Purchases (Active period)
(6, 1, 1, 15, '2026-09-05', 'PUR-2026-0905', 'Secondary utility vehicle allocation', 'logistics', '2026-09-05 10:30:00'),
(7, 1, 4, 30, '2026-09-08', 'PUR-2026-0908', 'Marksman precision rifles requisition', 'commander', '2026-09-08 11:00:00'),
(8, 2, 1, 20, '2026-09-10', 'PUR-2026-0910', 'Camp Bravo transport expansion', 'admin', '2026-09-10 13:00:00'),
(9, 1, 8, 50, '2026-09-15', 'PUR-2026-0915', 'Reserve ballistic vest supply', 'logistics', '2026-09-15 15:20:00');

-- Corresponding Movements for Purchases
INSERT INTO inventory_movements (id, base_id, equipment_type_id, movement_type, quantity, reference_id, movement_date, created_by, created_at) VALUES
(1, 1, 1, 'PURCHASE', 50, 1, '2026-08-01', 'admin', '2026-08-01 10:00:00'),
(2, 1, 3, 'PURCHASE', 200, 2, '2026-08-05', 'admin', '2026-08-05 11:30:00'),
(3, 1, 5, 'PURCHASE', 500, 3, '2026-08-10', 'admin', '2026-08-10 09:15:00'),
(4, 2, 7, 'PURCHASE', 40, 4, '2026-08-15', 'admin', '2026-08-15 14:00:00'),
(5, 1, 8, 'PURCHASE', 150, 5, '2026-08-20', 'admin', '2026-08-20 16:45:00'),
(6, 1, 1, 'PURCHASE', 15, 6, '2026-09-05', 'logistics', '2026-09-05 10:30:00'),
(7, 1, 4, 'PURCHASE', 30, 7, '2026-09-08', 'commander', '2026-09-08 11:00:00'),
(8, 2, 1, 'PURCHASE', 20, 8, '2026-09-10', 'admin', '2026-09-10 13:00:00'),
(9, 1, 8, 'PURCHASE', 50, 9, '2026-09-15', 'logistics', '2026-09-15 15:20:00');

-- 5. Transfers
-- Transfer 1: Alpha Base (1) sends 5 Tactical Vehicles to Bravo Base (2) on 2026-09-12
INSERT INTO transfers (id, from_base_id, to_base_id, equipment_type_id, quantity, transfer_date, reference_number, status, remarks, created_by, created_at) VALUES
(1, 1, 2, 1, 5, '2026-09-12', 'TRF-2026-001', 'COMPLETED', 'Tactical transport rebalancing for coastal border patrol', 'logistics', '2026-09-12 11:00:00'),
(2, 2, 1, 7, 10, '2026-09-18', 'TRF-2026-002', 'COMPLETED', 'Inter-base communications asset allocation', 'admin', '2026-09-18 14:30:00');

-- Inventory Movements for Transfers (Out from source, In to destination)
INSERT INTO inventory_movements (id, base_id, equipment_type_id, movement_type, quantity, reference_id, movement_date, created_by, created_at) VALUES
(10, 1, 1, 'TRANSFER_OUT', 5, 1, '2026-09-12', 'logistics', '2026-09-12 11:00:00'),
(11, 2, 1, 'TRANSFER_IN', 5, 1, '2026-09-12', 'logistics', '2026-09-12 11:00:00'),
(12, 2, 7, 'TRANSFER_OUT', 10, 2, '2026-09-18', 'admin', '2026-09-18 14:30:00'),
(13, 1, 7, 'TRANSFER_IN', 10, 2, '2026-09-18', 'admin', '2026-09-18 14:30:00');

-- 6. Expenditures
-- Alpha Base expends 60 boxes of 5.56mm ammo for Live-Fire Training Exercise on 2026-09-22
INSERT INTO expenditures (id, base_id, equipment_type_id, quantity, expenditure_date, reason, reference_number, recorded_by, created_at) VALUES
(1, 1, 5, 60, '2026-09-22', 'Annual Infantry Marksmanship & Live-Fire Qualification', 'EXP-2026-001', 'commander', '2026-09-22 16:00:00');

-- Inventory Movement for Expenditure
INSERT INTO inventory_movements (id, base_id, equipment_type_id, movement_type, quantity, reference_id, movement_date, created_by, created_at) VALUES
(14, 1, 5, 'EXPENDITURE', 60, 1, '2026-09-22', 'commander', '2026-09-22 16:00:00');

-- 7. Assignments (Separate tracking, does NOT reduce physical stock)
INSERT INTO assignments (id, base_id, equipment_type_id, personnel_name, quantity, assigned_date, returned_quantity, status, assigned_by, created_at) VALUES
(1, 1, 1, 'Capt. James Miller (Alpha Patrol 1)', 4, '2026-09-10', 0, 'ACTIVE', 'commander', '2026-09-10 09:00:00'),
(2, 1, 4, 'Sgt. Thomas Wright (Scout Unit)', 5, '2026-09-14', 2, 'PARTIALLY_RETURNED', 'commander', '2026-09-14 10:30:00'),
(3, 1, 8, 'Lt. Maria Garcia (Security Detail)', 25, '2026-09-16', 25, 'RETURNED', 'commander', '2026-09-16 11:00:00'),
(4, 1, 7, 'Sgt. David Kim (Comms Team)', 3, '2026-09-20', 0, 'ACTIVE', 'commander', '2026-09-20 14:00:00');

-- 8. Audit Logs
INSERT INTO audit_logs (id, user_id, action, entity_type, entity_id, description, ip_address, timestamp) VALUES
(1, 1, 'LOGIN', 'USER', 1, 'User admin logged in successfully', '127.0.0.1', '2026-08-01 08:30:00'),
(2, 1, 'PURCHASE', 'PURCHASE', 1, 'Purchase PUR-2026-0801 created (Qty: 50, Eq: Tactical Utility Vehicle)', '127.0.0.1', '2026-08-01 10:00:00'),
(3, 2, 'LOGIN', 'USER', 2, 'User commander logged in successfully', '127.0.0.1', '2026-09-08 10:55:00'),
(4, 2, 'PURCHASE', 'PURCHASE', 7, 'Purchase PUR-2026-0908 created (Qty: 30, Eq: 7.62mm DMR)', '127.0.0.1', '2026-09-08 11:00:00'),
(5, 3, 'LOGIN', 'USER', 3, 'User logistics logged in successfully', '127.0.0.1', '2026-09-12 10:45:00'),
(6, 3, 'TRANSFER', 'TRANSFER', 1, 'Transfer TRF-2026-001 executed from Fort Alpha to Camp Bravo (Qty: 5)', '127.0.0.1', '2026-09-12 11:00:00'),
(7, 2, 'ASSIGN', 'ASSIGNMENT', 1, 'Assigned 4 Tactical Utility Vehicles to Capt. James Miller', '127.0.0.1', '2026-09-10 09:00:00'),
(8, 2, 'EXPEND', 'EXPENDITURE', 1, 'Recorded expenditure EXP-2026-001 of 60 boxes 5.56mm ammo', '127.0.0.1', '2026-09-22 16:00:00');
