# NeetCode 150 Complete API Import Tasks

| ID | Task | Depends on | Parallel group | Spec references | Verification | Status |
|---|---|---|---|---|---|---|
| N150-001 | Complete Kotlin solutions, validation, and idempotent production API import | none | neetcode150-complete-api-import | Goal; AC-N150-01..07; constraints; decisions | `python3 docs/algorithm/neetcode150/checks/verify.py`; `python3 tools/test_neetcode150_import.py`; `python3 tools/neetcode150_import.py --validate`; `python3 tools/neetcode150_import.py --import-and-verify`; `git diff --check` | completed |

## Phase 1 — Complete NeetCode 150 import

### N150-001 — Complete material, validation, and idempotent production API import

- Deliverable: all 150 independently implemented, locally validated Kotlin
  solutions with executable cases; a safe repeatable importer; verified
  production data; redacted summary; and updated workflow records. Existing
  lessons remain optional reference material rather than a new deliverable.
- Depends on: none. Parallel group: `neetcode150-complete-api-import`.
- Spec refs: Goal; Scope and non-goals; AC-N150-01..07; Technical constraints;
  Decisions and assumptions.
- Execution contract: `single-agent`; `test-candidates`; `update-docs`;
  `infer`.
- Verification: `python3 docs/algorithm/neetcode150/checks/verify.py`;
  `python3 tools/test_neetcode150_import.py`; `python3
  tools/neetcode150_import.py --validate`; `python3
  tools/neetcode150_import.py --import-and-verify`; `git diff --check`.
- Status: completed.
