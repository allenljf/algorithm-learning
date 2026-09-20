# Spaced Repetition — Feature Plan

## Planning record

- Feature ID: `spaced-repetition`
- Specification: [spec.md](spec.md)
- Planning method: `work-graph` with three perspectives (planner, implementer,
  evaluator), because the feature changes a forward-only PostgreSQL migration,
  server scheduling behavior, the frozen REST contract, and the Compose
  data/presentation layers, with a production migration at the end.
- Execution contract: selected per ready task by `$execution-strategy`.

## Delivery strategy

Rebuild inside-out on the existing immutable review-event model. First add the
`adaptive-v1` policy plus the `V2` migration and the latest-event due/summary
derivation on the server; then expose the schedule metadata and localized
schedule context in the Compose client; then run cross-platform acceptance; and
finally apply the migration to production in an operator-gated release. Existing
`fixed-v1` events are never rewritten.

## Planned phases

| Phase | Tasks | Outcome |
|---|---|---|
| 1. Server scheduling | SR-001 | `adaptive-v1` policy, `V2` migration, snapshots, latest-event due/summary, API metadata |
| 2. Compose client | SR-002 | Schedule metadata mapped to domain state and localized schedule context on detail and Review completion |
| 3. Acceptance and release | SR-003, SR-004 | Cross-platform acceptance plus the operator-gated production migration |

## Dependency and parallelization rules

- `SR-001` has no dependency; it writes only `services/api`.
- `SR-002` depends on `SR-001` (the wire contract) and writes only
  `apps/multiplatform`; the two do not overlap, but the client needs the server
  contract resolved first, so they are sequential.
- `SR-003` depends on both and adds acceptance coverage across the two.
- `SR-004` depends on `SR-003` and is the only task authorized to run the
  production migration and deploy; it requires the user's authorization.

## Architecture guardrails carried into execution

- Java + Spring Boot, PostgreSQL, Spring Data JPA, Flyway, and the
  REST/error/security architecture stay locked; the REST contract is frozen
  except for the additive schedule metadata and the reconciled
  `GET /reviews/today` response.
- Reviews stay append-only; the latest owned event is the single schedule source
  of truth. No mutable schedule table.
- `V2` adds nullable snapshot columns and replaces the `V1`
  `reviews_policy_version_check` so historical `fixed-v1` rows remain valid.
- All timestamps are UTC and server assigned; the Compose client formats them
  only at the presentation boundary and resolves every string through
  `AppStrings`.
- Non-goals: no user-configurable policy, notifications, ML, offline scheduling,
  client-side scheduling authority, confidence-scale change, or history rewrite.

## Completion definition

The feature is complete when `adaptive-v1` schedules new events exactly as
section 3.2 specifies, historical `fixed-v1` events and their next-review dates
are unchanged, `GET /reviews/today` and the dashboard due count follow the latest
event, the Compose client shows localized persisted schedule context, the
cross-platform acceptance suite passes, and the operator-gated production
migration leaves the API ready with no history loss.
