---
name: task-execute
description: Use when executing one ready task from this project's work graph after its execution strategy has been selected.
---

# Task Execute

Perform the preflight in root `AGENTS.md`. Read every linked spec section, not only task summary. Confirm all dependencies are completed and task status is `ready`; set it to `in_progress` before edits.

Implement only the selected task and follow its execution contract. In `three-perspectives`, keep planner, implementer, and evaluator conclusions scoped to the same task. On any failure, record evidence and use `$recovery-loop`; otherwise use `$verification-closeout`.

## Task-completion gate

Before the first edit, translate the task deliverable and every linked acceptance
criterion into a private completion checklist. A task is not complete merely
because one screen, endpoint, model, or focused test exists. Continue through
every unchecked item, then run the task's exact verification and close it out.

Never end a turn with “the remaining parts are not complete” when the remaining
work is implementable from repository/spec context. Continue working instead.
The only permitted early exits are: a terminal recovery-loop result, a missing
material user decision, an unavailable external dependency that cannot be
emulated locally, or an explicit user request to pause.

When the user asks to directly complete the whole task, continue implementation
within the same execution phase until the task reaches verification-closeout, a
recovery-loop terminal condition, or a genuine external/user-decision blocker.
Do not stop after a partial vertical slice, a passing focused test, or an
intermediate checkpoint. Record checkpoints in `progress.md` while continuing.

Before returning, read and apply [`../WORKFLOW_CONTINUATION.md`](../WORKFLOW_CONTINUATION.md), using `$recovery-loop` or `$verification-closeout` based on the observed result. Do not apply its user-facing continuation section while the completion gate remains open.
