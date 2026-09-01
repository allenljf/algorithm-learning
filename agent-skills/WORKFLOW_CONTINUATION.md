# Workflow Continuation Contract

Every workflow skill must read and apply this contract before returning. It is a required user-facing closing section, not a suggestion.

## Auto-continuation

When `$execution-strategy` is invoked for a ready task, it records the contract
and immediately starts `$task-execute` in the same turn. This is one continuous
execution phase: do not emit a continuation prompt between those two skills.
Apply the normal continuation rules only after execution reaches closeout,
recovery, or a material user decision.

For an explicit “complete the whole task” request, partial implementation is
not a continuation boundary. A workflow response may be emitted only at task
closeout, terminal recovery/blocker, or after the user revokes that request.

This rule overrides the ordinary requirement to emit a continuation section at
the end of an intermediate skill response: the task-execution completion gate
must close first.

## Required closing output

End with exactly one continuation section. Decide the route automatically; never
ask the user to choose a conversation route and never output both routes.

Treat a workflow phase, not an individual message, as the decision boundary. Do
not switch conversations merely because a reply ended. Re-evaluate the route
when the current phase completes, the next action begins a materially different
phase, or current context no longer safely carries the needed evidence.

### Route selection

Choose **Continue in this conversation** when the next action continues the
active phase, depends on current tool output or reasoning not fully persisted in
artifacts, and the current context remains focused.

Choose **Start a new conversation** when a completed phase hands off to a
materially different phase and its state is fully persisted, when the current
context is crowded or stale, when an independent review is safer, or when the
user explicitly requests a new conversation.

When neither route has a clear advantage, choose **Continue in this
conversation** to preserve context.

### Continue in this conversation

Output only this section when that route is selected. Give one exact command the
user can send now. It must name the next skill, current feature or task ID, and
the immediate desired outcome.

```text
使用 $<next-skill>，針對 <feature-or-task-id> <immediate-outcome>。
```

When user input is the blocker, request only the material decision; do not ask
the user to choose a conversation route:

```text
請決定 <decision>；收到決定後，使用 $<next-skill>，針對 <feature-or-task-id> <immediate-outcome>。
```

### Start a new conversation

Output only this section when that route is selected. Give a complete copyable
prompt in a fenced `text` block. It must include:

1. the repository workflow preflight (`AGENTS.md`, `AI_DEVELOPMENT_GUIDE.md`, applicable framework guide, every existing full spec/plan/tasks artifact, graph node when it exists, and progress);
2. the exact feature or task ID and current status;
3. the selected execution contract when it exists;
4. the next skill and concrete desired outcome; and
5. any unanswered decision or failure evidence that the next agent needs.

Use this shape, replacing every bracketed value with current facts. Include only artifacts that already exist in the read list. State every missing artifact under `目前狀態`, and name the skill that will create it; never instruct a new agent to read a nonexistent file or graph node.

```text
請在此 repository 繼續 <feature-or-task-id>。

先完整閱讀：
- AGENTS.md
- AI_DEVELOPMENT_GUIDE.md
- flutter-dev-guide/AGENTS.md 與本任務適用的 guides/checklist
- <each existing full spec/plan/tasks path>
- agent-workflow/WORK_GRAPH.yaml 的 <task node, when it exists>
- agent-workflow/progress.md

目前狀態：<status and evidence; name missing artifacts and their creating skill>。
Execution contract：<contract or not selected yet>。

使用 $<next-skill>，完成 <immediate-outcome>。
<unanswered decision or failure evidence, if any>
```

Do not omit the selected section because the work is small, complete, blocked,
or already in a handoff skill. For completed work, recommend the next `ready`
task; if none exists, recommend `$workflow-intake` for the next request.
