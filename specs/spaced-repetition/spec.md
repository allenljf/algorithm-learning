# Spaced Repetition Specification

## Document status

- Feature ID: `spaced-repetition`
- Status: governed; ready for `$work-graph`
- Requirement baseline: [`../../requirement.md`](../../requirement.md), sections 4.4 and 11
- Intake mode: `brainstorm`
- Updated: 2026-09-20
- Governance mode: `update-docs + infer`
- Execution contract: not selected; chosen per ready task after planning

This specification defines the next post-MVP feature after the completed
`algorithm-learning-platform` MVP: an adaptive spaced-repetition scheduler. It
extends the existing immutable review-event model; it does not replace the
completed MVP's library, authentication, or review-disclosure behavior.

The client is now the Kotlin Compose Multiplatform app `apps/multiplatform`
(governed by [`../../apps/multiplatform/COMPOSE_GUIDE.md`](../../apps/multiplatform/COMPOSE_GUIDE.md));
the Flutter client is archived. References below are to the Compose client. The
delivered review stack uses policy version `fixed-v1` with immutable events and
server-calculated `nextReviewAt`.

## 1. Goal

Help a signed-in user revisit problems at progressively appropriate intervals
by deriving each next review date from prior review performance. The schedule
must be reproducible from persisted data, must never rewrite prior review
history, and must preserve the existing fixed-v1 outcomes for historical
events.

The feature is successful when a user can submit the existing confidence score
in Review Mode, see the next review calculated by the adaptive policy for new
events, and inspect enough schedule context to understand why a problem is due
or when it is next due.

## 2. Scope

### In scope

- An `adaptive-v1` scheduling policy for review events created after rollout.
- Per-problem schedule state derived from the latest immutable review event.
- Deterministic server-side calculation using an injected clock.
- A schedule explanation on problem detail and Review Mode completion.
- Due-list and dashboard derivation that use each problem's latest event's
  calculated next review time without duplicating mutable due state, including
  reconciling `GET /reviews/today` to return owned problem summaries.
- A forward-only database migration and API/Compose contract changes required
  to expose adaptive schedule metadata.
- Tests for policy transitions, historical compatibility, owner isolation, and
  user-visible loading/error/data states.

### Out of scope

- User-configurable intervals, policies, daily limits, or per-tag schedules.
- ML/AI scheduling, imported practice history, notifications, email, or push
  reminders.
- Rewriting, deleting, or recalculating existing `fixed-v1` review events.
- Offline scheduling, background client jobs, or a client-side source of truth.
- Changes to the Review Mode disclosure order or confidence scale.

## 3. Product behavior

### 3.1 Policy selection and historical compatibility

The server selects `adaptive-v1` for every newly submitted review after this
feature is enabled. Existing events retain `policyVersion: fixed-v1`, their
stored `nextReviewAt` values, and their original meaning. A problem's current
schedule is determined only by its latest event; therefore the first
`adaptive-v1` event naturally takes over scheduling without mutating history.

For a problem whose latest event is `fixed-v1`, the first adaptive calculation
uses a bootstrap state: `repetitions = 0`, `intervalDays = 0`, and
`easeFactor = 2.50`. It uses the prior event only to establish that the problem
has been reviewed; it does not reinterpret its old interval.

### 3.2 Adaptive-v1 algorithm

Each adaptive review persists both its scheduling inputs and outputs so the
result remains explainable if the implementation changes later. `easeFactor` is
stored to two decimal places and is clamped to `[1.30, 3.00]`. `intervalDays`
is a positive integer. Calendar math is calculated in UTC from server-assigned
`reviewedAt`.

| Confidence | Resulting repetitions | Interval calculation | Ease-factor adjustment |
|---:|---:|---|---:|
| 0 — cannot solve | `0` | `1` day | `-0.20` |
| 1 — needs hint | `0` | `1` day | `-0.15` |
| 2 — needs approach | `max(previous repetitions, 1)` | `max(2, round(previous interval × 1.20))` | `-0.05` |
| 3 — solves independently | `previous repetitions + 1` | bootstrap: `4` days; otherwise `max(4, round(previous interval × previous easeFactor))` | `0.00` |
| 4 — very familiar | `previous repetitions + 1` | bootstrap: `7` days; otherwise `max(7, round(previous interval × (previous easeFactor + 0.15)))` | `+0.15` |

