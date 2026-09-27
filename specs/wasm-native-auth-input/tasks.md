# Wasm Native Authentication Input Tasks

| ID | Task | Depends on | Parallel group | Spec references | Verification | Status |
|---|---|---|---|---|---|---|
| WNA-001 | Replace Wasm canvas credential fields with native browser inputs | none | wasm-native-auth-input | Goal; AC-WNA-01..06; constraints; decisions | `cd apps/multiplatform && ./gradlew :composeApp:verifyWasmNativeAuthInputs :composeApp:allTests :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://allenljf-algorithm.web.app`; `git diff --check` | completed |
| WNA-002 | Publish verified native Web authentication inputs | WNA-001 | wasm-native-auth-input-release | AC-WNA-07; Technical constraints; decisions | `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://allenljf-algorithm.web.app`; `npx firebase-tools deploy --only hosting --project allenljf-algorithm`; `curl --fail --silent --show-error https://allenljf-algorithm.web.app | rg -F 'native-auth-input {'`; `git diff --check` | completed |

## Phase 1 — Native Web authentication inputs

### WNA-001 — Replace Wasm canvas credential fields with native browser inputs

- Deliverable: Web-only native email/password controls synchronized with the
  existing auth form, platform-preserved Compose fields, regression coverage,
  and a deterministic native-input guard.
- Depends on: none.
- Parallel group: `wasm-native-auth-input`.
- Spec refs: Goal; Scope and non-goals; AC-WNA-01..06; Technical constraints;
  Decisions and assumptions.
- Execution contract: `single-agent` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew
  :composeApp:verifyWasmNativeAuthInputs :composeApp:allTests
  :composeApp:wasmJsBrowserProductionWebpack
  -PapiBaseUrl=https://allenljf-algorithm.web.app`; `git diff --check`.
- Status: completed.

## Phase 2 — Firebase Hosting publication

### WNA-002 — Publish verified native Web authentication inputs

- Deliverable: Fresh production Wasm bundle deployed to the existing Firebase
  Hosting target, with public root-document evidence of the native-input
  contract.
- Depends on: WNA-001.
- Parallel group: `wasm-native-auth-input-release`.
- Spec refs: AC-WNA-07; Technical constraints; Decisions and assumptions.
- Execution contract: `single-agent` analysis; `rapid` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew
  :composeApp:wasmJsBrowserProductionWebpack
  -PapiBaseUrl=https://allenljf-algorithm.web.app`; `npx firebase-tools deploy
  --only hosting --project allenljf-algorithm`; `curl --fail --silent
  --show-error https://allenljf-algorithm.web.app | rg -F 'native-auth-input {'`;
  `git diff --check`.
- Status: completed.
