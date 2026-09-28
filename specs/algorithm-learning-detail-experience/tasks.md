# Algorithm Learning Detail Experience Tasks

All task IDs, dependencies, statuses, and exact verification commands are
authoritative in `agent-workflow/WORK_GRAPH.yaml`.

## Phase 1 — Detail state and mapping

### DLE-001 — Establish lossless learning-detail state, mapping, and route intent

- Deliverable: immutable full-detail draft/state and repository-driven
  save/reload behavior; complete `ProblemDetail`/`ProblemWrite`/`Solution`
  mapping described by spec section 3.2; a canonical problem-detail navigator
  destination/intent; fakes and deterministic tests. No composable or DTO owns
  persistence or browser calls.
- Depends on: none.
- Parallel group: `detail-foundation`.
- Spec refs: 3.1 AC-DLE-01/02, 3.2 AC-DLE-04, 3.3 AC-DLE-05/06, 4.
- Execution contract: `three-perspectives`; `tdd`; `update-docs`; `infer`.
- Verification: `cd apps/multiplatform && ./gradlew :shared:allTests`;
  `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `git diff --check`.
- Status: completed.

## Phase 2 — Detail presentation and history

### DLE-002 — Build the complete responsive standalone Detail page

- Deliverable: localized stateless Detail page and callbacks that initially show
  all content, edit/save all mapped fields, preserve existing non-Kotlin
  solutions, create/save Kotlin only on explicit intent, and render the
  wide/two-column and narrow/single-column contracts without a master-detail or
  staged-review UI.
- Depends on: DLE-001.
- Parallel group: `detail-presentation`.
- Spec refs: 3.1 AC-DLE-01..03, 3.2 AC-DLE-04, 3.3 AC-DLE-06, 4.
- Execution contract: `three-perspectives`; `tdd`; `update-docs`; `infer`.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:allTests`;
  `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserTest`; `git diff --check`.
- Status: completed.

### DLE-003 — Synchronize Web Detail URLs with browser Back and Forward

- Deliverable: Web history adapter plus platform-safe route seam; URL deep-link,
  list-state restoration, Back/Forward/popstate, and app-back idempotency tests.
- Depends on: DLE-002.
- Parallel group: `detail-history`.
- Spec refs: 3.3 AC-DLE-05/06, 4.
- Execution contract: `three-perspectives`; `tdd`; `update-docs`; `infer`.
- Verification: `cd apps/multiplatform && ./gradlew :shared:allTests`;
  `cd apps/multiplatform && ./gradlew :composeApp:allTests`;
  `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserTest`; `git diff --check`.
- Status: pending.

## Phase 3 — NeetCode learning material

### DLE-004 — Complete NeetCode 150 learning-content projection and importer checks

- Deliverable: every 150 entry has original bilingual descriptions, category,
  difficulty, Chinese hint/approach, complexity, annotated Kotlin code, and
  executable/understandable tests; a lossless structured projection into the
  established fields; local importer checks and an idempotent authenticated sync
  only when the established safe environment is available.
- Depends on: DLE-001.
- Parallel group: `detail-content`.
- Spec refs: 3.2 AC-DLE-04, 3.4 AC-DLE-07/08, 4.
- Execution contract: `three-perspectives`; `test-candidates`; `update-docs`; `infer`.
- Verification: `python3 docs/algorithm/neetcode150/checks/verify.py`;
  `python3 tools/test_neetcode150_import.py`; `python3 tools/neetcode150_import.py --validate`;
  `git diff --check`.
- Status: ready.

## Phase 4 — Acceptance

### DLE-005 — Verify the end-to-end learning Detail experience

- Deliverable: deterministic cross-layer acceptance coverage proving complete
  initial visibility, all-field save/reload, responsive layout, history
  navigation, Review entry redirection, and NeetCode content coverage; workflow
  closeout and scoped local commit.
- Depends on: DLE-002, DLE-003, DLE-004.
- Parallel group: `detail-acceptance`.
- Spec refs: 3.1 AC-DLE-01..03, 3.2 AC-DLE-04, 3.3 AC-DLE-05/06, 3.4 AC-DLE-07/08, 5.
- Execution contract: `three-perspectives`; `test-candidates`; `update-docs`; `infer`.
- Verification: `cd apps/multiplatform && ./gradlew :shared:allTests`;
  `cd apps/multiplatform && ./gradlew :composeApp:allTests`;
  `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserTest`;
  `python3 docs/algorithm/neetcode150/checks/verify.py`;
  `python3 tools/test_neetcode150_import.py`; `python3 tools/neetcode150_import.py --validate`;
  `git diff --check`.
- Status: pending.
