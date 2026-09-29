# NeetCode 150 End-to-End Detail Upgrade Specification

## Goal

Every one of the 150 NeetCode Detail pages must survive a public deep-link
reload and display complete, readable bilingual teaching material. Traditional
Chinese prose and Kotlin comments must render as glyphs, never boxes or mojibake.

## Scope and non-goals

In scope: the 150 checked-in learning records and their importer projection;
official English prompt descriptions and examples where publicly available,
with a reliable web-search fallback; the existing Compose Detail
presentation and localized labels; a deterministic CJK/deep-link regression
check; production API synchronization and read-back; a production Wasm bundle,
existing Firebase Hosting deployment, and authenticated public verification.

Out of scope: REST schema, database migrations, accounts, credentials,
Firebase configuration, and any non-NeetCode-150 problem. Existing uncommitted
changes to `.firebase/` and `firebase.json` must neither be modified nor
committed.

## Acceptance criteria

- **AC-N150E-01:** `learning-content.json` has exactly 150 unique records. Each
  importable record has the canonical LeetCode URL, a reliable sourced English
  description and examples, a Chinese description and summary derived from the
  checked-in NeetCode lesson, Chinese hint and approach, complexities, Kotlin
  source with Chinese comments, and understandable executable Kotlin test
  material.
- **AC-N150E-02:** The content verifier and importer validate all required
  fields for every importable record. Payload projection and production detail read-back
  prove no required field is omitted, including the official URL, Kotlin code,
  and test material.
- **AC-N150E-03:** Detail renders, in reading order, the external official link,
  English description/examples, Chinese description/summary in its left column;
  and metadata, hint, approach, complexities, Kotlin solution, and tests in its
  right column. The external URL is a real click action.
- **AC-N150E-04:** A deterministic Web test proves a `/problems/{id}` deep link
  maps Compose resources to the Hosting root and makes the Noto Sans TC asset
  available for Chinese teaching prose and Kotlin comments.
- **AC-N150E-05:** The exact required Gradle, content, importer, API read-back,
  production bundle, Hosting deploy, authenticated public reload, and diff
  checks pass. The public check visits and reloads every imported Detail URL, confirms
  required primary teaching content and rendered Chinese glyphs, and verifies
  each official link is usable.

## Technical constraints

- Use only existing REST fields and API routes. Store English examples and the
  Chinese summary in the existing stable section delimiters, now for every
  record; do not introduce a schema or migration.
- Use the checked-in NeetCode Markdown as the Chinese teaching source. Fetch
  official LeetCode English prompt/examples first; where unavailable, use a
  public reliable web source and retain its URL/type in the manifest. A record
  still lacking a reliable source is retained but skipped, with its ID, title,
  source attempts, and reason in a checked-in skip ledger. Do not store
  credentials, tokens, cookies, complete private responses, or secret-bearing
  artifacts.
- The importer may load the two existing import credentials from `infra/env/.env`
  only in process memory. On a 401/403 it re-authenticates and retries once.
- The production Wasm bundle uses `https://allenljf-algorithm.web.app` as its
  API base URL and deploys only the existing Hosting target.

## Decisions and assumptions

- Documentation: `update-docs`; ambiguity: `infer`; execution: `tdd`.
- Existing detailed Chinese lessons are preserved and strengthened; generic
  placeholder prose is replaced only where necessary to make the lesson
  specific and instructional.
- Public browser verification may use the existing import identity solely via
  the automation path. It reports aggregate counts and sanitized failures only.
- An individual source-access failure is not a release blocker: record and skip
  that item. A production API/authentication/Firebase permission failure or
  inability to refresh credentials remains a terminal external blocker after
  one authorized retry.
