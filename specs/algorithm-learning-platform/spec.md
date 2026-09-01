# Algorithm Learning Platform Specification

## Document status

- Feature ID: `algorithm-learning-platform`
- Status: governed, ready for `work-graph`
- Requirement baseline: [`../../requirement.md`](../../requirement.md)
- Intake mode: `brainstorm`
- Updated: 2026-09-01
- Governance mode: `update-docs + infer`
- Execution contract: not selected; it is chosen per ready task after planning

This document is the formal product and technical specification for the MVP. If
it conflicts with `requirement.md`, this specification is the implementation
source of truth after the conflict is resolved through `spec-governance`.

## 1. Goal

Build a private, cross-platform workspace for recording solved algorithm
problems and reviewing the user's own summaries, reasoning, mistakes, and
solutions. Flutter provides one codebase for Web, Android, and iOS. Web is the
primary management experience; mobile is optimized for browsing and staged
review. A Java + Spring Boot REST API and PostgreSQL own durable data.

The MVP is successful when one authenticated user can manage a personal problem
library on Web, browse the same data on mobile, complete a staged review without
seeing the answer early, and see the resulting confidence and due-review state
on subsequent sessions.

## 2. Product principles

1. **Personal notes, not copied problem statements.** The system stores the
   user's summary and work, plus a link to the source platform.
2. **Reveal progressively.** Review Mode never exposes approach or solution
   content before the user explicitly advances to that step.
3. **One durable source of truth.** PostgreSQL, accessed only through the API,
   owns product data in the MVP.
4. **Adaptive, not duplicated.** One Flutter application shares domain and data
   code while presenting Web management and mobile review-oriented navigation.
5. **Simple rules before smart scheduling.** The MVP uses a documented fixed
   review interval by confidence and preserves review events for a future spaced
   repetition policy.
6. **Modular monolith before distributed services.** Features have explicit
   boundaries but deploy as one API service.

## 3. Scope and non-goals

### 3.1 MVP scope

- Email/password registration, login, session refresh, logout, and per-user API
  authorization.
- Problem list, search, filtering, creation, detail, editing, and deletion.
- Multiple solutions per problem in Kotlin, Java, Python, or Dart.
- User-defined tags with many-to-many assignment to problems.
- Dashboard and mobile home summaries.
- Staged Review Mode and immutable review history.
- Flutter Web, Android, and iOS clients from one application package.
- Java + Spring Boot API, PostgreSQL migrations, and local Docker Compose for
  API plus DB.
- Unit, repository, API, state, and key widget tests.
- Architecture that can later support additional users, scheduling algorithms,
  deployment to Kubernetes, and AI adapters.

### 3.2 Explicit non-goals for MVP

- Importing, scraping, mirroring, or displaying full LeetCode/HackerRank problem
  statements.
- OAuth/social login, teams, sharing, public profiles, roles beyond the resource
  owner, or admin tooling.
- Offline-first sync, conflict resolution, or a client-side problem database.
- Syntax highlighting, code execution, judging, or sandboxing untrusted code.
- A configurable or ML-based spaced-repetition algorithm.
- AI hints, grading, solution comparison, or LLM provider integration.
- Push notifications, email reminders, analytics, or crash-reporting SDKs.
- Production Kubernetes manifests or an in-cluster production PostgreSQL setup.
- Bulk import/export and integration with third-party coding-platform APIs.

### 3.3 Platform capability matrix

| Capability | Web | Android/iOS |
|---|---:|---:|
| Register/login/logout | Yes | Yes |
| Dashboard/home | Yes | Yes |
| Browse/search/filter | Yes | Yes |
| Problem detail | Yes | Yes |
| Review Mode and confidence | Yes | Yes |
| Create/edit/delete problems | Yes | No in MVP |
| Create/edit/delete solutions | Yes | No in MVP |
| Create/assign tags | Yes | No in MVP |

The API authorizes by identity, not client platform. Mobile write routes remain
valid so mobile management can be enabled later without changing the contract;
the MVP mobile UI simply does not expose those actions.

## 4. User journeys

### 4.1 First use and authentication

1. The user registers with email and password or logs into an existing account.
2. A successful authentication restores the originally requested safe route or
   opens Home.
3. Invalid credentials return a generic localized error that does not reveal
   whether an email exists.
4. Expired access credentials are refreshed once. If refresh fails, local
   session state is cleared and the user returns to Login.

### 4.2 Manage a problem on Web

1. The user opens the problem list, searches or filters it, and selects New.
2. The editor validates required fields and accepts personal notes and tags.
3. Save creates or replaces the problem and returns to its detail view. Once a
   problem exists, the user manages zero or more solutions through independent
   solution forms. Failed validation preserves the active draft and identifies
   invalid fields.
4. Delete requires confirmation, removes the problem and its dependent
   solutions/tag links/reviews, and returns to the list with a success message.

### 4.3 Browse and review

1. The user enters a problem from Home, search results, or Today's Review.
2. Normal detail follows the content order in section 7.3.
3. Review Mode begins with only problem metadata and the user's summary visible.
4. The user explicitly advances through Think, Hint, My Approach, and Solution.
5. The confidence control is disabled until Solution has been revealed.
6. Submitting confidence records a review event, calculates the next review
   time, updates dashboard-derived state, and shows completion without mutating
   prior history.

## 5. Acceptance criteria

### Authentication and authorization

