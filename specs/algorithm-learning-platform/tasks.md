# Algorithm Learning Platform — Task List

All task IDs, dependencies, statuses, and exact verification commands are
authoritative in `agent-workflow/WORK_GRAPH.yaml`. This document is the
human-readable execution contract. Select the per-task execution contract with
`$execution-strategy` before changing product code.

## Phase 1 — Foundations

### ALG-001 — Create monorepo and reproducible toolchain skeleton

- Deliverable: Root layout for `apps/learning_app`, `services/api`, and `infra`;
  git ignore and non-secret environment example; language/tooling version files;
  baseline readmes that state local entry points.
- Depends on: none
- Parallel group: `foundation`
- Spec refs: 6.1, 9, 13.1, 14.1, 18
- Execution contract: `single-agent` analysis; `rapid` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Implementation strategy: create only the root directory skeleton, ignore and
  non-secret configuration examples, pinned toolchain/version metadata, and
  entry-point READMEs. Do not bootstrap either application composition root,
  create product code, or run checks beyond this task's four listed
  verifications.
- Verification: `git diff --check`; `test -d apps/learning_app`; `test -d services/api`; `test -f infra/env/.env.example`
- Status: completed

### ALG-002 — Bootstrap the Spring Boot modular-monolith composition root

- Deliverable: Java 21 Spring Boot application with feature package boundaries,
  configured Spring Web/JPA/Security/Validation/Flyway/Actuator dependencies,
  production-safe configuration binding, error/request-ID composition seam, and
  backend test harness.
- Depends on: ALG-001
- Parallel group: `platform-roots`
- Spec refs: 6.1, 6.3, 10.7, 13.1-13.2, 15, 16.1
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Implementation strategy: select and lock mutually compatible stable Java 21,
  Spring Boot, Maven Wrapper, and test-library versions. Establish only the
  modular-monolith composition root: feature package boundaries, Spring Web/JPA/
  Security/Validation/Flyway/Actuator dependencies, non-secret configuration
  binding, request-ID/error composition seams, and a backend test harness.
  Implement the harness first, then add candidate context/configuration tests;
  defer all migrations, authentication flows, REST resources, and Docker Compose
  to their assigned tasks. Update the API README with the actual local entry
  points and configuration prerequisites.
- Verification: `cd services/api && ./mvnw -q test`; `cd services/api && ./mvnw -q package -DskipTests`
- Status: completed

### ALG-003 — Bootstrap the Flutter application composition root

- Deliverable: Flutter Web/Android/iOS application shell with Material 3,
  English and Traditional Chinese localization, Riverpod, one `GoRouter`
  composition root, feature-first directories, theme tokens, and test harness.
- Depends on: ALG-001
- Parallel group: `platform-roots`
- Spec refs: 9, 12.1, 12.3-12.4, 16.2; Flutter guides 00, 01, 02, 03, 13, 14, 15
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Implementation strategy: use the pinned Flutter SDK to generate the
  Web/Android/iOS project foundation, then establish only the app composition
  root: Material 3 light/dark theme tokens, English and Traditional Chinese
  localization, one `GoRouter` route table, a `ProviderScope` entry point,
  feature-first directories, and app-root widget tests. Lock compatible package
  versions in `pubspec.lock`; defer auth state, API clients, repositories,
  feature behavior, and product screens to their assigned tasks. Update the
  Flutter README with actual bootstrap, test, and run commands.
- Verification: `cd apps/learning_app && flutter analyze`; `cd apps/learning_app && flutter test`
- Status: completed

### ALG-004 — Implement the initial PostgreSQL schema and migration test base

- Deliverable: Flyway migrations for extensions, users, auth sessions, problems,
  solutions, tags, problem tags, and reviews, including constraints, indexes,
  cascades, timestamps, and real-PostgreSQL Testcontainers migration coverage.
- Depends on: ALG-002
- Parallel group: `backend-schema`
- Execution contract: `three-perspectives` analysis; `tdd` test approach; `update-docs` documentation; `infer` ambiguity handling.
- Implementation strategy: define migration-test cases first, then implement ordered Flyway migrations and Testcontainers coverage for all specified tables, constraints, indexes, cascades, and timestamps; defer API behavior to later tasks.
- Spec refs: 8.1-8.4, 13.1, 16.1; AC-OPS-02
- Verification: `cd services/api && ./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'`
- Status: completed

## Phase 2 — Secured library API

### ALG-005 — Deliver authentication, JWT, refresh rotation, and ownership infrastructure

- Deliverable: Register/login/refresh/logout/me API, Argon2id password hashing,
  15-minute JWT access tokens, opaque hashed rotating refresh sessions,
  rate-limit seam, security filters, and owner-scoped repository foundation.
- Depends on: ALG-002, ALG-004
- Parallel group: `backend-security`
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Implementation strategy: build the authentication and ownership foundation
  test-first: Argon2id hashing, signed 15-minute JWTs, opaque 30-day refresh
  rotation with keyed hashes, cookie/origin handling, rate-limit seam, and
  user-scoped persistence/security wiring. Keep transport endpoints and access
  rules minimal but complete; defer tag/problem/solution/review/dashboard
  behavior.
