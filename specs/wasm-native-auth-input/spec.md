# Wasm Native Authentication Input Specification

## Goal

Make the public Web authentication form reliably invoke a mobile browser's
software keyboard by using browser-native email and password controls while
preserving the existing authentication state, validation, localization, and API
contract.

## Scope and non-goals

In scope: a Web-only native-input presentation layer for the login and
registration email/password fields, bidirectional synchronization with the
existing `AuthViewModel`, localized labels and errors, password visibility,
deterministic Web verification of focusable native controls, and publication
to the existing Firebase Hosting target when explicitly requested.

Out of scope: changes to Android or iOS presentation, authentication routes,
request/response formats, password policy, endpoint configuration, account
management, or a visual redesign of the authenticated application.

## User-facing behavior and acceptance criteria

- **AC-WNA-01:** On a mobile browser, tapping the email or password field opens
  the browser's software keyboard and accepts typed text.
- **AC-WNA-02:** The Web controls expose native `email` and `password` input
  semantics, with appropriate autocomplete hints, labels, and accessible names.
- **AC-WNA-03:** The typed values update the existing `AuthViewModel`; submit,
  server errors, local password-length validation, and mode switching retain
  their current behavior.
- **AC-WNA-04:** The password visibility action changes only the password
  control's masking, without losing its value or focus.
- **AC-WNA-05:** Android and iOS retain the current shared Compose fields and
  do not consume Web DOM APIs.
- **AC-WNA-06:** A deterministic Web check fails if the authentication form
  stops supplying focusable native inputs, and the production Wasm bundle still
  builds with the public API origin.
- **AC-WNA-07:** When the user explicitly requests publication, Firebase
  Hosting serves the verified production Wasm bundle and the public document
  retains the native authentication-input contract.

## Technical constraints

- Keep the shared domain, repository, API, and `AuthViewModel` contracts
  unchanged.
- Contain browser DOM APIs in `wasmJsMain`; `commonMain` remains platform-free.
- The native controls must be activated directly by the user's tap; do not
  synthesize a delayed focus event that mobile browsers may reject for keyboard
  activation.
- Keep all user-visible wording in `AppStrings`; no hard-coded labels or errors
  in browser scripts.
- Do not log, persist, expose, or test with real credentials.
- Preserve the full-height `ComposeViewport` host and its resize observer.
- Publishing is limited to the existing `allenljf-algorithm` Firebase Hosting
  target; do not alter Firebase configuration, Cloud Run, secrets, or API
  deployment settings.

## Decisions and assumptions

- Documentation decision: `update-docs`.
- Ambiguity decision: `infer`. The supported remediation is a Web-specific
  presentation seam rather than a Compose upgrade because the project already
  uses Compose Multiplatform 1.11.1 and the reported symptom remains a known
  class of canvas-text-input failure on mobile browsers.
- The Web layer may use browser-native DOM APIs from Kotlin/Wasm; the official
  Kotlin/Wasm documentation permits browser API access through JavaScript
  interoperability.
- Native elements will visually match the existing authentication layout closely
  enough for the current product; exact pixel identity is not an acceptance
  criterion.
