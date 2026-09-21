# Project Handoff README Specification

## Document status

- Feature ID: `project-handoff-readme`
- Status: intake complete; pending design-spec review
- Intake mode: `quick-analysis`
- Date: 2026-09-22

## Goal

Publish a detailed bilingual final README for the Algorithm Learning project.
It must serve as both a polished repository landing page and a factual handoff
manual for operators, developers, and end users.

## Scope

- Replace the minimal English root README with a complete English project guide.
- Add `README.zh-TW.md` as an equivalent Traditional-Chinese guide.
- Explain backend architecture, Docker local operation, database schema, and
  API endpoints from the checked-in Spring Boot source and Flyway migrations.
- Explain Compose Multiplatform modules, major features/pages, technology
  choices, and end-to-end data flow from API to rendered UI.
- Include a usable user-operation manual and an explanation of the repository's
  spec-driven AI workflow, its skills, and scenario guidance.
- Include Mermaid architecture and flow diagrams that render on GitHub.
- Commit and push only this documentation work and its workflow artifacts.

## Non-goals

- No product behavior, schema, API, deployment resource, or client code change.
- Do not stage or alter the pre-existing iOS/Xcode changes, Firebase cache, or
  other user-owned working-tree changes.
- Do not publish credentials, secret values, or an Android/iOS release artifact.

## Acceptance criteria

- **AC-HRD-01:** `README.md` and `README.zh-TW.md` are complete counterpart
  handoff guides with navigation links between languages.
- **AC-HRD-02:** Both guides document the requested backend, Docker, database,
  API, Compose client, user-guide, and AI-workflow subjects accurately.
- **AC-HRD-03:** Module, request/data, and workflow diagrams use valid Mermaid
  and explain the relationships named in the request.
- **AC-HRD-04:** Documentation validation and `git diff --check` pass.
- **AC-HRD-05:** The documentation-only commit is pushed successfully without
  including protected existing local changes.

## Assumptions

The requested bilingual output is two separate complete files: English is the
repository default because GitHub displays `README.md`; Traditional Chinese is
the reader-equivalent counterpart. Existing detailed runbooks remain canonical
for operator-only GCP/Neon credentials and deployment tasks; the README links
to them rather than duplicating sensitive procedures.
