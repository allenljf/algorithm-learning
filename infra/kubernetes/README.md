# Kubernetes deployment target

This document describes the future production target for the Algorithm Learning
API. It is an architecture target, not a deployment package: this MVP does not
ship Kubernetes manifests, select a cloud provider, or run PostgreSQL inside a
production cluster.

## Runtime topology

```text
Internet
  │ TLS
Ingress ──► ClusterIP Service ──► API Deployment ──► managed PostgreSQL
                                      │
                                  migration Job
```

The API is a stateless Java 21 Spring Boot modular monolith. It has no durable
local state, so a Deployment may scale it horizontally after the migration has
completed. A ClusterIP Service exposes port 8080 only inside the cluster. An
Ingress terminates TLS and routes the API hostname to that Service; its provider
or controller is deliberately unspecified.

## API Deployment

The future Deployment should:

- run the image built from `services/api/Dockerfile` as a non-root user;
- expose container port 8080 and use resource requests/limits chosen from
  observed production traffic rather than guessed defaults;
- receive non-secret configuration through a ConfigMap and credentials/keys
  through Secrets or an external secret manager;
- use a rolling-update policy that preserves available replicas; and
- avoid multiple API replicas starting Flyway concurrently.

Configuration names remain consistent with local Compose: `DATABASE_URL`,
`DATABASE_USERNAME`, `DATABASE_PASSWORD`, `APP_CORS_ALLOWED_ORIGINS`,
`APP_JWT_KEY`, `APP_JWT_ISSUER`, `APP_JWT_AUDIENCE`, and
`APP_REFRESH_HASH_KEY`. ConfigMaps may contain only non-secret values such as
the JDBC host/name, browser origins, issuer, and audience. Database passwords,
JWT signing material, and refresh-token HMAC keys belong in Secrets or an
external secret manager and must never be baked into an image or committed.

## Health and traffic handling

Kubernetes probes map directly to Actuator:

| Probe | Endpoint | Purpose |
| --- | --- | --- |
| Startup | `/actuator/health/liveness` | Allow JVM, Spring, and Flyway startup before liveness failures count. |
| Liveness | `/actuator/health/liveness` | Restart a process that can no longer make progress. |
| Readiness | `/actuator/health/readiness` | Keep an unready API, including one without usable dependencies, out of Service endpoints. |

The Service must route traffic only to Ready pods. Ingress enforces TLS and
should set request size, timeout, and forwarding-header behavior deliberately
for the chosen controller. The application retains request IDs and its existing
log redaction rules: headers, cookies, passwords, tokens, search text, notes,
and solution code must not enter logs.

## Migration release flow

Flyway exclusively owns schema changes; Hibernate validates but never generates
DDL. A release should run exactly one migration Job with the same API image and
database configuration before rolling out the Deployment version that depends
on the migration. The Job must be observed to successful completion before API
pods advance. Forward-compatible, additive migrations are preferred so an
interrupted rollout can retain service from the previous API version. Rollback
does not apply a reverse Flyway migration; it restores a compatible API image
or follows a separately approved data-recovery procedure.

Local Compose starts a single API instance and therefore runs Flyway safely at
startup. Kubernetes separates that responsibility into the one-shot Job before
replicas are introduced.

## PostgreSQL expectations

Production PostgreSQL is a managed service, outside the Kubernetes cluster. It
must provide encrypted transport, restricted network access from API workloads,
automated backups, retention appropriate to product data, and point-in-time
recovery. Credential rotation is performed through the secret-management path
and coordinated with API rollout. An in-cluster StatefulSet is acceptable only
for non-production experimentation; it is not a production substitute for the
managed database, backup, and recovery responsibilities.

## Operations boundaries

- Metrics, alerting, log retention, image registry, TLS certificate management,
  and the exact Kubernetes controller/provider remain deployment decisions.
- Before a production rollout, verify migration backup/restore procedures,
  least-privilege database access, secret rotation, TLS routing, probe behavior,
  and a rollback plan against the selected platform.
- This document intentionally does not add Helm charts, Kustomize overlays,
  Deployment/Service/Ingress manifests, or database manifests. Those require a
  separate approved production-delivery task.