`nextReviewAt` equals `reviewedAt` plus the resulting `intervalDays`. Rounding
uses half-up rounding. A new, never-reviewed problem remains immediately due
at `createdAt`, as in MVP.

### 3.3 User-visible schedule context

Problem detail and the successful Review Mode completion state show:

- last reviewed time, next review time, and current interval in days;
- the policy version used by the latest event; and
- a concise localized explanation derived from persisted values, for example
  “Rated 3: next review in 4 days.”

The UI never exposes raw ease-factor values as a control. Loading, unavailable,
and error states must not invent schedule values. A never-reviewed problem uses
the existing due presentation and a clear first-review explanation. Every string
resolves through `AppStrings` in English and Traditional Chinese; the server
returns a `scheduleExplanationKey` with locale-neutral parameters that the
Compose client renders, consistent with the localization rule in
`COMPOSE_GUIDE.md`.

## 4. Acceptance criteria

- **AC-SR-01:** New review submissions use `adaptive-v1`; existing review
  events and their stored next-review dates remain unchanged.
- **AC-SR-02:** For every confidence value `0...4`, the server calculates the
  resulting state and `nextReviewAt` exactly as section 3.2 specifies.
- **AC-SR-03:** Replaying an adaptive event's persisted scheduling inputs and
  policy version reproduces its stored output without depending on wall-clock
  time or mutable client state.
- **AC-SR-04:** `GET /reviews/today` returns owned **problem summaries**
  selected by each problem's **latest** review event, so an owned problem is
  included when its latest `nextReviewAt <= now` (a never-reviewed problem is due
  from `createdAt`) and is excluded immediately after a successful review until
  that latest event's `nextReviewAt` is due. The dashboard `dueReviewCount` uses
  the same latest-event rule.
- **AC-SR-05:** A user cannot read or submit scheduling state for another
  user's problem; the API returns the existing non-disclosing `404` behavior.
- **AC-SR-06:** The Compose problem-detail and Review-completion views show
  localized, persisted schedule context for adaptive events and a valid
  first-review state for never-reviewed problems, resolving text through
  `AppStrings`.
- **AC-SR-07:** Review Mode still enforces Problem → Think → Hint → My
  Approach → Solution → Mark confidence, and the server remains the sole
  authority for scheduling.
- **AC-SR-08:** The delivered due contract is reconciled: `GET /reviews/today`
  returns problem summaries derived from the latest event (matching the Compose
  `ReviewRepository.due` contract), replacing the current review-event response;
  `POST /reviews` continues to return the created review event with its schedule
  metadata.

## 5. Technical design and constraints

### 5.1 Data and migration

The existing `reviews` table remains append-only. A forward Flyway migration
`V2` adds nullable adaptive-event snapshot fields sufficient to reproduce
section 3.2: `previous_interval_days`, `previous_ease_factor`, `interval_days`,
`ease_factor`, and `repetitions` (numeric ease factors are `numeric(4,2)`).
`V2` also replaces the `V1` inline check that pinned `policy_version` to
`fixed-v1` (PostgreSQL constraint `reviews_policy_version_check`) with a check
allowing exactly `fixed-v1` or `adaptive-v1`, so historical rows stay valid.

Adaptive rows (`policy_version = 'adaptive-v1'`) require non-null `interval_days
>= 1`, `ease_factor between 1.30 and 3.00`, and `repetitions >= 0`; the
`previous_*` snapshot columns may be null for a bootstrap event. Historical
`fixed-v1` rows keep all new columns null. Database checks must not reject
historical rows.

No separate mutable schedule table is introduced: the latest owned review event
is the schedule source of truth. The existing `reviews (problem_id, reviewed_at
desc, id desc)` index already supports latest-per-problem reads; additional
indexes or projections may be added, but no second writable schedule may exist.

### 5.2 API contract

`POST /api/v1/reviews` keeps its request body unchanged and continues to return
the created review event. Its review-event response and the problem's existing
review summary gain nullable schedule metadata:

- `policyVersion` (already present on review events);
- `intervalDays`;
- `easeFactor`;
- `repetitions`; and
- `scheduleExplanationKey` with locale-neutral parameters needed for the
  Compose client to render localized text.