- **AC-AUTH-01:** A valid registration creates an account with a normalized,
  case-insensitively unique email and never stores or logs the raw password.
- **AC-AUTH-02:** Valid credentials start a session; invalid credentials receive
  the same `401` response regardless of whether the email exists.
- **AC-AUTH-03:** Every product-data query is scoped by authenticated `userId`.
  Requesting another user's known resource identifier returns `404`.
- **AC-AUTH-04:** Logout revokes the presented refresh session. An expired or
  revoked refresh token cannot create a new access token.

### Problem library

- **AC-PROB-01:** Web can create, read, update, and delete a problem with all
  fields defined in section 8.2.
- **AC-PROB-02:** List search matches title, external problem ID, description,
  notes, and key insight case-insensitively.
- **AC-PROB-03:** Filters for difficulty, platform, tags, and derived review
  status can be combined and retain a deterministic sort order.
- **AC-PROB-04:** Empty, loading, error, and populated list states are distinct,
  with retry available for recoverable errors.
- **AC-PROB-05:** A malformed or non-HTTPS external URL is rejected. LeetCode and
  HackerRank records must use their expected host; `Other` may use any valid
  HTTPS host.

### Solutions and tags

- **AC-SOL-01:** A problem can contain zero or more independently editable and
  deletable solutions; solution code is never stored in `problems`.
- **AC-SOL-02:** MVP solution language accepts only `kotlin`, `java`, `python`,
  or `dart`.
- **AC-TAG-01:** A user can create arbitrary tags; names are unique per user
  after trim and case-fold normalization.
- **AC-TAG-02:** Deleting a problem removes its tag links but not the tags.

### Review and dashboard

- **AC-REV-01:** Review Mode reveals stages only in the order Problem → Think →
  Hint → My Approach → Solution → Mark confidence.
- **AC-REV-02:** Confidence accepts integer values `0...4` only and cannot be
  submitted before the Solution stage is revealed.
- **AC-REV-03:** A review submission adds a history event and calculates
  `nextReviewAt` from the fixed MVP intervals in section 7.5.
- **AC-REV-04:** Today's Review contains owned problems whose latest
  `nextReviewAt` is due, including never-reviewed problems from their creation
  time, and excludes a problem after it is completed until its new due time.
- **AC-DASH-01:** Dashboard reports total problems, difficulty counts, tag/topic
  counts, due count, and recently created problems from the same authorized
  dataset.
- **AC-DASH-02:** Mobile Home also shows weak topics and a random owned problem.
  No-data variants explain how to add the first problem rather than failing.

### Operations and quality

- **AC-OPS-01:** `docker compose up -d` starts healthy PostgreSQL and API
  services from documented environment variables without committing secrets.
- **AC-OPS-02:** Migrations run deterministically on a new database and preserve
  data on forward upgrades.
- **AC-OPS-03:** API errors use the contract in section 10.7 and include a request
  ID without leaking secrets, passwords, tokens, code bodies, or personal notes
  into logs.
- **AC-QUAL-01:** Core validation, review scheduling, repository mappings, API
  authorization, Riverpod state transitions, and key screen states have tests
  at the lowest appropriate layer.

## 6. Chosen architecture and alternatives

### 6.1 Selected approach: modular monorepo and modular monolith

The repository contains one Flutter application, one Java + Spring Boot API, infrastructure,
and specs. The API is organized by feature but built and deployed as one service.
PostgreSQL is the only durable product store. The Flutter repository layer uses
the remote API as its source of truth and does not add offline caching in MVP.

This approach is selected because the product is for one person initially, yet
the ownership and authorization boundaries needed for multi-user support are
cheap to model now. It minimizes operational work and still provides seams for
future scheduling or AI adapters.

### 6.2 Alternatives considered

| Alternative | Benefit | Why not selected now |
|---|---|---|
| Backend-as-a-Service | Faster auth and CRUD bootstrap | Does not demonstrate the required Java/Spring layered backend skills and spreads vendor types into the design |
| Local-first Flutter app with later sync | Excellent offline mobile review | Requires conflict, migration, sync, and identity policies before core product value is proven |
| Microservices by feature | Independent deployment | Adds network contracts, observability, and deployment overhead with no current scale or team need |

### 6.3 Dependency direction

```text
Flutter View → ViewModel/Notifier → Repository contract → Remote service → REST
Spring MVC Controller → Application service → Domain policy → Repository port → Spring Data JPA/PostgreSQL
```

Transport DTOs, SQL entities/rows, domain models, and UI state are distinct.
Mapping happens at layer boundaries. Neither Flutter widgets nor Spring MVC controllers
contain persistence rules.

## 7. Product behavior

### 7.1 Problem list and filters

- Default order: `updatedAt desc, id desc`.
- Search input is trimmed; blank search is equivalent to no search.
- Difficulty and platform are single-select or unset in MVP.
- Tags are multi-select with AND semantics: a result must contain every selected
  tag.
- Review status values are `neverReviewed`, `due`, and `scheduled`; they are
  derived from the latest review event and current UTC time, not stored.
- Pagination resets to page 1 when search, filter, or sort changes.
- Web displays management actions. Mobile displays browse and review actions.

### 7.2 Problem editor

- Required: title, platform, and difficulty. The UI begins with no platform
  selection; `Other` is persisted only when the user explicitly selects it.
  There is no silent platform inference.
