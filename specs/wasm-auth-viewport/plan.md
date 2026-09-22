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
- `apps/multiplatform/composeApp/build/firebaseHosting`: receives the rebuilt
  production bundle that Firebase Hosting publishes.
- `firebase.json` and `.firebaserc`: remain the authoritative existing Hosting
  configuration and project selection; this release does not modify either.

## Verification strategy

WAV-001 already proved the focused viewport-contract task and client suite.
WAV-002 rebuilds the production bundle with the public same-origin API default,
deploys only Hosting, then fetches the public root document to confirm the
full-height stylesheet is live.

## Dependencies and rollout

This is a Web-only correction followed by a bounded Hosting release. Firebase
Hosting deploys the existing static directory and rewrite configuration; Cloud
Run, API configuration, credentials, and browser authentication exercises are
outside this task.
