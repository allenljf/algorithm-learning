# Compose Multiplatform Migration — Task List

All task IDs, dependencies, statuses, and exact verification commands are
authoritative in `agent-workflow/WORK_GRAPH.yaml`. This document is the
human-readable execution contract.

## Phase 1 — Foundation

### CMP-001 — Establish the Compose Multiplatform project and engineering guide

- Deliverable: `apps/multiplatform` Gradle/Kotlin DSL root with `composeApp` and
  `shared` modules, Android + Web (Wasm) targets, a version catalog, theme,
  navigation, localization, DI composition root, and a smoke test; plus a
  Compose-specific engineering guide covering architecture, DI, data, and testing.
- Depends on: none
- Parallel group: `cmp-foundation`
- Spec refs: 2, 3, 4 AC-CMP-01/02, 5-7
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew :shared:allTests`; `cd apps/multiplatform && ./gradlew :composeApp:assembleDebug`; `test -f apps/multiplatform/COMPOSE_GUIDE.md`; `git diff --check`
- Status: completed

## Phase 2 — Data

### CMP-002 — Implement shared auth/session data flow

- Deliverable: Ktor-based auth remote contract, auth/session repository, in-memory
  access token, single-flight refresh, and session state holder in `commonMain`,
  with fakes and provider/DAC seams.
- Depends on: CMP-001, ALG-005
- Parallel group: `cmp-data`
- Spec refs: 3, 4 AC-CMP-03, 5-6
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew :shared:allTests`; `git diff --check`
- Status: completed

### CMP-003 — Implement shared problem-library, review, and dashboard data flows

- Deliverable: typed remote contracts and repositories for problems, tags,
  solutions, reviews, and dashboard in `commonMain`, with immutable domain models
  and no DTO leakage.
- Depends on: CMP-001, CMP-002, ALG-007, ALG-008, ALG-009, ALG-010
- Parallel group: `cmp-data`
- Spec refs: 3, 4 AC-CMP-04..06, 5-6
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew :shared:allTests`; `git diff --check`
- Status: completed

## Phase 3 — Experiences

### CMP-004 — Build auth and problem management UI

- Deliverable: Compose screens for register/login/session and problem
  list/search/filter/create/edit/delete with solutions and tags, matching ALG-013.
- Depends on: CMP-002, CMP-003
- Parallel group: `cmp-experiences`
- Spec refs: 3, 4 AC-CMP-03/04, 5-6
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `git diff --check`
- Status: ready

### CMP-005 — Build adaptive browse and staged Review Mode UI

- Deliverable: adaptive browse and staged Problem → Think → Hint → My Approach →
  Solution → confidence disclosure with submit gating, matching ALG-014.
- Depends on: CMP-002, CMP-003
- Parallel group: `cmp-experiences`
- Spec refs: 3, 4 AC-CMP-05, 5-6
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `git diff --check`
- Status: ready

### CMP-006 — Build dashboard and home UI

- Deliverable: dashboard/home states for loading, retry, empty, totals,
  difficulty distribution, and due-review count, matching ALG-015.
- Depends on: CMP-002, CMP-003
- Parallel group: `cmp-experiences`
- Spec refs: 3, 4 AC-CMP-06, 5-6
- Execution contract: `three-perspectives` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `git diff --check`
- Status: ready

## Phase 4 — Release

### CMP-007 — Run cross-platform acceptance for the Compose client

- Deliverable: an acceptance suite and evidence that exercises the four
  experiences against a controlled API adapter on Android and Web.
- Depends on: CMP-004, CMP-005, CMP-006
- Parallel group: `cmp-release`
- Spec refs: 4 AC-CMP-01..06
- Execution contract: `three-perspectives` analysis; `test-candidates` test
  approach; `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:assembleDebug`; `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `git diff --check`
- Status: pending

### CMP-008 — Retire the Flutter client and record migration notes

- Deliverable: remove or explicitly archive `apps/learning_app`, update entry
  READMEs and the requirement baseline, and record the parity/migration notes.
- Depends on: CMP-007
- Parallel group: `cmp-release`
- Spec refs: 4 AC-CMP-07, 7
- Execution contract: `single-agent` analysis; `rapid` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `test ! -d apps/learning_app || test -f apps/learning_app/ARCHIVED.md`; `git diff --check`
- Status: pending
