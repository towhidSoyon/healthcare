# Project Constitution: Healthcare Ecosystem

This document serves as the absolute source of truth and constitution for all engineering efforts within the Healthcare Ecosystem (Patient App, Doctor App, Backend, Admin Panel, Moderator Panel). All developers and AI Agents must adhere strictly to these rules.

---

## 1. General Principles

*   **Clean Architecture:** The system must be strictly divided into layers (Domain, Data, Presentation). Inner layers must never depend on outer layers. Domain logic must be framework-agnostic.
*   **SOLID:** All code must adhere to Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation, and Dependency Inversion principles.
*   **DRY (Don't Repeat Yourself):** Logic should be defined in exactly one place. If you find yourself copying code, extract it into a reusable component, utility, or use case.
*   **KISS (Keep It Simple, Stupid):** Avoid unnecessary complexity. Choose the simplest solution that effectively solves the problem and meets the requirements.
*   **YAGNI (You Aren't Gonna Need It):** Do not write code or add dependencies for hypothetical future use cases. Build only what is required for the current feature.
*   **Feature First Architecture:** Code must be organized by feature (e.g., `feature/appointments`, `feature/auth`) rather than by technical type (e.g., all ViewModels in one folder). Everything related to a feature should live as close together as possible.

---

## 2. Naming Conventions

*   **Classes:** `PascalCase` (e.g., `PatientProfileViewModel`, `AuthRepositoryImpl`).
*   **Files:**
    *   Kotlin/Java/C#: `PascalCase.kt` (e.g., `LoginScreen.kt`).
    *   TypeScript/JavaScript/React: `PascalCase.tsx` for components, `camelCase.ts` for utilities.
*   **Packages/Directories:** `lowercase` (e.g., `com.healthcare.auth`, `components`). Do not use underscores or hyphens in package names.
*   **APIs (REST):** `kebab-case` for endpoints, noun-based, versioned (e.g., `GET /api/v1/patient-records`).
*   **Database Tables:** `snake_case`, plural (e.g., `users`, `medical_histories`, `appointment_slots`).
*   **Database Columns:** `snake_case` (e.g., `created_at`, `user_id`).
*   **Branch Names:** `<type>/<ticket-id>-<brief-description>` (e.g., `feature/HC-101-qr-login`, `bugfix/HC-204-crash-on-sos`).
*   **Git Commits:** Conventional Commits standard. `<type>(<scope>): <subject>` (e.g., `feat(auth): implement biometric login fallback`, `fix(ui): resolve overlap in patient dashboard`).

---

## 3. Folder Structure Rules

*   **Root Structure:** The project must be divided by application boundaries (e.g., `/android-patient`, `/android-doctor`, `/backend`, `/web-admin`).
*   **Feature Modules:** Inside each application, code must be grouped by feature.
    ```text
    /feature-name
      /presentation (UI, ViewModels, State)
      /domain (Use Cases, Entities, Repository Interfaces)
      /data (Repository Impls, DTOs, Local DB entities, API interfaces)
    ```
*   **Shared/Core:** Common utilities, design system components, and base classes must reside in a `core/` or `shared/` directory, completely isolated from specific business logic.

---

## 4. Code Style Rules

*   **Immutability First:** Use immutable data structures (`val` in Kotlin, `const` in JS/TS, `readonly` where applicable) by default. Mutability must be explicitly justified.
*   **Null Safety:** Leverage language features to eliminate NullPointerExceptions. Use Optionals, nullable types with safe calls, and avoid forceful unwrapping (`!!`).
*   **Maximum File Size:** No file should exceed 400 lines of code. If it does, it violates the Single Responsibility Principle and must be refactored.
*   **Magic Numbers/Strings:** Hardcoded values are strictly prohibited in business logic. Extract them to configuration files, constants, or string resource files.

---

## 5. Security Rules

*   **Zero Trust:** Never trust client input. All data must be validated and sanitized on the backend before processing.
*   **Data Encryption:** All Personal Health Information (PHI) must be encrypted at rest (AES-256) and in transit (TLS 1.3+).
*   **Secrets Management:** API keys, database credentials, and cryptographic salts must NEVER be hardcoded or committed to version control. Use environment variables and secure secret managers.
*   **Authorization:** Every protected endpoint must explicitly verify that the authenticated user possesses the exact role/permission required to perform the action on the requested resource.

---

## 6. Error Handling Rules

*   **Fail Fast:** Validate inputs immediately at the system boundary. Throw exceptions or return error monads (e.g., `Result<T>`) early.
*   **No Swallowing Exceptions:** Empty catch blocks are strictly prohibited. Every caught exception must be handled, logged, or re-thrown appropriately.
*   **User-Facing Errors:** Backend exceptions must never leak stack traces to the client. The backend must map internal exceptions to standardized, user-friendly error codes and messages.
*   **Graceful Degradation:** The mobile apps and web panels must handle network failures gracefully using offline caches, retry mechanisms, and informative UI states.

---

## 7. Logging Rules

*   **Contextual Logging:** Logs must include contextual trace IDs to track a request across microservices or layers.
*   **Log Levels:** Use levels correctly: `ERROR` (system failure, requires immediate attention), `WARN` (unexpected behavior, system recovered), `INFO` (business events, state changes), `DEBUG` (development only).
*   **No PHI in Logs:** NEVER log passwords, tokens, full names, medical conditions, or any identifiable patient data. Mask or hash sensitive information before logging.

---

## 8. Testing Rules

*   **Test Pyramid:** Maintain a healthy ratio of Unit Tests (highest volume), Integration Tests (moderate volume), and E2E Tests (lowest volume).
*   **Coverage Target:** All domain logic (Use Cases) and ViewModels must have a minimum of 80% test coverage.
*   **Mocking:** Mock external dependencies (APIs, Databases, Sensors) in unit tests. Do not mock internal domain logic.
*   **Given-When-Then:** Structure all tests logically. Set up the state (Given), execute the action (When), and assert the outcome (Then).

---

## 9. Documentation Rules

*   **Self-Documenting Code:** Code should be readable without comments. Use clear variable names and small functions.
*   **Docstrings/KDoc:** Public interfaces, complex algorithms, and shared core utilities must be documented with standard docstrings detailing parameters, return values, and exceptions.
*   **Architecture Decision Records (ADRs):** Any significant architectural change, library adoption, or pattern shift must be documented in a `/docs/adr` folder.
*   **READMEs:** Every major module must contain a README.md explaining its purpose, how to build it, and how to run its tests.

---

## 10. AI Agent Instructions

All AI Agents assisting with this project are explicitly commanded to obey the following directives at all times:

1.  **Never invent architecture:** Stick strictly to Clean Architecture and Feature First structures as defined above.
2.  **Never change naming conventions:** Ensure complete alignment with the styles defined in Section 2.
3.  **Never change folder structure:** Place generated files exactly where they belong according to Section 3.
4.  **Never generate code outside project standards:** Conform to Immutability, Error Handling, and Security rules natively.
5.  **Always update documentation:** If you modify an API, update its docs. If you add a complex function, add a docstring.
6.  **Always generate tests:** Any logic generated must be accompanied by appropriate Unit Tests using the Given-When-Then structure.
7.  **Always explain design decisions:** Briefly explain *why* a specific code approach was taken before outputting it.
