# Healthcare Ecosystem Coding Standards

This document defines the strict engineering standards for Android, Backend, and Admin Panel development. It must be adhered to by all engineers and AI agents contributing to the Healthcare Ecosystem.

---

## 1. Architecture

### Android (Patient & Doctor Apps)
*   **Paradigm:** Clean Architecture with MVI (Model-View-Intent) or strict MVVM at the Presentation layer.
*   **Layers:** 
    *   `presentation`: UI (Jetpack Compose), ViewModels, UI State, UI Intents.
    *   `domain`: UseCases, Domain Models, Repository Interfaces. Completely Android-framework agnostic.
    *   `data`: Repository Implementations, DTOs, Local DB (Room) DAOs, Remote APIs (Retrofit/Ktor).

### Backend
*   **Paradigm:** Domain-Driven Design (DDD) combined with Hexagonal Architecture (Ports and Adapters).
*   **Layers:**
    *   `Core/Domain`: Entities, Value Objects, Domain Events.
    *   `Application`: UseCases/Command Handlers, Interface definitions (Ports).
    *   `Infrastructure`: Database adapters, External API clients, Message Brokers.
    *   `Presentation/API`: Controllers, Routers, GraphQL Resolvers.

### Admin Panel (Web)
*   **Paradigm:** Component-Based Architecture (React/Next.js or Vue).
*   **Structure:** Feature-sliced design (`features/`, `entities/`, `shared/`, `pages/`).

---

## 2. Design Patterns

*   **Android:** Observer Pattern (via StateFlow), Factory Pattern (for ViewModels/UseCases), Builder Pattern (for complex UI states), Strategy Pattern (for varying data source strategies).
*   **Backend:** Repository Pattern, Unit of Work, Command Query Responsibility Segregation (CQRS) for complex read/write operations, Factory Pattern for entity creation.
*   **Admin Panel:** Container/Presentational Components, Higher-Order Components (HOCs) or Custom Hooks for shared logic.

---

## 3. Dependency Injection

*   **Android:** Use **Hilt**. Define specific modules for `DataModule`, `DomainModule`, `NetworkModule`. Scoping must be kept as narrow as possible (`@ViewModelScoped`, rarely `@Singleton`).
*   **Backend:** Use native framework DI container (e.g., Spring DI for Java/Kotlin, NestJS DI for Node, Microsoft.Extensions.DependencyInjection for .NET). Favor Constructor Injection exclusively.
*   **Admin Panel:** Context API for global state, React Query/SWR for server state, constructor/props for UI components.

---

## 4. Naming Rules

*   **Android:**
    *   Composables: `PascalCase` (e.g., `PatientProfileScreen`).
    *   ViewModels: `<Feature>ViewModel` (e.g., `LoginViewModel`).
    *   Layout/Resource XMLs (if any): `snake_case` (e.g., `ic_heart_rate.xml`).
*   **Backend:**
    *   Controllers: `<Entity>Controller` (e.g., `AppointmentController`).
    *   Services/UseCases: `<Action><Entity>UseCase` (e.g., `CancelAppointmentUseCase`).
*   **Admin Panel:**
    *   Components: `PascalCase.tsx`.
    *   Hooks: `camelCase` starting with `use` (e.g., `usePatientData.ts`).
*   **General:** No prefixes like `m` or `_` for variables. Interfaces should not be prefixed with `I` (use `UserRepository` for interface, `UserRepositoryImpl` for implementation).

---

## 5. Coroutine & Concurrency Rules

*   **Android:**
    *   Never hardcode dispatchers. Inject `CoroutineDispatcher` (e.g., `@IoDispatcher`).
    *   Use `viewModelScope` for UI operations.
    *   Use `suspend` functions in UseCases and Repositories. Do not expose `Deferred` or `Job`.
*   **Backend:**
    *   Kotlin/Java: Use Coroutines (Spring WebFlux/Ktor) or Virtual Threads. Do not block threads.
    *   Node.js: Use `async/await`. Avoid `.then().catch()` chains.

---

## 6. State Management

*   **Android:** Represent UI state as a single immutable data class. Expose via `StateFlow` from ViewModel. UI strictly observes this state and sends `Intent` / `Event` objects back to the ViewModel.
*   **Admin Panel:** Redux Toolkit or Zustand for global client state. React Query / Apollo for server state caching. Do not mix server state into client state stores.

---

## 7. Repository Rules

