# Algorithm Learning Detail Experience Specification

## Document status

- Feature ID: `algorithm-learning-detail-experience`
- Status: governed; ready for `$work-graph`
- Intake mode: `quick-analysis`
- Governance mode: `update-docs + infer`
- Updated: 2026-09-28

## 1. Goal

Replace the current staged, interview-like Review Mode with a direct algorithm
learning experience. Selecting a problem opens a stable, addressable Detail page
where all study material is visible immediately and can be edited, saved, and
verified by a subsequent reload. The existing review-event and scheduling system
may remain available as background progress data, but it must not gate or
progressively disclose learning material.

## 2. Scope and non-goals

### In scope

- Compose Multiplatform problem Detail presentation, state, navigation, Web URL
  integration, localization, and deterministic tests.
- Mapping every editable learning field through the existing `ProblemDetail`,
  `ProblemWrite`, `Solution`, and REST adapters without DTO leakage to UI.
- A responsive standalone Web Detail route: two columns on wide layouts and a
  coherent single-column reading/editing flow on narrow layouts.
- Reworking existing problem/review entry points so they open the new Detail
  experience rather than a staged reveal or confidence gate.
- Enriching the checked-in NeetCode 150 source material and its safe, repeatable
  importer so each entry has complete editable learning content.
- Local verification, workflow closeout, and one scoped local commit.

### Out of scope

- Database migrations, API schema changes, online code execution, judge or
  submission functionality, accounts, deployment, Firebase/Cloud Run releases,
  Git push, or a redesign of review scheduling and review events.
- Altering the user's pre-existing `.firebase/`, `firebase.json`, or other
  unrelated uncommitted work.

## 3. Product behavior and acceptance criteria

### 3.1 Full learning detail

**AC-DLE-01:** A selected problem renders all of its learning content on initial
load. There are no Think/Hint/Approach/Solution stages, reveal actions,
confidence controls, gated submits, editor execution controls, judge results, or
online submission affordances in this flow.

**AC-DLE-02:** The Detail page reads and edits English description, Traditional
Chinese description, tags, difficulty, Chinese one-line hint, Chinese full
approach, time complexity, space complexity, Kotlin code with Chinese comments,
and executable or understandable test material. Saving then reloading displays
the persisted values without loss.

**AC-DLE-03:** The wide Web layout is a standalone full page, not a
master-detail split: English and Chinese descriptions occupy the left reading
column; metadata, hint, approach, complexity, code, and tests occupy the right
column. At narrow widths it becomes one accessible reading order. Every visible
label and action resolves through `AppStrings` in English and Traditional
Chinese.

### 3.2 Data preservation and mapping

**AC-DLE-04:** No database migration is introduced. The established REST/domain
mapping is:

| Learning content | Existing durable field |
|---|---|
| English description | `Problem.description` / `ProblemWrite.description` |
| Chinese description | `notes` |
| Tags and difficulty | `tags`, `difficulty` |
| Chinese one-line hint | `keyInsight` |
| Chinese full approach | Kotlin `Solution.explanation` |
| Time / space complexity | `timeComplexity` / `spaceComplexity` |
| Kotlin code with Chinese comments | Kotlin `Solution.code` |
| Test method or implementation | `interviewNotes` |
| Optional legacy common-mistake notes | `mistakes` |

The UI must preserve existing `mistakes` and must not silently repurpose or drop
stored fields. A selected Kotlin solution is the canonical editable learning
solution. Existing non-Kotlin solutions remain visible and are never overwritten
unless explicitly selected and edited.

### 3.3 Navigation and compatibility

**AC-DLE-05:** Browser navigation is real navigation, not only Compose state.
Opening a problem creates a recognizable detail URL/history entry. Browser Back
returns to the prior page and reasonable prior list state; Forward restores the
detail route. App-level back has the same single-source-of-truth behavior and
does not create duplicate history entries or loops.

**AC-DLE-06:** Existing Review tab/list and server-side review events/scheduling
remain unchanged in purpose and data contract. Any retained problem entry point
opens the complete Detail page; it does not expose staged disclosure or a
confidence-rating gate.

### 3.4 NeetCode 150 material

**AC-DLE-07:** Every checked-in NeetCode 150 entry has a locally authored,
editable content record with English and Chinese descriptions, category tags,
difficulty, Chinese hint and approach, complexity, Kotlin implementation with
Chinese comments, and test material. Existing local lessons, metadata, Kotlin
solutions, and cases are reused first. Missing Chinese content may be written
from public references in original words; third-party descriptions, explanations,
and code are not copied in bulk.

**AC-DLE-08:** The existing importer remains credential-safe and idempotent. It
does not print, write, stage, or commit credentials/tokens. Its static and local
checks prove complete 150-item content coverage and lossless REST payload
mapping. An authenticated synchronization is attempted only through the
existing safe importer when credentials and service availability permit it.

## 4. Technical constraints and architecture

- `shared` owns immutable route, detail editor state contracts, domain models,
  REST adapters, and browser-history abstractions; `composeApp` receives state
  and callbacks only. Composables never read a repository, HTTP client, DTO, or
  browser API directly.
- A platform history adapter keeps browser APIs out of `commonMain`; its Web
  actual maps canonical destinations and problem identifiers to URL/history
  state, handles `popstate`, and suppresses echo pushes. Native targets use a
  no-op/in-memory implementation with the same route contract.
- Problem writes use the complete existing `ProblemWrite` payload. Solution
  updates use the existing independent solution CRUD boundary. If a problem has
  no Kotlin solution, the detail editor creates one only on explicit save.
- The safe content representation may be a checked-in structured local manifest
  derived from existing NeetCode assets, provided it round-trips exactly to the
  fields in section 3.2 and retains source/provenance metadata. No schema or
  REST endpoint is changed.
- Tests are deterministic: fakes/controlled history adapters for shared and
  presentation tests; Wasm browser tests for rendered layout and history.

## 5. Verification and stop conditions

The work graph defines the exact commands per task. The feature-level release
evidence includes `:shared:allTests`, `:composeApp:allTests`, focused Wasm
browser Detail/history tests, NeetCode material/importer verification, and
`git diff --check`. Tests must prove mapping round-trip, save/reload, full
initial visibility, responsive layout contract, and Back/Forward behavior.

Stop only after all graph tasks are completed, their listed verification passes,
workflow records are updated, and the scoped local commit exists; or after an
external API/credential failure, or conclusive evidence that lossless content
cannot be represented without a schema change. The latter is a material decision
that must be reported rather than inferred.

## 6. Governance decisions and assumptions

- Documentation decision: `update-docs`; ambiguity decision: `infer`.
- The user supplied explicit outcome, scope, verification, and stop condition,
  which constitutes approval of this architecture-level design for autonomous
  execution.
- `interviewNotes` is the test-material field because it is a separately named,
  long-form existing field; `mistakes` remains an optional common-mistake field.
- Review analytics/scheduling remain visible only where already appropriate;
  they do not control reading order or access to learning content.
- The existing NeetCode importer is the sole authorized production sync path.
