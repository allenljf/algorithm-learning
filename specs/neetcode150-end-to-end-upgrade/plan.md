# NeetCode 150 End-to-End Detail Upgrade Plan

## Architecture

The learning manifest remains the canonical 150-item projection. It gains
separate sourced English examples and Chinese summaries for every importable entry; the
existing stable delimiters retain the frozen API boundary. The importer and
content checker enforce lossless importable-record coverage and a skip ledger before any authenticated
production synchronization. The Compose Detail split remains presentation-only,
with tests for all required sections and external-link dispatch. The Wasm entry
keeps all Compose resource URLs root-relative, verified independently before the
Hosting build and public browser acceptance.

## Files and responsibilities

- `docs/algorithm/neetcode150/learning-content.json` and existing lesson Markdown:
  complete bilingual record projection, source-grounded Chinese teaching text.
- `docs/algorithm/neetcode150/checks/verify.py`: full-record, Kotlin, and
  source/skip-ledger coverage checks.
- `docs/algorithm/neetcode150/source-skips.json`: auditable exclusions for only
  records lacking a reliable public source after official and web-search tries.
- `tools/neetcode150_import.py` and its test: lossless payload and 150-item
  authenticated read-back with redacted aggregate diagnostics.
- `apps/multiplatform/composeApp/.../ProblemsScreen.kt` and Wasm tests: Detail
  section rendering and official link dispatch.
- `apps/multiplatform/composeApp/.../index.html`, build checks, and browser
  acceptance support: root resource mapping and CJK/deep-link proof.

## Verification strategy

Run the acceptance commands named in the work graph: shared and Compose tests,
Wasm browser tests, all-150 content/importer validation, authenticated
import-and-read-back, CJK resource validation, production webpack, Hosting
deploy, all-150 authenticated public reload assertions, and `git diff --check`.

## Dependencies and rollout

Content/importer completeness, Detail UI, and deterministic CJK coverage are
completed before external synchronization. Production import/read-back precedes
the production bundle and Hosting deploy. Public all-150 verification is the
final gate.
