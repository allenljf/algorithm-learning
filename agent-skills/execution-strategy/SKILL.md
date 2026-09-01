---
name: execution-strategy
description: Use when a ready task needs an explicit agent, testing, documentation, and ambiguity-handling contract before implementation begins.
---

# Execution Strategy

Read root `AGENTS.md`, complete spec, plan, task, and graph node. Record this contract on the task before modifying code:

| Dimension | Options |
|---|---|
| Analysis | `single-agent` / `three-perspectives` |
| Test approach | `rapid` / `test-candidates` / `tdd` |
| Documentation | `update-docs` / `direct-development` |
| Ambiguity | `infer` / `ask-with-options` |

`rapid` permits implementation without tests. `test-candidates` implements first then writes candidate tests. `tdd` requires test-first behavior. `ask-with-options` stops for a user choice after presenting 2-3 options and a recommendation.

After recording a contract for a ready task, immediately invoke and perform
`$task-execute` in the same turn. Do not wait for another user instruction
unless `ask-with-options` produced a material unresolved decision.

Before returning, read and apply [`../WORKFLOW_CONTINUATION.md`](../WORKFLOW_CONTINUATION.md). When auto-continuing, the next user-facing continuation is selected only after the task-execution phase reaches closeout, recovery, or a genuine blocker.
