# Wasm Native Authentication Layout Repair Plan

## Architecture

Keep the existing native DOM-overlay seam. Its Compose layout coordinates are
device pixels, while `HTMLElement.style` is expressed in CSS pixels, so the
Wasm adapter will convert every overlay position and dimension with the current
device-pixel ratio before writing it to the DOM.

## Files and responsibilities

- `composeApp/src/wasmJsMain/.../PlatformAuthFields.wasmJs.kt`: owns the
  device-pixel-to-CSS-pixel conversion and applies it to both inputs and the
  password action.
- `composeApp/src/wasmJsTest/.../NativeAuthFieldsUiTest.kt`: proves the helper
  preserves DPR 1 geometry and halves the prior high-DPI values.
- `composeApp/build.gradle.kts`: extends the deterministic source guard so a
  future direct device-pixel CSS assignment is rejected.

## Verification strategy

The focused regression test establishes the high-DPI conversion. The task then
runs the existing native-input guard, all client tests, and the production Wasm
build. The same completed task publishes only the verified bundle to existing
Firebase Hosting and reads the public document back. A final read-only browser
measurement at DPR 2 confirms both native inputs are inside the viewport.

## Dependencies and rollout

`WNL-001` is one sequential Web-only repair-and-release task. It preserves the
already-published native input architecture and only corrects coordinate units.
