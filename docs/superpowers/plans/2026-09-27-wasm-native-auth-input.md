# Wasm Native Authentication Input Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make mobile Web login and registration reliably invoke the software keyboard through native browser inputs.

**Architecture:** Extract the existing shared Compose fields behind a platform presentation seam. Android and iOS delegate to the retained Compose implementation; Wasm creates and positions native DOM controls over its Compose placeholders and synchronizes them with the unchanged `AuthViewModel` callbacks.

**Tech Stack:** Kotlin 2.4.10, Compose Multiplatform 1.11.1, Kotlin/Wasm browser DOM APIs, Compose UI browser tests.

**Spec:** `specs/wasm-native-auth-input/spec.md`; `docs/superpowers/specs/2026-09-27-wasm-native-auth-input-design.md`

## Global Constraints

- Keep shared domain, repository, API, and `AuthViewModel` contracts unchanged.
- Browser DOM APIs stay in `composeApp/src/wasmJsMain`; `commonMain` remains platform-free.
- Native inputs receive direct user taps; never defer their focus programmatically.
- All visible copy continues to come from `AppStrings`.
- Do not persist, log, or place real credentials in tests.
- Preserve `ComposeViewport` full-height CSS and its `ResizeObserver` bridge.

## Review Focus

- Register mode changes autocomplete to `new-password` while login uses `current-password`; test this in the Wasm DOM check.
- Recomposition from an `input` event must not reset a still-focused native input; test value retention after a callback update.
- Disabling while submission is in progress must make both native inputs non-editable; test the DOM `disabled` property.
- Toggling password visibility must retain both value and focus; test `type` changes only.
- Removing the authentication screen after a successful session must remove its DOM inputs; test cleanup on composition disposal.

---

### Task 1: Platform authentication-input seam

**Files:**
- Create: `apps/multiplatform/composeApp/src/commonMain/kotlin/com/algorithmlearning/app/auth/PlatformAuthFields.kt`
- Create: `apps/multiplatform/composeApp/src/androidMain/kotlin/com/algorithmlearning/app/auth/PlatformAuthFields.android.kt`
- Create: `apps/multiplatform/composeApp/src/iosMain/kotlin/com/algorithmlearning/app/auth/PlatformAuthFields.ios.kt`
- Create: `apps/multiplatform/composeApp/src/wasmJsMain/kotlin/com/algorithmlearning/app/auth/PlatformAuthFields.wasmJs.kt`
- Modify: `apps/multiplatform/composeApp/src/commonMain/kotlin/com/algorithmlearning/app/auth/AuthScreen.kt`
- Test: `apps/multiplatform/composeApp/src/wasmJsTest/kotlin/com/algorithmlearning/app/NativeAuthFieldsUiTest.kt`

**Interfaces:**
- Consumes: `AuthFormState`, `AppStrings`, `onEmailChange: (String) -> Unit`, `onPasswordChange: (String) -> Unit`.
- Produces: `@Composable expect fun PlatformAuthFields(...)`, with platform actuals that preserve the form-field rendering contract.

- [x] **Step 1: Write failing Wasm tests for native email/password controls**

Assert that composing login produces `input[type=email][autocomplete=username]` and `input[type=password][autocomplete=current-password]`; assert registration uses `new-password`, submission sets `disabled`, and input events invoke the supplied callbacks.

- [x] **Step 2: Run the focused Wasm test to verify it fails**

Run: `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserTest --tests '*NativeAuthFieldsUiTest*'`

Expected: FAIL because no native controls or DOM-test helper exists.

- [x] **Step 3: Add the `PlatformAuthFields` expect/actual boundary and retain the Compose implementation for Android/iOS**

Move the existing Compose `OutlinedTextField` behavior into the common helper called by Android/iOS actuals. The helper keeps its existing test tags, localized labels, password visibility state, and disabled behavior.

- [x] **Step 4: Implement the Wasm native input actual**

Create native `HTMLInputElement` controls in `#composeTarget`, subscribe to `input` events, and synchronize them from form state without replacing the active element. Position them from Compose layout coordinates, provide semantic labels and autocomplete, update `type` only for masking, and remove listeners/elements in `DisposableEffect`.

- [x] **Step 5: Run the focused test to verify it passes**

Run: `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserTest --tests '*NativeAuthFieldsUiTest*'`

Expected: PASS.

- [x] **Step 6: Commit the platform seam**

```bash
git add apps/multiplatform/composeApp/src
git commit -m "fix(web): use native auth inputs on Wasm"
```

### Task 2: Web production guard and workflow closeout

**Files:**
- Modify: `apps/multiplatform/composeApp/build.gradle.kts`
- Modify: `specs/wasm-native-auth-input/plan.md`
- Modify: `specs/wasm-native-auth-input/tasks.md`
- Modify: `agent-workflow/WORK_GRAPH.yaml`
- Modify: `agent-workflow/WORK_GRAPH.md`
- Modify: `agent-workflow/progress.md`

**Interfaces:**
- Consumes: the `data-auth-native-input` marker emitted by the Wasm actual.
- Produces: `verifyWasmNativeAuthInputs`, a deterministic guard task that fails when the native-input implementation marker is absent.

- [x] **Step 1: Write the failing production-guard assertion**

Extend the existing Web verification task so its source check requires the native input marker and both `email` and `password` control creation.

- [x] **Step 2: Run the guard to verify it fails before the marker is supplied**

Run: `cd apps/multiplatform && ./gradlew :composeApp:verifyWasmNativeAuthInputs`

Expected: FAIL with an explicit missing-native-input-contract message.

- [x] **Step 3: Add the `verifyWasmNativeAuthInputs` Gradle task**

Keep it source-based, deterministic, and scoped to `PlatformAuthFields.wasmJs.kt`; do not add network access or a device dependency.

- [x] **Step 4: Run the guard and task-defined verification**

Run: `cd apps/multiplatform && ./gradlew :composeApp:verifyWasmNativeAuthInputs :composeApp:allTests :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://allenljf-algorithm.web.app`

Expected: PASS, with the normal existing toolchain warnings only.

- [x] **Step 5: Close out the task and commit workflow evidence**

```bash
git add specs/wasm-native-auth-input agent-workflow apps/multiplatform/composeApp/build.gradle.kts
git commit -m "docs: close native Web auth input task"
```
