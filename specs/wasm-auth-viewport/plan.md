# Wasm Authentication Viewport Plan

## Architecture

The Wasm page contains the Compose canvas host. CSS in the entry document makes
the document root, body, and `#composeTarget` occupy the browser viewport, so
the existing `ComposeViewport` call can measure the intended height.

## Files and responsibilities

- `apps/multiplatform/composeApp/src/wasmJsMain/resources/index.html`: declares
  the full-height host layout.
- `apps/multiplatform/composeApp/build.gradle.kts`: provides a deterministic
  verification task for the entry-document contract.

## Verification strategy

Run the focused viewport-contract task, then the existing Compose client test
suite and whitespace check. The focused task must fail before the CSS change and
pass afterwards.

## Dependencies and rollout

This is a standalone Web-only corrective patch. A new Firebase Hosting deploy
will publish the built HTML; deployment itself is outside this repository task.
