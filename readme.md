# Project Overview
Update: Oct 27th, 2025

This project is a multi-microservice architecture for a budget tracker application. It includes several distinct services, each with a specific role, and all traffic is routed through an API gateway.

## Microservices

### API Gateway
- **Purpose:** Routes all incoming requests to the appropriate internal services.
- **Endpoints:** No business logic, just routing and basic checks.

### Public Microservice
- **Purpose:** Handles endpoints that do not require authentication.
- **Endpoints:** User registration, login, forgot password/username, about, and contact.

### User Microservice
- **Purpose:** Manages authenticated user functionalities.
- **Endpoints:** Category management, income entry, transaction details and edits, and monthly summaries.

### Admin Microservice
- **Purpose:** For admin-level functions.
- **Endpoints:** Reset user passwords, edit accounts, access logs, superuser management of admin accounts.

### Authentication Microservice
- **Purpose:** Internal service for handling all authentication.
- **Endpoints:** Internal APIs for validating tokens and sessions.

### Logging and Monitoring Microservice
- **Purpose:** Collects logs from all services and forwards them to a central logging system.
- **Endpoints:** Internal only, for log aggregation.

## Next Steps
- Begin with basic implementation of each service.
- Decide on infrastructure (Docker, bare metal, database placement) as the project evolves.
- Integrate each service and test routing through the API Gateway.

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
