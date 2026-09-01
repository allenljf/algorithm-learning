---
name: work-graph
description: Use when a complete specification needs a dependency-aware task breakdown, parallelization assessment, or resumable task state.
---

# Work Graph

Read root `AGENTS.md`, the full spec, and existing work graph. Create or update `specs/<feature>/plan.md`, `specs/<feature>/tasks.md`, and `agent-workflow/WORK_GRAPH.yaml`.

Each task must include: ID, deliverable, `depends_on`, `parallel_group`, detailed `spec_refs`, exact `verification`, and status. Mark tasks parallel only if they do not write overlapping files or consume unfinished outputs.

Choose analysis depth: `single-agent` for ordinary tasks; `three-perspectives` for cross-system, high-risk, or ambiguous work. The three perspectives are planner, implementer, and evaluator. Render the dependencies in `WORK_GRAPH.md`. Next skill: `$execution-strategy` for a `ready` task.

Before returning, read and apply [`../WORKFLOW_CONTINUATION.md`](../WORKFLOW_CONTINUATION.md), using `$execution-strategy` and the selected ready task.
