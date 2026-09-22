# Wasm Authentication Viewport Specification

## Goal

Ensure the public Wasm Web client uses the full browser viewport so its
authentication form remains visible and editable, then publish that verified
correction to the public Firebase Hosting site.

## Scope and non-goals

In scope: the Wasm HTML entry document, a deterministic check for its viewport
contract, the resulting public login/register layout, and a Firebase Hosting
release of the verified production Wasm bundle.

Out of scope: authentication behavior, API routes, Compose form semantics,
visual redesign, Android/iOS layout changes, and any Cloud Run or Firebase
configuration change.

## User-facing behavior and acceptance criteria

- **AC-WAV-01:** Opening the public Web URL gives the Compose root the browser
  viewport height; the sign-in form is not clipped after the Email field.
- **AC-WAV-02:** Typed email text is visible in its input field, with the
  password field and actions remaining reachable in the same form.
- **AC-WAV-03:** A local deterministic verification fails if the Wasm entry
  document stops declaring its full-height viewport contract.
- **AC-WAV-04:** The public `https://allenljf-algorithm.web.app` site serves
  the rebuilt production bundle containing the full-height viewport contract.
- **AC-WAV-05:** Changing the browser content area's dimensions after startup
  (including Chrome's vertical-tab layout) causes the Compose viewport to
  remeasure, so the complete sign-in form remains visible without manually
  resizing the browser window.

## Technical constraints

- Keep the shared Compose UI and frozen backend/API contract unchanged.
- Apply the layout rule in the Wasm entry document so `ComposeViewport` receives
  a full-height host element.
- Preserve the existing `composeTarget` id and generated API-base-url script.
- Observe the host element's dimensions and notify Compose through its existing
  browser-resize path; do not change shared UI layout or browser chrome.
- Deploy only the checked-in Firebase Hosting configuration to the existing
  `allenljf-algorithm` project; do not alter Cloud Run, secrets, or hosting
  rewrites.

## Decisions and assumptions

- Documentation decision: `update-docs`.
- Ambiguity decision: `infer`. The verified production DOM shows `html`, `body`,
  and `#composeTarget` are content-height rather than viewport-height; setting
  all three to the viewport contract is the smallest root-cause fix.
- Release assumption: the authenticated local Firebase CLI retains deployment
  access to the existing `allenljf-algorithm` Hosting site. Rebuilding from the
  committed viewport fix before deployment prevents stale build output from
  being released.
- Responsiveness assumption: Chrome's vertical-tab transition changes the host
  element's box after Compose starts without reliably giving the Compose canvas
  a useful initial browser resize. A `ResizeObserver` on `composeTarget` is the
  narrowest browser-native signal for that change.
