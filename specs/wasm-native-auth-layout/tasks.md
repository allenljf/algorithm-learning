# Wasm Native Authentication Layout Repair Tasks

## Phase 1 — Repair and publish

| ID | Task | Depends on | Parallel group | Spec references | Verification | Status |
|---|---|---|---|---|---|---|
| WNL-001 | Repair and publish high-DPI native authentication layout | none | wasm-native-auth-layout | Goal; Incident evidence; AC-WNL-01..05; Technical constraints; Decisions and assumptions | `cd apps/multiplatform && ./gradlew :composeApp:verifyWasmNativeAuthInputs :composeApp:allTests :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://allenljf-algorithm.web.app`; `npx firebase-tools deploy --only hosting --project allenljf-algorithm`; `curl --fail --silent --show-error https://allenljf-algorithm.web.app | rg -F 'native-auth-input {'`; `git diff --check` | completed |

### WNL-001 — Repair and publish high-DPI native authentication layout

- Deliverable: a Web-only CSS-pixel conversion for each native-auth overlay,
  a high-DPI regression test and guard, and a verified bundle published to the
  existing Hosting target.
- Depends on: none.
- Parallel group: `wasm-native-auth-layout`.
- Spec refs: Goal; Incident evidence; AC-WNL-01..05; Technical constraints;
  Decisions and assumptions.
- Execution contract: `single-agent` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd apps/multiplatform && ./gradlew
  :composeApp:verifyWasmNativeAuthInputs :composeApp:allTests
  :composeApp:wasmJsBrowserProductionWebpack
  -PapiBaseUrl=https://allenljf-algorithm.web.app`; `npx firebase-tools deploy
  --only hosting --project allenljf-algorithm`; `curl --fail --silent
  --show-error https://allenljf-algorithm.web.app | rg -F 'native-auth-input {'`;
  `git diff --check`.
- Status: completed.