- Optional text fields are stored as `null` after trimming when blank.
- The problem form supports tag assignment. After the problem has been created,
  its editor/detail management view exposes a repeatable solution section with
  independent save/delete state per solution. A new unsaved problem cannot stage
  solutions locally, preventing a partially successful multi-resource save.
- A failed server save keeps the local draft. Field errors attach to their
  controls; non-field errors appear as a localized, dismissible message.
- The MVP multiline code editor preserves plain text and line endings. It does
  not execute, lint, or highlight code.

### 7.3 Problem detail

The normal detail view renders, when present, in this order:

1. Title
2. Difficulty
3. Tags
4. Problem summary
5. My Approach (`notes`)
6. Key Insight
7. Time Complexity
8. Space Complexity
9. Common Mistakes
10. Interview Notes
11. Solutions, each with language, explanation, and code
12. Latest review summary and review action

Missing optional sections are omitted rather than shown as empty headings.
External links open only after client-side HTTPS validation; the API performs the
same validation on writes.

### 7.4 Review Mode disclosure

| Stage | Visible content | Advance action |
|---|---|---|
| Problem | title, difficulty, tags, summary, source link | Start thinking |
| Think | previous content plus an instruction to attempt unaided; no additional answer content | Reveal hint |
| Hint | `keyInsight` as the MVP hint; if absent, an explicit “no hint saved” state | Reveal my approach |
| My Approach | `notes`, `mistakes`, complexity notes | Reveal solution |
| Solution | selected solution explanation and code; user may switch among solutions | Rate confidence |
| Mark confidence | confidence `0...4` and optional review note | Submit review |

Back navigation within the mode may revisit already revealed content. It never
hides content and does not unlock a later stage. Leaving before submission does
not create a review event.

### 7.5 MVP review policy

The API, not Flutter, calculates scheduling. `reviewedAt` is server time and the
fixed next intervals are:

| Confidence | Meaning | Next review |
|---:|---|---:|
| 0 | Cannot solve | 1 day |
| 1 | Needs hint | 2 days |
| 2 | Needs approach | 4 days |
| 3 | Can solve independently | 7 days |
| 4 | Very familiar | 14 days |

Every new problem is treated as due at `createdAt` until first reviewed. The
latest review event determines current confidence, last-reviewed time, and next
review time. Review count is the number of events. A future algorithm may use
the event history without changing its meaning.

Weak topics are the five tags with the lowest average latest confidence among
problems that have been reviewed, ordered by average ascending, then problem
count descending, then normalized tag name. Tags with no reviewed problems are
not called weak. Random Problem is uniformly selected from the user's problems;
the response may change on each Home refresh.

### 7.6 Deletion

- Problem deletion is hard deletion in MVP and requires explicit UI
  confirmation.
- The database cascades from problem to solutions, problem-tag links, and review
  events.
- User deletion is not exposed in MVP; when later added, it must cascade all
  owned records through a separate audited flow.
- Tag deletion is not exposed in MVP. Unused tags remain available for reuse.

## 8. Data model

### 8.1 Conventions

- Primary keys are UUIDs generated by the API.
- Timestamps are PostgreSQL `timestamptz`, stored and exchanged in UTC.
- All mutable aggregate tables have `created_at` and `updated_at`; join and event
  tables use only the timestamps that carry product meaning.
- API enums use lower camel case; database checks use lower snake case.
- All owned tables include or inherit a path to `user_id`; queries never trust a
  client-supplied owner ID.

### 8.2 Tables and constraints

#### `users`

| Column | Type | Null | Rules |
|---|---|---:|---|
| `id` | uuid | No | primary key |
| `email` | citext | No | unique, trimmed, normalized by DB semantics |
| `password_hash` | text | No | Argon2id encoded hash only |
| `created_at` | timestamptz | No | server default now |
| `updated_at` | timestamptz | No | maintained on update |

Enable PostgreSQL `citext` by migration. Email length is limited to 254 Unicode
code points before hashing or persistence.

#### `auth_sessions`

| Column | Type | Null | Rules |
|---|---|---:|---|
| `id` | uuid | No | primary key |
| `user_id` | uuid | No | FK `users(id)` on delete cascade |
| `token_hash` | bytea | No | unique keyed hash; raw refresh token is never stored |
| `expires_at` | timestamptz | No | absolute expiry |
| `revoked_at` | timestamptz | Yes | set on rotation or logout |
| `created_at` | timestamptz | No | server default now |
| `last_used_at` | timestamptz | Yes | set after successful refresh |

Indexes: `(user_id, expires_at)` and unique `(token_hash)`. Expired and revoked
rows may be removed by an operational cleanup job; removal does not affect the
product review history.

#### `problems`

| Column | Type | Null | Rules |
|---|---|---:|---|
| `id` | uuid | No | primary key |
| `user_id` | uuid | No | FK `users(id)` on delete cascade |
| `title` | varchar(200) | No | trimmed, non-blank |
| `platform` | varchar(20) | No | check: `leetcode`, `hacker_rank`, `other` |
| `external_problem_id` | varchar(120) | Yes | trimmed |
| `external_url` | text | Yes | valid HTTPS URL and platform host policy |
| `difficulty` | varchar(10) | No | check: `easy`, `medium`, `hard` |
| `description` | text | Yes | user's summary, max 20,000 characters |
| `notes` | text | Yes | max 20,000 characters |
| `key_insight` | text | Yes | max 10,000 characters |
| `time_complexity` | varchar(200) | Yes | plain text |
| `space_complexity` | varchar(200) | Yes | plain text |
| `mistakes` | text | Yes | max 20,000 characters |
| `interview_notes` | text | Yes | max 20,000 characters |
| `created_at` | timestamptz | No | server default now |
| `updated_at` | timestamptz | No | maintained on update |

