# ARCHIVED — Flutter client retired

This directory is **archived and inactive**. It is retained only as a frozen
historical reference for the original Flutter client.

- Status: retired by `CMP-008` (feature `compose-multiplatform-migration`) on
  2026-09-19.
- Replacement: [`../multiplatform`](../multiplatform) is the active Kotlin
  Compose Multiplatform client. Its engineering contract is
  [`../multiplatform/COMPOSE_GUIDE.md`](../multiplatform/COMPOSE_GUIDE.md).
- Do not build, test, deploy, or extend this code. Tooling (`flutter analyze`,
  `flutter test`, `flutter test integration_test`, `check-rules.py`) must not be
  run against it.
- Parity and migration notes:
  [`../../specs/compose-multiplatform-migration/retirement.md`](../../specs/compose-multiplatform-migration/retirement.md).

`flutter-dev-guide/` continues to govern this archived client only; it does not
govern `apps/multiplatform`.
