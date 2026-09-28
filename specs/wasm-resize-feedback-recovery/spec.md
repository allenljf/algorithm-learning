# Wasm Resize Feedback Recovery Specification

## Goal

Restore a stable Compose Web sign-in surface by preventing browser overflow and
the host-resize bridge from repeatedly re-triggering Compose layout.

## Scope and non-goals

In scope: the Wasm entry document's overflow and `ResizeObserver` boundary, its
source guard, a deterministic browser reproduction check, a production Wasm
build, and publication to the existing Firebase Hosting target.

Out of scope: shared Compose UI, authentication semantics, native browser inputs, API and Firebase configuration changes, and test-suite execution.

## User-facing behavior and acceptance criteria

- **AC-WRF-01:** Opening the public Web URL does not emit a synthetic resize for the host's initial unchanged size.
- **AC-WRF-02:** A real `#composeTarget` size change still notifies Compose exactly once for that distinct size.
- **AC-WRF-03:** The published public page contains the guarded resize bridge and production API origin.
- **AC-WRF-04:** At a fixed browser viewport, repeated samples of
  `#composeTarget` retain one width-height pair; page overflow must not create a
  scrollbar-width feedback loop.

## Technical constraints

- Keep the correction in `wasmJsMain/resources/index.html`.
- Preserve the full-height host, native-input CSS, and the `ResizeObserver`-based browser-chrome responsiveness path.
- Do not change Firebase Hosting configuration or deploy backend resources.
- Preserve the already-uncommitted absolute `/composeApp.js` asset path; it is
  owned by the separate #371 reload repair.

## Decisions and assumptions

- Documentation: `update-docs`.
- Ambiguity: `infer`. The original distinct-dimension guard prevents duplicate
  observer entries but cannot reject a two-size cycle caused by document
  overflow. Constrain the Wasm host's overflow so a Compose child cannot create
  a document scrollbar and feed its width back into the observer. Retain the
  existing distinct-dimension guard for genuine browser-chrome changes.
- Incident evidence (public site, 2026-09-28): over 30 samples at one fixed
  viewport, `#composeTarget` alternated 10–13 times between `613x889` and
  `628x904`. The host itself was `613x889`; its normal-flow Compose child was
  `613x909.5`, while the document scroll size was `628x910`. This confirms the
  scrollbar-width feedback path.
