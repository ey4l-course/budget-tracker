# Contributing to Budget Tracker

## 1. Branching Strategy
We follow a **Feature-Branch Workflow** to maintain stability across the microservice cluster.

* **main**: Production-ready state. High-stability RSA keys only.
* **develop**: Integration branch for S2S testing.
* **feature/**: Created from `develop`. Use for logic or infrastructure changes (e.g., `feature/txn-h2-support`).
* **hotfix/**: Created from `main` for critical security or routing failures.

## 2. Commit Message Standards
We use structured commit messages to ensure the VCS history is scannable.
**Format:** `type: description` followed by bullet points for impact.

### Types:
- `feat`: A new feature (e.g., a new endpoint in `TransactionsController`).
- [cite_start]`refactor`: Code changes that neither fix a bug nor add a feature (e.g., route realignment ).
- `fix`: A bug fix.
- [cite_start]`chore`: Maintenance tasks (updating `pom.xml` [cite: 66, 68] [cite_start]or `curlTests.txt` [cite: 74, 77]).

## 3. Development Workflow & Security
- [cite_start]**Internal S2S**: All internal calls must use `X-internal-Auth` headers managed by `InternalTokenManager`[cite: 18, 22].
- [cite_start]**Dev Endpoints**: Access to `/h2-console` and `/dev/**` is permitted only in the `dev` profile[cite: 71, 82].
- [cite_start]**Dependency Management**: New dependencies in `common` must be verified against all microservices to prevent auto-configuration conflicts (e.g., `DataSourceAutoConfiguration` ).

## 4. Testing
Before pushing, verify that:
1. [cite_start]`mvn clean install` passes from the root `pom.xml`[cite: 44, 50].
2. [cite_start]The `InternalTokenManager` successfully fetches keys from the `auth` service[cite: 9, 22].
3. [cite_start]Manual integration tests in `curlTests.txt` are updated with any new payload structures[cite: 32, 74].