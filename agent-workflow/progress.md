# Progress

The Flutter application root and backend composition root are complete.

## ALG-005 closeout — 2026-09-01

- Completed the authentication and ownership foundation: Argon2id password
  hashing at OWASP's 19 MiB / two-iteration / one-lane minimum profile; signed
  15-minute HS256 JWTs; opaque 30-day refresh values persisted only as keyed
  hashes; one-time refresh rotation and logout revocation.
- Added JPA adapters for users and refresh sessions, a bearer authentication
  filter plus `CurrentUser` owner identity seam, and all required
  `/api/v1/auth` register/login/refresh/logout/me endpoints. Refresh cookies are
  `Secure`, `HttpOnly`, `SameSite=Lax`, and origin checks protect cookie-backed
  refresh/logout. Registration, login, refresh, and password verification use a
  replaceable IP/account-key rate-limit seam.
- Added focused red-green tests for Argon2id behavior and refresh rotation; the
  pre-existing JWT, password-policy, and keyed refresh-token tests remain green.
- Task-limited verification passed: `cd services/api && ./mvnw -q test -Dtest='*AuthTest,*SecurityTest'` and `cd services/api && ./mvnw -q verify`.
  The latter emitted the expected Testcontainers no-Docker diagnostic, while its
  Docker-optional migration fixture was skipped and Maven exited successfully.
- `ALG-005` is `completed`. `ALG-006` and `ALG-011` now have all dependencies
  complete and are ready for separate execution-strategy selection.

## ALG-004 closeout — 2026-09-01

- Added Flyway V1 for the `citext` extension and all initial product tables:
  users, auth sessions, problems, solutions, tags, problem tags, and reviews.
  It includes the required checks, indexes, ownership cascades, UTC timestamps,
  mutable-table update triggers, and a GIN full-text index over problem text
  only.
- Added a real PostgreSQL Testcontainers migration test. It proves empty-schema
  migration, key database checks, and user-owned record cascades.
- Task-limited verification passed: `cd services/api && DOCKER_HOST=unix:///Users/allen/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true ./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'`.
- `ALG-004` is `completed`; `ALG-005` is now `ready`.

## ALG-005 execution strategy — 2026-09-01

- Selected contract: `three-perspectives` / `tdd` / `update-docs` / `infer`.
- Scope: test-first authentication and ownership infrastructure only—Argon2id,
  15-minute JWT, opaque 30-day rotating refresh sessions, cookie/origin and
  rate-limit seams, and user-scoped persistence/security wiring. Product library
  behavior remains assigned to later tasks.
- Status is now `in_progress`; tests will first cover authentication outcomes and
  ownership boundaries. Argon2id will use OWASP's current 19 MiB / 2 iteration /
  1 parallelism minimum profile.

## ALG-005 implementation checkpoint — 2026-09-01

- Began the required TDD cycle with focused `*AuthTest` / `*SecurityTest`
  coverage. The initial tests correctly failed because `PasswordPolicy`,
  `RefreshTokenService`, and `JwtTokenService` did not exist.
- Added only the corresponding domain primitives so far: 12–128-character
  password boundary validation, 256-bit opaque refresh values with HMAC-SHA-256
  keyed persistence hashes and constant-time comparison, plus HS256 access JWT
  issuance/verification containing issuer, audience, subject, issued-at,
  expiry, and token ID claims. JWT lifetime is exactly 15 minutes.
- Evidence: `cd services/api && ./mvnw -q test -Dtest='*AuthTest,*SecurityTest'`
  passes. ALG-005 remains `in_progress`; Argon2id configuration, JPA/JDBC
  persistence, session rotation, endpoints, cookie/origin validation, rate-limit
  seam, bearer filter, ownership repository foundation, task verification, and
  closeout have not yet been implemented.

## ALG-004 recovery attempt 1 — 2026-09-01

- Diagnosis: the initial Testcontainers migration test could not be compiled
  because Spring Boot 4.1.1 does not manage Testcontainers dependency versions.
- Change: import the Testcontainers 2.0.5 BOM and use its v2 artifact IDs
  (`testcontainers-junit-jupiter`, `testcontainers-postgresql`) for test-scoped
  dependencies.