Indexes: `(user_id, updated_at desc, id desc)`, `(user_id, difficulty)`, and
`(user_id, platform)`. Add a partial unique index on
`(user_id, platform, external_problem_id)` when `external_problem_id is not null`.
Search uses PostgreSQL full-text search over the user-authored searchable fields,
with a GIN index maintained by an expression or generated vector selected during
schema implementation. Search does not index solution code.

#### `solutions`

| Column | Type | Null | Rules |
|---|---|---:|---|
| `id` | uuid | No | primary key |
| `problem_id` | uuid | No | FK `problems(id)` on delete cascade |
| `language` | varchar(20) | No | check: `kotlin`, `java`, `python`, `dart` |
| `code` | text | No | may be blank only when explanation is non-blank; max 100,000 characters |
| `explanation` | text | Yes | max 20,000 characters |
| `created_at` | timestamptz | No | server default now |
| `updated_at` | timestamptz | No | maintained on update |

Index: `(problem_id, created_at, id)`. At least one of `code` or `explanation`
must be non-blank, enforced by API validation and a DB check equivalent to
`length(btrim(code)) > 0 OR length(btrim(coalesce(explanation, ''))) > 0`.

#### `tags`

| Column | Type | Null | Rules |
|---|---|---:|---|
| `id` | uuid | No | primary key |
| `user_id` | uuid | No | FK `users(id)` on delete cascade |
| `name` | varchar(80) | No | trimmed display name |
| `normalized_name` | varchar(80) | No | Unicode case-folded comparison form |
| `created_at` | timestamptz | No | server default now |
| `updated_at` | timestamptz | No | maintained on update |

Unique: `(user_id, normalized_name)`. Index: `(user_id, name)`. Normalization is
Unicode NFKC, trim, collapse internal whitespace to one space, then Unicode
lowercase. The suggested tag names in `requirement.md` are optional seed
suggestions, not an enum.

#### `problem_tags`

| Column | Type | Null | Rules |
|---|---|---:|---|
| `problem_id` | uuid | No | FK `problems(id)` on delete cascade |
| `tag_id` | uuid | No | FK `tags(id)` on delete cascade |

Composite primary key: `(problem_id, tag_id)`. The application verifies both
records belong to the authenticated user before insertion.

#### `reviews`

| Column | Type | Null | Rules |
|---|---|---:|---|
| `id` | uuid | No | primary key |
| `problem_id` | uuid | No | FK `problems(id)` on delete cascade |
| `confidence` | smallint | No | check `0 <= confidence <= 4` |
| `reviewed_at` | timestamptz | No | server-assigned event time |
| `next_review_at` | timestamptz | No | server-calculated from policy version |
| `notes` | text | Yes | max 10,000 characters |
| `policy_version` | varchar(40) | No | MVP value `fixed-v1` |

Indexes: `(problem_id, reviewed_at desc, id desc)` and
`(next_review_at, problem_id)`. Reviews are append-only through the public API;
there is no update or delete endpoint in MVP.

### 8.3 Relationship view

```text
users 1 ─── * auth_sessions
  │
  ├── * problems 1 ─── * solutions
  │        │
  │        └──── * reviews
  │
  └── * tags * ───── * problems through problem_tags
```

### 8.4 Transaction boundaries

- Creating or replacing a problem and its supplied tag links is one transaction.
- Solution CRUD is independently transactional.
- Review insertion and next-date calculation are one transaction.
- Dashboard reads may use multiple aggregate queries in one read-only transaction;
  exact snapshot equivalence between cards is not a product requirement.

## 9. Repository structure

```text
algorithm-learning/
  apps/
    learning_app/                 # Flutter: Web, Android, iOS
      lib/
        app/                      # bootstrap, theme, localization
        core/                     # router, HTTP client, cross-feature contracts
        features/
          auth/
          dashboard/
          problems/
          reviews/
      test/
      integration_test/
  services/
    api/                          # Java/Spring Boot modular monolith
      src/main/java/.../
        app/                      # Spring Boot composition root and shared adapters
        auth/
        problems/
        tags/
        reviews/
        dashboard/
      src/test/
  infra/
    compose.yaml
    env/.env.example
    kubernetes/README.md          # target architecture, no production manifests in MVP
  specs/
    algorithm-learning-platform/
      spec.md
      plan.md                     # created later by work-graph
      tasks.md                    # created later by work-graph
```

Do not create shared Dart or Java packages until at least two consumers need a
stable public contract. Feature internals cannot import another feature's
private implementation; cross-feature composition occurs at app roots or
through explicit application contracts.

## 10. REST API contract

### 10.1 Conventions

- Base path: `/api/v1`.
- JSON uses UTF-8 and lower camel case field names.
- IDs are UUID strings. Times are RFC 3339 UTC instants.
- Authenticated endpoints require `Authorization: Bearer <access-token>`.
- Request bodies reject unknown fields and invalid enum values.
- Blank optional strings normalize to `null`; response fields use explicit
  `null` rather than omitting schema-defined nullable values.
