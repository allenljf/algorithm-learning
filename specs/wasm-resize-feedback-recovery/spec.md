# Wasm Resize Feedback Recovery Specification

## Goal

Restore a stable, dark Compose Web sign-in surface by preventing the browser-host resize bridge from repeatedly re-triggering Compose layout.

## Scope and non-goals

In scope: the Wasm entry document's `ResizeObserver` bridge, its source guard, a production Wasm build, and publication to the existing Firebase Hosting target.

Out of scope: shared Compose UI, authentication semantics, native browser inputs, API and Firebase configuration changes, and test-suite execution.

## User-facing behavior and acceptance criteria

- **AC-WRF-01:** Opening the public Web URL does not emit a synthetic resize for the host's initial unchanged size.
- **AC-WRF-02:** A real `#composeTarget` size change still notifies Compose exactly once for that distinct size.
- **AC-WRF-03:** The published public page contains the guarded resize bridge and production API origin.

## Technical constraints

- Keep the correction in `wasmJsMain/resources/index.html`.
- Preserve the full-height host, native-input CSS, and the `ResizeObserver`-based browser-chrome responsiveness path.
- Do not change Firebase Hosting configuration or deploy backend resources.

## Decisions and assumptions

- Documentation: `update-docs`.
- Ambiguity: `infer`. Cache the initial host dimensions and dispatch only after a distinct observed width or height, which breaks the resize feedback path while retaining actual browser-chrome resize handling.
- User explicitly requested direct repair and Hosting publication without running a test suite; the task uses `rapid` and limits verification to the production build, Hosting deployment, public-source readback, and diff check.
