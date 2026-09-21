# Flutter Retirement Removal Plan

- Feature ID: `flutter-retirement-removal`
- Specification: [spec.md](spec.md)
- Planning method: `work-graph`, single-agent: this is a bounded deletion and
  documentation update with no runtime behavior change.

## Delivery strategy

One task removes the retired source and guide atomically, then updates the
active entry points and migration record in the same change. Historical task
records remain intact as provenance.

## Phase 1 — Removal

| Task | Outcome |
|---|---|
| FRR-001 | Remove Flutter assets and obsolete active references |

## Dependency rules

`FRR-001` has no unfinished dependency: Compose migration task `CMP-008` is
already complete. It is deliberately a single task because the deletions and
their link/document updates must land together.
