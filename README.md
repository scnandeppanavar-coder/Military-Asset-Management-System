# Military Asset Management System (MAMS)

[![Spring Boot 3.3.4](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React 18](https://img.shields.io/badge/React-18-blue.svg)](https://react.dev/)
[![MySQL 8](https://img.shields.io/badge/MySQL-8.0-orange.svg)](https://www.mysql.com/)
[![Java 17+](https://img.shields.io/badge/Java-17%2B-red.svg)](https://www.oracle.com/java/)
[![JWT Security](https://img.shields.io/badge/Security-Spring%20Security%206%20%2B%20JWT-purple.svg)](https://jwt.io/)
[![Swagger OpenAPI 3](https://img.shields.io/badge/API%20Docs-Swagger%20%2F%20OpenAPI%203-yellowgreen.svg)](http://localhost:8080/swagger-ui/index.html)

A production-grade, full-stack **Military Asset Management System (MAMS)** engineered for comprehensive lifecycle tracking, audited inter-base logistics, personnel field custody allocations, and live balance reconciliations across defense installations.

---

## 1. Project Overview

The **Military Asset Management System (MAMS)** is an operational logistics command web application designed to track military defense assets across multiple bases without operational military tactics or classified information.

MAMS replaces fragmented spreadsheets and ad-hoc inventories with a secure, centralized, double-entry inventory movement ledger. It empowers defense installations to:
- Procure military assets with verified reference numbers and automated stock intake.
- Dispatch transactional, atomic inter-base asset transfers with automated stock availability verification.
- Issue and return equipment assignments to military personnel.
- Track material expenditures (combat exercises, live fire, disposal, depreciation) with mandatory inventory deductions.
- Compute historical **Opening Balance**, **Net Movement**, and **Closing Balance** across any combination of military base, equipment classification, and temporal query range.
- Enforce strict server-side **Role-Based Access Control (RBAC)** across Headquarters Administrators, Base Commanders, and Logistics Officers.
- Provide a cryptographic, non-repudiable audit ledger capturing every mutation, authorization, and security event.

---

## 2. Features

- **Double-Entry Inventory Movement Ledger:** Every inventory-impacting transaction atomically writes to an `inventory_movements` ledger table with types `PURCHASE`, `TRANSFER_IN`, `TRANSFER_OUT`, or `EXPENDITURE`.
- **Dynamic KPI Dashboard:** Visualizes 5 core inventory metrics:
  - **Opening Balance:** Historical net movement occurring strictly before the query start date.
  - **Net Movement:** Calculated mathematically as Purchases + Transfer In − Transfer Out.
  - **Closing Balance:** Opening Balance + Net Movement − Expenditures.
  - **Assigned:** Equipment currently checked out to field personnel (tracked separately; does not deduct physical inventory).
  - **Expended:** Stock permanently consumed in live fire or decommissioned.
- **Audited Net Movement Modal:** Clicking the *Net Movement* KPI card triggers an interactive mathematical breakdown displaying itemized contributions: `+Purchases`, `+Transfer In`, `-Transfer Out`, and `=Net Movement`.
- **Transactional Inter-Base Transfers:** Dual-entry atomic movement (`TRANSFER_OUT` for origin base and `TRANSFER_IN` for destination base) committed inside a single `@Transactional` boundary. Insufficient inventory attempts are rejected with HTTP 400 Bad Request.
- **Personnel Custody Roster:** Equipment checkouts to officers and units with partial and full return lifecycles (`ACTIVE`, `PARTIALLY_RETURNED`, `RETURNED`).
- **Consumption & Expenditure Ledger:** Operational and training usage logging with automatic balance deduction.
- **System-Wide Audit Trail:** Immutable logging of operator actions, timestamps, entity IDs, descriptions, and client IP addresses.
- **Role-Based Access Control (RBAC):** Backend security via `@PreAuthorize` and `SecurityFilterChain` prevents unauthorized base data access or administrative privilege escalation.
- **Dark Tactical UI:** High-contrast, responsive enterprise styling built with vanilla CSS design tokens, collapsible navigation, toasts, and confirmation dialogs.
- **Demo Clearance Switcher:** 1-click credential selector on the login portal for rapid evaluation of all 3 clearance roles.

---

## 3. Architecture

MAMS follows an **N-Tier Client-Server Architecture**:

```
┌────────────────────────────────────────────────────────┐
│             React 18 + Vite Frontend (Port 5173)       │
│  - React Router v6        - Lucide Icons               │
│  - Auth & Toast Contexts  - Axios HTTP Interceptors    │
└───────────────────────────┬────────────────────────────┘
                            │ Reverse Proxy /api (JWT Bearer Token)
┌───────────────────────────▼────────────────────────────┐
│          Spring Boot 3.3.4 REST Backend (Port 8080)    │
│  ├── Security: Spring Security 6, JWT Filter, BCrypt   │
│  ├── Controllers: RESTful API Endpoints + Validation   │
│  ├── Service Layer: Core Inventory & RBAC Logic        │
│  ├── Transactions: @Transactional (Atomicity/Rollback) │
│  └── Persistence: Spring Data JPA / Hibernate          │
└───────────────────────────┬────────────────────────────┘
                            │ JDBC Connection Pool (HikariCP)
┌───────────────────────────▼────────────────────────────┐
│                    MySQL 8.0 Database                  │
│  - schema.sql (Foreign Keys, Cascade Rules, Indexes)   │
│  - seed.sql (BCrypt Demo Accounts, Bases, Equipment)   │
└────────────────────────────────────────────────────────┘
```

---

## 4. Tech Stack

| Layer | Technology | Details |
|---|---|---|
| **Frontend Framework** | React 18, Vite 5 | Single Page Application with optimized ESM bundling |
| **Routing** | React Router v6 | Declarative client-side routing with role-based guards |
| **HTTP Client** | Axios | Interceptors for auto JWT bearer tokens & 401 handling |
| **Styling** | Vanilla CSS + Design Tokens | Custom dark enterprise tactical theme, CSS variables |
| **Icons** | Lucide React | High-contrast enterprise iconography |
| **Backend Framework** | Java 17+ (JDK 26 tested), Spring Boot 3.3.4 | Robust enterprise REST service |
| **Security** | Spring Security 6, JJWT (0.12.6), BCrypt | Stateless JWT Bearer token authentication |
| **Persistence** | Spring Data JPA, Hibernate, HikariCP | ORM with database constraints and custom HQL queries |
| **Validation** | Jakarta Bean Validation (`@Valid`) | `@Positive`, `@NotBlank`, `@Email`, `@Size` |
| **API Documentation** | SpringDoc OpenAPI / Swagger 3 | Interactive API documentation at `/swagger-ui/index.html` |
| **Database** | MySQL 8.0 Community Server | InnoDB engine, foreign keys, compound indexes |

---

## 5. Database Design

```
+--------------------+       +-----------------------+       +-------------------+
|       users        |       |         bases         |       |  equipment_types  |
+--------------------+       +-----------------------+       +-------------------+
| id (PK)            |   +---| id (PK)               |   +---| id (PK)           |
| username (UQ)      |   |   | base_code (UQ)        |   |   | name              |
| password (BCrypt)  |   |   | base_name             |   |   | category          |
| role (ENUM)        |   |   | location              |   |   | unit              |
| base_id (FK)-------+---+   | status                |   |   +-------------------+
+--------------------+       +-----------------------+   |             |
                                  |            |         |             |
                                  |            |         |             |
                                  v            v         v             v
                     +-------------------------------------------------------+
                     |         purchases / transfers / assignments           |
                     |             expenditures / inventory_movements        |
                     +-------------------------------------------------------+
```

### Tables Overview:
1. `bases`: Defense installations (`base_code`, `base_name`, `location`, `status`).
2. `equipment_types`: Master catalog (`name`, `category`, `description`, `unit`).
3. `users`: System operators (`username`, `password_hash`, `role`, `base_id`).
4. `purchases`: Procurement inflows (`base_id`, `equipment_type_id`, `quantity`, `purchase_date`, `reference_number`).
5. `transfers`: Inter-base shipments (`from_base_id`, `to_base_id`, `equipment_type_id`, `quantity`, `transfer_date`, `status`).
6. `assignments`: Field personnel custody (`base_id`, `equipment_type_id`, `personnel_name`, `quantity`, `returned_quantity`, `status`).
7. `expenditures`: Consumed inventory (`base_id`, `equipment_type_id`, `quantity`, `reason`, `reference_number`).
8. `inventory_movements`: Central double-entry ledger (`base_id`, `equipment_type_id`, `movement_type`, `quantity`, `reference_id`, `movement_date`).
9. `audit_logs`: Immutable security ledger (`user_id`, `action`, `entity_type`, `entity_id`, `description`, `ip_address`, `timestamp`).

---

## 6. Role-Based Access Control (RBAC)

RBAC is strictly enforced at the **Spring Security API layer** via method annotations (`@PreAuthorize`) and service-level data filtering:

| Role | Access Scope | Purchases | Transfers | Assignments | Expenditures | Admin Operations |
|---|---|:---:|:---:|:---:|:---:|:---:|
| `ADMIN` | Global HQ (All Bases) | Full (CRUD) | Full (CRUD) | View | Full | Manage Users, Bases, Equipment, Audit Logs |
| `BASE_COMMANDER` | Own Assigned Base | View & Create | View & Create (own base) | Full (Assign & Return) | Full (Record & View) | Restricted |
| `LOGISTICS_OFFICER` | Own Base Operations | View & Create | View & Create | 403 Forbidden | 403 Forbidden | 403 Forbidden |

### Security Enforcements:
- **Base Isolation:** When a `BASE_COMMANDER` queries `/api/dashboard`, `/api/purchases`, `/api/transfers`, or `/api/assignments`, the system forcibly locks the query to their authenticated `baseId`. Attempts to query another base return `403 Forbidden`.
- **Privilege Partitioning:** `LOGISTICS_OFFICER` accounts cannot access custody assignments, expenditures, or admin settings. Accessing `/api/assignments` or `/api/users` immediately returns `403 Forbidden`.

---

## 7. Business Logic

The system implements double-entry military inventory accounting:

### Core Formulas:

$$\text{Net Movement} = \sum \text{PURCHASE} + \sum \text{TRANSFER\_IN} - \sum \text{TRANSFER\_OUT}$$

$$\text{Closing Balance} = \text{Opening Balance} + \text{Net Movement} - \sum \text{EXPENDITURE}$$

### Business Rules & Key Assumptions:
1. **Opening Balance:** Calculated from all ledger movements occurring strictly *before* the filtered start date (`from`). If no start date is supplied, `Opening Balance = 0`.
2. **Transfer Atomicity:** A single transfer from Alpha Base to Bravo Base of 5 vehicles creates two inventory ledger records within a `@Transactional` block:
   - `TRANSFER_OUT` (-5) at Alpha Base
   - `TRANSFER_IN` (+5) at Bravo Base
3. **Over-Drafting Prevention:** Before dispatching a transfer or recording an expenditure, the backend checks:
   $$\text{Available Stock} = \sum \text{Movements In} - \sum \text{Movements Out}$$
   If $\text{Available Stock} < \text{Requested Quantity}$, an `InsufficientInventoryException` is raised and the transaction rolls back with HTTP 400.
4. **Personnel Assignment Assumption:** Personnel equipment checkouts are tracked on the personnel roster separately. An assignment does **NOT** automatically reduce the physical inventory closing balance unless expended or written off.

---

## 8. API Endpoints

All protected endpoints require an `Authorization: Bearer <token>` header:

| Group | Method | Endpoint | Allowed Roles | Description |
|---|---|---|---|---|
| **Auth** | `POST` | `/api/auth/login` | Public | Authenticate operator, returns JWT |
| | `POST` | `/api/auth/register` | ADMIN | Provision new user account |
| | `GET` | `/api/auth/me` | Authenticated | Fetch current operator profile |
| **Dashboard** | `GET` | `/api/dashboard` | All Roles | Computes Opening, Net Movement, Closing, etc. |
| | `GET` | `/api/dashboard/net-movement` | All Roles | Itemized breakdown (+Purchases, +In, -Out) |
| **Purchases** | `GET` / `POST` | `/api/purchases` | All Roles | List / Record procurement (+Purchase movement) |
| | `GET` / `PUT` / `DELETE`| `/api/purchases/{id}` | All / ADMIN | Manage individual purchase records |
| **Transfers** | `GET` / `POST` | `/api/transfers` | All Roles | List / Dispatch transfers (Dual atomic movement) |
| | `GET` | `/api/transfers/{id}` | All Roles | View transfer manifest |
| **Assignments**| `GET` / `POST` | `/api/assignments` | ADMIN, COMMANDER | List / Check out equipment to personnel |
| | `PUT` | `/api/assignments/{id}` | ADMIN, COMMANDER | Record return (Partial or Full) |
| **Expenditures**| `GET` / `POST` | `/api/expenditures` | ADMIN, COMMANDER | List / Record consumption (-Expenditure movement) |
| **Bases** | `GET` / `POST` / `PUT` / `DELETE` | `/api/bases` | ADMIN (GET for all) | Master military base registry |
| **Equipment** | `GET` / `POST` / `PUT` / `DELETE` | `/api/equipment-types` | ADMIN (GET for all) | Master equipment catalog taxonomy |
| **Users** | `GET` / `POST` / `PUT` / `DELETE` | `/api/users` | ADMIN Only | Operator accounts, roles, base assignment |
| **Audit Logs** | `GET` | `/api/audit-logs` | ADMIN Only | Immutable system audit trail |

---

## 9. API Logging

Audit logging is implemented via `AuditLogService` and persisted directly to the `audit_logs` table:
- **Logged Events:** `LOGIN`, `LOGOUT`, `PURCHASE`, `TRANSFER`, `ASSIGN`, `EXPEND`, `CREATE`, `UPDATE`, `DELETE`.
- **Captured Metadata:**
  - Operator Username and User ID
  - Action Category and Target Entity Type
  - Target Entity Record ID
  - Comprehensive human-readable description
  - Client IP Address (extracted from `HttpServletRequest`)
  - Precise UTC Timestamp
- **Non-Repudiation:** The audit table is append-only and cannot be altered by normal user operations.

---

## 10. Setup Instructions

### Prerequisites:
- **Java Development Kit (JDK):** Version 17 or higher (tested with JDK 26).
- **Apache Maven:** Version 3.8 or higher.
- **Node.js & npm:** Version 18 or higher.
- **MySQL Server:** Version 8.0 or higher.

---

## 11. Environment Variables

Create `.env` based on the supplied [.env.example](file:///d:/MilitaryAssetManagement/.env.example):

### Backend (`application.properties` or environment):
```properties
DB_URL=jdbc:mysql://localhost:3306/mams_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=your_secure_password
JWT_SECRET=your_base64_or_hex_encoded_256_bit_jwt_secret_key_here
JWT_EXPIRATION=86400000
SERVER_PORT=8080
```

### Frontend (`frontend/.env`):
```properties
VITE_API_BASE_URL=/api
```

---

## 12. Database Setup

1. Open MySQL CLI or MySQL Workbench:
```sql
CREATE DATABASE IF NOT EXISTS mams_db;
```

2. Execute the schema script:
```bash
mysql -u root -p mams_db < database/schema.sql
```

3. Seed initial demo bases, equipment catalog, demo users, and transactions:
```bash
mysql -u root -p mams_db < database/seed.sql
```

---

## 13. Backend Setup

Navigate to `backend/` and run Spring Boot:

```bash
cd backend
mvn clean spring-boot:run
```

The backend server starts on **http://localhost:8080**.

To run automated unit & integration tests:
```bash
mvn test
```

---

## 14. Frontend Setup

Navigate to `frontend/`:

```bash
cd frontend
npm install
npm run dev
```

The frontend will run on **http://localhost:5173** and automatically proxy API calls to the backend at port 8080.

To build the production bundle:
```bash
npm run build
```

---

## 15. Demo Credentials

The seed data provides pre-configured accounts representing each clearance level:

| Clearance Level | Username | Password | Assigned Installation | Capabilities |
|---|---|---|---|---|
| **ADMIN** | `admin` | `Admin@123` | Global HQ (All Bases) | Full system administration, audit logs, all bases |
| **BASE COMMANDER** | `commander` | `Commander@123` | Fort Alpha Garrison | Alpha Base management, assignments, expenditures |
| **BASE COMMANDER (B)** | `commander_bravo`| `Commander@123` | Camp Bravo Forward Base | Bravo Base management, assignments, expenditures |
| **LOGISTICS OFFICER** | `logistics` | `Logistics@123` | Fort Alpha Garrison | Alpha purchases and transfers only |

> **Pro Tip:** The login screen includes a **1-Click Demo Clearance Switcher** to easily toggle between Admin, Base Commander, and Logistics Officer personas during interviews and live demos.

---

## 16. Swagger URL

Interactive Swagger/OpenAPI documentation is available when the backend is running:
- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON Spec:** [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## 17. Deployment Instructions

1. **Production Build:**
   - Package backend JAR: `cd backend && mvn clean package -DskipTests`
   - Build frontend assets: `cd frontend && npm run build`
2. **Reverse Proxy (Nginx):**
   - Serve frontend `dist/` as static assets.
   - Proxy all `/api` requests to `http://localhost:8080/api`.
3. **Database Migration:**
   - Use Flyway or Liquibase for continuous schema evolutions across staging and production clusters.

---

## 18. Assumptions

1. **Equipment Taxonomy:** Equipment catalog data is generic (e.g., *Armored Personnel Carrier*, *Standard Infantry Rifle*, *Field Communication Transceiver*) to maintain strict compliance with assessment guidelines.
2. **Assignment Custody Model:** Assignments represent temporary custody checkouts to field personnel. They do not debit physical on-hand installation counts from closing inventory unless formally classified as an expenditure or write-off.
3. **Double-Entry Stock:** Inter-base shipments immediately deduct from origin inventory and credit destination inventory upon transfer confirmation.

---

## 19. Limitations

1. Single database instance setup; distributed multi-region replication requires distributed transaction locks or Kafka event streams for inter-base reconciliation.
2. Transfer status is immediately completed upon creation; in multi-day logistical transit scenarios, an `IN_TRANSIT` waypoint tracking state can be added.

---

## 20. Future Improvements

1. **Barcode / RFID Asset Scanning:** Integration with mobile handheld barcode scanners for warehouse intake.
2. **Preventative Maintenance Scheduling:** Service hours tracking with automated inspection alerts.
3. **Waybill PDF Export:** Automated PDF generation for transport manifests.
4. **WebSocket Live Telemetry:** Real-time push notifications when transfers are dispatched across bases.
