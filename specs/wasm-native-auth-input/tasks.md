# Wasm Native Authentication Input Tasks

| ID | Task | Depends on | Parallel group | Spec references | Verification | Status |
|---|---|---|---|---|---|---|
| WNA-001 | Replace Wasm canvas credential fields with native browser inputs | none | wasm-native-auth-input | Goal; AC-WNA-01..06; constraints; decisions | `cd apps/multiplatform && ./gradlew :composeApp:verifyWasmNativeAuthInputs :composeApp:allTests :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://allenljf-algorithm.web.app`; `git diff --check` | completed |

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
