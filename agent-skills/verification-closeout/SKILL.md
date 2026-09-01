---
name: verification-closeout
description: Use when an implementation task is ready to be validated, closed, and used to select the next dependency-ready task.
---

# Verification Closeout

Read root `AGENTS.md`, the full spec, selected task, and graph node. Execute only the commands in that task's `verification` field. Do not add a review pass, broad test run, or unrelated validation unless the task explicitly lists it.

On success: update `tasks.md`, `WORK_GRAPH.yaml`, `WORK_GRAPH.md`, and `progress.md`; complete the requested commit; identify all next `ready` tasks; then apply `WORKFLOW_CONTINUATION.md` before emitting a next-task command. When the next task is under a different `Phase` heading, output its complete **Start a new conversation** prompt. On failure: preserve output and use `$recovery-loop`.

Before returning, read and apply [`../WORKFLOW_CONTINUATION.md`](../WORKFLOW_CONTINUATION.md), using `$execution-strategy` for the next ready task, `$recovery-loop` after failure, or `$session-handoff` when pausing.
