---
name: session-handoff
description: Use when pausing work, resuming after an interruption, changing conversations, or determining the next ready task in this project's workflow.
---

# Session Handoff

This skill works independently and is also the normal endpoint recommended by other skills. Read root `AGENTS.md`, the full relevant spec, plan, tasks, `WORK_GRAPH.yaml`, and `progress.md`.

Update persisted status if evidence supports it, then list completed, in-progress, blocked, and ready tasks. Recommend the next task or parallel group. Use the current conversation and persisted artifacts as evidence for the continuation route selected by the shared contract.

For `new-session`, output a copyable prompt that names the exact spec, task ID, graph node, chosen execution contract, and mandatory preflight documents. Select the next skill explicitly.

Before returning, read and apply [`../WORKFLOW_CONTINUATION.md`](../WORKFLOW_CONTINUATION.md). This applies even here: automatically select and output only the continuation route required by that contract.