`GET /api/v1/reviews/today` is reconciled to return owned problem summaries
(not review events), ordered oldest due first, selected by each problem's latest
event per AC-SR-04; this matches the delivered Compose
`ReviewRepository.due(): List<ProblemSummary>` contract. `GET
/api/v1/reviews/history` keeps its review-event response and gains the same
nullable schedule metadata.

The server rejects client-supplied policy versions or schedule values. Existing
`page`/`pageSize` parameters, bare-list review responses, error-problem format,
bearer authorization, and `404` ownership semantics remain unchanged. `fixed-v1`
history serializes with null adaptive fields.

### 5.3 Compose client boundaries

The Ktor review remote owns response DTO changes; `RemoteReviewRepository` maps
them to the immutable `Review`/`ProblemSummary` domain models. `ReviewViewModel`
and the problem view model expose derived, immutable UI state; composables
render values and emit only the existing confidence/notes intent and never
calculate dates or hold writable schedule copies (`COMPOSE_GUIDE.md` sections 3,
4, 10, 12). DTOs stay inside `library.data`. Tests use `MockEngine` for DTO
mapping, the existing fakes plus state-holder tests for deterministic schedule
responses, and `AppStrings` for localized explanation text.

### 5.4 Guardrails

- Java + Spring Boot, PostgreSQL, Spring Data JPA, Flyway, and the established
  REST/error/security architecture remain locked.
- All timestamps are UTC and server assigned; the Compose client formats them
  only at the presentation boundary.
- The deployment must be forward-migration safe: an old event is valid before
  and after the migration, and rollback plans must not require deleting review
  history.
- API logs continue to exclude private review notes, problem notes, solution
  code, credentials, and tokens.

## 6. Governed assumptions

- The roadmap's “Spaced Repetition” item is the intended next post-MVP
  requirement; no AI feature is authorized by this intake.
- `adaptive-v1` is a product policy owned by the server, not a user setting.
- The policy is deliberately simple and deterministic before any configurable
  or ML-based scheduler is considered.
- The first adaptive event after fixed-v1 history bootstraps instead of trying
  to infer an ease factor from old confidence intervals.
- The delivered `fixed-v1` events, their stored `nextReviewAt`, and prior
  confidence semantics are immutable history; `adaptive-v1` applies only to new
  events.
- No `plan.md`, `tasks.md`, or `WORK_GRAPH.yaml` node exists yet; `$work-graph`
  creates them after this governance pass.

## 7. Governance record

- Documentation decision: `update-docs`. The feature edits migrations, server
  review scheduling, the REST contract, the Compose data/presentation layers,
  and the reviewer-facing docs.
- Ambiguity decision: `infer`. The intake left three items open; governance
  rules on each and records the rationale.
- Client ruling: the spec was written before the Compose migration. All client
  boundaries are the Compose Multiplatform app `apps/multiplatform`; the Flutter
  client is archived and `flutter-dev-guide` does not govern the replacement.
- Migration ruling: `V2` must replace the `V1` `reviews_policy_version_check`
  (`policy_version = 'fixed-v1'`) with a check allowing `fixed-v1` and
  `adaptive-v1`, and add the nullable snapshot columns; otherwise new adaptive
  events cannot be inserted. Historical rows stay valid.
- Due-contract ruling: the delivered `ReviewsController` returns review events
  for `GET /reviews/today`, but the ALG MVP spec (AC-REV-04) and the delivered
  Compose client both define it as due **problem summaries** selected by the
  latest event. Governance resolves the contradiction in favor of the MVP spec
  and the client, so `AC-SR-08` reconciles the endpoint as part of the due
  change; `POST /reviews` keeps returning the created review event.
- Contradiction resolved: `policyVersion` on the wire is `fixed-v1` (not
  `mvp-1`; that value appears only in a Compose test fixture). The adaptive
  policy version is `adaptive-v1`.
- Preserved non-goals: no user-configurable policy limits, notifications, ML,
  offline scheduling, client-side scheduling authority, confidence-scale change,
  or rewrite of historical events.
- The algorithm in section 3.2, the UTC/injected-clock rule, the append-only
  model, and owner-isolation `404` semantics are accepted as the executable
  contract for `$work-graph`.

