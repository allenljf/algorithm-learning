---
name: task-execute
description: Use when executing one ready task from this project's work graph after its execution strategy has been selected.
---

# Task Execute

Perform the preflight in root `AGENTS.md`. Read every linked spec section, not only task summary. Confirm all dependencies are completed and task status is `ready`; set it to `in_progress` before edits.

Implement only the selected task and follow its execution contract. In `three-perspectives`, keep planner, implementer, and evaluator conclusions scoped to the same task. On any failure, record evidence and use `$recovery-loop`; otherwise use `$verification-closeout`.

Before returning, read and apply [`../WORKFLOW_CONTINUATION.md`](../WORKFLOW_CONTINUATION.md), using `$recovery-loop` or `$verification-closeout` based on the observed result.
