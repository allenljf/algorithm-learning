# Algorithm Learning & Review Platform — Requirements

## 1. 專案目標

建立一個個人使用的跨平台演算法學習與複習平台，用來管理已完成的 HackerRank、LeetCode 與其他平台題目，並在行動裝置上快速複習。

Web 是主要的題目管理後台；Android 與 iOS 專注於瀏覽、複習與記錄熟悉程度。架構需預留 Spaced Repetition 與 AI 輔助學習的擴充空間，但不在 MVP 優先實作。

## 2. 平台與技術方向

| 領域 | 需求 |
|---|---|
| 前端平台 | Kotlin Compose Multiplatform 單一程式碼庫，支援 Android 與 Web（Wasm）；iOS/Desktop 為後續目標 |
| 前端架構 | Kotlin `commonMain` 共用 UI/domain/state、Repository Pattern、composition root 注入依賴 |
| 前端套件方向 | Compose Multiplatform、Ktor、kotlinx；實際選型與版本以 `apps/multiplatform/gradle/libs.versions.toml` 與 `COMPOSE_GUIDE.md` 為準 |
| 後端 | Java + Spring Boot REST API；此為已確認且不可替換的決策 |
| 資料庫 | PostgreSQL |
| 資料存取 | Spring Data JPA（JPA/Hibernate） |
| 開發與部署 | Docker Compose 本機環境；Kubernetes 為後續部署目標 |

### 2.1 已確認、不可替換的後端決策

- Backend production code 必須使用 Java + Spring Boot；REST API 供 Compose
  Multiplatform client（Android 與 Web）共用。
- 必須使用 PostgreSQL、Spring Web、Spring Data JPA、Spring Security、Bean
  Validation、Flyway 與 Spring Boot Actuator。
- 目標是展示 Java 後端面試能力：分層架構、JWT authentication/authorization、
  migration、測試、health check、Docker 與 Kubernetes-ready 設定。
- 不得改用 FastAPI、Ktor、Go 或其他後端語言／框架。任何變更都必須先取得使用者
  明確同意，並同步更新本需求基線與正式 SPEC。

### 2.2 前端 client 遷移（已確認）

- 原 Flutter client 已由 `compose-multiplatform-migration` 的 `CMP-001..CMP-007`
  遷移為 Kotlin Compose Multiplatform client `apps/multiplatform`；原始碼與其
  Flutter 專用開發指南已在 `FRR-001` 完整移除。
- Compose client 沿用同一後端 REST 契約，不變更 API；其建構與測試準則見
  `apps/multiplatform/COMPOSE_GUIDE.md`。
- 對等性與遷移記錄見
  `specs/compose-multiplatform-migration/retirement.md`。

## 3. 使用者與產品範圍

第一版以單一使用者為主要情境，但資料模型與 API authorization 必須保留未來多使用者的可能性。

### 3.1 Web 管理後台

- 管理已做過的題目：新增、瀏覽、搜尋、篩選、編輯、刪除。
- 檢視 dashboard：總題數、Easy/Medium/Hard 分布、依 Topic 分布、待複習題目、最近新增題目。
- 編輯題目資訊、個人筆記與多個解法。
- 初版 code editor 使用一般 multiline editor；語法高亮為後續改善。

### 3.2 Mobile 複習 App

- 瀏覽題目、搜尋與篩選。
- 檢視題目摘要、自己的解題思路與 solution。
- 進入 Review Mode，逐步揭露資訊，不可一開始直接顯示答案。
- 記錄每題的熟悉程度與複習資訊。
- 首頁顯示 Today’s Review、Weak Topics、Recent Problems、Random Problem 與統計。

## 4. 功能需求

### 4.1 Problem Management

每個 Problem 至少包含：

| 欄位 | 說明 |
|---|---|
| `id` | 內部識別碼 |
| `title` | 題目名稱 |
| `platform` | `HackerRank`、`LeetCode` 或 `Other` |
| `externalProblemId` | 外部平台的題目 ID，可為空 |
| `externalUrl` | 外部題目 URL，可為空 |
| `difficulty` | `Easy`、`Medium` 或 `Hard` |
| `description` | 使用者撰寫的題目摘要，而非完整轉載題目 |
| `notes` | 個人解題思路 |
| `keyInsight` | 核心洞察 |
| `timeComplexity` | 時間複雜度 |
| `spaceComplexity` | 空間複雜度 |
| `mistakes` | 曾犯錯誤 |
| `interviewNotes` | 面試重點 |
| `createdAt` / `updatedAt` | 建立與更新時間 |

題目支援 list、search、difficulty/platform/tag/review-status filter、create、edit 與 delete。

### 4.2 Solutions

一個 Problem 可以有多個 Solution，不可將 solution code 直接存入 Problem 資料表。

每個 Solution 至少包含：`id`、`problemId`、`language`、`code`、`explanation`、`createdAt`、`updatedAt`。

初期語言至少支援 Kotlin、Java、Python、Dart。

### 4.3 Tags

一個 Problem 可有多個 Tags，資料庫使用 many-to-many relationship。

預期 Tags 包含 Array、HashMap、String、Two Pointer、Sliding Window、Binary Search、Stack、Queue、Tree、Graph、Dynamic Programming、BFS、DFS、Greedy，但系統不得將此清單視為固定上限。

### 4.4 Review System

