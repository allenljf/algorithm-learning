# Progress

Flutter product code has not started; the backend composition root is complete.

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