*   **Rule:** Repositories mediate between Domain and Data. They must map Data sources (DTOs/Entities) to Domain Models.
*   **Rule:** Repositories must abstract whether data comes from local cache or network (Single Source of Truth).
*   **Rule:** Return Domain models, standard Kotlin `Result<T>`, or `Flow<T>`. Never return HTTP responses or SQL cursors.

---

## 8. UseCase Rules

*   **Rule:** UseCases (Interactors) encapsulate a single piece of business logic.
*   **Rule:** Must have exactly one public function (e.g., `operator fun invoke()`).
*   **Rule:** UseCases can depend on Repositories or other UseCases, but never on ViewModels or Controllers.

---

## 9. DTO (Data Transfer Object) Rules

*   **Rule:** DTOs are explicitly for crossing system boundaries (Network/Database).
*   **Rule:** Naming: `[Entity]Request`, `[Entity]Response`, `[Entity]Dto`.
*   **Rule:** Must be mapped to a Domain Model as soon as they cross the boundary into the application core. Never pass a DTO into a ViewModel or Domain UseCase.

---

## 10. API Rules

*   **Rule:** Strictly RESTful or GraphQL.
*   **Rule:** Use proper HTTP verbs: `GET` (read), `POST` (create), `PUT` (replace), `PATCH` (update partial), `DELETE` (remove).
*   **Rule:** Standardized JSON envelope for responses (e.g., `{ "data": {}, "meta": {}, "error": null }`).
*   **Rule:** Always implement pagination (`limit`, `offset` or cursor) for collections.

---

## 11. Database Rules

*   **Rule:** Use Migrations (Flyway/Liquibase for Backend, Room Migrations for Android). Never use auto-sync schemas in production.
*   **Rule:** Use foreign keys and constraints strictly to ensure data integrity at the database level.
*   **Rule:** Soft deletes (`deleted_at` timestamp) are preferred over hard deletes for critical health records.

---

## 12. Exception Rules

*   **Rule:** Do not use Exceptions for control flow. Use `Result<T>` or `Either<L, R>` for expected business failures (e.g., "Invalid Password").
*   **Rule:** Throw Exceptions ONLY for truly exceptional, unrecoverable states (e.g., Database connection lost).
*   **Rule:** Backend must catch unhandled exceptions at the global middleware level and return a standard 500 error without exposing stack traces.

---

## 13. Testing Rules

*   **Rule:** Minimum 80% coverage for Domain and ViewModel/Controller layers.
*   **Rule:** Use JUnit5 and MockK/Mockito for Backend/Android. Jest/React Testing Library for Admin Panel.
*   **Rule:** Treat tests as first-class code. Refactor them. Keep them DRY.
*   **Rule:** UI tests (Espresso/Compose Test) should mock the network layer.

---

## 14. Formatting Rules

*   **Android/Backend (Kotlin):** ktlint standard. Max 100 characters per line.
*   **Admin Panel (JS/TS):** Prettier + ESLint strict mode.
*   **Rule:** Formatting must be enforced via pre-commit hooks (e.g., Husky / Spotless).

---

## 15. Forbidden Practices

*   **DO NOT** use `!!` (non-null assertion) in Kotlin.
*   **DO NOT** put business logic inside UI components (Composables, React Components, Activities, Fragments).
*   **DO NOT** use Global Variables or Singleton objects for holding mutable state.
*   **DO NOT** hardcode strings, API keys, colors, or dimensions.
*   **DO NOT** swallow exceptions (`catch (e: Exception) {}` with no action).

---

## 16. Required Practices

*   **MUST** use strings.xml (Android) or i18n JSON files (Web) for all user-facing text from day one.
*   **MUST** log all authentication and authorization failures on the backend.
*   **MUST** clean up resources (cancel coroutines, unsubscribe from streams) when UI components are destroyed.

---

## 17. Performance Rules

*   **Android:** Use `LazyColumn` for lists. Avoid unnecessary recompositions by using `remember` and immutable state. Do not perform DB/Network operations on the Main thread.
*   **Backend:** Ensure N+1 query problems are solved using joins or batch loaders. Add indexes to all foreign keys and frequently queried columns.
*   **Admin Panel:** Code-split heavy routes using React.lazy. Memoize heavy computations.

---

## 18. Security Rules

*   **Rule:** Hash passwords using Argon2 or Bcrypt. Never store plain text.
*   **Rule:** Implement rate limiting on all public API endpoints.
*   **Rule:** Validate JWT tokens on every authenticated request, verifying signature, expiration, and audience.
*   **Rule:** Prevent SQL Injection by strictly using ORMs/Parameterized queries.
*   **Rule:** Prevent XSS in Admin Panel by sanitizing all user-generated content before rendering.
