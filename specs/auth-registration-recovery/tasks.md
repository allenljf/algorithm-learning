# Authentication Registration Recovery Tasks

| ID | Task | Depends on | Parallel group | Spec references | Verification | Status |
|---|---|---|---|---|---|---|
| ARR-001 | Diagnose, repair, and release public registration | none | auth-registration-recovery | Goal; Evidence; AC-ARR-01..04; constraints; decisions | `cd services/api && ./mvnw -q test`; `cd services/api && ./mvnw -q -DskipTests package`; Cloud Run diagnostic/final deployments; public readiness; one synthetic public registration; `git diff --check` | completed |

## Phase 1 — Registration recovery

### ARR-001 — Diagnose, repair, and release public registration

- Deliverable: redacted correlated server-side failure logging, a confirmed
  underlying registration correction with regression coverage, and an API
  release verified through the public Firebase origin.
- Depends on: none.
- Parallel group: `auth-registration-recovery`.
- Spec refs: Goal; Evidence and root-cause investigation; AC-ARR-01..04;
  Technical constraints; Decisions and assumptions.
- Execution contract: `single-agent` analysis; `tdd` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `cd services/api && ./mvnw -q test`; `cd services/api && ./mvnw
  -q -DskipTests package`; deploy the diagnostic/final API image to the existing
  `allenljf-algorithm` `asia-east1` Cloud Run service while retaining its current
  configuration; `curl --fail --silent --show-error
  https://allenljf-algorithm.web.app/api/actuator/health/readiness`; perform one
  generated synthetic `POST /api/v1/auth/register` through the public origin
  and assert HTTP 201; `git diff --check`.
- Status: completed.
