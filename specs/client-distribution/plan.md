# Client Distribution — Feature Plan

## Planning record

- Feature ID: `client-distribution`
- Specification: [spec.md](spec.md)
- Planning method: `work-graph` with three perspectives because this crosses
  Kotlin Multiplatform configuration, mobile signing/distribution, iOS/Xcode,
  browser credential boundaries, and operator-controlled external systems.
- Governance decisions: build-time default plus runtime endpoint override;
  signed direct Android APK; TestFlight external testing; production Web and
  GCP CORS deferred; signing and Apple identity are operator-only.
- Execution contract: selected per ready task by `$execution-strategy`.

## Delivery strategy

First make endpoint selection safe and cross-platform in the existing shared
client. Then add the iOS target and an Xcode shell, because it consumes that
same configuration path. Android packaging and the iOS TestFlight release
procedure follow only after their build outputs exist. Production Web remains a
documented deliberate deferral rather than a partially configured host/CORS
release.

## Planned phases

| Phase | Tasks | Outcome |
|---|---|---|
| 1. Client configuration | CDS-001 | Build defaults and a safe persistent runtime endpoint setting across Android, Wasm, and iOS. |
| 2. iOS foundation | CDS-002 | Kotlin/Native iOS targets, Ktor iOS engine, and an `iosApp` Xcode entry point. |
| 3. Release channels | CDS-003, CDS-004, CDS-005 | Signed Android artifact/runbook, operator TestFlight release, and explicit Web/CORS deferral documentation. |

## Dependency and parallelization rules

- `CDS-001` is the configuration seam and is the sole initial ready task.
- `CDS-002` depends on `CDS-001`, so iOS uses the completed endpoint contract
  rather than creating a divergent default or persistence path.
- `CDS-003` depends on `CDS-001`; `CDS-004` depends on the iOS foundation; and
  `CDS-005` depends on the endpoint work. They are recorded in one release
  group but are serialized here because their runbook sections and release
  evidence overlap.
- No task changes the frozen REST API, database, Cloud Run topology, WIF, or
  Neon identities. `CDS-005` must not set `GCP_CORS_ALLOWED_ORIGINS`.

## Completion definition

The feature is complete when every supported target has a safe endpoint default
and user override, iOS builds through its Xcode entry point, Android has a
repeatable signed-APK procedure, an operator has completed the TestFlight path,
and the documentation precisely records that public Web hosting/CORS is deferred
until an owned HTTPS origin exists.
