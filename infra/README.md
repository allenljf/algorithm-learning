# Infrastructure

This directory holds non-application operational assets.

- [`env/.env.example`](env/.env.example) documents required local environment
  variable names with placeholders only.
- `compose.yaml` is intentionally deferred to ALG-016.
- Kubernetes target documentation is intentionally deferred to ALG-017.

When Compose is introduced, its local entry point will be:

```sh
docker compose -f infra/compose.yaml up -d --build
```
