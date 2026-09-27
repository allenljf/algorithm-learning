# README Use-case Guide Plan

## Architecture

This is a documentation-only enhancement. The existing `User guide` / `使用者
操作手冊` sections remain the single reader entry point; they gain a short
onboarding sequence followed by equivalent practical scenarios.

## Files and responsibilities

| File | Responsibility |
|---|---|
| `README.md` | Explain website use in English with three actionable learner scenarios. |
| `README.zh-TW.md` | Provide the equivalent Traditional-Chinese scenarios. |
| Workflow artifacts | Preserve the scope, task contract, and closeout evidence. |

## Verification strategy

- Confirm both READMEs include the three scenario headings.
- Confirm the wording references the implemented navigation and review flow.
- Run `git diff --check`.

## Dependencies and rollout

No runtime, deployment, or migration dependency exists. The documentation is
available to repository readers as soon as it is committed.
