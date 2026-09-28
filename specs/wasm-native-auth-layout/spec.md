# Wasm Native Authentication Layout Repair Specification

## Goal

Restore the public Web login and registration form so its browser-native email
and password controls align with their Compose placeholders on desktop and
mobile displays, while retaining the native-input path required for mobile
software keyboards.

## Incident evidence

The published page reproduces the fault at a 1280-by-720 CSS-pixel viewport
with `window.devicePixelRatio = 2`: the email field is measured at
`left=860`, `top=536`, `width=840`, and `height=112` CSS pixels, so it extends
beyond the right edge. The Compose layout coordinates were written directly to
CSS properties even though the Wasm canvas uses device pixels. The observed
values are exactly twice the intended CSS geometry.

## Scope and non-goals

In scope: Web-only conversion from Compose device-pixel layout coordinates to
CSS pixels for the native email field, password field, and password visibility
button; automated regression coverage at a high-DPI viewport; production Wasm
build and publication to the existing Firebase Hosting target.

Out of scope: Android or iOS UI changes, authentication APIs, form state,
validation, localization, input semantics, Firebase configuration, Cloud Run,
secrets, and visual redesign.

## Acceptance criteria

- **AC-WNL-01:** At device-pixel ratios greater than one, native credential
  fields and the password action use CSS-pixel geometry and remain fully inside
  the viewport when their Compose placeholders are inside it.
- **AC-WNL-02:** At a device-pixel ratio of one, the same conversion preserves
  the existing field geometry.
- **AC-WNL-03:** Both login and registration retain native `email` and
  `password` controls, current autocomplete behavior, value synchronization,
  password visibility, and direct user focus behavior.
- **AC-WNL-04:** A deterministic Wasm browser test fails for the prior
  unconverted geometry and passes for the corrected geometry.
- **AC-WNL-05:** The published Firebase Hosting page serves the verified
  correction without modifying its Hosting configuration or any backend
  deployment.

## Technical constraints

- Keep browser DOM APIs in `wasmJsMain`; `commonMain` remains platform-free.
- Convert only the DOM overlay geometry. Do not scale the Compose canvas or
  modify the viewport host.
- Keep all user-visible wording in `AppStrings`; the repair must not introduce
  literals into browser scripts.
- Do not log, persist, expose, or test with real credentials.
- Publication is limited to the existing `allenljf-algorithm` Firebase Hosting
  target and uses no UI operation.

## Decisions and assumptions

- Documentation decision: `update-docs`.
- Ambiguity decision: `infer`. The direct high-DPI measurement establishes the
  conversion boundary, so a CSS-pixel conversion at the existing DOM overlay
  seam is safer and narrower than replacing the native-input approach.
- The prior explicit Firebase Hosting publication authorization remains limited
  to the existing `allenljf-algorithm` target for this public regression repair.
