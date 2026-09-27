# Wasm Native Authentication Input Plan

## Architecture

`AuthScreen` keeps ownership of shared form state and actions but delegates its
two credential fields to a platform presentation seam. Android and iOS retain
the existing Compose controls. The Wasm actual creates accessible native DOM
inputs positioned over Compose layout placeholders and synchronizes their values
with the existing callbacks during the direct browser input event.

## Files and responsibilities

- `commonMain/.../auth/PlatformAuthFields.kt`: the platform seam and shared
  Compose field implementation.
- `androidMain` and `iosMain` actuals: retain the Compose presentation.
- `wasmJsMain/.../auth/PlatformAuthFields.wasmJs.kt`: native DOM lifecycle,
  direct input events, accessibility, masking, and cleanup.
- `AuthScreen.kt`: uses the seam without changing view-model or API behavior.
- `wasmJsTest/.../NativeAuthFieldsUiTest.kt`: verifies the native field contract
  and synchronization behavior.
- `composeApp/build.gradle.kts`: deterministic native-Web-input source guard.

## Verification strategy

The regression test runs in the Wasm browser target, asserts native input
semantics and callback propagation, and proves visibility/disabled behavior.
The task additionally runs the project client suite, deterministic Web guard,
and production Wasm webpack build using the public origin. Hosting deployment
is intentionally excluded; publishing is an external operation not requested
by this task.

## Dependencies and rollout

WNA-001 is a single implementation and verification task. The change is
Web-only and preserves the existing viewport contract, native mobile targets,
and backend contract.

When publication is explicitly requested, WNA-002 rebuilds with the canonical
public API origin, deploys only the existing Firebase Hosting target, and
confirms the public document includes the native authentication-input CSS
contract. It makes no code or infrastructure configuration change.
