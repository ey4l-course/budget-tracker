# Project Overview
Update: May 28, 2026

This project is a multi-microservice architecture for a budget tracker application. It includes several distinct services, each with a specific role, and all traffic is routed through an API gateway.

## Microservices

### API Gateway
***Purpose:*** - Routes all incoming requests to the appropriate internal services.
- Separates public endpoints from those which require authentication.
- Owns request-response life-cycle, error handling, and logging.
- Owns Security filter and configurations.

***Features:***
- **request context filter:** A `OncePerRequestFilter` with the highest precedence. It initiates a context DTO, feeds it with preliminary data (route, source IP, etc...), and attaches it as a request attribute for further enrichment during the life-cycle. It is the owner of the request-response cycle and responsible for writing responses (overriding Spring mechanisms). Before committing responses, it gathers all information and logs it.
- **Security filter:** A standard `SecurityFilterChain` that runs after the context filter. It defines security configurations like CORS, CSRF, and private/public endpoints.
- **Composite Orchestration Layer:** Houses the consolidated `GatewayController` which serves as the boundary layer, pulling complex composite payloads (`FetchDashDTO`) across downstream microservices.

### User Microservice
***Purpose:***
- Acts as a pure data owner rather than an orchestrator.
- Manages authenticated user functionalities and domain entities (e.g., `BankDetails`, `CreditDetails`, `ExpenseFrequency`) using strict value-type safety.
- **Private endpoints:** Category management, income entry, transaction details and edits, and monthly summaries.
- **Public endpoints:** Available to unauthenticated users at `/public/register`, login, forgot password/username, about, and contact.

### Transactions Microservice
***Purpose:***
- Core transactional data engine utilizing specialized, modularized SQL scripts (`newTxn.sql`, `fetchCategories.sql`) rather than in-line string code.
- Features an aggressive startup caching lifecycle controlled via a dedicated `WarmupService` and `WarmupRepository` to eliminate runtime cold-start database latency.

### Admin Microservice
- **Purpose:** For admin-level functions.
- **Endpoints:** Reset user passwords, edit accounts, access logs, superuser management of admin accounts.

### Authentication Microservice
- **Purpose:** Dedicated, internal isolation service handling cluster-wide security under a Zero-trust security model.
- **Features:** Validates identities and stateful web sessions, distributing secure RSA-signed JWT tokens handled securely via HTTP-Only cookies between the application layers.

---

## 📌 Project Status & Architectural Pivot (May 2026)

### Done (Sprint 1)
* **Zero-Trust Security Integration:** Extracted custom authentication logic out of application services and centralized token signing within the dedicated `Auth` microservice.
* **Tree Flattening & Git Linearization:** Merged all ongoing dashboard and transaction feature lines directly back into the stable main track, ensuring a completely clean tracking history before baseline hibernation.
* **Cluster Refactoring:** * Formalized service communication contracts using explicit network clients (`InternalTxnClient`, `InternalUserClient`) and shared DTOs.
  * Decoupled runtime environments by extracting local testing hooks (`add_dummy_txn.sh`) from shared codebase dependencies.

### Current Status: HIBERNATION
The microservice application cluster layer is completely functional, compiling, and safely frozen on the unified `dev` branch tracking `origin/dev`.

### Next Up (On Return)
* **Database Layer Transformation:** Restructuring the backend data persistence engine based on enterprise relational evaluations (Oracle SQL & PL/SQL migration).

---

## 🛡️ Privacy and Data Handling Notice

This project is an **educational prototype** and does **not** collect, process, or store real personal data.

### Purpose
All data structures (e.g., `RegisterDTO`, `UserEntity`) and validation logic are designed **for learning purposes only** — to practice DTO design, validation, and secure storage patterns (e.g., hashing, encryption).

### Privacy Principles
If this project were used in a real-world scenario, the following would apply:
- **Minimal Data Collection:** Only essential fields (name, contact info, ID for one-time verification).
- **Purpose Limitation:** Data used solely for user registration, authentication, or consented CRM purposes.
- **Security Measures:**
  - No logging of sensitive identifiers or full contact info.
  - Encryption or hashing of identifiers at rest.
  - HTTPS / TLS enforced for all network communication.
- **Consent:** Collection of contact details for CRM or marketing use requires explicit user consent (typically via UI checkbox).
- **Retention:** Personal data retained only as long as necessary for its purpose, then securely deleted.

### Disclaimer
This repository uses **dummy or simulated data only**.  
No real personal information should be entered, processed, or stored when testing or demonstrating this application.