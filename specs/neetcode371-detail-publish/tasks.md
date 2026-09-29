# NeetCode #371 Detail Lesson and Publishing Tasks

## Phase 1 — #371 lesson, Detail rendering, and publication

### N371-001 — Complete and publish the #371 Detail lesson

- Deliverable: AC-371-01 through AC-371-04, including structured content,
  importer read-back coverage, localized Detail UI coverage, production sync,
  production bundle, Hosting deployment, and public reload evidence.
- Depends on: none. Parallel group: `neetcode371-detail-publish`.
- Execution contract: `single-agent`; `tdd`; `update-docs`; `infer`.
- Verification: `cd apps/multiplatform && ./gradlew :shared:allTests`; `cd
  apps/multiplatform && ./gradlew :composeApp:allTests`; `cd
  apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserTest`; `python3
  docs/algorithm/neetcode150/checks/verify.py`; `python3
  tools/test_neetcode150_import.py`; `python3 tools/neetcode150_import.py
  --validate`; `python3 tools/neetcode150_import.py --import-and-verify`; `cd
  apps/multiplatform && ./gradlew :composeApp:wasmJsBrowserProductionWebpack
  -PapiBaseUrl=https://allenljf-algorithm.web.app`; `firebase deploy --only
  hosting`; public #371 reload assertion; `git diff --check`.
- Status: completed — importer HTTP method repair, scoped production API
  synchronization, Hosting publication, authenticated deep-link reload, rendered
  bilingual lesson content, and the official-link navigation are verified.
  authority to use the existing import account in a browser.

### N371-002 — Correct CJK rendering on published Detail deep links

- Deliverable: AC-371-05. Map Compose Web resources from the hosting root so
  the existing bundled Noto Sans TC font works below `/problems/{id}`.
- Depends on: N371-001. Parallel group: `neetcode371-detail-publish`.
- Execution contract: `single-agent`; `tdd`; `update-docs`; `infer`.
- Verification: `cd apps/multiplatform && ./gradlew :composeApp:verifyWasmCjkFont
  :composeApp:wasmJsBrowserProductionWebpack -PapiBaseUrl=https://allenljf-algorithm.web.app`;
  `firebase deploy --only hosting`; authenticated public #371 reload and visual
  Chinese-glyph assertion; `git diff --check`.
- Status: in progress.
