# NeetCode #371 Detail Lesson and Publishing Specification

## Goal

Publish a reload-safe NeetCode #371 Detail page that presents the official
LeetCode prompt and examples beside the original Traditional-Chinese lesson,
annotated Kotlin solution, and understandable verification material.

## Scope and non-goals

In scope: #371's structured lesson record and Kotlin source, lossless importer
projection/read-back checks, Detail-page rendering and localization, production
API synchronization, production Wasm bundle, Firebase Hosting deployment, and
workflow evidence. The existing `externalUrl` must be visibly rendered as a
clickable official LeetCode link.

Out of scope: all other 149 records, API schema or migrations, credentials,
accounts, Firebase configuration, and the user's existing `.firebase/` and
`firebase.json` changes.

## Acceptance criteria

- **AC-371-01:** The #371 record contains official English description and
  examples, Chinese description and summary, Chinese hint/approach,
  complexities, Kotlin source with Chinese comments, and Kotlin test material.
- **AC-371-02:** The importer projects all #371 material through existing
  fields and its payload/read-back verification preserves it and the canonical
  `https://leetcode.com/problems/sum-of-two-integers/` URL.
- **AC-371-03:** Detail's left column renders a clickable external URL, English
  description, English examples, Chinese description, and Chinese summary; the
  right column renders metadata/tags, hint, approach, complexities, Kotlin code,
  and test material. The narrow layout retains this reading order.
- **AC-371-04:** The listed local suites, API synchronization/read-back, Wasm
  production bundle, Hosting deploy, and public reload verify the result.

## Constraints and decisions

- Existing REST fields are the storage boundary. For #371 only, the importer
  stores English examples inside `description` using a stable private separator,
  and Chinese summary inside `notes` using another; the Detail UI separates
  them before rendering. No API schema changes are required.
- Official English source is LeetCode #371. Chinese teaching prose derives from
  `docs/algorithm/neetcode150/03-回溯動態規劃貪心與數學.md`.
- Documentation: `update-docs`; ambiguity: `infer`; execution: `tdd`.
- The supplied goal contract explicitly authorizes API login/sync and Firebase
  Hosting publication. Tokens and credentials remain process-local.

## Verification and stop condition

Run the task's exact Gradle, NeetCode/importer, diff, API, build, deployment,
and public reload checks. Stop only after all pass, or an external API/auth/
Firebase blocker prevents the authorized operation.

## Post-publication correction — CJK glyph rendering

- **AC-371-05:** A production `/problems/{id}` deep link resolves Compose Web
  resources from the domain root, so the bundled Traditional-Chinese font is
  loaded and Chinese lesson prose, including the Kotlin comments and solution
  approach, renders as glyphs rather than missing-glyph boxes.
