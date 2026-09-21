# Client Distribution — Feature Plan

## Planning record

- Feature ID: `client-distribution`
- Specification: [spec.md](spec.md)
- Planning method: `work-graph` with three perspectives because this crosses
  Kotlin Multiplatform configuration, mobile signing/distribution, iOS/Xcode,
  browser credential boundaries, and operator-controlled external systems.
- Governance decisions: build-time default plus runtime endpoint override;
  Android source-build guidance; public Firebase Hosting at
  `https://allenljf-algorithm.web.app`; same-project Cloud Run rewrite; and iOS
  development/simulator demonstration only.
- Execution contract: selected per ready task by `$execution-strategy`.

## Delivery strategy

The completed endpoint, iOS, and Android packaging foundations stay in place.
First retire the now-unneeded TestFlight release requirement and document iOS as
a development target. Then make browser refresh authentication compatible with
Firebase Hosting and produce the production Wasm/Firebase configuration. Finally
provision the isolated showcase project, deploy Cloud Run and Hosting, and
document the reproducible Android source-build path.

## Planned phases

| Phase | Tasks | Outcome |
|---|---|---|
| 1. Client configuration | CDS-001 | Build defaults and a safe persistent runtime endpoint setting across Android, Wasm, and iOS. |
| 2. iOS foundation | CDS-002 | Kotlin/Native iOS targets, Ktor iOS engine, and an `iosApp` Xcode entry point. |
| 3. Showcase release | CDS-003, CDS-004 | Signed Android packaging and iOS development-only documentation. |
| 4. Public Web foundation | CDS-005 | Firebase-compatible browser auth, production Wasm packaging, and Hosting configuration. |
| 5. Public deployment | CDS-006 | Operator-gated `allenljf-algorithm` Firebase/GCP project, Cloud Run replica, and public Hosting deployment. |
| 6. Android source build | CDS-007 | Public source checkout and local Android debug-build guidance. |

## Dependency and parallelization rules

- `CDS-001` is the configuration seam and is the sole initial ready task.
- `CDS-002` depends on `CDS-001`, so iOS uses the completed endpoint contract
  rather than creating a divergent default or persistence path.
- `CDS-004` follows the delivered iOS foundation and clears its obsolete
  TestFlight release path. `CDS-005` uses the endpoint foundation and then
  `CDS-006` deploys it into the new isolated showcase project.
- `CDS-006` is operator-gated because the new project's Neon configuration and
  secret values must be supplied privately. It never changes the existing GCP
  production project's WIF, Cloud Run topology, or Neon identities.
- `CDS-007` consumes the completed Android build foundation and documents a
  source-only debug build; it does not publish an APK to Firebase or elsewhere.

## Completion definition

The feature is complete when the public Wasm client is reachable at
`https://allenljf-algorithm.web.app` with same-origin authenticated API access,
Android has a documented, reproducible local debug-build path from public source,
iOS builds through its Xcode entry point for local demonstration, and the
existing production delivery path remains unchanged.
