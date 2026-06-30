# Development Workflow: Healthcare Ecosystem

This document defines the strict, linear development lifecycle for implementing any new feature in the Healthcare Ecosystem. Every engineer and AI Agent must follow this sequence to guarantee architectural consistency, high test coverage, and enterprise-grade quality.

---

## The Workflow Pipeline

`Requirement` ➔ `Database` ➔ `API` ➔ `Backend` ➔ `Unit Test` ➔ `Android` ➔ `Integration Test` ➔ `Documentation` ➔ `Review` ➔ `Merge`

---

## 1. Requirement Phase

*   **Inputs:** User request, JIRA Ticket, PRD, Feature Breakdown.
*   **Outputs:** Clear understanding of the feature scope, impacted systems, and necessary architecture updates.
*   **AI Prompt Strategy:** *"Review the PRD and Feature Breakdown for [Feature X]. Identify all edge cases, required permissions, and necessary user flows before writing any code."*
*   **Quality Gates:** The scope must be fully defined and unambiguous.
*   **Checklist:**
    *   [ ] Read PRD.
    *   [ ] Read User Flow.
    *   [ ] Identify target user roles (Patient, Doctor, Admin, Moderator).
*   **Definition of Done:** Feature requirements and architectural boundaries are fully understood and acknowledged.

---

## 2. Database Phase

*   **Inputs:** Requirements, `DATABASE_DESIGN.md`.
*   **Outputs:** New tables, columns, indexes, constraints, and Flyway/Liquibase migration scripts.
*   **AI Prompt Strategy:** *"Generate the PostgreSQL migration script for [Feature X] adhering to 3NF, using UUID primary keys, and including `created_at`, `updated_at`, and `deleted_at` audit fields."*
*   **Quality Gates:** Must not break existing foreign key relationships. Must include rollback scripts.
*   **Checklist:**
    *   [ ] Write `up` migration script.
    *   [ ] Write `down` rollback script.
    *   [ ] Verify indexes are added for new foreign keys.
*   **Definition of Done:** Migration script successfully executes locally and passes syntax checks.

---

## 3. API Phase

*   **Inputs:** Database schema, Requirements, `API_SPEC.md`.
*   **Outputs:** Updated `API_SPEC.md` with new REST/GraphQL endpoints, request/response DTOs, and error codes.
*   **AI Prompt Strategy:** *"Update API_SPEC.md to include the endpoints for [Feature X]. Define the URL, HTTP Method, Headers, DTOs, and expected HTTP status codes."*
*   **Quality Gates:** Must adhere strictly to RESTful naming conventions and established JSON envelope formats.
*   **Checklist:**
    *   [ ] Define Request DTO.
    *   [ ] Define Response DTO.
    *   [ ] Document HTTP Error Codes (400, 401, 403, 404).
*   **Definition of Done:** `API_SPEC.md` is updated and reviewed.

---

## 4. Backend Phase

*   **Inputs:** API Specification, Database Migrations, `CODING_STANDARDS.md`.
*   **Outputs:** Ktor Routes, Controllers, UseCases, Repositories, and Exposed DAO entities.
*   **AI Prompt Strategy:** *"Implement the backend logic for [Feature X] in Ktor. Create the UseCase in the Domain layer, the Repository in the Data layer, and the Route in the Presentation layer. Follow Clean Architecture."*
*   **Quality Gates:** No business logic in Controllers. Proper mapping between DTOs and Domain Models.
*   **Checklist:**
    *   [ ] Implement Data layer (Exposed DAO, RepositoryImpl).
    *   [ ] Implement Domain layer (UseCase).
    *   [ ] Implement Presentation layer (Route/Controller).
*   **Definition of Done:** Code compiles, endpoints are reachable, and basic manual testing via Postman succeeds.

---

## 5. Unit Test Phase (Backend)

