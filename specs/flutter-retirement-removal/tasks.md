# Flutter Retirement Removal — Task List

## Phase 1 — Removal

### FRR-001 — Remove Flutter assets and obsolete active references

- Deliverable: delete `apps/learning_app` and `flutter-dev-guide`; update the
  repository agent entry, root README, requirement baseline, migration record,
  graph, and progress so Compose is the sole client and no active document links
  to the removed paths.
- Depends on: none (CMP-008 completed)
- Parallel group: `flutter-removal`
- Spec refs: Goal, Scope, AC-FRR-01..05, Assumptions and rationale
- Execution contract: `single-agent` analysis; `rapid` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification: `test ! -d apps/learning_app`; `test ! -d flutter-dev-guide`;
  `test -d apps/multiplatform`; `git diff --check`
- Status: completed
