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
  T10[ALG-010 Dashboard API]
  T11[ALG-011 Flutter auth]
  T12[ALG-012 Flutter repositories]
  T13[ALG-013 Web management]
  T14[ALG-014 Browse/review]
  T15[ALG-015 Dashboard/home]
  T16[ALG-016 Compose]
  T17[ALG-017 K8s target doc]
  T18[ALG-018 MVP acceptance]

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
