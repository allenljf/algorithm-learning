# Wasm Authentication Viewport Specification

## Goal

Ensure the public Wasm Web client uses the full browser viewport so its
authentication form remains visible and editable.

## Scope and non-goals

In scope: the Wasm HTML entry document, a deterministic check for its viewport
contract, and the resulting public login/register layout.

Out of scope: authentication behavior, API routes, Compose form semantics,
visual redesign, and Android/iOS layout changes.

## User-facing behavior and acceptance criteria

- **AC-WAV-01:** Opening the public Web URL gives the Compose root the browser
  viewport height; the sign-in form is not clipped after the Email field.
- **AC-WAV-02:** Typed email text is visible in its input field, with the
  password field and actions remaining reachable in the same form.
- **AC-WAV-03:** A local deterministic verification fails if the Wasm entry
  document stops declaring its full-height viewport contract.

## Technical constraints

- Keep the shared Compose UI and frozen backend/API contract unchanged.
- Apply the layout rule in the Wasm entry document so `ComposeViewport` receives
  a full-height host element.
- Preserve the existing `composeTarget` id and generated API-base-url script.

## Decisions and assumptions

- Documentation decision: `update-docs`.
- Ambiguity decision: `infer`. The verified production DOM shows `html`, `body`,
  and `#composeTarget` are content-height rather than viewport-height; setting
  all three to the viewport contract is the smallest root-cause fix.
