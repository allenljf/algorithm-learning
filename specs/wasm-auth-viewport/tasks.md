# Wasm Authentication Viewport Tasks

| ID | Task | Depends on | Parallel group | Spec references | Verification | Status |
|---|---|---|---|---|---|---|
| WAV-001 | Restore full-height Wasm authentication viewport | none | wasm-auth-viewport | 1, AC-WAV-01..03, 4 | `cd apps/multiplatform && ./gradlew :composeApp:verifyWasmViewport`; `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `git diff --check` | completed |
| WAV-002 | Publish the verified Wasm viewport correction | WAV-001 | wasm-auth-viewport-release | Goal, AC-WAV-04, constraints, decisions | `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://allenljf-algorithm.web.app`; `npx firebase-tools deploy --only hosting --project allenljf-algorithm`; `curl --fail --silent --show-error https://allenljf-algorithm.web.app | rg -F 'height: 100%'`; `git diff --check` | completed |

## Phase 1 — Web viewport correction

### WAV-001 — Restore full-height Wasm authentication viewport

- Deliverable: viewport CSS on the Web entry document and a regression check
  that guards the `html`/`body`/`composeTarget` sizing contract.
- Depends on: none.
- Parallel group: `wasm-auth-viewport`.
- Spec refs: 1, AC-WAV-01..03, 4.
- Execution contract: `single-agent` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:verifyWasmViewport`;
  `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `git diff --check`.
- Status: completed.

## Phase 2 — Public Hosting publication

### WAV-002 — Publish the verified Wasm viewport correction

- Deliverable: a freshly rebuilt production Wasm bundle deployed to the existing
  `allenljf-algorithm` Firebase Hosting site, with public-source evidence that
  the full-height viewport stylesheet is served.
- Depends on: WAV-001.
- Parallel group: `wasm-auth-viewport-release`.
- Spec refs: Goal, AC-WAV-04, technical constraints, decisions and assumptions.
- Execution contract: `single-agent` analysis; `rapid` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew
  :composeApp:wasmJsBrowserProductionWebpack
  -PapiBaseUrl=https://allenljf-algorithm.web.app`; `npx firebase-tools deploy
  --only hosting --project allenljf-algorithm`; `curl --fail --silent
  --show-error https://allenljf-algorithm.web.app | rg -F 'height: 100%'`; and
  `git diff --check`.
- Status: completed.
