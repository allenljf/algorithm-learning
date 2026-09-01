# Algorithm Learning Platform

This repository contains the Algorithm Learning Platform modular monorepo.

## Toolchains

- Java 21 is pinned in [`.java-version`](.java-version).
- Flutter 3.47.0 is pinned in [`apps/learning_app/.fvmrc`](apps/learning_app/.fvmrc).

Install the matching Java runtime and FVM-managed Flutter SDK before bootstrapping
either application. The initial application composition roots are deliberately
created in later tasks; this repository skeleton contains no runnable product
application yet.

## Entry points

- [`apps/learning_app`](apps/learning_app) will contain the Flutter Web, Android,
  and iOS client after ALG-003.
- [`services/api`](services/api) will contain the Java/Spring Boot API after
  ALG-002.
- [`infra`](infra) will contain local environment and deployment assets. Docker
  Compose is added by ALG-016.

See each directory's README for its task boundary and eventual local command.
