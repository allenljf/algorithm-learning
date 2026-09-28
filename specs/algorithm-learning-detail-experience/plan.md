# Algorithm Learning Detail Experience Plan

## Architecture

The existing problem and solution REST contracts remain the storage boundary.
`shared` receives a small immutable detail-editing model and route contract that
maps every existing durable field. `ProblemsViewModel` becomes the sole owner of
detail draft, load/save/reload, selected Kotlin solution, and list-return state.
The new stateless Compose Detail screen receives only that state and callbacks.

A platform browser-history adapter reconciles canonical library/detail routes
with the existing shared navigator. Its Web implementation owns URL parsing,
push/replace/popstate behavior; its shared contract is deterministic under a
fake adapter. The review entry points delegate to the same detail-route intent
instead of starting a staged review session.

NeetCode content is normalized from the checked-in metadata, lessons, Kotlin
sources, and executable cases into a round-trippable learning-content manifest.
The importer projects that manifest exactly into the existing problem/solution
fields, preserving its current safety and idempotency constraints.

## Files and responsibilities

- `shared/.../library`: preserve and test field mapping; no new DTO reaches UI.
- `shared/.../Navigation*` and platform actuals: canonical detail route and
  browser-history abstraction.
- `composeApp/.../problems`: immutable learning-detail state, complete editor,
  standalone responsive screen, and localized labels.
- `composeApp/.../review`: remove staged learning controls and delegate entries
  to the detail route while leaving review data/event scheduling untouched.
- `docs/algorithm/neetcode150` and `tools/neetcode150_import.py`: structured
  learning content, provenance, safe import mapping, and local coverage checks.
- `specs/algorithm-learning-detail-experience` and `agent-workflow`: durable
  scope, tasks, closeout evidence, and graph state.

## Delivery phases

| Phase | Tasks | Outcome |
|---|---|---|
| 1. Detail state and mapping | DLE-001 | Full editable state, lossless field mapping, save/reload behavior, and canonical route intent |
| 2. Detail presentation and history | DLE-002, DLE-003 | Standalone responsive Detail page and real Web Back/Forward integration |
| 3. NeetCode learning material | DLE-004 | Complete 150-item editable content manifest and safe importer projection |
| 4. Acceptance | DLE-005 | Cross-cutting deterministic acceptance evidence and closeout |

## Verification strategy

Task-local tests prove the narrow contract before the release task combines
them. Shared tests cover mappings, state transitions, persistence round-trips,
and a fake history adapter. Wasm browser tests render the responsive page and
exercise the real Web history bridge. NeetCode checks prove 150-item coverage,
original source material, and importer payload/read-back behavior without
printing credentials. The final task runs only its enumerated test suites plus
`git diff --check`.

## Dependencies and rollout

DLE-001 establishes the contract consumed by presentation, history, and content
work. DLE-002 and DLE-003 may share the route contract but are serialized to
avoid overlapping `App.kt`/problem-screen changes. DLE-004 is independent after
DLE-001 because its output is the same durable field projection. DLE-005
requires all implementation tasks. No deployment, schema, or API release is
authorized; any safe authenticated import is performed only by the established
tool when its existing credentials and endpoint are available.