- Command: `cd services/api && ./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'`.
- Result: after dependency correction, the test reached the intended missing-schema red state.

## ALG-004 migration implementation adjustment — 2026-09-01

- Diagnosis: PostgreSQL rejected the initial full-text GIN expression because
  `concat_ws` is not immutable.
- Change: replace it with an explicitly immutable `problem_search_vector`
  function that indexes only the specified problem text fields.
- Command: `cd services/api && DOCKER_HOST=unix:///Users/allen/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true ./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'`.
- Result: reran successfully after the immutable-vector correction.

## ALG-004 recovery attempt 2 — 2026-09-01

- Diagnosis: Testcontainers 2.0.5's PostgreSQL container is managed by the
  JUnit 5 `@Container` annotation, not `@RegisterExtension`.
- Change: use the Testcontainers JUnit 5 container annotation on the real
  PostgreSQL test fixture.
- Command: `cd services/api && ./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'`.
- Result: the fixture then reached Docker discovery; the initial default socket
  was unavailable.

## ALG-004 recovery attempt 3 — 2026-09-01

- Diagnosis: the default Docker socket is unavailable, but the local Colima
  context is healthy at `/Users/allen/.colima/default/docker.sock`; Colima
  cannot mount that macOS socket into Testcontainers' Ryuk cleanup container.
- Change: no repository change; run the task's exact Maven verification against
  the available Docker endpoint with Ryuk disabled for this local test process.
- Command: `cd services/api && DOCKER_HOST=unix:///Users/allen/.colima/default/docker.sock TESTCONTAINERS_RYUK_DISABLED=true ./mvnw -q test -Dtest='*MigrationTest,*RepositoryIntegrationTest'`.
- Result: with the available Colima endpoint and Ryuk disabled, the test reached
  the intended missing-schema red state before V1 was added.

## ALG-003 closeout — 2026-09-01

- Created Flutter 3.47.0 Web/Android/iOS app root with Riverpod, GoRouter,
  Material 3 light/dark themes, English and Traditional Chinese locale support,
  and a widget smoke test.
- Task-limited verification passed: `flutter analyze`; `flutter test`.
- `ALG-003` is `completed`; `ALG-004` remains `ready`.

## ALG-002 closeout — 2026-09-01

- Established the Java 21 Spring Boot 4.1.1 modular-monolith composition root
  with Maven Wrapper 3.9.16, feature package boundaries, Web/JPA/Security/
  Validation/Flyway/Actuator dependencies, PostgreSQL runtime support,
  non-secret configuration binding, request-ID propagation, Problem Details
  error seam, and candidate configuration/filter tests.
- Recovery attempt 1 fixed only a candidate-test compile error caused by the
  generic `ServletResponse` type exposed by `FilterChain`; the request-ID
  assertion now uses the concrete mock response after the chain returns.
- Task-limited verification passed: `cd services/api && ./mvnw -q test` and
  `cd services/api && ./mvnw -q package -DskipTests`.
- `ALG-002` is `completed`. `ALG-003` and `ALG-004` are both `ready`; neither
  initially had an execution contract.
- `ALG-003` 已由 `$execution-strategy` 選定 contract：
  `three-perspectives` / `test-candidates` / `update-docs` / `infer`。實作
  限於 Flutter application composition root、Material 3 light/dark theme、
  英文與繁體中文 localization、單一 GoRouter、ProviderScope、feature-first
  directories 與 app-root widget tests；auth、API、repository 與產品畫面仍由
  後續 task 負責。
- 下一步：使用 `$task-execute` 依上述 contract 執行 `ALG-003`。

## ALG-002 recovery attempt 1 — 2026-09-01

- Diagnosis: `./mvnw -q test` failed during test compilation because the
  `FilterChain` lambda exposes its second parameter as `ServletResponse`, which
  has no `getHeader` method. The failure occurred before application startup or
  test execution.
- Change: moved the request-ID equality assertion outside the lambda, where the
  concrete `MockHttpServletResponse` is available; the test still verifies the
  same request-to-response propagation behavior.
- Result: reran `./mvnw -q test`; it passed. Next command is the remaining
  task-limited verification: `./mvnw -q package -DskipTests`.

## ALG-001 closeout — 2026-09-01

