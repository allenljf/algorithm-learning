---
name: recovery-loop
description: Use when a task's explicitly required verification fails and the project needs a bounded repair process or a forced inference attempt.
---

# Recovery Loop

Read root `AGENTS.md`, full spec, task contract, and latest failure output. Choose one mode:

| Mode | Rule |
|---|---|
| `max-3-healing` | Diagnose, change, and rerun only task verification; stop after three failed rounds. |
| `force-once` | Make one recorded best inference, continue until verification succeeds or an external blocker prevents progress. |

Record each diagnosis, change, command, and result in `progress.md`. At a terminal failure, do not claim completion: provide evidence, cause, and 2-3 next options with recommendation. On success, return to `$verification-closeout`.

Before returning, read and apply [`../WORKFLOW_CONTINUATION.md`](../WORKFLOW_CONTINUATION.md), using `$verification-closeout` after recovery or `$session-handoff` after terminal failure.
