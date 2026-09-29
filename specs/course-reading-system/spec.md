# Course Reading System Specification

## Status

- Feature ID: `course-reading-system`
- Status: governed, ready for work graph
- Intake mode: quick-analysis (architectural design recorded from the autonomous goal)
- Governance: `update-docs + infer`

## Goal

Provide a public, read-only Course library that is completely separate from the
personal Problem, Solution, Tag, and Review library. It publishes four local
algorithm collections as searchable lessons for Web, Android, and iOS, while a
token-protected importer can safely create or update their records.

## Scope and non-goals

In scope are the exact source roots and slugs below, canonical per-lesson
Markdown, a checked-in manifest and verification tool, Course-only PostgreSQL
schema/API/importer, and shared Compose Multiplatform reading UI.

| Source root | Course slug |
|---|---|
| `docs/algorithm/leetcode75-kotlin` | `leetcode75` |
| `docs/algorithm/neetcode150` | `neetcode150` |
| `docs/algorithm/hackerrank-interview-kit-kotlin` | `hackerrank-interview` |
| `docs/algorithm/hackerrank-three-month-prep-kotlin` | `hackerrank-three-month-prep-kotlin` |

Out of scope: deployment, production writes, credentials, ordinary-user Course
editing UI, changes to existing Problem/Solution/Tag/Review tables or routes,
and any modification to `N371-002` files or workflow state.

## Acceptance criteria

- **AC-COURSE-01:** Census tooling discovers every real problem in the four
  roots and excludes README, index, tracker, verification, and provenance
  documents. The manifest has one stable source identity per lesson and proves
  each lesson belongs to exactly one declared course category.
- **AC-COURSE-02:** Every canonical lesson preserves full Traditional-Chinese
  teaching Markdown (including code, examples, complexity, hints and sources),
  has an English title and independent searchable tags, and uses executable
  Kotlin examples/tests. Missing official solutions use a reliable public URL
  and source type with newly authored teaching prose; no third-party prose or
  solution is copied wholesale.
- **AC-COURSE-03:** The Course schema contains only `course_categories` and
  `course_lessons` (plus their indexes/constraints). Lessons reference their
  category only; they do not reference or populate existing practice tables.
- **AC-COURSE-04:** Exactly three Course routes exist: protected idempotent
  batch upsert, public paged/filterable list, and public full-detail lookup.
  The write route requires a configured non-secret ingestion token and does not
  expose it; public routes require no personal-library authentication.
- **AC-COURSE-05:** List filtering combines category slug, tag, keyword and
  pagination deterministically. Detail returns the stored `detail` byte-for-
  byte as imported. Upsert is idempotent by source identity.
- **AC-COURSE-06:** The importer is non-secret, repeatable, defaults to local
  validation/dry-run, and performs real HTTP writes only when an explicitly
  supplied ingestion token is present. Its coverage verifier compares canonical
  Markdown hashes with manifest and outgoing payload detail.
- **AC-COURSE-07:** Shared domain/data/presentation layers expose Course data
  without DTO/HTTP types in Compose. Each platform has a Course tab with
  category home, category/list filtering, search, and a non-truncating detail;
  there is no Course authoring control. Every visible string is localized in
  English and Traditional Chinese.

## Technical constraints and decisions

- Canonical truth is `docs/algorithm/course-materials/<slug>/<lesson-id>.md`;
  source documents remain inputs, never silently rewritten. A manifest records
  original path, source identity, canonical path, title, tags, order, URL/type,
  and SHA-256 detail digest.
- A migration introduces only `course_categories` (slug, display name, sort
  order) and `course_lessons` (category FK, English title, tags as a searchable
  independent field, detail, source path/identity, sort order). Database search
  indexes may support the stated list contract without extra domain tables.
- `APP_COURSE_INGESTION_TOKEN` is configuration only. The importer receives an
  explicit environment value, redacts diagnostics, and never writes defaults,
  secrets, or production data during verification.
- Course API shape is frozen to the three routes in AC-COURSE-04. The selected
  paths are `/api/v1/courses/import`, `/api/v1/courses`, and
  `/api/v1/courses/{sourceIdentity}`.
- Source availability is treated as content evidence: a failed reliable-source
  lookup is recorded in a checked-in exception ledger and blocks that lesson's
  import until resolved, rather than fabricating provenance.
- Material count is derived mechanically by the census; it is intentionally not
  guessed from aggregate README claims because source collections contain
  overlapping lessons and compiled documents.

## Verification and stop condition

Tasks define the exact Maven, Gradle, content/importer, and diff commands.
Completion requires every Course task closeout, scoped commit, and evidence in
the manifest/coverage checker. Stop only for a missing ingestion token needed
for an explicitly requested real write (which this goal excludes), unavailable
reliable public source after recorded attempts, or a material scope decision.
