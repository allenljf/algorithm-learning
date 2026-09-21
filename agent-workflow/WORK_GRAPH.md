# Algorithm Learning Platform — Dependency Graph

```mermaid
flowchart TD
  T1[ALG-001 Monorepo skeleton\ncompleted]
  T2[ALG-002 Spring root\ncompleted]
  T3[ALG-003 Flutter root\ncompleted]
  T4[ALG-004 Flyway schema\ncompleted]
  T5[ALG-005 Auth/security\ncompleted]
  T6[ALG-006 Tags API\ncompleted]
  T7[ALG-007 Problems API\ncompleted]
  T8[ALG-008 Solutions API\ncompleted]
  T9[ALG-009 Reviews API\ncompleted]
  T10[ALG-010 Dashboard API\ncompleted]
  T11[ALG-011 Flutter auth\ncompleted]
  T12[ALG-012 Flutter repositories\ncompleted]
  T13[ALG-013 Web management\ncompleted]
  T14[ALG-014 Browse/review\ncompleted]
  T15[ALG-015 Dashboard/home\ncompleted]
  T16[ALG-016 Compose\ncompleted]
  T17[ALG-017 K8s target doc\ncompleted]
  T18[ALG-018 MVP acceptance\ncompleted]

  T1 --> T2
  T1 --> T3
  T2 --> T4
  T2 --> T5
  T4 --> T5
  T4 --> T6
  T5 --> T6
  T4 --> T7
  T5 --> T7
  T6 --> T7
  T4 --> T8
  T5 --> T8
  T7 --> T8
  T4 --> T9
  T5 --> T9
  T7 --> T9
  T5 --> T10
  T6 --> T10
  T7 --> T10
  T9 --> T10
  T3 --> T11
  T5 --> T11
  T3 --> T12
  T7 --> T12
  T8 --> T12
  T9 --> T12
  T10 --> T12
  T11 --> T12
  T11 --> T13
  T12 --> T13
  T11 --> T14
  T12 --> T14
  T11 --> T15
  T12 --> T15
  T2 --> T16
  T4 --> T16
  T5 --> T16
  T10 --> T16
  T16 --> T17
  T10 --> T18
  T13 --> T18
  T14 --> T18
  T15 --> T18
  T16 --> T18
  T17 --> T18
```

Parallel groups are recorded in `WORK_GRAPH.yaml`; they are eligibility groups,
not authorization to begin a task without `$execution-strategy`.

# GCP Cloud Run Delivery — Dependency Graph

```mermaid
flowchart TD
  G1[GCP-001 Bootstrap tooling and runbook\ncompleted]
  G2[GCP-002 Migration-only API mode\ncompleted]
  G3[GCP-003 OIDC deployment workflow\ncompleted]
  G4[GCP-004 First controlled GCP release\nblocked: superseded by NEO-003]

  G1 --> G3
  G2 --> G3
  G3 --> G4
```

`GCP-001`, `GCP-002`, and `GCP-003` are completed. `GCP-004` is blocked: it is
superseded by the Neon delivery path below and must not run.

# Neon Cloud Run Delivery — Dependency Graph

```mermaid
flowchart TD
  N1[NEO-001 Neon bootstrap and runbook\ncompleted]
  N2[NEO-002 Neon deploy workflow\ncompleted]
  G2[GCP-002 Migration-only API mode\ncompleted]
  N3[NEO-003 First Neon release\ncompleted]

  N1 --> N2
  G2 --> N2
  N2 --> N3
```

# Compose Multiplatform Migration — Dependency Graph

```mermaid
flowchart TD
  C1[CMP-001 Compose MP root + guide\ncompleted]
  C2[CMP-002 Auth/session data\ncompleted]
  C3[CMP-003 Library/review/dashboard data\ncompleted]
  A5[ALG-005 Auth API\ncompleted]
  A7[ALG-007..010 Library APIs\ncompleted]
  C4[CMP-004 Auth + problem UI\ncompleted]
  C5[CMP-005 Browse + Review Mode UI\ncompleted]
  C6[CMP-006 Dashboard/home UI\ncompleted]
  C7[CMP-007 Cross-platform acceptance\ncompleted]
  C8[CMP-008 Retire Flutter client\ncompleted]

  C1 --> C2
  A5 --> C2
  C1 --> C3
  C2 --> C3
  A7 --> C3
  C2 --> C4
  C3 --> C4
  C2 --> C5
  C3 --> C5
  C2 --> C6
  C3 --> C6
  C4 --> C7
  C5 --> C7
  C6 --> C7
  C7 --> C8
```

# Neon Least-Privilege Role — Dependency Graph

```mermaid
flowchart TD
  L1[NLP-001 Role rotation artifact + runbook\ncompleted]
  L2[NLP-002 Rotate credential + verify readiness\ncompleted]

  L1 --> L2
```

# Spaced Repetition — Dependency Graph

```mermaid
flowchart TD
  S1[SR-001 adaptive-v1 policy + V2 migration\ncompleted]
  S2[SR-002 Compose schedule context + due contract\ncompleted]
  S3[SR-003 cross-platform acceptance\ncompleted]
  S4[SR-004 production migration + readiness\ncompleted]

  S1 --> S2
  S1 --> S3
  S2 --> S3
  S3 --> S4
```

# Neon Migration/Runtime Role Separation — Dependency Graph

```mermaid
flowchart TD
  R1[NRS-001 Split-role SQL artifact + runbook\ncompleted]
  R2[NRS-002 Bootstrap + workflow split identity\ncompleted]
  R3[NRS-003 Apply split roles + verify readiness\ncompleted]

  R1 --> R3
  R2 --> R3
```

`R1`, `R2`, and `R3` are completed. The runtime role is SQL-created (no
`neon_superuser`), the serialized release migrated once as
`algorithm_learning_migrate` and serves readiness as `algorithm_learning_app`,
and the operator's probe proves DDL is denied while DML succeeds.

# Client Distribution — Dependency Graph

```mermaid
flowchart TD
  D1[CDS-001 Endpoint configuration\nready]
  D2[CDS-002 iOS target + Xcode shell\npending]
  D3[CDS-003 Signed Android APK\npending]
  D4[CDS-004 TestFlight release\npending]
  D5[CDS-005 Web/CORS deferral\npending]

  D1 --> D2
  D1 --> D3
  D1 --> D5
  D2 --> D4
```

The selected distribution contract is: dual endpoint configuration, direct
signed Android APK, operator-owned TestFlight external testing, and deferred
production Web/CORS. `CDS-001` is the only ready task.

Parallel groups are recorded in `WORK_GRAPH.yaml`; they are eligibility groups,
not authorization to begin a task without `$execution-strategy`.
