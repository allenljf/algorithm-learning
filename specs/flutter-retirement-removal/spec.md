# Flutter Retirement Removal Specification

## Document status

- Feature ID: `flutter-retirement-removal`
- Status: completed
- Intake mode: `quick-analysis`
- Request: remove all remaining Flutter implementation assets from the project.
- Date: 2026-09-22

## Goal

Complete the Compose Multiplatform migration by deleting the inactive Flutter
client and its Flutter-only engineering guide. The active Kotlin Compose client
and the Java/Spring Boot API must remain untouched.

## Scope

- Delete `apps/learning_app/` in its entirety, including Dart code, Flutter
  platform projects, tests, generated metadata, and archive notices.
- Delete `flutter-dev-guide/` in its entirety.
- Update active repository entry points and requirement baseline so they no
  longer claim that a Flutter archive or Flutter guide is present.
- Update migration-retirement documentation to record that the previous archive
  was superseded by full deletion and that historical parity evidence remains
  described without linking to removed files.
- Add a completed work-graph task and progress entry for this removal.

## Non-goals

- Do not change `apps/multiplatform/`, the API, infrastructure, deployment, or
  pre-existing user-owned iOS/Firebase working-tree changes.
- Do not rewrite historical ALG task records merely because they mention Flutter;
  those records are provenance, not active Flutter assets.
- Do not remove Kotlin code comments that refer to the delivered Flutter client
  as migration history.

## Acceptance criteria

- **AC-FRR-01:** `apps/learning_app` and `flutter-dev-guide` do not exist.
- **AC-FRR-02:** Root README and requirement baseline identify Compose
  Multiplatform as the sole active client and contain no links to the removed
  Flutter client or guide.
- **AC-FRR-03:** The Compose migration retirement record states that Flutter was
  first archived and later fully removed, without a dead file link.
- **AC-FRR-04:** The work graph and progress record this completed removal.
- **AC-FRR-05:** `git diff --check` passes, and the Compose project structure
  remains present.

## Assumptions and rationale

The request uses "刪除" rather than archive, so source and guide directories are
deleted rather than retained as historical references. Historical workflow and
specification records are retained because they provide an auditable migration
history and do not reintroduce Flutter tooling or code.

## Governance record

- Documentation decision: `update-docs`.
- Ambiguity decision: `infer`.
- The phrase "Flutter 的內容" is governed as live source/tooling and active
  entry-point documentation, not immutable task-history prose. This prevents
  dead links and obsolete prerequisites while retaining the migration audit
  trail.
