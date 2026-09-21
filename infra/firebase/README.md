# Firebase Hosting public showcase

This directory documents the non-secret Firebase Hosting boundary for the
public Compose Wasm showcase. The checked-in root [configuration](../../firebase.json)
selects project `algorithmlearning`, deploys the production Wasm bundle from
`apps/multiplatform/composeApp/build/firebaseHosting`, and
orders its rewrites so `/api/**` reaches the same project's
`algorithm-learning-api` Cloud Run service in `asia-east1` before the SPA
fallback serves `index.html`.

## Build and deploy boundary

Build the bundle with its canonical same-origin API default:

```sh
cd apps/multiplatform
./gradlew :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://algorithmlearning.web.app
```

That task finalizes the deploy directory by combining the optimized Wasm
artifacts with the processed `index.html`; it is therefore the only build step
needed before a future `firebase deploy --only hosting` in CDS-006.

CDS-006, not this configuration task, is authorized to create or select the
Firebase/GCP project, configure the Cloud Run service, set its exact
`https://algorithmlearning.web.app` CORS origin, and deploy Hosting. It must
not change the existing production project's delivery topology or copy any
Neon, signing, or service-account credential into the repository.

The browser uses `/api/**` at `https://algorithmlearning.web.app`; the API's
rotating refresh cookie is named `__session`, the name Firebase Hosting forwards
to a rewritten dynamic request. The cookie remains `HttpOnly`, `Secure`,
`SameSite=Lax`, scoped to `/api/v1/auth`, and has the existing 30-day lifetime.
