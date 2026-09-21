# Algorithm Learning Platform

This repository contains the Algorithm Learning Platform modular monorepo.

## Toolchains

- Java 21 is pinned in [`.java-version`](.java-version); the Spring Boot API
  builds with the Maven Wrapper.
- The Compose Multiplatform client builds with the Gradle wrapper pinned in
  [`apps/multiplatform/gradle/wrapper`](apps/multiplatform/gradle/wrapper) on a
  JDK 17+ toolchain.

## Entry points

- [`apps/multiplatform`](apps/multiplatform) is the active client: a Kotlin
  Compose Multiplatform application (Android + Web/Wasm) following
  [`COMPOSE_GUIDE.md`](apps/multiplatform/COMPOSE_GUIDE.md).
- [`services/api`](services/api) is the Java/Spring Boot API.
- [`infra`](infra) holds local environment and deployment assets, including
  [`infra/acceptance`](infra/acceptance) release-evidence boundaries.

See each directory's README for its task boundary and local command.