- Spec refs: 4.1, 5 AC-AUTH-01..04, 10.2, 11.1-11.3, 13.1-13.2, 16.1
- Verification: `cd services/api && ./mvnw -q test -Dtest='*AuthTest,*SecurityTest'`; `cd services/api && ./mvnw -q verify`
- Status: completed

### ALG-006 — Implement owned tag API and normalization policy

- Deliverable: Tag domain/application/data/API layers with NFKC whitespace and
  case-fold normalization, idempotent create behavior, owned listing/search, and
  duplicate handling.
- Depends on: ALG-004, ALG-005
- Parallel group: `backend-library`
- Spec refs: 5 AC-TAG-01..02, 8.2 tags/problem_tags, 10.4, 10.7, 11.2, 16.1
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd services/api && ./mvnw -q test -Dtest='*TagTest,*TagControllerTest'`
- Status: completed

### ALG-007 — Implement owner-scoped Problem CRUD, search, filters, and URLs

- Deliverable: Problem domain/application/data/API layers, full write validation,
  transactional tag replacement, full-text search, combined filters/pagination,
  deterministic sorting, URL/platform-host policy, and non-disclosing 404s.
- Depends on: ALG-004, ALG-005, ALG-006
- Parallel group: `backend-library`
- Spec refs: 5 AC-PROB-01..05, 7.1-7.3, 8.2-8.4, 10.1, 10.3, 10.6-10.7, 11.2, 16.1
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd services/api && ./mvnw -q test -Dtest='*ProblemTest,*ProblemControllerTest,*ProblemRepositoryIntegrationTest'`
- Status: completed

### ALG-008 — Implement independent Solution CRUD

- Deliverable: Owner-authorized solution API under problem and solution routes,
  language/content validation, stable ordering, independent transactions, and
  cascade-safe persistence behavior.
- Depends on: ALG-004, ALG-005, ALG-007
- Parallel group: `backend-library`
- Spec refs: 5 AC-SOL-01..02, 7.2-7.3, 8.2 solutions, 10.3, 10.6-10.7, 16.1
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Implementation strategy: establish owner-scoped solution CRUD and validation
  through focused failing tests first. Each solution operation is independent of
  Problem CRUD; parent/problem and solution lookups are owner-scoped, reads use
  stable creation order, and review/dashboard behavior remains deferred.
- Verification: `cd services/api && ./mvnw -q test -Dtest='*SolutionTest,*SolutionControllerTest'`
- Status: completed

### ALG-009 — Implement review policy, events, due lists, and history

- Deliverable: Pure fixed-v1 review policy with injected clock/UUID, append-only
  review events, authorized review submission, latest-state derivation, due-list
  query, and paginated history API.
- Depends on: ALG-004, ALG-005, ALG-007
- Parallel group: `backend-reports`
- Spec refs: 5 AC-REV-01..04, 7.4-7.5, 8.2 reviews, 10.5-10.7, 13.2, 16.1
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Implementation strategy: fixed-v1 policy and owner-scoped append-only review
  events with injected Clock/UUID seams; derive latest state, due lists, and
  history in PostgreSQL, deferring dashboard aggregation to ALG-010.
- Verification: `cd services/api && ./mvnw -q test -Dtest='*ReviewPolicyTest,*ReviewTest,*ReviewControllerTest'`
- Status: completed

### ALG-010 — Implement dashboard aggregation API

- Deliverable: Authorized dashboard query for totals, difficulty/tag counts,
  due count, recent problems, deterministic weak topics, and random problem,
  including no-data behavior.
- Depends on: ALG-005, ALG-006, ALG-007, ALG-009
- Parallel group: `backend-reports`
- Spec refs: 5 AC-DASH-01..02, 7.5, 8.4, 10.5-10.7, 16.1
- Execution contract: `three-perspectives` analysis; `tdd` test approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd services/api && ./mvnw -q test -Dtest='*DashboardTest,*DashboardControllerTest'`
- Status: completed

## Phase 3 — Flutter shared data and session

### ALG-011 — Implement Flutter authentication/session data flow

- Deliverable: Auth domain contract, remote DTO/service/repository, secure
  refresh-cookie handling per platform, in-memory access token, single-flight
  refresh/retry, typed failures, auth notifier, and route redirect state.
- Depends on: ALG-003, ALG-005
- Parallel group: `flutter-data`
- Spec refs: 4.1, 5 AC-AUTH-01..04, 10.1-10.2, 11.1-11.3, 12.1-12.3, 15, 16.2; Flutter guides 00, 01, 03, 05, 06, 08, 13, 15, 17
- Execution contract: `three-perspectives` analysis; `tdd` test approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/learning_app && flutter analyze`; `cd apps/learning_app && flutter test test/features/auth`
- Status: completed

### ALG-012 — Implement Flutter problem-library remote contracts and repositories

- Deliverable: Typed API client/DTO mappers/domain contracts for tags, problems,
  solutions, reviews, and dashboard; explicit timeout/cancellation/error mapping;
  repository fakes and provider override seams; no local problem cache.
