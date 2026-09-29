# Course Reading System Plan

## Architecture

The content pipeline first parses existing aggregate Markdown into immutable
canonical per-lesson Markdown and a manifest. A checker owns coverage, duplicate
identity, Kotlin-example, provenance, and lossless payload assertions. The
backend stores category metadata and exactly the raw canonical detail. The
shared client maps Course transport DTOs to domain models; a Course view model
feeds a stateless Compose screen and navigation destinations.

## Delivery slices

1. Content census and canonical material/manifest checker.
2. Isolated Course migration and the three-route API with unit/integration tests.
3. Safe importer with mock transport tests and dry validation.
4. Shared domain/repository/remote mapping with tests.
5. Compose tab, search/filter/list/detail/navigation and platform tests.
6. End-to-end local acceptance proving lossless content projection and all
   client/backend contracts without deployment or credentials.

## Files and responsibilities

- `docs/algorithm/course-materials/`: canonical lessons, manifest, exceptions,
  and checker.
- `tools/course_import.py` and test: dry-run/default-safe batch projection.
- `services/api/.../courses/` and `V3__course_reading_system.sql`: isolated
  application/data/API layers and storage.
- `apps/multiplatform/shared/.../course/`: domain and Ktor data boundary.
- `apps/multiplatform/composeApp/.../course/`: state holder and stateless UI.

## Verification strategy

Each task runs only its listed commands. The final task runs the Course checker,
importer tests/dry validation, focused Maven Course tests, shared and Compose
tests, Wasm browser tests, and `git diff --check`.
