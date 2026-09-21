# Project Handoff README Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Deliver and publish accurate English and Traditional-Chinese project handoff READMEs.

**Architecture:** `README.md` is the English GitHub landing page; `README.zh-TW.md` mirrors its information architecture in Traditional Chinese. Both are generated from checked-in Java, Flyway, Compose, Docker, and workflow artifacts, with Mermaid diagrams explaining ownership and data flow.

**Tech Stack:** Markdown, GitHub Mermaid, Java 21/Spring Boot source, PostgreSQL/Flyway migrations, Docker Compose, Kotlin Compose Multiplatform, Git.

**Spec:** `docs/superpowers/specs/2026-09-22-project-handoff-readme-design.md` and `specs/project-handoff-readme/spec.md`

## Global Constraints

- Document only checked-in behavior, configuration names, routes, and database fields.
- Keep `README.md` as English and make `README.zh-TW.md` a complete Traditional-Chinese counterpart.
- Do not expose secrets, alter runtime code, or imply published Android/iOS artifacts.
- Preserve existing iOS/Xcode and `.firebase/` working-tree changes; never stage them.
- Use standard GitHub Mermaid and validate every local Markdown link.

## Review Focus

- Removed Flutter paths must not appear as current setup or verification instructions; grep for `apps/learning_app` and `flutter-dev-guide` in the two READMEs.
- API documentation must use the deployed `/api/v1` routes and correct HTTP verbs; compare it to controller mappings.
- Database documentation must include V2 adaptive review snapshot fields; compare it to both Flyway migrations.
- Docker instructions must warn that `down --volumes` destroys local data and must not show secret values.
- English and Chinese section anchors must cover the same reader questions; compare their heading lists after writing.

---

### Task 1: Build the fact inventory and document skeleton

**Files:**
- Modify: `README.md`
- Create: `README.zh-TW.md`
- Read: `services/api/src/main/resources/db/migration/V1__initial_product_schema.sql`, `services/api/src/main/resources/db/migration/V2__adaptive_review_schedule.sql`, `services/api/src/main/java/dev/algorithmlearning/api/**/api/*Controller.java`, `apps/multiplatform/COMPOSE_GUIDE.md`, `infra/README.md`, `infra/compose.yaml`, `agent-skills/*.md`

**Interfaces:**
- Consumes: Flyway DDL, Spring controller annotations, Compose architecture guide, Docker compose configuration, and project workflow skill contracts.
- Produces: a one-to-one bilingual heading structure and a source-backed fact inventory used by both README files.

- [ ] **Step 1: Enumerate the facts that must be identical in both languages**

Record the client modules (`shared`, `composeApp`, `iosApp`), backend areas
(`auth`, `problems`, `solutions`, `tags`, `reviews`, `dashboard`), local Docker
services (`postgres`, `api`), Flyway tables, and each REST route/verb.

- [ ] **Step 2: Define the complete shared heading sequence**

Use this exact reader order: overview; features; architecture; prerequisites and
quick start; Docker; backend; database; API; client; user guide; AI workflow;
testing/deployment/security; repository map; license/status.

- [ ] **Step 3: Add language-switch links and table of contents targets**

Place an English/繁體中文 switch at the top of both files and use stable
GitHub-compatible headings so all local contents links resolve.

### Task 2: Author the English handoff README

**Files:**
- Modify: `README.md`
- Read: sources named in Task 1 and `apps/multiplatform/ANDROID_RELEASE.md`, `apps/multiplatform/iosApp/README.md`, `infra/gcp/README.md`, `infra/firebase/README.md`

**Interfaces:**
- Consumes: Task 1 fact inventory and heading sequence.
- Produces: the canonical English content and Mermaid source to translate exactly in Task 3.

- [ ] **Step 1: Write product, run, and Docker sections**

Describe the Java 21 API, PostgreSQL, and Compose Android/Web client; include
the `.env.example` copy command, `docker compose -f infra/compose.yaml up -d --build`,
health probes, normal shutdown, and the explicit destructive-volume warning.

- [ ] **Step 2: Write backend, database, and API references**

Explain controller → application → persistence ownership and JWT/refresh-session
boundaries. Use compact tables for `users`, `auth_sessions`, `problems`,
`solutions`, `tags`, `problem_tags`, and `reviews`, including V2 adaptive
columns. List register, login, refresh, logout, current user, problems,
solutions, tags, reviews, and dashboard endpoints with their verb and purpose.

- [ ] **Step 3: Write Compose modules, features, and data flow**

Explain why `shared` owns domain/contracts/state/data adapters and `composeApp`
owns thin screens, with `iosApp` as a shell. List Sign in/Register, Problem
Library, Problem Editor/Detail, Review Mode, Dashboard/Home, and Settings.
Include one module/feature Mermaid diagram and one request-to-render Mermaid
diagram with `Ktor remote → repository → view model/state → composable → callback`.

- [ ] **Step 4: Write user and AI-workflow manuals**

Give numbered user paths for sign-in, managing problems/tags/solutions, filtering,
staged confidence review, dashboard, language, and endpoint settings. Explain
the `workflow-intake → spec-governance → work-graph → execution-strategy →
task-execute → verification-closeout` path, recovery, handoff, graph status,
and a scenario table that maps new feature, ambiguous requirement, planned
change, implementation, validation failure, and pause/resume to the appropriate
skill.

### Task 3: Author the Traditional-Chinese counterpart

**Files:**
- Create: `README.zh-TW.md`
- Read: `README.md`

**Interfaces:**
- Consumes: the complete English document from Task 2.
- Produces: a Traditional-Chinese document with matching technical scope,
  commands, diagram semantics, and safe-operation warnings.

- [ ] **Step 1: Mirror the heading hierarchy and links**

Preserve the English document's section order, tables, command blocks, and
Mermaid node relationships; translate explanatory prose and labels into clear
Traditional Chinese.

- [ ] **Step 2: Keep machine-readable material identical**

Retain command lines, file paths, environment-variable names, HTTP verbs,
routes, schema field names, module names, and workflow skill names exactly.

- [ ] **Step 3: Verify translation completeness**

Compare heading counts and ensure every English reader-facing section has a
Chinese counterpart with the same operational conclusion.

### Task 4: Validate, close out, commit, and push

**Files:**
- Modify: `README.md`, `README.zh-TW.md`
- Modify: `specs/project-handoff-readme/plan.md`, `specs/project-handoff-readme/tasks.md`, `agent-workflow/WORK_GRAPH.yaml`, `agent-workflow/WORK_GRAPH.md`, `agent-workflow/progress.md`

**Interfaces:**
- Consumes: complete bilingual READMEs and documentation workflow artifacts.
- Produces: validated, committed, pushed final handoff documentation.

- [ ] **Step 1: Run documentation assertions**

Run `git diff --check`; assert both README files exist; use `rg` to ensure no
removed Flutter setup path is presented; inspect every Markdown link target;
and compare English/Chinese heading lists.

- [ ] **Step 2: Record and run the task-limited verification contract**

Create the project work-graph node with the exact README existence, link-check,
heading-parity, and `git diff --check` commands; mark it complete only after all
commands pass.

- [ ] **Step 3: Stage only owned files and commit**

Stage `README.md`, `README.zh-TW.md`, and the documentation workflow artifacts
by explicit path. Confirm `git status --short` leaves the existing iOS/Xcode and
`.firebase/` changes unstaged, then create a `docs: finalize bilingual project handoff`
commit.

- [ ] **Step 4: Push the committed handoff work**

Run `git push` against the current branch, report the remote result, and leave
user-owned working-tree changes intact.