- Depends on: ALG-003, ALG-007, ALG-008, ALG-009, ALG-010, ALG-011
- Parallel group: `flutter-data`
- Spec refs: 6.3, 7.1-7.5, 10.3-10.7, 12.1-12.2, 15, 16.2; Flutter guides 00, 01, 03, 04, 05, 06, 08, 15, 17
- Execution contract: `three-perspectives` analysis; `tdd` test approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/learning_app && flutter analyze`; `cd apps/learning_app && flutter test test/features/problems test/features/reviews test/features/dashboard`
- Status: completed

## Phase 4 — Product experiences

### ALG-013 — Build Web problem management experience

- Deliverable: Responsive Web list/search/filter, problem create/edit/delete with
  retained drafts and field errors, tag assignment, independent solution forms,
  detail content order, localized states/messages, and accessibility semantics.
- Depends on: ALG-011, ALG-012
- Parallel group: `flutter-experiences`
- Spec refs: 3.3, 4.2, 5 AC-PROB-01..05/AC-SOL-01..02/AC-TAG-01..02, 7.1-7.3, 12.2-12.4, 15, 16.2; Flutter guides 08, 09, 10, 13, 14, 15, 17
- Verification: `cd apps/learning_app && flutter analyze`; `cd apps/learning_app && flutter test test/features/problems/presentation`
- Status: completed

### ALG-014 — Build adaptive browse and staged Review Mode

- Deliverable: Mobile-first browse/detail routes and review stage state that
  enforces disclosure order, prevents confidence submission before solution,
  handles chosen solution, records confidence/notes, and renders all key states.
- Depends on: ALG-011, ALG-012
- Parallel group: `flutter-experiences`
- Spec refs: 3.3, 4.3, 5 AC-REV-01..04, 7.3-7.5, 12.2-12.4, 15, 16.2; Flutter guides 08, 09, 10, 13, 14, 15, 17
- Execution contract: `three-perspectives` analysis; `tdd` test approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/learning_app && flutter analyze`; `cd apps/learning_app && flutter test test/features/reviews test/features/problems/presentation`
- Status: completed

### ALG-015 — Build dashboard and mobile Home experience

- Deliverable: Dashboard/home states for totals, distributions, due reviews,
  recent problems, weak topics, random problem, and localized no-data guidance;
  Web and mobile adapt navigation without divergent business state.
- Depends on: ALG-011, ALG-012
- Parallel group: `flutter-experiences`
- Spec refs: 3.3, 5 AC-DASH-01..02, 7.5, 10.5-10.6, 12.2-12.4, 16.2; Flutter guides 08, 09, 13, 14, 15
- Execution contract: `three-perspectives` analysis; `tdd` test approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/learning_app && flutter analyze`; `cd apps/learning_app && flutter test test/features/dashboard/presentation`
- Status: completed

## Phase 5 — Operability and acceptance

### ALG-016 — Deliver local Docker Compose and operational documentation

- Deliverable: PostgreSQL/API Compose services with named volume, health checks,
  environment mapping, migration-startup behavior, non-secret example values,
  and local run instructions.
- Depends on: ALG-002, ALG-004, ALG-005, ALG-010
- Parallel group: `operations`
- Spec refs: 5 AC-OPS-01..03, 11.3, 14.1, 15, 16.1
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `docker compose -f infra/compose.yaml config`; `docker compose -f infra/compose.yaml up -d --build`; `docker compose -f infra/compose.yaml ps`; `docker compose -f infra/compose.yaml down`
- Status: completed

### ALG-017 — Document the Kubernetes deployment target

- Deliverable: Provider-neutral Kubernetes target document describing API
  deployment/service/ingress, config/secrets, probes, migration Job, and managed
  PostgreSQL expectations; no production manifest is created.
- Depends on: ALG-016
- Parallel group: `operations`
- Spec refs: 3.2, 14.2, 18
- Execution contract: `single-agent` analysis; `rapid` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `test -f infra/kubernetes/README.md`; `git diff --check`
- Status: completed

### ALG-018 — Run the MVP acceptance and integration suite

- Deliverable: Controlled API integration environment and documented evidence for
  auth, Web create/edit/browse, owner isolation, review completion, migrations,
  Compose health, Flutter key journeys, and all task-limited quality checks.
- Depends on: ALG-010, ALG-013, ALG-014, ALG-015, ALG-016, ALG-017
- Parallel group: `release-acceptance`
- Spec refs: 4.1-4.3, 5, 10, 11, 14-17; AC-AUTH-01..04, AC-PROB-01..05, AC-SOL-01..02, AC-TAG-01..02, AC-REV-01..04, AC-DASH-01..02, AC-OPS-01..03, AC-QUAL-01
- Verification: `cd services/api && ./mvnw -q verify`; `cd apps/learning_app && flutter analyze`; `cd apps/learning_app && flutter test`; `cd apps/learning_app && flutter test integration_test`; `python3 flutter-dev-guide/tools/check-rules.py --staged`; `docker compose -f infra/compose.yaml up -d --build`; `docker compose -f infra/compose.yaml ps`; `docker compose -f infra/compose.yaml down`
- Status: pending
