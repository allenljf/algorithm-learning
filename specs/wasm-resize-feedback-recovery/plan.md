# Wasm Resize Feedback Recovery Plan

## Architecture

The entry document retains its distinct-size `ResizeObserver` guard and makes
`#composeTarget` the overflow boundary. A taller normal-flow Compose child can
then no longer create a document scrollbar, so its scrollbar-width change
cannot be forwarded back through the existing browser `resize` path.

## Files and responsibilities

- `apps/multiplatform/composeApp/src/wasmJsMain/resources/index.html`: guarded
  host-resize bridge and overflow boundary.

## Verification strategy

Per the user's request, do not run test suites. Build the production Wasm
bundle, deploy only Hosting, fetch its root document for the guarded bridge,
overflow boundary, and production origin, then repeat the public fixed-viewport
measurement that previously reproduced the size oscillation.

## Dependencies and rollout

`WRF-002` depends on the completed initial guard and is a single Web-only task.
The release uses the existing Hosting target and does not alter its configuration.
