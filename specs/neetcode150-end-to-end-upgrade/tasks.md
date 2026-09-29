# NeetCode 150 End-to-End Detail Upgrade Tasks

| ID | Task | Depends on | Parallel group | Spec references | Verification | Status |
|---|---|---|---|---|---|---|
| N150E-001 | Complete all-150 structured bilingual content and lossless importer validation | none | n150e-foundation | AC-N150E-01/02; constraints | `python3 docs/algorithm/neetcode150/checks/verify.py`; `python3 tools/test_neetcode150_import.py`; `python3 tools/neetcode150_import.py --validate`; `git diff --check` | completed |
| N150E-002 | Prove complete Detail sections and official external-link dispatch | N150E-001 | n150e-presentation | AC-N150E-03 | `cd apps/multiplatform && ./gradlew :shared:allTests`; `cd apps/multiplatform && ./gradlew :composeApp:allTests`; `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserTest`; `git diff --check` | completed |
| N150E-003 | Extend CJK root-resource and deep-link regression to full learning material | N150E-002 | n150e-web | AC-N150E-04 | `cd apps/multiplatform && ./gradlew :composeApp:verifyWasmCjkFont :composeApp:wasmJsBrowserTest`; `git diff --check` | completed |
| N150E-004 | Synchronize and read back all 150 production Detail records | N150E-003 | n150e-release | AC-N150E-02; constraints | `python3 tools/neetcode150_import.py --import-and-verify`; `git diff --check` | completed |
| N150E-005 | Build, publish, and verify all 150 public Detail deep links | N150E-004 | n150e-release | AC-N150E-05 | `cd apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://allenljf-algorithm.web.app`; `firebase deploy --only hosting`; authenticated all-150 public reload and official-link assertion; `git diff --check` | completed |

## Phase 1 — Content and client readiness

### N150E-001 — Complete 150-record teaching content and importer contract

- Deliverable: all importable records have a reliable official-or-web sourced
  URL/English description/English examples and Chinese lesson fields, Kotlin
  source/comments/tests; any excluded record has a precise source-attempt ledger;
  local validation and importer projection prove the resulting set is lossless.
- Execution contract: `three-perspectives`; `tdd`; `update-docs`; `infer`.

### N150E-002 — Detail section and official-link UI contract

- Deliverable: deterministic UI coverage of both columns, every required
  section, and actual `externalUrl` dispatch using complete representative data.
- Execution contract: `single-agent`; `tdd`; `update-docs`; `infer`.

### N150E-003 — CJK deep-link resource regression

- Deliverable: a root-mapped Compose resource assertion covering Chinese prose
  and Kotlin comments under a Detail deep link.
- Execution contract: `single-agent`; `tdd`; `update-docs`; `infer`.

## Phase 2 — Production release

### N150E-004 — Authorized production import and complete read-back

- Deliverable: idempotent authenticated import and sanitized aggregate proof
  that every production Detail contains the canonical full record.
- Execution contract: `single-agent`; `test-candidates`; `update-docs`; `infer`.

### N150E-005 — Hosting release and all-150 public browser acceptance

- Deliverable: production bundle, existing-target Hosting deployment, and
  automated authenticated reload/visible-CJK/official-link verification for all
  150 Detail URLs.
- Execution contract: `single-agent`; `test-candidates`; `update-docs`; `infer`.
