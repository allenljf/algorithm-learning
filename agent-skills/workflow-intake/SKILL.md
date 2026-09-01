---
name: workflow-intake
description: Use when a new feature, change request, or unclear product need must enter this project's spec-driven workflow.
---

# Workflow Intake

Read root `AGENTS.md` first. Choose one mode with the user or state the recommended mode:

| Mode | Use when | Output |
|---|---|---|
| `grill-me` | Stakes, assumptions, or trade-offs need stress-testing | Constraints and decision record |
| `brainstorm` | The product or architecture is still open-ended | Proposed design choices |
| `quick-analysis` | A bounded request is already clear | Concise requirements analysis |

Create or update `specs/<feature>/spec.md` from the template. It must state scope, non-goals, acceptance criteria, constraints, and assumptions. Do not plan or implement before the spec exists. Next skill: `$spec-governance`.

Before returning, read and apply [`../WORKFLOW_CONTINUATION.md`](../WORKFLOW_CONTINUATION.md), using `$spec-governance` as the next skill.