- Lists use `page` (default 1) and `pageSize` (default 20, maximum 100), returning
  `{items, page, pageSize, totalItems, totalPages}`.
- Create returns `201` plus `Location`; update returns `200`; delete returns
  `204`; successful reads return `200`.

### 10.2 Authentication

| Method and path | Auth | Request | Response |
|---|---|---|---|
| `POST /auth/register` | No | `{email, password}` | `201 {user, accessToken, expiresAt}` plus refresh cookie |
| `POST /auth/login` | No | `{email, password}` | `200 {user, accessToken, expiresAt}` plus refresh cookie |
| `POST /auth/refresh` | Refresh session | none | `200 {accessToken, expiresAt}` and rotated refresh session |
| `POST /auth/logout` | Refresh session | none | `204` and refresh session revoked |
| `GET /auth/me` | Yes | none | `200 {id, email, createdAt}` |

Passwords must be 12–128 characters. The API returns no password metadata.

### 10.3 Problems and solutions

| Method and path | Purpose |
|---|---|
| `GET /problems` | Search/filter/paginate problem summaries |
| `GET /problems/{problemId}` | Fetch owned detail with tags, solutions, and latest review summary |
| `POST /problems` | Create an owned problem and tag links |
| `PUT /problems/{problemId}` | Replace mutable problem fields and tag links |
| `DELETE /problems/{problemId}` | Delete an owned problem and dependents |
| `GET /problems/{problemId}/solutions` | List solutions in stable creation order |
| `POST /problems/{problemId}/solutions` | Add a solution |
| `PUT /solutions/{solutionId}` | Replace language, code, and explanation |
| `DELETE /solutions/{solutionId}` | Delete a solution |

`GET /problems` query parameters:

- `q`
- `difficulty=easy|medium|hard`
- `platform=leetcode|hackerRank|other`
- repeatable `tagId`
- `reviewStatus=neverReviewed|due|scheduled`
- `sort=updatedDesc|createdDesc|titleAsc`
- `page`, `pageSize`

`ProblemWriteRequest` contains all mutable problem fields plus `tagIds`. It does
not contain `id`, owner, timestamps, reviews, or solutions. Solutions use their
own endpoints to keep transaction and error boundaries clear.

`ProblemSummary` contains `id`, `title`, `platform`, `externalProblemId`,
`difficulty`, tags, latest confidence, review status, next review time, and
timestamps. `ProblemDetail` adds all note fields, `externalUrl`, solutions, and a
latest review summary.

### 10.4 Tags

| Method and path | Purpose |
|---|---|
| `GET /tags` | List owned tags, optionally filtered by `q` |
| `POST /tags` | Create one owned tag with `{name}` |

Duplicate normalized names return the existing tag as `200` rather than creating
a second row. Tag rename and delete are deferred; problem assignment changes via
`PUT /problems/{problemId}`.

### 10.5 Reviews and dashboard

| Method and path | Purpose |
|---|---|
| `GET /reviews/today` | Page through due problem summaries, oldest due first |
| `POST /reviews` | Record `{problemId, confidence, notes}` and return the review summary |
| `GET /reviews/history` | Page history, optionally filtered by `problemId`, newest first |
| `GET /dashboard` | Fetch summary cards, recent problems, weak topics, and random problem |

The API rejects a review for an unowned problem as `404`. Duplicate submissions
are possible user actions and create distinct events; the client disables repeat
submission while one request is active.

### 10.6 Resource schemas

The following tables define the JSON shapes. `string?` means the key is present
and may be `null`. Write requests must include every listed key for `PUT`; a
nullable field is cleared by sending `null`.

#### Authentication resources

| Schema | Fields |
|---|---|
| `User` | `id: uuid`, `email: string`, `createdAt: instant` |
| `AuthResponse` | `user: User`, `accessToken: string`, `expiresAt: instant` |
| `AccessTokenResponse` | `accessToken: string`, `expiresAt: instant` |

The refresh value exists only in the cookie transport and never appears in JSON.

#### Problem writes

| Field | Type | Validation |
|---|---|---|
| `title` | string | trim, 1–200 characters |
| `platform` | enum | `leetcode`, `hackerRank`, `other` |
| `externalProblemId` | string? | trim, 1–120 when non-null |
| `externalUrl` | string? | HTTPS and platform-host policy |
| `difficulty` | enum | `easy`, `medium`, `hard` |
| `description` | string? | maximum 20,000 |
| `notes` | string? | maximum 20,000 |
| `keyInsight` | string? | maximum 10,000 |
| `timeComplexity` | string? | maximum 200 |
| `spaceComplexity` | string? | maximum 200 |
| `mistakes` | string? | maximum 20,000 |
| `interviewNotes` | string? | maximum 20,000 |
| `tagIds` | array of uuid | unique values, maximum 50, every tag owned by caller |

`POST /problems` and `PUT /problems/{problemId}` both accept this
`ProblemWriteRequest`; `PUT` is full replacement of mutable fields.

#### Problem reads