每題可記錄：

- `confidence`
- review status
- last reviewed time
- next review time
- review count
- notes

`confidence` 定義：

| 值 | 意義 |
|---:|---|
| 0 | 完全不會 |
| 1 | 看 Hint 才會 |
| 2 | 看思路才會 |
| 3 | 可以自己寫 |
| 4 | 非常熟 |

第一版不實作複雜的 spaced-repetition 演算法，但資料設計必須可支援未來擴充。

### 4.5 Review Mode

Review Mode 順序如下：

```text
Problem → Think → Hint → My Approach → Solution → Mark confidence
```

題目詳情顯示順序：Title、Difficulty、Tags、Problem summary、My Approach、Key Insight、Time Complexity、Space Complexity、Common Mistakes、Interview Notes、Solution Code、Review。

## 5. 資料庫需求

初始關聯模型包含：

```text
users
problems
solutions
tags
problem_tags
reviews
```

正式資料庫設計必須定義 primary key、foreign key、index、unique constraints、created/updated timestamps、cascade behavior 與 nullable fields，並避免過度設計。

## 6. REST API 需求

初始 API 範圍：

| 類別 | Endpoints |
|---|---|
| Authentication | `POST /api/auth/register`、`POST /api/auth/login` |
| Problems | `GET /api/problems`、`GET /api/problems/{id}`、`POST /api/problems`、`PUT /api/problems/{id}`、`DELETE /api/problems/{id}` |
| Solutions | `GET /api/problems/{id}/solutions`、`POST /api/problems/{id}/solutions`、`PUT /api/solutions/{id}`、`DELETE /api/solutions/{id}` |
| Tags | `GET /api/tags`、`POST /api/tags` |
| Reviews | `GET /api/reviews/today`、`POST /api/reviews`、`GET /api/reviews/history` |

實際 request/response schema、錯誤模型與路由調整需在 SPEC/API design 階段確認。

## 7. 安全需求

- Authentication。
- 安全 password hashing。
- JWT 或其他合理 session/token mechanism。
- API authorization。
- 使用 environment variables 管理設定。
- 資料庫憑證不得 hardcode。
- secrets 不得 commit。
- 提供 `.env.example`，不得提交包含真實 secret 的 `.env`。

## 8. Docker 與部署需求

- 開發環境可透過 `docker compose up -d` 啟動 PostgreSQL 與 Backend。
- Compose Multiplatform client 可在本機開發執行（見 `COMPOSE_GUIDE.md`）。
- 後續 Kubernetes 部署需涵蓋 API、設定與 secrets；PostgreSQL 的實際部署策略於 SPEC 確認。

## 9. 測試與品質需求

Backend 至少涵蓋 unit tests、Spring MVC/API tests 與 PostgreSQL persistence
integration tests；核心授權與 migration 行為必須可驗證。

Compose client 至少涵蓋 data-layer tests、state-holder tests、Compose 畫面 structure tests，以及跨平台 acceptance suite（見 `COMPOSE_GUIDE.md`）。第一版不要求 100% coverage，但核心 business logic 必須有測試。

程式品質原則：SOLID、Clean Architecture、separation of concerns、small functions、meaningful naming、避免不必要 abstraction、避免過早最佳化、避免重複與無理由套件。

## 10. Copyright 與內容限制

本 App 儲存使用者自己的學習資料，不大量複製 HackerRank 或 LeetCode 完整題目內容。資料主要保存 platform、problem ID、URL、title、自己的 summary、thinking、solution 與 notes；完整題目透過 external URL 回原平台查看。

## 11. 開發策略與路線圖

採 incremental development，不一次產生大型專案。

1. 建立 repository structure：Client、Backend、Database、Docker。
2. 完成 PostgreSQL schema。
3. 完成 Backend：health check、Problem CRUD、Solution CRUD、Tag CRUD。
4. 完成 Client Web：Problem List、Problem Editor、CRUD。
5. 完成 Client 瀏覽與 Review：Problem List、Problem Detail、Review。
6. Authentication。
7. Tests。
8. Docker / deployment。
9. Spaced Repetition。
10. AI features。

每個 phase 完成後需 formatter、static analysis、task 明定測試與錯誤修正，並回報完成內容及下一個建議 phase。

## 12. Future AI Features

未來可支援：分析解法、判斷時間複雜度、提供 Hint、根據錯誤產生 Review Question、整理 Interview Answer、依弱項推薦題目，以及與最佳解法比較。AI 提示優先採「不直接給答案，只給 Hint」的互動方式。

## 13. Git 要求

使用小而清楚的 commit，例如：

- `feat: add problem CRUD API`
- `feat: add problem list screen`
- `feat: add problem editor`
- `feat: add review flow`
- `test: add problem repository tests`

不得把所有功能合併成單一巨大 commit。

## 14. 後續規格工作

本文件是需求基線，不取代實作規格。正式規格位於
`specs/algorithm-learning-platform/spec.md`；它必須維持整體 architecture、
monorepo structure、Compose Multiplatform client / Java Spring Boot backend
structure、ERD、schema、API contract、authentication、Docker/Kubernetes 與 MVP
task graph 的可追溯性。

前端遷移的正式規格位於 `specs/compose-multiplatform-migration/spec.md`，其任務
與對等性記錄見同目錄 `tasks.md`、`plan.md` 與 `retirement.md`。
