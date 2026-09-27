# README Use-case Guide Specification

## Document status

- Feature ID: `readme-use-case-guide`
- Status: completed
- Intake mode: `quick-analysis`
- Date: 2026-09-27

## Goal

Make the root English and Traditional-Chinese READMEs show prospective users how
to use the website through concrete, end-to-end learning scenarios.

## Scope

- Expand the existing user guide in `README.md` and `README.zh-TW.md` with a
  concise getting-started path and three matching use cases.
- Cover recording a solved problem, organizing a problem library, and completing
  a scheduled review while checking progress.
- Name only flows that are implemented in the Compose client: authentication,
  Dashboard, Problems, Review, search, filters, tags, solutions, confidence,
  and the derived next-review date.
- Keep the two language versions semantically equivalent and retain their
  language-navigation links.

## Non-goals

- No client, API, database, hosting, deployment, or authentication behavior
  changes.
- No screenshots, analytics claims, external links, credentials, or setup
  instructions beyond the existing README content.

## Acceptance criteria

- **AC-RUG-01:** Each README has a clearly discoverable user-guide section with
  a short getting-started path.
- **AC-RUG-02:** Each README contains three numbered use cases with the user
  goal, concrete navigation/actions, and expected result.
- **AC-RUG-03:** Use cases accurately describe only checked-in product flows:
  create/edit problems, tags and solutions; search/filter; ordered review,
  confidence, next-review derivation, and Dashboard aggregates.
- **AC-RUG-04:** English and Traditional-Chinese content cover the same
  scenarios, and `git diff --check` passes.

## Technical constraints

- The change is documentation-only and must preserve existing README structure
  and local links.
- Follow the existing Markdown style and headings.

## Decisions and assumptions

- Documentation decision: `update-docs`; the request changes reader guidance,
  not product behavior.
- Ambiguity decision: `infer`; the three most useful first-time learner flows
  are capture, retrieve/organize, and review/progress. This follows the product
  navigation and domain capabilities in the checked-in Compose source.
