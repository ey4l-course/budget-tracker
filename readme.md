# Project Overview
Update: Jan 09, 2026

This project is a multi-microservice architecture for a budget tracker application. It includes several distinct services, each with a specific role, and all traffic is routed through an API gateway.

## Microservices

### API Gateway
***Purpose:*** 
- Routes all incoming requests to the appropriate internal services.
- Separates public endpoints from those which requires authentication.
- Owns request-response life-cycle, error handling and logging.
- Owns Security filter and configurations.

***Features:***
- **request context filter:** a OncePerRequest filter with the highest precedence.
It initiates a context DTO feeds it with preliminary data (route, source IP, etc...)
and attaches it as request attribute for further enrichment during life-cycle.
It is the owner of the request-response cycle and responsible for writing responses (overriding Spring mechanisms).
Before commiting responses it gathers all information and logs it.
- **Security filter:** A standard SecurityFilterChain that runs after context filter.
It defines security configurations like CORS, CSRF and private/public endpoints.

### User Microservice
***Purpose:***
- Handles public endpoints: register, login, forgot password, contact us.
- Manages authenticated user functionalities.
- **Private endpoints:** Category management, income entry, transaction details and edits, and monthly summaries.
- **Public endpoints:** User registration, login, forgot password/username, about, and contact.

### Admin Microservice
- **Purpose:** For admin-level functions.
- **Endpoints:** Reset user passwords, edit accounts, access logs, superuser management of admin accounts.

### Authentication Microservice
- **Purpose:** Internal service for handling all authentication.
- **Endpoints:** Internal APIs for validating tokens and sessions.

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
