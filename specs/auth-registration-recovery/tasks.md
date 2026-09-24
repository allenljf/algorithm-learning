# Authentication Registration Recovery Tasks

| ID | Task | Depends on | Parallel group | Spec references | Verification | Status |
|---|---|---|---|---|---|---|
| ARR-001 | Diagnose, repair, and release public registration | none | auth-registration-recovery | Goal; Evidence; AC-ARR-01..05; constraints; decisions | `cd apps/multiplatform && ./gradlew :composeApp:allTests`; production Wasm build; Firebase Hosting release; `git diff --check` | completed |

## Phase 1 — Registration recovery

### ARR-001 — Diagnose, repair, and release public registration

- Deliverable: redacted correlated server-side failure logging, a confirmed
  underlying registration correction with regression coverage, and an API
  release verified through the public Firebase origin. The reopened delivery
  additionally provides a localized pre-submit password-length message and a
  published Wasm bundle.
- Depends on: none.
- Parallel group: `auth-registration-recovery`.
- Spec refs: Goal; Evidence and root-cause investigation; AC-ARR-01..05;
  Technical constraints; Decisions and assumptions.
- Execution contract: `single-agent` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `cd
  apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserProductionWebpack
  -PapiBaseUrl=https://allenljf-algorithm.web.app`; `npx firebase-tools deploy
  --only hosting --project allenljf-algorithm`; `git diff --check`.
- Status: completed.
