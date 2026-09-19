# Spaced Repetition Specification

## Document status

- Feature ID: `spaced-repetition`
- Status: draft; awaiting `$spec-governance`
- Requirement baseline: [`../../requirement.md`](../../requirement.md), sections 4.4 and 11
- Intake mode: `brainstorm`
- Updated: 2026-09-10
- Governance and execution contracts: not selected

This specification defines the next post-MVP feature after the completed
`algorithm-learning-platform` MVP: an adaptive spaced-repetition scheduler. It
extends the existing immutable review-event model; it does not replace the
completed MVP's library, authentication, or review-disclosure behavior.

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
- Due-list and dashboard derivation that continue to use the calculated next
  review time without duplicating mutable due state.
- A forward-only database migration and API/Flutter contract changes required
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
the existing due presentation and a clear first-review explanation.

## 4. Acceptance criteria

- **AC-SR-01:** New review submissions use `adaptive-v1`; existing review
  events and their stored next-review dates remain unchanged.
- **AC-SR-02:** For every confidence value `0...4`, the server calculates the
  resulting state and `nextReviewAt` exactly as section 3.2 specifies.
- **AC-SR-03:** Replaying an adaptive event's persisted scheduling inputs and
  policy version reproduces its stored output without depending on wall-clock
  time or mutable client state.
- **AC-SR-04:** Due lists and dashboard due counts include an owned problem
  based on its latest event's `nextReviewAt`, and exclude it immediately after
  a successful review until that new time is due.
- **AC-SR-05:** A user cannot read or submit scheduling state for another
  user's problem; the API returns the existing non-disclosing `404` behavior.
- **AC-SR-06:** Detail and Review completion show localized, persisted schedule
  context for adaptive events and a valid first-review state for never-reviewed
  problems.
- **AC-SR-07:** Review Mode still enforces Problem → Think → Hint → My
  Approach → Solution → Mark confidence, and the server remains the sole
  authority for scheduling.

## 5. Technical design and constraints

### 5.1 Data and migration

The existing `reviews` table remains append-only. A forward Flyway migration
adds nullable adaptive-event snapshot fields sufficient to reproduce section
3.2: `previous_interval_days`, `previous_ease_factor`, `interval_days`,
`ease_factor`, and `repetitions`. They are populated for `adaptive-v1` events
and remain null for historical `fixed-v1` events. Database checks constrain
adaptive rows to valid confidence, interval, ease-factor, and repetition ranges
without rejecting historical rows.

No separate mutable schedule table is introduced: the latest owned review event
is the schedule source of truth. Queries may use indexes or projections to make
latest-event reads efficient, but cannot create a second writable schedule.

### 5.2 API contract

`POST /api/v1/reviews` keeps its request body unchanged. Its review-event
response and the existing review summary gain nullable schedule metadata:

- `policyVersion` (already present on review events);
- `intervalDays`;
- `easeFactor`;
- `repetitions`; and
- `scheduleExplanationKey` with locale-neutral parameters needed for the
  Flutter client to render localized text.

The server rejects client-supplied policy versions or schedule values. Existing
pagination, error-problem format, bearer authorization, and `404` ownership
semantics remain unchanged.

### 5.3 Flutter boundaries

The remote review service owns response DTO changes; the repository maps them
to immutable domain models. Review and problem-detail notifiers expose derived
UI state. Widgets render values and send only the existing confidence/notes
intent; they do not calculate dates or hold writable schedule copies. Provider
override seams must allow deterministic fake schedule responses in tests.

### 5.4 Guardrails

- Java + Spring Boot, PostgreSQL, Spring Data JPA, Flyway, and the established
  REST/error/security architecture remain locked.
- All timestamps are UTC and server assigned; Flutter formats them only at the
  presentation boundary.
- The deployment must be forward-migration safe: an old event is valid before
  and after the migration, and rollback plans must not require deleting review
  history.
- API logs continue to exclude private review notes, problem notes, solution
  code, credentials, and tokens.

## 6. Assumptions and decisions to govern

- The roadmap's “Spaced Repetition” item is the intended next post-MVP
  requirement; no AI feature is authorized by this intake.
- `adaptive-v1` is a product policy owned by the server, not a user setting.
- The policy is deliberately simple and deterministic before any configurable
  or ML-based scheduler is considered.
- The first adaptive event after fixed-v1 history bootstraps instead of trying
  to infer an ease factor from old confidence intervals.
- No new plan, task list, or work-graph node exists yet. `$work-graph` may
  create them only after `$spec-governance` accepts this scope and algorithm.

