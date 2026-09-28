# HackerRank 實戰與複習

[回學習入口](README.md)｜[平台資訊來源](來源查證.md)

## 1. LeetCode 解答怎麼搬到 HackerRank？

演算法思路可以搬，外殼要看題目。LeetCode 常要 `class Solution` 中的指定函式；HackerRank 可能給 `object Result` 的函式、已完成的 main，也可能要完整程式。**保留它提供的名稱、參數型別和輸出方式**，把核心逻輯搬進去，別整份 Solution 直接覆蓋模板。

| 題目提供什麼 | 你要做什麼 |
|---|---|
| 指定函式並由主程式接收回傳值 | 寫函式、回傳結果；不要自己另讀 stdin |
| 指定函式但要求列印 | 依題意列印，不能只 return |
| 完整模板含 OUTPUT_PATH 寫檔 | 保留既有輸出程式，只補演算法 |
| 空白編輯器，要求讀入並輸出 | 自己寫 main、讀入、計算、輸出 |

平台一般 Kotlin 支援與出題者開放的語言是兩件事。查證日的官方一般 Coding 環境為 Kotlin 1.9.0、4 秒、512 MB，但請看實際題目的語言選單與限制；不要把此表當成你的台積電考試承諾。[官方執行環境](https://candidatesupport.hackerrank.com/articles/2201684846-execution-environment)、[出題語言設定](https://support.hackerrank.com/articles/2474669643-coding-question)。

## 2. 先熟悉兩種輸入規模

小型練習可以 `readln().trim().split(...)`，易懂；大量整數輸入時，逐行切分會產生很多短字串。下面是可重用的數字讀取器，讀取空格、換行分隔的十進位整數，支援負數。初學先會使用，不必第一天背下整份。

**自製練習規格**：第一個數是 n，接著讀 n 個整數，輸出總和。例：輸入 `4` 接著 `5 -2 8 1`，輸出 12。這只是 I/O 練習，不是假冒公司考題。數字、n 與總和應在型別範圍內。

```kotlin
import java.io.BufferedInputStream

private class FastScanner {
    private val input = BufferedInputStream(System.`in`)

    fun nextLong(): Long {
        var c = input.read()
        while (c <= 32 && c != -1) c = input.read()
        check(c != -1) { "輸入提前結束" }
        var sign = 1L
        if (c == '-'.code) {
            sign = -1L
            c = input.read()
        }
        var value = 0L
        while (c in '0'.code..'9'.code) {
            value = value * 10 + (c - '0'.code)
            c = input.read()
        }
        return value * sign
    }
}

fun main() {
    val scanner = FastScanner()
    val n = scanner.nextLong().toInt()
    var sum = 0L
    repeat(n) { sum += scanner.nextLong() }
    print(sum)
}
```

`BufferedInputStream` 是先整批讀進緩衝區，避免每個字元都直接碰外部輸入；`System.in` 是標準輸入。此數字模板用於合法、可表示的題目輸入，不是任意文字解析器。若題目要讀含空格的整行句子，改用行讀取，不能照套。

多筆輸出可放 StringBuilder，最後一次 print。不要在正式 STDOUT 印「請輸入數字」「答案是」或除錯變數，平台通常比對格式；除錯可先用 `System.err.println`，提交前移除。[官方作答說明](https://candidatesupport.hackerrank.com/articles/9623161883-answer-coding-questions)

## 3. 每次寫完固定查這幾件事

1. **題意**：要回傳索引、值、次數、長度，還是直接改輸入？連續與不連續有沒有搞混？
2. **邊界**：最小有效輸入、全相同、全零、全負、沒有答案、答案在頭尾；只測題目允許的條件。
3. **數字**：乘法先轉 Long 了嗎？平均值有小數嗎？比較器相減可能溢位嗎？
4. **容器**：Array 的相等判斷正確嗎？Set 是否錯拿來計次？空 queue 有沒有直接 remove？
5. **狀態**：遞迴回來有沒有撤銷？同一個物件連續呼叫，狀態需要保留還是重設？
6. **效率**：有沒有在迴圈裡重複排序、截字串、從 List 頭部刪除？是否可能出現極深遞迴？
7. **平台**：簽名與型別正確？`return`/`println` 選對？沒有額外輸出？

「範例過了」只能證明那些範例通過，不能證明所有邊界與大輸入都正確。這份教材附本機範例驗證，但仍應親自到平台提交。

## 4. 限時練習安排

新版採答案優先：先看答案、走讀與重寫；按 [8週計畫](neetcode150/README.md)，在同類題已能重寫後再做限時練習，主要放第7–8週。下面是後期自訂訓練，不是第一次接觸新題的流程，也不代表正式考試題數或時間。

| 模擬 | 時間 | 練習組合 | 檢查重點 |
|---|---:|---|---|
| A | 45 分鐘 | #724 + #1004 | 前綴與視窗邊界 |
| B | 60 分鐘 | #206 + #994 | 節點操作與多源 BFS |
| C | 75 分鐘 | #2300 + #198 + #739 | Long、DP 狀態、單調堆疊 |
| D | 75 分鐘 | 從已學題隨機抽 3 題，先隱藏分類與解答 | 自己辨識模式與分配時間 |

熟題模擬主要測實作流暢度。之後要到 HackerRank 練陌生題，才測得到題意閱讀與遷移能力。考試前至少熟悉一次平台的 Sample Test／Practice 操作，包含跑自訂輸入與選語言。[HackerRank 候選人作答說明](https://candidatesupport.hackerrank.com/articles/9623161883-answer-coding-questions)

正式限時練習時：先用約 5–10% 時間看完題目，挑能確實完成的先做；中段留時間處理主體；最後保留約 15–20% 查邊界、型別、輸出。這是起始策略，依實際題數調整。卡住 15–20 分鐘沒有新進展可先換題；不預設任何平台一定給暴力解部分分數，評分以該測驗為準。

## 5. 碰到 Hard 怎麼辦？

這 75 題沒有 Hard，完成它們後仍需要擴大練習。先建立 Easy 可獨立寫、常見 Medium 可解釋的能力，再選單調堆疊、滑動視窗、heap、DP 等熟悉模式的進階題，每週一題深入推演。不要因為想防 Hard，就在尚未懂陣列與 queue 時直接背複雜答案。

陌生難題先拆小：輸入變成三個元素會怎樣？最慢但正確的方法是什麼？哪一部分重複計算？能否利用排序、前綴、Map 或已算過的小問題？即使最後沒完成，這些問題也能指出下一步該補哪個觀念。

## 6. 錯題紀錄模板

每做完一題用兩三句填，不要整段複製解答：

```text
日期／題號：
第一次做法與耗時：
卡住的真正原因：題意／觀念／狀態定義／索引／Kotlin API／溢位／時間
能讓我想到正確方法的線索：
一個讓錯法失敗的小例子：
正確解法為何不漏答案：
時間與空間複雜度：
下次複習日：
```

例：#1493；錯因是把「刪除恰好一個」看成「最多翻一個零」；反例 `[1,1,1]` 應是 2；以後先圈出 exactly，視窗答案要扣一格。這比「忘記扣一」更能幫助下次遇到變形題。

## 7. 面試時如何說明自己的解法

依序說「我理解的輸入輸出 → 最直覺做法 → 哪裡重複 → 要記什麼狀態 → 為何不會漏 → 複雜度 → 邊界測試」。不需要背專有名詞串燒。能用自己的話解釋為何移動短邊、為何刪掉 heap 最小值、為何 dp 依賴前兩格，比只說『我用雙指標／DP』更有意義。
