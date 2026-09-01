# Algorithm Learning Platform — Feature Plan

## Planning record

- Feature ID: `algorithm-learning-platform`
- Specification: [spec.md](spec.md)
- Planning method: `work-graph` with three perspectives (planner, implementer,
  evaluator), selected because this MVP crosses Flutter, Spring Security,
  PostgreSQL, Docker, and a public API contract.
- Execution contract: not selected. It is chosen by `$execution-strategy` for
  each ready task; this plan does not authorize implementation.

## Delivery strategy

Build a modular monorepo in dependency order. Establish repeatable toolchains
and the two application composition roots first. Create the PostgreSQL schema
before feature modules, then establish authentication and owner-scoped access.
Implement server features in contract order (tags, problems, solutions, reviews,
dashboard), followed by Flutter remote data contracts and the platform-specific
experiences that consume them. Finish with an end-to-end local Compose path and
the MVP acceptance suite.

The API remains the durable source of truth. Flutter has no problem-data cache
in the MVP; it maps API DTOs to domain models through repositories. Spring MVC
controllers stay transport-only, application services own transactions, and
JPA entities do not cross module boundaries.

## Planned phases

| Phase | Tasks | Outcome |
|---|---|---|
| 1. Foundations | ALG-001 to ALG-004 | Monorepo, Flutter and Spring roots, deterministic Flyway schema/test infrastructure |
| 2. Secured library API | ALG-005 to ALG-010 | Auth, owned tags/problems/solutions, review policy, and dashboard REST API |
| 3. Flutter shared data/auth | ALG-011 to ALG-012 | Session lifecycle and typed remote repository layer |
| 4. Product experiences | ALG-013 to ALG-015 | Web management, adaptive browse/review, dashboard/home |
| 5. Operability and acceptance | ALG-016 to ALG-018 | Compose, Kubernetes-target documentation, integrated acceptance evidence |

## Dependency and parallelization rules

- Only `ALG-001` is initially `ready`; every other task is `pending` until all
  listed predecessors are complete.
- Parallel groups identify tasks that may be worked concurrently only after
  their dependencies complete and only when each task preserves its declared
  file boundary.
- Backend modules may proceed in parallel only when one task does not modify
  another task's controller, service, migration, or shared contract files.
- Flutter product screens may proceed in parallel only after their shared
  repository/API contract task is complete; reusable shared UI changes require
  coordination and are therefore excluded from parallel groups.

## Architecture guardrails carried into execution

- Use Java + Spring Boot, PostgreSQL, Spring Web, Spring Data JPA, Spring
  Security, Bean Validation, Flyway, and Actuator only for production backend
  code.
- Scope all product reads and mutations by the authenticated user and return
  `404` for an unowned identifier.
- Keep raw passwords, tokens, cookies, notes, solution code, and search text out
  of logs. Refresh tokens are opaque, rotated, and stored only as hashes.
- Flutter widgets consume immutable state, not repositories or Dio. Repository
  contracts expose domain models; remote services own DTOs and HTTP mechanics.
- No task introduces offline problem persistence, solution execution, platform
  scraping, AI features, or production Kubernetes manifests.

## Completion definition

The feature is complete only when every task in [tasks.md](tasks.md) is closed
through `$verification-closeout`, the graph records all tasks as `completed`,
the documented Docker health path succeeds, and the acceptance suite covers the
MVP journeys and authorization boundaries defined by the specification.
