# Wasm Authentication Viewport Tasks

| ID | Task | Depends on | Parallel group | Spec references | Verification | Status |
|---|---|---|---|---|---|---|
| WAV-001 | Restore full-height Wasm authentication viewport | none | wasm-auth-viewport | 1, AC-WAV-01..03, 4 | `cd apps/multiplatform && ./gradlew :composeApp:verifyWasmViewport`; `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `git diff --check` | completed |

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
