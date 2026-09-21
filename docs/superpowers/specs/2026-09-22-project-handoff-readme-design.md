# Project Handoff README Design

## Purpose

Produce the final, public-facing handoff documentation for Algorithm Learning.
It must let a new developer, evaluator, or user understand what the product
does, run it safely, navigate it, and trace the AI-assisted engineering process
without needing the historical task conversations.

## Deliverables

- `README.md`: English GitHub landing page and complete technical handoff.
- `README.zh-TW.md`: Traditional-Chinese counterpart with the same information
  architecture and operational meaning.
- Both documents link to one another at the top and link only to existing local
  source, infrastructure, and runbook files.

## Information architecture

1. Product overview, live showcase boundary, feature list, and repository map.
2. Prerequisites, environment configuration, local Docker lifecycle, and client
   development commands.
3. Backend: Java/Spring Boot package architecture, security and data ownership,
   Docker behavior, Flyway schema/table-and-column reference, and endpoint table.
4. Client: Compose Multiplatform targets, module/feature graph, technology
   rationale, major pages, and API-to-screen data flow.
5. User guide: account lifecycle, problem/solution/tag workflows, filtering,
   review progression, dashboard, language, and endpoint settings.
6. AI development workflow: source-of-truth artifacts, workflow state machine,
   every project-local workflow skill, and scenario-to-skill guidance.
7. Verification, deployment links, security boundaries, and project status.

## Diagrams

Mermaid diagrams will explain:

- repository and backend layer relationships;
- Compose `shared` / `composeApp` module and feature ownership;
- client request, repository, state-holder, and UI render/callback flow; and
- spec-driven AI workflow from intake through verification, recovery, and
  session handoff.

## Content rules

- Derive routes, schema fields, configuration names, and commands from source
  and checked-in runbooks; do not invent APIs or claim unverified deployment
  behavior.
- Identify the Compose client as current; do not instruct readers to use removed
  Flutter tooling.
- Document public Android builds as source/debug builds only, and iOS as a
  simulator demonstration; do not imply released mobile artifacts.
- Do not expose secrets. Explain only the names and locations of required
  non-secret configuration values.
- Preserve unrelated, pre-existing iOS/Xcode and `.firebase/` working-tree
  changes; they are excluded from the README commit and push.

## Acceptance criteria

- English and Traditional-Chinese READMEs each contain every planned section.
- Docker, client, API, database, endpoint, user-guide, and workflow claims map
  to checked-in source or documentation.
- Every diagram renders as standard Mermaid and every local Markdown link has a
  valid target.
- The README change passes Markdown/link validation and `git diff --check`.
- Only handoff documentation and its workflow artifacts are committed and
  pushed; protected pre-existing local changes remain untracked/unstaged.
