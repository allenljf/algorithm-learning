# Wasm Resize Feedback Recovery Tasks

## Phase 1 — Repair and publish

| ID | Task | Depends on | Parallel group | Spec references | Verification | Status |
|---|---|---|---|---|---|---|
| WRF-001 | Guard Wasm host resize forwarding and publish | none | wasm-resize-feedback-recovery | Goal; AC-WRF-01..03; Technical constraints; Decisions and assumptions | `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://allenljf-algorithm.web.app`; `npx firebase-tools deploy --only hosting --project allenljf-algorithm`; `curl --fail --silent --show-error https://allenljf-algorithm.web.app | rg -F 'let observedWidth = composeTarget.getBoundingClientRect().width'`; `git diff --check` | completed |
| WRF-002 | Stop overflow-driven Web resize oscillation and publish | WRF-001 | wasm-resize-feedback-recovery | Goal; AC-WRF-01..04; Technical constraints; Decisions and assumptions | `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://allenljf-algorithm.web.app`; `npx firebase-tools deploy --only hosting --project allenljf-algorithm`; `curl --fail --silent --show-error https://allenljf-algorithm.web.app | rg -F 'overflow: hidden'`; fixed-viewport public `#composeTarget` sampling; `git diff --check` | completed |

### WRF-001 — Guard Wasm host resize forwarding and publish

- Deliverable: record the initial host size, forward only distinct size observations, update the source guard, and publish the rebuilt Web bundle.
- Execution contract: `single-agent` / `rapid` / `update-docs` / `infer`.
- Status: completed.

### WRF-002 — Stop overflow-driven Web resize oscillation and publish

- Deliverable: contain Compose child overflow at the Web host while retaining
  the existing distinct-size observer guard, build and publish the Wasm bundle,
  then prove the public host remains one size at a fixed viewport.
- Execution contract: `single-agent` / `rapid` / `update-docs` / `infer`.
- Status: completed.