*   **Inputs:** Completed Backend Code.
*   **Outputs:** JUnit 5 test suites for UseCases and Repositories.
*   **AI Prompt Strategy:** *"Write comprehensive unit tests for [UseCase X]. Use MockK to mock the Repository. Cover the happy path, unauthorized path, and database exception path using Given-When-Then structure."*
*   **Quality Gates:** Minimum 80% line coverage for the new feature.
*   **Checklist:**
    *   [ ] Test Happy Path.
    *   [ ] Test Error/Exception Paths.
    *   [ ] Verify Mock interactions.
*   **Definition of Done:** All unit tests pass locally (`./gradlew test`).

---

## 6. Android Phase

*   **Inputs:** `SCREEN_INVENTORY.md`, API Specification, Backend Endpoints.
*   **Outputs:** Jetpack Compose UI, ViewModels, State/Intents, Domain UseCases, Data Repositories, Koin Modules.
*   **AI Prompt Strategy:** *"Implement the Android UI and logic for [Feature X]. Create the Compose screen, the MVI ViewModel, and the corresponding Data Repository using Retrofit/Ktor Client. Inject dependencies via Koin."*
*   **Quality Gates:** UI state must be immutable. No blocking operations on the Main thread.
*   **Checklist:**
    *   [ ] Define UI State and Intent data classes.
    *   [ ] Implement Repository and Network API interface.
    *   [ ] Implement ViewModel.
    *   [ ] Build Jetpack Compose UI.
    *   [ ] Register dependencies in Koin module.
*   **Definition of Done:** The Android app compiles, UI renders correctly, and successfully communicates with the local Backend.

---

## 7. Integration Test Phase (Android)

*   **Inputs:** Completed Android Code, Running Backend.
*   **Outputs:** UI tests (Compose Testing) and Integration tests (MockWebServer).
*   **AI Prompt Strategy:** *"Write UI tests for [Screen X] to verify state rendering. Write integration tests for [Repository X] using MockWebServer to simulate API responses."*
*   **Quality Gates:** Critical user flows must be tested end-to-end.
*   **Checklist:**
    *   [ ] Mock network responses.
    *   [ ] Test Loading, Error, and Success UI states.
    *   [ ] Verify ViewModel state emissions.
*   **Definition of Done:** All UI and integration tests pass successfully.

---

## 8. Documentation Phase

*   **Inputs:** Completed code, previously updated specs.
*   **Outputs:** Updated inline KDoc, READMEs, and any relevant Architecture Decision Records (ADRs).
*   **AI Prompt Strategy:** *"Review the implemented code for [Feature X] and generate concise KDoc comments for all public classes and functions. Update the module README if new setup steps are required."*
*   **Quality Gates:** Documentation accurately reflects the final implementation.
*   **Checklist:**
    *   [ ] Add KDoc to UseCases and Repositories.
    *   [ ] Update README if dependencies changed.
*   **Definition of Done:** Code is fully documented and readable.

---

## 9. Review Phase

*   **Inputs:** Completed PR (Pull Request).
*   **Outputs:** Approved PR or requested changes.
*   **AI Prompt Strategy:** *"Act as a strict Senior Reviewer. Review the provided code against `PROJECT_RULES.md` and `CODING_STANDARDS.md`. Point out any security flaws, architectural violations, or missing tests."*
*   **Quality Gates:** Must pass CI checks (Lint, Tests, Build). Must receive at least one approval from a Code Owner.
*   **Checklist:**
    *   [ ] CI/CD pipeline is green.
    *   [ ] Code follows Clean Architecture.
    *   [ ] No hardcoded secrets.
*   **Definition of Done:** PR is approved by designated reviewers.

---

## 10. Merge Phase

*   **Inputs:** Approved PR.
*   **Outputs:** Code merged into the `main` or `develop` branch.
*   **AI Prompt Strategy:** N/A (Automated or manual merge).
*   **Quality Gates:** Branch must be up-to-date with the target branch before merging.
*   **Checklist:**
    *   [ ] Squash commits (if required by team policy).
    *   [ ] Verify post-merge deployment hooks trigger successfully.
*   **Definition of Done:** Feature is live in the staging environment and ready for QA testing.