| Schema | Fields |
|---|---|
| `TagSummary` | `id`, `name` |
| `ReviewSummary` | `status`, `confidence: integer?`, `lastReviewedAt: instant?`, `nextReviewAt: instant?`, `reviewCount: integer` |
| `ProblemSummary` | `id`, `title`, `platform`, `externalProblemId: string?`, `difficulty`, `tags: TagSummary[]`, `review: ReviewSummary`, `createdAt`, `updatedAt` |
| `ProblemDetail` | every `ProblemSummary` identity/timestamp field plus `externalUrl: string?`, `description: string?`, `notes: string?`, `keyInsight: string?`, `timeComplexity: string?`, `spaceComplexity: string?`, `mistakes: string?`, `interviewNotes: string?`, `solutions: Solution[]` |

For a never-reviewed problem, `ReviewSummary.status` is `neverReviewed`, both
confidence and last-reviewed time are null, `nextReviewAt` equals problem
`createdAt`, and `reviewCount` is zero.

#### Solution, tag, and review resources

| Schema | Fields and validation |
|---|---|
| `SolutionWriteRequest` | `language` enum; `code` string max 100,000; `explanation: string?` max 20,000; code or explanation must be non-blank |
| `Solution` | `id`, `problemId`, every write field, `createdAt`, `updatedAt` |
| `TagCreateRequest` | `name` string after trim, 1–80 characters |
| `ReviewCreateRequest` | `problemId: uuid`, `confidence: integer 0...4`, `notes: string?` max 10,000 |
| `ReviewEvent` | `id`, `problemId`, problem `title`, `confidence`, `reviewedAt`, `nextReviewAt`, `notes: string?`, `policyVersion` |

#### Dashboard response

`DashboardResponse` contains:

- `totalProblems: integer`;
- `difficultyCounts: {easy, medium, hard}` with zero values included;
- `tagCounts: [{tag: TagSummary, problemCount}]`, ordered by count descending
  then normalized name;
- `dueReviewCount: integer`;
- `recentProblems: ProblemSummary[]`, maximum five, newest created first;
- `weakTopics: [{tag: TagSummary, averageConfidence, reviewedProblemCount}]`,
  maximum five using section 7.5 ordering; and
- `randomProblem: ProblemSummary?`, null when the library is empty.

Paged problem and review-history endpoints wrap their item schema in the common
pagination object from section 10.1. Today's Review uses `ProblemSummary` items;
review history uses `ReviewEvent` items.

### 10.7 Error contract

Errors use `application/problem+json`:

```json
{
  "type": "https://algorithm-learning.local/problems/validation-error",
  "title": "Request validation failed",
  "status": 422,
  "code": "validation_error",
  "detail": "One or more fields are invalid.",
  "instance": "/api/v1/problems",
  "requestId": "01J...",
  "fieldErrors": {
    "title": ["must not be blank"]
  }
}
```

Status use: `400` malformed request, `401` unauthenticated, `404` missing or not
owned, `409` unique conflict, `422` field validation, `429` rate limited, and
`500` unexpected failure. Production details never expose stack traces, SQL,
tokens, password data, or private note/code content.

## 11. Authentication and security

### 11.1 Session design

- Passwords are hashed with Argon2id using parameters selected from current
  OWASP guidance at implementation time and stored in self-describing format.
- Access tokens are signed JWTs with a 15-minute lifetime, `sub=userId`, issuer,
  audience, issued/expiry times, and a unique token ID. The signing key is an
  environment secret.
- Refresh tokens are random opaque values with a 30-day absolute lifetime. Only
  a keyed hash and session metadata are stored server-side. Every refresh rotates
  the token and revokes the prior value.
- The API always transports the refresh token as a `Secure`, `HttpOnly`,
  `SameSite=Lax` cookie scoped to the refresh/logout path and returns the access
  token in JSON. Web relies on the browser cookie jar. The mobile Dio adapter uses
  a cookie jar whose persisted encrypted bytes are stored through platform secure
  storage; it never exposes the refresh value to feature code. Access tokens stay
  in memory on both clients.
- Refresh and logout validate the allowed application origin. State-changing API
  calls use bearer access tokens and do not authenticate from ambient cookies.

Refresh-session persistence uses the `auth_sessions` table defined in section
8.2. It is an authentication support table added to the initial product-domain
model; raw refresh tokens are never persisted.

### 11.2 Authorization and input safety

- Repository queries take authenticated `userId` as a required argument and
  scope ownership in SQL rather than fetching globally and checking afterward.
- The API never accepts `userId` in product write bodies.
- Rate limit registration, login, refresh, and password verification by a
  combination of IP and normalized account key without logging raw credentials.
- Validate sizes before persistence. JSON request bodies have a configured limit;
  solution code is capped as specified in section 8.2.
- External URLs are data only; neither API nor client fetches their contents.
  Both allow only HTTPS and validate expected platform hosts.
- Code and notes render as text/code, never executable HTML. Flutter Web does not
  inject them into raw DOM HTML.

### 11.3 Secrets and logs

- All secrets and DB credentials come from environment variables or a deployment
  secret store. Only `.env.example` with non-secret placeholders is committed.
- Flutter contains no API keys, DB credentials, signing keys, or server tokens.
- Structured logs include timestamp, level, request ID, route template, status,
  latency, and pseudonymous user ID when needed. They exclude authorization
  headers, cookies, passwords, full request/response bodies, search text, notes,
  and solution code.

## 12. Flutter design

### 12.1 Packages and boundaries

Selected baseline packages:

- `flutter_riverpod` plus Riverpod code generation for composition and state.
- `go_router` for a single app-level router and auth redirect.
- `dio` only inside remote services, with explicit timeouts, auth interceptor,
  one refresh retry, request ID propagation, and cancellation.
- `freezed` for immutable unions/state and `json_serializable` for transport DTOs.
- Flutter localization tooling and Material 3 theme at app entry.
- Platform secure storage only for mobile refresh-session material. Web refresh
  state is owned by the browser cookie and is not copied to local storage.

Each feature is feature-first and may contain `domain`, `data`, `application`,
and `presentation` only where each layer is needed. Use cases are introduced only
for shared or independently testable business policies; they do not wrap every
repository call.

### 12.2 State and data flow

- Repositories expose domain models and translate transport/serialization errors
  into app failures.
- Notifiers own business screen state. Widgets render immutable state and send
  user intent through callbacks.
- Async screens explicitly render loading, error, empty, and data states.
- Route inputs such as `problemId` are provider-family parameters, never shared
  mutable selection state.
- Reusable widgets receive values plus callbacks and do not read providers or
  call `go_router` directly.
- One-off messages and navigation effects are consumable state and are cleared
  after handling.
- No durable problem cache is present in MVP. Small non-secret UI preferences,
  such as the last filter, may use platform preferences behind a local service.

### 12.3 Routes

```text
/login
/register
/
/problems
/problems/new                # Web management
/problems/:problemId
/problems/:problemId/edit    # Web management
/problems/:problemId/review
/reviews/history
```

The app has one `GoRouter` composition root. Routes carry IDs and filters only.
Auth redirect is derived from replayable auth state and performs no network I/O.
Deep links to owned resources resolve through this table; unauthorized or missing
resources render the same Not Found state.

### 12.4 Adaptive UI and design system

- Material 3 light and dark themes are centralized. Colors, type, spacing, and
  radii come from theme/tokens rather than feature literals.
- All user-facing strings are localized. MVP ships English and Traditional
  Chinese resources; dates and numbers format at the UI boundary.
- Web uses a width-adaptive management shell. Mobile uses review-oriented bottom
  navigation only if route/back-stack behavior justifies it during UI design;
  otherwise it uses a simple top-level navigation rail/drawer adaptation.
- Interactive targets are at least approximately 48×48 logical pixels; controls
  have semantic labels; content remains usable at 200% text scale.
- Long lists use lazy builders and stable problem IDs as keys. Search requests
  cancel superseded calls; cancellation is not surfaced as an error.

## 13. Backend design

### 13.1 Locked technology choices

- Production backend code uses Java 21 LTS or later compatible LTS Java and
  Spring Boot. Java + Spring Boot is a locked decision; FastAPI, Ktor, Go, and
  all other backend languages/frameworks are out of scope unless the user first
  explicitly approves a specification revision.
- Spring Web provides REST controllers, JSON content negotiation, request-ID
  propagation, CORS, compression, and centralized exception handling.
- Spring Data JPA with JPA/Hibernate provides persistence adapters. JPA entities
  stay inside the data layer and do not cross into API DTOs or domain models.
- Spring Security provides stateless JWT authentication and authorization.
  Password hashing and refresh-session handling remain application security
  responsibilities, not controller logic.
- Jakarta Bean Validation validates transport request DTOs at the API boundary.
- Flyway exclusively owns PostgreSQL schema migrations; Hibernate DDL
  auto-generation is disabled outside isolated test setup.
- Spring Boot Actuator provides health, liveness, readiness, and metrics-ready
  operational endpoints.
- PostgreSQL is used in production-like development and integration tests; H2 is
  not used as a behavioral substitute.

### 13.2 Module shape

Each feature module exposes controller registration and application contracts.
Spring MVC controllers parse transport DTOs, rely on Bean Validation, and map
application results to HTTP. Application services enforce workflows and own
`@Transactional` boundaries. Pure domain policies validate scheduling and
ownership-independent rules. Spring Data JPA repository adapters contain JPA
queries and entity mapping. Shared infrastructure is limited to DB, JWT security,
time/ID generation, error handling, and HTTP composition.

Clock and UUID generation are injected so scheduling and event creation are
deterministic in tests. Spring MVC, Spring Security, and JPA types do not appear
in pure domain policies.

## 14. Docker and deployment target

### 14.1 Local Compose

`infra/compose.yaml` defines:

- PostgreSQL with a named volume and health check.
- API built from `services/api`, waiting on healthy DB and exposing Spring Boot
  Actuator health endpoints.
- An isolated application network and explicit environment-variable mapping.

Flutter runs from the host for hot reload. Actuator exposes
`/actuator/health/liveness` for process liveness and
`/actuator/health/readiness` for dependency readiness. Migrations run through
Flyway at controlled API startup or as a one-shot service, with only one
migrator allowed at a time.

### 14.2 Kubernetes target, not MVP deliverable

The future deployment uses a stateless API Deployment, ClusterIP Service,
Ingress with TLS, ConfigMaps for non-secret configuration, Secrets or an external
secret manager for credentials, probes mapped to the health endpoints, and a
separate migration Job. Production PostgreSQL should be a managed service with
backups and point-in-time recovery. An in-cluster StatefulSet is acceptable only
for non-production experimentation.

## 15. Error handling and resilience

- Flutter maps HTTP problem `code` values to typed app failures and localized UI
  copy. Raw Dio/SQL/stack-trace text never reaches a widget.
