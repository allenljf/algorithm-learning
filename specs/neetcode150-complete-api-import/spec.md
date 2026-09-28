# NeetCode 150 Complete API Import Specification

## Goal

Provide all 150 NeetCode 150 entries as independently implemented Kotlin
solutions with executable cases, and import exactly one owner-scoped LeetCode
problem, its NeetCode category tag, and a Kotlin solution for each entry
through the existing production REST API.

## Scope and non-goals

In scope: the checked-in NeetCode metadata, Kotlin solutions and executable
case data, a repeatable non-secret importer, API-side creation/update and
read-back verification, and workflow closeout records. Existing lessons may be
retained but are not a deliverable. The importer reads the
two specifically named local environment values only while running and never
persists or reports either value or an access token.

Out of scope: deployed application code, API contracts, database schema, CI,
deployment configuration, site UI, browser/UI automation, account creation,
and copying or scraping third-party problem statements or solutions.

## User-facing behavior and acceptance criteria

- **AC-N150-01:** `problems.json` has exactly 150 unique LeetCode IDs with
  title, slug, difficulty, and NeetCode category.
- **AC-N150-02:** Every ID has an independently implemented Kotlin solution
  and at least two locally authored executable cases. Existing lessons may be
  reused, but no original Traditional-Chinese teaching prose is required.
- **AC-N150-03:** The Kotlin verification compiles and runs all 150 solutions
  and checks at least two cases per ID.
- **AC-N150-04:** The importer logs in only through `POST /api/v1/auth/login`,
  keeps the bearer token in memory, retries a 401/403 request once after a
  fresh login, and emits only redacted diagnostics.
- **AC-N150-05:** Import is idempotent: it pages through existing owner
  problems, reuses normalized tags, and creates or replaces the single
  `platform=leetcode` row identified by each LeetCode ID.
- **AC-N150-06:** API read-back proves 150 expected owner problems occur
  exactly once and checks title/difficulty/ID/URL/category tag plus a Kotlin
  solution on every detail response.
- **AC-N150-07:** A non-secret summary records create/update/skip/failure
  counts, failed IDs, and safe-rerun status. All scoped files and workflow
  records are committed.

## Technical constraints

- Use only the existing REST endpoints under `https://allenljf-algorithm.web.app/api/v1`.
- Load `ALGORITHM_LEARNING_IMPORT_EMAIL` and
  `ALGORITHM_LEARNING_IMPORT_PASSWORD` from `infra/env/.env` in process memory;
  never print, serialize, write, stage, or commit them or an access token.
- Use LeetCode URLs formed from checked-in slug metadata and use independent
  Kotlin implementations. Source records retain provenance, without copied
  code or prose.
- Treat missing credentials, failed re-login/retry, or unavailable production
  service as the only external terminal blockers.

## Decisions and assumptions

- Documentation: `update-docs`; ambiguity: `infer`.
- Existing 87 local lessons remain authoritative where present. The canonical
  import manifest is generated/validated from checked-in original lesson and
  test material so its data cannot silently diverge from the 150-item index.
- A replace operation is preferred for an existing matching problem; a single
  existing Kotlin solution is replaced and an absent one is created. Duplicate
  solutions are never intentionally created.
