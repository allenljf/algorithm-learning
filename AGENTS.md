# 專案 Agent 入口

所有分析、規劃、實作、驗證或交接任務開始前，先完整閱讀 [AI_DEVELOPMENT_GUIDE.md](AI_DEVELOPMENT_GUIDE.md)。

任何任務在執行（寫入檔案、修改程式、執行驗證或委派 agent）前，必須依序讀取：

1. 本功能已存在的完整 `spec.md`、`plan.md`、`tasks.md`；不可只根據摘要實作。若目前 workflow 正在建立其中一項工件，先讀取已存在工件，並在 progress 記錄待建立項目。
2. `agent-workflow/WORK_GRAPH.yaml` 中已存在的對應 task 節點與 `agent-workflow/progress.md`。尚未有節點時，由 `work-graph` 建立。

若任務涉及客戶端實作，另讀取 `apps/multiplatform/COMPOSE_GUIDE.md` 的適用章節。

使用 `agent-skills/` 的 workflow。每個已完成 task 都要由 `verification-closeout` 更新工作圖與進度；中斷或換對話時使用 `session-handoff`。只執行 task 明定的驗證，驗證通過並完成提交後即回報，除非 task 明定，否則不額外增加 review 或重跑。

每個 workflow skill 回覆前都必須讀取並遵守 `agent-skills/WORKFLOW_CONTINUATION.md`：以 workflow phase 為界自動判定並只輸出一種續作方式（同對話精確指令或新對話完整 prompt），不得要求使用者選擇對話路徑。