- 已完成 monorepo/toolchain skeleton：建立 `apps/learning_app`、`services/api`
  與 `infra` 的 tracked entry-point READMEs、非機密
  `infra/env/.env.example`、root `.gitignore`，並鎖定 Java 21 和 Flutter
  3.47.0。
- 僅執行 task 限定驗證，且全部通過：`git diff --check`、
  `test -d apps/learning_app`、`test -d services/api`、
  `test -f infra/env/.env.example`。
- `ALG-001` 已標記 `completed`。`ALG-002` 與 `ALG-003` 的所有依賴均已完成，
  因此已標記為 `ready`。
- `ALG-002` 已由 `$execution-strategy` 選定 contract：
  `three-perspectives` / `test-candidates` / `update-docs` / `infer`。實作
  限於 Spring Boot modular-monolith composition root、非機密 configuration
  binding、request-ID/error seams 與測試 harness；migrations、auth、REST
  resources 和 Compose 仍由後續 task 負責。目前 status 為 `in_progress`。

## Work-graph planning — 2026-09-01

- 已使用 `$work-graph`（`three-perspectives` 規劃深度）建立
  `specs/algorithm-learning-platform/plan.md`、
  `specs/algorithm-learning-platform/tasks.md` 與可讀 dependency 視圖
  `agent-workflow/WORK_GRAPH.md`。
- `agent-workflow/WORK_GRAPH.yaml` 現為產品 task 相依與狀態來源真相，包含
  `ALG-001` 至 `ALG-018`；每個節點已記錄 deliverable、`depends_on`、
  `parallel_group`、`spec_refs` 與限定 verification。
- 只有 `ALG-001`（monorepo/toolchain skeleton）為 `ready`；所有後續 task
  維持 `pending`，直到圖上的依賴完成。尚未開始產品程式碼或產品驗證。
- `ALG-001` 已由 `$execution-strategy` 選定 contract：`single-agent` /
  `rapid` / `update-docs` / `infer`。實作僅限根目錄骨架、非機密設定範例、
  toolchain version metadata 與 entry-point README；不得提前建立任一產品
  composition root。目前 status 為 `in_progress`；將在實作後僅跑該 task
  已宣告的四項驗證。

## Product requirement baseline

- 已建立根目錄 `requirement.md`，整理 Algorithm Learning & Review Platform 的需求基線。
- 已於 2026-09-01 使用 `$workflow-intake` 的 `brainstorm` 模式建立正式規格：
  `specs/algorithm-learning-platform/spec.md`。
- 規格已涵蓋 MVP scope/non-goals、驗收條件、整體 architecture、monorepo 與
  Flutter/Backend 結構、ERD/schema、REST API、authentication、安全、Docker、
  Kubernetes target、測試策略、決策與假設。
- 已於 2026-09-01 使用 `$spec-governance` 的 `update-docs + infer` 路徑完成
  規格治理；正式規格狀態為 governed，已可交由 `$work-graph` 拆分。
- Backend 為已鎖定決策：Java + Spring Boot、PostgreSQL、Spring Web、Spring
  Data JPA、Spring Security、Bean Validation、Flyway 與 Actuator；不得改用
  其他後端語言或框架，除非使用者明確同意並更新規格。
- 尚未建立 feature plan、task list 或產品 task graph 節點；這三項須在規格治理
  通過後由 `$work-graph` 建立。
- Execution contract 尚未選定；應在有 ready task 後由 `$execution-strategy` 選定。

## Workflow setup

- 已建立 project-local harness、8 個 workflow skills 與 `AI_DEVELOPMENT_GUIDE.md`。
- 已通過每個 skill 的結構驗證，以及 `flutter-dev-guide` 的 rules self-check。
- 情境驗證確認：在「快速實作、不寫測試」下，workflow 仍要求完整 spec、plan、task graph、明確 execution contract 與 task 限定驗證；無 graph 不可直接實作。
- 已加入 `WORKFLOW_CONTINUATION.md` 共用契約；8 個 skills 都明確引用它，且結尾必須提供同對話指令與新對話 prompt。重測確認 prompt 不會要求讀取尚未建立的工件。

下一個建議動作是以 `$work-graph` 為 `algorithm-learning-platform` 建立 feature
plan、task list 與產品 task graph；不要直接開始產品程式碼實作。
