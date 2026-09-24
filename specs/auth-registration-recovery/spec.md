# Authentication Registration Recovery Specification

## Goal

Restore public account registration at `https://allenljf-algorithm.web.app` and
make unexpected server failures diagnosable without exposing credentials,
tokens, or request bodies.

## Scope and non-goals

In scope: the Spring API's unexpected-error observability, the registration
path's production diagnosis and root-cause correction, regression coverage, a
Cloud Run API release, and a public same-origin registration verification.

Out of scope: changing the frozen registration request/response contract,
password policy, refresh-cookie lifecycle, Firebase configuration, account
deletion, UI redesign, or reading/persisting a user's credentials.

## Evidence and root-cause investigation

On 2026-09-24 at approximately 10:07 Asia/Taipei, the public site's
`POST /api/v1/auth/register` received a real HTTP 500 from the same-project
Cloud Run service. The readiness endpoint returned HTTP 200 before and after
that request, which rules out Firebase Hosting and a service-wide outage.

`ApiExceptionHandler` deliberately maps any unhandled exception to the generic
`internal_error` response shown by the client, but it does not log the exception
or its request ID. Cloud Run therefore retains only the request-status record;
the originating exception cannot be recovered from the completed incident.

## User-facing behavior and acceptance criteria

- **AC-ARR-01:** A valid, previously unused account registration from the public
  Web origin receives HTTP 201 and returns the existing authentication response
  contract plus the existing secure refresh cookie.
- **AC-ARR-02:** An unexpected API failure remains a generic HTTP 500 response
  to the client, but server logs include the correlating request ID and exception
  stack trace; they do not log credential fields, bearer tokens, refresh tokens,
  cookie values, or request bodies.
- **AC-ARR-03:** The original 500's originating component is corrected when it
  can be reproduced. If the incident does not recur under a synthetic public
  registration, the server must instead retain a tested, redacted correlation
  record that makes any later recurrence attributable without exposing secrets.
- **AC-ARR-04:** The corrected API image is deployed to the existing
  `allenljf-algorithm` Cloud Run service, the public readiness endpoint is UP,
  and an authorized disposable synthetic registration succeeds through the
  public Hosting origin.

## Technical constraints

- Preserve the frozen `/api/v1/auth` route, JSON shape, status codes, Argon2
  policy, and `__session` cookie attributes.
- Use the existing production project, Cloud Run service, Firebase rewrite, and
  secret references. Do not read secret values or mutate Firebase Hosting.
- Diagnostic logging must be server-only, correlation-safe, and must not render
  exception details to the browser.
- The production reproduction uses a newly generated synthetic address and a
  non-reused generated password; neither is committed, logged, or reported.
- A created synthetic account is a minimal intentional verification artifact;
  no user-provided account or screenshot data is retried.

## Decisions and assumptions

- `$workflow-intake` uses `quick-analysis`: the user explicitly requested a
  bounded repair and deployment after the server-side 500 was established.
- `$spec-governance` uses `update-docs` and `infer`: recording the root-cause
  evidence and deployment outcome is required, while the implementation stays
  within the existing API and Cloud Run architecture.
- The current diagnosis gap is production observability, not a conclusion that
  any one database, cryptography, or session component is faulty. The task must
  capture a redacted exception before selecting the production correction.
- Recovery evidence: the first diagnostic image was built on an ARM development
  host and Cloud Run rejected it before application startup with `exec format
  error`. All API release images for this task must explicitly target
  `linux/amd64`; the failed revision received no traffic.
- The production 500 did not recur during the authorized synthetic checks, so
  no component-specific correction can be truthfully attributed. The accepted
  recovery is the tested diagnostic boundary plus a verified, working public
  registration on a Linux `amd64` release.
