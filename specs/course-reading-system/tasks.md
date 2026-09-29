# Course Reading System Tasks

## Phase 1 — Canonical content

### COURSE-001 — Census and canonical Kotlin teaching material

- Deliverable: deterministic extractor/census, all discovered real lessons as
  canonical Markdown, source/provenance ledger, Kotlin examples, and manifest
  coverage verifier.
- Depends on: none. Parallel group: `course-content`.
- Spec refs: AC-COURSE-01/02/06; constraints.
- Verification: `python3 docs/algorithm/course-materials/checks/verify.py`; `git diff --check`.
- Execution contract: `three-perspectives`; `tdd`; `update-docs`; `infer`.
- Status: completed. Verification passed: `python3 docs/algorithm/course-materials/checks/verify.py`; `git diff --check`.

## Phase 2 — Course service and ingestion

### COURSE-002 — Isolated Course migration and three-route API

- Deliverable: V3 Course schema, Course-only layered backend, token guard,
  idempotent upsert, public filtering/paging/detail, and focused tests.
- Depends on: COURSE-001. Parallel group: `course-service`.
- Spec refs: AC-COURSE-03/04/05; constraints.
- Verification: `cd services/api && ./mvnw -q test -Dtest='*CourseTest,*CourseControllerTest,*CourseMigrationTest'`; `git diff --check`.
- Execution contract: `three-perspectives`; `tdd`; `update-docs`; `infer`.
- Status: completed. Verification passed: `cd services/api && ./mvnw -q test -Dtest='*CourseTest,*CourseControllerTest,*CourseMigrationTest'`; `git diff --check`.

### COURSE-003 — Safe Course importer and lossless payload verification

- Deliverable: non-secret dry-run importer and mock-transport tests that prove
  idempotent batch projection and detail digest preservation.
- Depends on: COURSE-001, COURSE-002. Parallel group: `course-service`.
- Spec refs: AC-COURSE-01/02/05/06.
- Verification: `python3 tools/test_course_import.py`; `python3 tools/course_import.py --validate`; `git diff --check`.
- Execution contract: `single-agent`; `tdd`; `update-docs`; `infer`.
- Status: completed. Verification passed: `python3 tools/test_course_import.py`; `python3 tools/course_import.py --validate`; `git diff --check`.

## Phase 3 — Cross-platform reading

### COURSE-004 — Shared Course domain and data mapping

- Deliverable: transport-isolated domain models, repository, Ktor remote and
  mapping/fake tests wired through AppContainer.
- Depends on: COURSE-002. Parallel group: `course-client`.
- Spec refs: AC-COURSE-05/07; Compose guide sections 1–6.
- Verification: `cd apps/multiplatform && ./gradlew :shared:allTests`; `git diff --check`.
- Execution contract: `single-agent`; `tdd`; `update-docs`; `infer`.
- Status: completed. Verification passed: `cd apps/multiplatform && ./gradlew :shared:allTests`; `git diff --check`.

### COURSE-005 — Compose Course navigation and non-truncating reader

- Deliverable: localized Course tab/category/list/search/filter/detail UI and
  view-model/Compose/Wasm tests; no editing UI.
- Depends on: COURSE-004. Parallel group: `course-client`.
- Spec refs: AC-COURSE-07; Compose guide sections 1–7.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:allTests :composeApp:wasmJsBrowserTest`; `git diff --check`.
- Execution contract: `single-agent`; `tdd`; `update-docs`; `infer`.
- Status: completed. Verification passed: `cd apps/multiplatform && ./gradlew :composeApp:allTests :composeApp:wasmJsBrowserTest`; `git diff --check`.

## Phase 4 — Local acceptance

### COURSE-006 — Local end-to-end Course acceptance

- Deliverable: deterministic local proof joining content checker, importer
  projection, focused API behavior, shared mapping and Compose reader.
- Depends on: COURSE-003, COURSE-005. Parallel group: `course-acceptance`.
- Spec refs: AC-COURSE-01..07; verification and stop condition.
- Verification: `python3 docs/algorithm/course-materials/checks/verify.py`; `python3 tools/test_course_import.py`; `python3 tools/course_import.py --validate`; `cd services/api && ./mvnw -q test -Dtest='*CourseTest,*CourseControllerTest,*CourseMigrationTest'`; `cd apps/multiplatform && ./gradlew :shared:allTests :composeApp:allTests :composeApp:wasmJsBrowserTest`; `git diff --check`.
- Status: ready.
