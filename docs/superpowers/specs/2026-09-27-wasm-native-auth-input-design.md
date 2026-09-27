# Wasm Native Authentication Input Design

## Intent

The public login and registration form must be usable on phone browsers. Tapping
an input must invoke the system keyboard, while the rest of the client keeps its
existing shared Compose behavior.

## Selected approach

Create a Web-only authentication presentation seam. It renders the email and
password controls as browser-native DOM inputs, connected to the existing
`AuthViewModel` callbacks. The common presentation state and all backend code
remain unchanged. Android and iOS continue rendering their existing Compose
fields.

Native elements give the browser a direct focus target during a tap, which is
the prerequisite for a mobile software keyboard. The Web implementation owns
DOM creation, focus, password masking, accessibility semantics, and cleanup;
the common layer owns strings, form values, validation, submit state, and errors.

## Data flow

`input` and `change` events send values to the existing email/password
callbacks. State changes update the input values, disabled state, labels,
password type, and mode-specific autocomplete. Submit and mode-switch actions
continue to use the existing view-model callbacks. No credential value is stored
outside the current page input and existing in-memory form state.

## Verification

The regression test must inspect the Web DOM and assert that the login form
contains focusable native email and password inputs. It must also prove input
events reach the existing presentation state and that toggling password
visibility changes input type without clearing the value. The task's build
verification will produce the existing production Wasm bundle.