- Dio connect, send, and receive timeouts are explicit. GET retry is limited to
  transient failures with bounded backoff; mutations are not automatically
  retried unless an idempotency design is added later.
- Authentication refresh is single-flight so concurrent `401` responses do not
  race multiple rotations. After one successful refresh, the original request is
  retried once.
- API transactions roll back atomically. Unexpected failures return the standard
  `500` problem response and retain diagnostic correlation only by request ID.
- The UI preserves edit drafts on recoverable network/server errors and prevents
  duplicate submit actions while a mutation is active.

## 16. Testing and verification strategy

### 16.1 Backend

- Pure unit tests: validators, URL/platform rules, normalization, fixed review
  policy, auth/session policies.
- Repository integration tests: real PostgreSQL via Testcontainers for mappings,
  indexes/constraints, ownership-scoped queries, search/filter combinations, and
  cascade behavior.
- Spring MVC/API tests using `MockMvc`: request validation, response schemas,
  error mapping, status codes, JWT authentication, authorization isolation,
  pagination, and critical CRUD and review journeys.
- Migration test: empty DB to current and previous supported schema to current.

### 16.2 Flutter

- Pure tests: DTO/domain mapping, validation display mapping, and UI model
  derivation.
- Repository tests: fake remote service covering success, validation errors,
  unauthenticated refresh behavior, timeout, malformed data, and cancellation.
- Riverpod notifier tests: loading → data/error/empty, filter changes, draft save,
  stage disclosure, review submit, and consumable effects using provider
  overrides.
- Widget tests: Login; problem list in loading/empty/error/data; editor
  validation and draft preservation; detail; every Review Mode disclosure gate;
  Home no-data and populated states; semantics for icon-only actions.
- Integration tests: login, create/edit a problem on Web, browse it, and complete
  one review against a controlled API environment. Branch rules remain in unit
  and widget tests rather than being duplicated in E2E.

No task may claim verification commands until `work-graph` records them in its
task contract. Expected repository-level candidates include backend format/test,
Flutter format/analyze/test, integration tests where wired, Docker health smoke,
and `flutter-dev-guide/tools/check-rules.py --staged`.

## 17. Copyright and content policy

- The product UI explains that descriptions should be personal summaries.
- The API does not scrape or proxy source-platform content.
- External source URLs remain links to the original service.
- Bulk reproduction of copyrighted statements is out of scope. The system does
  not attempt to automatically classify copyright ownership of user-entered text.

## 18. Assumptions and decisions

### Confirmed by requirement

- Flutter is the single client codebase for Web, Android, and iOS.
- The API uses REST, Java + Spring Boot, and PostgreSQL.
- The first use case is personal, while schema and authorization are user-scoped.
- Web prioritizes management; mobile prioritizes browse and review.
- Spaced repetition and AI are future work.

### Decisions selected during brainstorm intake

- Use a modular monolith, not microservices.
- Use Spring Data JPA with Flyway migrations.
- Use UUIDs, UTC timestamps, `/api/v1`, and RFC-style problem details.
- Persist immutable review events and derive current review state.
- Use fixed confidence intervals `1/2/4/7/14` days for MVP.
- Treat every new problem as immediately due.
- Use Argon2id, short-lived JWT access tokens, and rotated opaque refresh tokens.
- Keep the MVP Flutter problem data remote-only; no offline cache or Drift DB.
- Ship English and Traditional Chinese localization baselines.
- Keep production Kubernetes artifacts outside MVP while documenting the target.

### Deferred choices with an explicit default

These are not blockers to governance or task planning; the listed default applies
unless a later approved spec revision changes it.

| Choice | Default |
|---|---|
| Exact Java, Spring Boot, Flutter, and package versions | Java 21 LTS is the runtime baseline; select mutually compatible stable Spring Boot and Flutter package versions when scaffolding is planned, then lock them in version files |
| Argon2id numeric parameters | Select from then-current OWASP guidance and record them in the auth task without weakening below that baseline |
| PostgreSQL full-text vector implementation | Prefer a stored/generated indexed vector if supported by the selected PostgreSQL version; otherwise use an expression GIN index |
| Mobile top-level navigation component | Use the simplest adaptive component consistent with finalized screen wireframes; route contract remains unchanged |
| Production hosting provider | Provider-neutral Kubernetes target and managed PostgreSQL |

## 19. Spec governance checklist

Before `work-graph` creates the feature plan, tasks, or product nodes,
`spec-governance` must verify:

- scope and non-goals match the intended MVP cut;
- user-facing acceptance criteria are testable and internally consistent;
- auth transport and review scheduling defaults are acceptable;
- API schemas and data constraints are sufficiently precise for task breakdown;
- the capability matrix does not unintentionally exclude desired mobile writes;
- deferred defaults do not hide a decision that changes dependencies;
- no execution contract is selected before a ready task exists.

### Locked backend governance record

- Documentation decision: `update-docs`.
- Ambiguity decision: `infer`.
- Production backend: Java + Spring Boot with PostgreSQL, Spring Web, Spring
  Data JPA, Spring Security, Bean Validation, Flyway, and Actuator.
- The backend exists to demonstrate layered Java design, JWT
  authentication/authorization, migration discipline, tests, health checks,
  Docker, and Kubernetes-ready configuration.
- No alternative backend language or framework may be selected without explicit
  user approval followed by an update to `requirement.md` and this specification.
