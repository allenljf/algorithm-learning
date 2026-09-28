# Wasm Resize Feedback Recovery Plan

## Architecture

The entry document records the initial CSS-pixel dimensions of `#composeTarget`. Its `ResizeObserver` ignores a callback whose dimensions match the last recorded pair. Only a changed pair is recorded and forwarded through the existing browser `resize` path used by ComposeViewport.

## Files and responsibilities

- `apps/multiplatform/composeApp/src/wasmJsMain/resources/index.html`: guarded host-resize bridge.
- `apps/multiplatform/composeApp/build.gradle.kts`: source contract guard.

## Verification strategy

Per the user's request, do not run test suites. Build the production Wasm bundle, deploy only Hosting, fetch its root document for the guarded bridge and production origin, then check the scoped diff.

## Dependencies and rollout

One independent Web-only task. The release uses the existing Hosting target and does not alter its configuration.
