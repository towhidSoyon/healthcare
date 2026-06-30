# AI Agent Guide: Healthcare Ecosystem

This document strictly defines the operational protocols, workflows, and constraints for all Artificial Intelligence Coding Agents contributing to the Healthcare Ecosystem repository. 

As an AI Agent operating within this ecosystem, you are bound by these rules. Failure to adhere to these instructions constitutes a violation of project integrity.

---

## 1. General Rules

1.  **Do No Harm:** Your code operates within a critical healthcare environment. Never implement shortcuts that compromise data integrity, patient privacy, or system availability.
2.  **Absolute Compliance:** You must strictly follow `PROJECT_RULES.md` and `CODING_STANDARDS.md`. They supersede any generalized AI coding knowledge you possess.
3.  **No Hallucinations in Architecture:** Never invent design patterns, libraries, or architectural layers not explicitly documented in `SYSTEM_ARCHITECTURE.md` or `TECH_STACK.md`.
4.  **Idempotent Execution:** Ensure that your generated code or scripts can be safely run multiple times without causing unintended side effects.

---

## 2. How to Read Project Documentation

Before touching any code for a new feature or bug fix, you **must**:
1.  Read the relevant section in the **PRD** and **FEATURE_BREAKDOWN.md** to understand the business context.
2.  Check the **SCREEN_INVENTORY.md** and **USER_FLOW.md** if you are modifying the UI or user journeys.
3.  Cross-reference **DATABASE_DESIGN.md** and **API_SPEC.md** when implementing data or network layers.
4.  Verify placement using **REPOSITORY_STRUCTURE.md**.

*Do not assume the structure; verify it by reading the docs.*

---

## 3. Coding Workflow

1.  **Analyze Request:** Break down the user prompt into actionable steps.
2.  **Locate Context:** Identify exactly which module(s) and layers (Presentation, Domain, Data) are affected.
3.  **Draft Implementation (Mental or Scratchpad):** Formulate how the code will look.
4.  **Execute Code Changes:** Write the code adhering to Clean Architecture. 
5.  **Write Tests:** (See Section 6).
6.  **Self-Review:** Compare your changes against the Code Review Checklist (Section 10).
7.  **Finalize:** Present changes to the user or commit if authorized.

---

## 4. How to Update Documentation

*   **API Changes:** If you add or modify a backend route, you *must* immediately update `API_SPEC.md` reflecting the new URL, parameters, or DTOs.
*   **Database Changes:** If you add a column or table, update `DATABASE_DESIGN.md`.
*   **Inline Docs:** Always add or update KDoc/JSDoc for public functions and complex logic blocks. Do not add redundant comments (e.g., `// Sets the name` for `setName()`).

---

## 5. How to Create Commits

If instructed to execute Git commits, use the Conventional Commits format exactly:

*   **Format:** `<type>(<scope>): <subject>`
*   **Types allowed:** `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`.
*   **Scope:** The specific module or feature (e.g., `auth`, `appointment-ui`, `db-migration`).
*   **Subject:** Imperative mood, lowercase, no trailing period (e.g., `feat(auth): add biometric login fallback`).

---

## 6. How to Generate Tests

*   **Test-Driven Execution:** For every new UseCase or ViewModel, you must generate a corresponding unit test file.
*   **Structure:** Use the `Given-When-Then` format explicitly in your test naming or inline comments.
*   **Mocking:** Use MockK (Kotlin) or Jest (React) to mock boundary dependencies (Repositories, APIs). Do not execute real database or network calls in unit tests.
*   **Coverage Target:** Ensure the happy path, alternative path, and all explicit error paths defined in the PRD are tested.

---

## 7. How to Ask Clarification Questions

If a user request is ambiguous, contradictory to the architecture docs, or underspecified, **stop** and ask for clarification.
*   Do not guess business rules.
*   Do not guess database types if not specified.
*   Format your questions clearly as bullet points before writing any code.

---

## 8. Forbidden Actions

*   **Forbidden:** Bypassing the Repository layer (e.g., calling a Ktor API directly from a Compose ViewModel).
*   **Forbidden:** Storing hardcoded secrets, API keys, or plain text passwords.
*   **Forbidden:** Generating monolithic files. If a generated file exceeds 400 lines, you must refactor it into smaller, focused components.
*   **Forbidden:** Utilizing `!!` (non-null assertion) in Kotlin.
*   **Forbidden:** Introducing third-party dependencies without explicit user permission.

---

## 9. Architecture Constraints

*   **Dependency Rule:** Domain models must never import from Data (DTOs/APIs) or Presentation (UI).
*   **Data Mapping:** Data Transfer Objects (DTOs) must be mapped to Domain Models immediately upon entering the data boundary.
*   **State:** Mobile UI state must be immutable and passed down. Events flow up.

---

## 10. Code Review Checklist (Self-Evaluation)

Before presenting code, internally verify:
- [ ] Does this code violate any rule in `PROJECT_RULES.md`?
- [ ] Does this code violate any standard in `CODING_STANDARDS.md`?
- [ ] Are variables and functions named clearly according to the conventions?
- [ ] Is error handling robust? Are exceptions caught and mapped gracefully?
- [ ] Are background threads (Coroutines) properly scoped to avoid memory leaks?
- [ ] Has the corresponding documentation been updated?
- [ ] Have unit tests been written for the new logic?

---

## 11. Definition of Done

An AI Agent's task is only considered "Done" when:
1.  The code perfectly implements the requested feature as per the PRD.
2.  The code strictly conforms to the architecture and coding standards.
3.  Unit tests are generated and pass logically.
4.  Relevant Markdown documentation (`API_SPEC`, `DATABASE_DESIGN`, etc.) is updated.
5.  A clear summary of the exact files changed and the architectural reasoning is provided to the user.
