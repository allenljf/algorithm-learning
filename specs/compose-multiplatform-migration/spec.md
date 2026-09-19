# Compose Multiplatform Migration Specification

## Document status

- Feature ID: `compose-multiplatform-migration`
- Status: governed; ready for `$work-graph`
- Requirement baseline: [`../../requirement.md`](../../requirement.md); mirrors
  the delivered `algorithm-learning-platform` MVP UX and REST contract
- Relates to: replaces the Flutter client `apps/learning_app`
- Intake mode: `quick-analysis`
- Updated: 2026-09-19
- Governance mode: `update-docs + infer`
- Execution contract: not selected; chosen per ready task after planning

## 1. Goal

Rebuild the client as a Kotlin **Compose Multiplatform** application that
targets Android, iOS, Web (Wasm), and Desktop, preserving the proven MVP user
journeys — authentication, problem management, adaptive browse, staged Review
Mode, and dashboard/home — against the unchanged Spring Boot REST API.

## 2. Chosen architecture

```text
apps/multiplatform/
  composeApp/            # shared Compose UI (commonMain)
  shared/                # domain models, repository contracts, use cases (commonMain)
    data/                # Ktor remote services, DTOs, mappers (commonMain)
    androidMain/iosMain/jsMain/jvmMain   # platform actuals (HTTP engine, storage, DI)
```

- One codebase, platform targets produced by Gradle/Kotlin Multiplatform.
- Shared domain models and repository contracts match the API DTO boundaries; no
  DTO leaks into the UI.
- Dependency injection at the composition root (Koin or manual), no service
  locator inside UI.
- Networking through Ktor `HttpClient` with a platform engine; auth access token
  in memory, refresh single-flight, exactly as the Flutter client did.
- State is immutable and flows one way into Compose UI.

## 3. Scope

### In scope

- A Compose Multiplatform project root and shared module boundaries.
- Platform targets Android, iOS, Web (Wasm), Desktop.
- Auth/session data flow, problem-library/review/dashboard remote contracts and
  repositories, and the four UI experiences mirroring ALG-011..015.
- A Compose-specific engineering guide (architecture, DI, data layer, testing)
  because `flutter-dev-guide` governs the Flutter client only.
- Retirement of the Flutter client once parity and acceptance pass.

### Out of scope

- API, database, or backend behavior changes; the REST contract is frozen.
- New product features, notifications, offline scheduling, or AI features.
- App-store submission, signing, or custom domains.

## 4. Acceptance criteria

- **AC-CMP-01:** `apps/multiplatform` builds for at least Android and Web from a
  single shared `commonMain` UI and domain layer.
- **AC-CMP-02:** Architecture/DI/data/testing guide exists and the implementation
  follows it (no DTO leaks into Compose, no repository access inside composables).
- **AC-CMP-03:** Authentication, refresh, and session state match ALG-011 behavior.
- **AC-CMP-04:** Problem list/search/filter/create/edit/delete and solutions match
  ALG-012/ALG-013 behavior.
- **AC-CMP-05:** Browse and the staged Review Mode progression match ALG-014,
  including confidence gating.
- **AC-CMP-06:** Dashboard/home aggregates and states match ALG-015.
- **AC-CMP-07:** A cross-platform acceptance suite passes and the Flutter client is
  removed or explicitly archived.

## 5. Constraints and decisions

- Kotlin and Compose Multiplatform stable versions are pinned in the version
  catalog; Gradle Kotlin DSL is used.
- Shared code lives in `commonMain`; platform code only for engine/storage actuals.
- iOS and Desktop are lower priority than Android/Web until parity is proven.
- The backend API contract does not change; the same base URL injection and CORS
  behavior applies.

## 6. Assumptions to govern

- The team accepts that `flutter-dev-guide` does not govern the new client and a
  Compose-specific guide is required before implementation.
- The Flutter client is replaced rather than kept in parallel long term.
- No `plan.md`, `tasks.md`, or `WORK_GRAPH.yaml` node exists for this feature.

## 7. Governance record

- Documentation decision: `update-docs`.
- Ambiguity decision: `infer`.
- Chose to mirror the existing ALG-011..015 task decomposition so parity is
  verifiable against the delivered MVP acceptance criteria.
- Recorded that a Compose-specific guide is a prerequisite deliverable, not an
  implicit assumption.
