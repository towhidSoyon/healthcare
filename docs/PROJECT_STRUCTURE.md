# Enterprise Repository Structure: Healthcare Ecosystem

This document outlines the strict monorepo directory structure designed to support a 100+ person engineering organization scaling the Healthcare Ecosystem. This structure enforces strict boundary separation, maximizes code reuse, and guarantees independent build/deploy pipelines.

---

## High-Level Monorepo Hierarchy

```text
healthcare-platform/
├── android/            # Android Patient & Doctor Apps
├── backend/            # Ktor Microservices / Modular Monolith
├── web/                # Admin & Moderator React Panels
├── infra/              # Infrastructure as Code (Terraform)
├── docs/               # System Architecture, PRD, Technical Specs
└── .github/            # Shared CI/CD GitHub Actions Workflows
```

---

## 1. Android Workspace (`/android`)

### `/android/apps/patient`
*   **Purpose:** The entry point and application shell for the Patient App.
*   **Responsibilities:** Application initialization, Koin module aggregation, manifest configuration, and top-level navigation graph.
*   **Allowed dependencies:** All `feature-*` and `core-*` modules.
*   **Forbidden dependencies:** `/android/apps/doctor`, direct external networking libraries (must route through `core-network`).
*   **Naming conventions:** Base package `com.healthcare.patient`.

### `/android/apps/doctor`
*   **Purpose:** The entry point and application shell for the Doctor App.
*   **Responsibilities:** Application initialization, Koin module aggregation, manifest configuration, and top-level navigation graph.
*   **Allowed dependencies:** Specific `feature-*` modules (e.g., `feature-prescription`), all `core-*` modules.
*   **Forbidden dependencies:** `/android/apps/patient`, patient-specific features.
*   **Naming conventions:** Base package `com.healthcare.doctor`.

### `/android/shared/features/feature-[name]` (e.g., `feature-auth`, `feature-telemedicine`)
*   **Purpose:** Encapsulated business features adhering to Clean Architecture.
*   **Responsibilities:** Contains Presentation (UI/VM), Domain (UseCases), and Data (Repositories) for a single feature.
*   **Allowed dependencies:** `core-ui`, `core-network`, `core-domain`.
*   **Forbidden dependencies:** Other `feature-*` modules. (Features must not depend on features; navigation happens via deep links or a centralized navigator).
*   **Naming conventions:** Module name: `feature-[domain]`. Packages: `com.healthcare.feature.[domain].[layer]`.

### `/android/shared/core/core-ui`
*   **Purpose:** The centralized Design System.
*   **Responsibilities:** Typography, Colors, Buttons, TextFields, and reusable generic Jetpack Compose components.
*   **Allowed dependencies:** Jetpack Compose, Material Design.
*   **Forbidden dependencies:** ANY domain logic, UseCases, or network models.
*   **Naming conventions:** `PascalCase` for Composable files (e.g., `PrimaryButton.kt`).

### `/android/shared/core/core-network`
*   **Purpose:** Centralized networking client.
*   **Responsibilities:** Retrofit/Ktor setup, JWT interceptors, TLS pinning.
*   **Allowed dependencies:** Ktor Client, OkHttp.
*   **Forbidden dependencies:** UI components, feature-specific DTOs.

---

## 2. Backend Workspace (`/backend`)

### `/backend/app-gateway`
*   **Purpose:** The primary Ktor application serving as the API Gateway.
*   **Responsibilities:** Routing incoming HTTP requests to the appropriate service modules, handling global CORS, and global exception mapping.
*   **Allowed dependencies:** All `service-*` modules, `shared-core`.
*   **Forbidden dependencies:** Direct database access (must go through services).
*   **Naming conventions:** Package `com.healthcare.backend.gateway`.

### `/backend/services/service-[name]` (e.g., `service-auth`, `service-appointments`)
*   **Purpose:** Isolated business domains (Modular Monolith architecture).
*   **Responsibilities:** Controllers/Routes, Application UseCases, Data Repositories, and Exposed DAO entities for a specific business vertical.
*   **Allowed dependencies:** `shared-core`, database drivers.
*   **Forbidden dependencies:** Other `service-*` modules (cross-service communication must happen via domain events/Redis PubSub, not direct method calls).
*   **Naming conventions:** `service-[domain]`. Database tables inside must be prefixed or cleanly isolated.

### `/backend/shared/shared-core`
*   **Purpose:** Foundational backend utilities.
*   **Responsibilities:** Security (JWT signing/verification), global DTOs, custom exception classes, and base repository interfaces.
*   **Allowed dependencies:** Utility libraries (e.g., Argon2, Jackson).
*   **Forbidden dependencies:** Feature-specific logic.

---

## 3. Web Workspace (`/web`)

### `/web/apps/admin-panel`
*   **Purpose:** React/Next.js application for Super Admins.
*   **Responsibilities:** Dashboard rendering, routing, page assembly.
*   **Allowed dependencies:** `web/shared/ui-components`, `web/shared/api-client`.
*   **Forbidden dependencies:** `/web/apps/moderator-panel`.
*   **Naming conventions:** `kebab-case` for directories, `PascalCase.tsx` for pages.

### `/web/apps/moderator-panel`
*   **Purpose:** React/Next.js application for Moderators (Document verification, tickets).
*   **Responsibilities:** Routing, queue rendering, action dispatching.
*   **Allowed dependencies:** `web/shared/ui-components`, `web/shared/api-client`.
*   **Forbidden dependencies:** `/web/apps/admin-panel`.

### `/web/shared/ui-components`
*   **Purpose:** Web Design System.
*   **Responsibilities:** Reusable React components (Tables, Modals, Charts).
*   **Allowed dependencies:** TailwindCSS, Headless UI libraries.
*   **Forbidden dependencies:** API clients, global state stores (Redux).
*   **Naming conventions:** `PascalCase.tsx`.

### `/web/shared/api-client`
*   **Purpose:** Centralized Axios/Fetch wrappers and React Query hooks.
*   **Responsibilities:** Handling JWT injection, token refresh loops, and standardizing API responses.
*   **Allowed dependencies:** Axios, React Query.
*   **Forbidden dependencies:** UI components.

---

## 4. Infrastructure & DevOps (`/infra`)

### `/infra/terraform`
*   **Purpose:** Infrastructure as Code (IaC).
*   **Responsibilities:** Defining AWS VPCs, RDS, ECS clusters, and S3 buckets.
*   **Allowed dependencies:** AWS Provider.
*   **Forbidden dependencies:** Application code.
*   **Naming conventions:** `snake_case.tf` files (e.g., `main.tf`, `rds_cluster.tf`).

### `/infra/docker`
*   **Purpose:** Container definitions.
*   **Responsibilities:** Dockerfiles for Backend, Admin Panel, and Moderator Panel.
*   **Naming conventions:** `Dockerfile.[service-name]`.

---

## Enforcement & CI Rules

1.  **Strict Boundary Checks:** CI pipelines (GitHub Actions) will utilize tools like `dependency-cruiser` (Web) or `detekt` (Android) to fail the build if a forbidden dependency rule is violated.
2.  **Independent Builds:** Modifying code in `/android/apps/patient` will **only** trigger the Patient App build pipeline, thanks to the isolated directory structure.
3.  **Code Owners:** Every major directory (e.g., `/backend/services/service-auth`) must have an associated `CODEOWNERS` file requiring mandatory PR reviews from the specialized team assigned to that domain.
