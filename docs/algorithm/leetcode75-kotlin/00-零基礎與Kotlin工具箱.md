# 零基礎觀念與 Kotlin 工具箱

[回學習入口](README.md)

你已經會 Kotlin、物件、函式、除錯。現在需要補的是：**資料該怎麼擺，才能少做重複工作？** 做 Android 時框架常替你處理資料結構；演算法題要你自己選擇。這份教材不要求先讀完一本大學課本。

## 1. 資料結構與演算法到底是什麼？

資料結構是收納方式，演算法是做事步驟。同樣找一本書，亂堆在地上需要逐本翻；依編號放書架就能快速定位。書沒有變，變的是收納方法與查找流程。

例如算每個使用者出現幾次：每看到一位就重掃整份名單，很慢；拿 `HashMap<使用者,次數>` 邊看邊記，就少做很多事。你不需要先手刻 HashMap 的底層才能使用它，但要知道「它適合用 key 查 value」。

## 2. 先分清楚題目裡的幾個字

| 用語 | 白話 | 例子 |
|---|---|---|
| index／索引 | 第幾格，從 0 開始 | `[5,8,2]` 的索引 1 放的是 8 |
| element／元素 | 格子裡的值 | 值 8 不代表索引 8 |
| subarray／連續子陣列 | 不能跳格的一段 | `[1,2,3]` 可取 `[1,2]`，不能取 `[1,3]` |
| substring／子字串 | 字串的連續一段 | abc 的 ab |
| subsequence／子序列 | 可跳格，順序不能變 | abc 的 ac |
| subset／子集合 | 著重挑哪些元素 | 需另看題目是否區分重複值 |
| in-place／原地 | 在原資料上修改 | Move Zeroes 直接改 nums |
| strictly increasing | 每次都真的變大 | 1,2,3 可以；1,1,2 不行 |
| at most／至多 | 可以少於指定次數 | 最多翻 2 個零，也可翻 0 個 |
| exactly／恰好 | 不能少也不能多 | 必須刪 1 個，即使全是 1 |

做題時先圈出「連續」「順序」「重複」「恰好」這幾個字，常常比急著想演算法更有用。

## 3. 時間複雜度：資料變多，工作增加多少？

O 讀作 Big O。它是成長速度，不是精確秒數。n 通常是輸入長度；看到新的符號，先問它代表什麼。

| 寫法 | 想像 | n=1000 時的量級 |
|---|---|---:|
| O(1) | 不管名單多長，只做固定工作 | 約固定幾步 |
| O(log n) | 每次砍掉一半 | 約 10 輪 |
| O(n) | 整份看一遍 | 約 1000 |
| O(n log n) | 常見有效排序 | 約 10000 |
| O(n²) | 每人都跟所有人比較 | 約 1000000 |
| O(2ⁿ) | 每個元素都分選／不選 | 很快大到不可行 |

兩個前後排列的 for 各跑 n 次，是 2n，寫 O(n)。for 裡面再完整跑 n 次才是 O(n²)。但**不能只數迴圈層數**：滑動視窗雖有巢狀 while，left 整段只前進 n 次，總共仍 O(n)。

空間複雜度算另外需要多少記憶體。一個 Int 是 O(1)，n 格陣列是 O(n)，n×n 表格是 O(n²)。遞迴也是要空間：呼叫自己時，系統會記住回來後要繼續做什麼；深度 h 就要 O(h) 呼叫堆疊。

平均 O(1) 不等於永遠 O(1)。HashMap 通常以平均查找成本分析；教材會寫平均時間。攤銷 O(1) 則是：某次可能很慢，但把一連串操作總成本平均後很小，例如每個請求最多進出佇列各一次。

## 4. 先學這些容器就夠了

| 工具 | 它在幫你做什麼 | 常見題目訊號 | 主要成本 |
|---|---|---|---|
| IntArray | 一排編號格子 | 固定大小、數字、索引 | 讀改一格 O(1) |
| MutableList | 可長大的清單 | 答案數量事先不知道 | ArrayList 尾加攤銷 O(1)，中間插刪 O(n) |
| HashSet | 不重複名冊 | 有沒有出現過、去重 | 查找／新增平均 O(1) |
| HashMap | key 對 value 的筆記本 | 次數、配對、快速查詢 | 查找／更新平均 O(1) |
| ArrayDeque 當 stack | 疊盤子，最後放的先拿 | 配對、退格、撤銷、巢狀 | 尾加尾取攤銷 O(1) |
| ArrayDeque 當 queue | 排隊，先來的先走 | 分層、擴散、時間先後 | 尾加頭取攤銷 O(1) |
| PriorityQueue／heap | 隨時拿目前最小或最大 | 前 k 大、每次取最低成本 | 看頂 O(1)，加／取 O(log n) |

### IntArray、List 與比較

```kotlin
val nums = intArrayOf(4, 8, 2)
val counts = IntArray(26) // 26 格，自動填 0
for (i in nums.indices) { // i = 0, 1, 2
    println(nums[i])
}
for (value in nums) { // 直接拿值
    println(value)
}
val answer = mutableListOf<Int>()
answer.add(7)
val copy = nums.copyOf() // 獨立複本
val same = nums.contentEquals(copy) // true；不要用 == 比陣列內容
```

`val` 表示變數不能改指向另一個陣列，**不表示陣列內容不能改**。`nums[0]=99` 是合法的。`0 until n` 不包含 n；`0..n` 包含 n，常造成越界。`lastIndex` 是 size-1。

### HashMap 與 HashSet

```kotlin
val counts = HashMap<Int, Int>()
for (x in intArrayOf(4, 4, 9)) {
    counts[x] = (counts[x] ?: 0) + 1
}
// counts[4] 是 2；counts[8] 是 null
val seen = HashSet<Int>()
val first = seen.add(4) // true，成功加入新元素
val second = seen.add(4) // false，本來就有
```

`?: 0` 意思是「查不到時當作 0」。不要把 `!!` 當萬用修復：它只是在告訴 Kotlin「我保證不是 null」，保證錯了照樣崩潰。Set 不記錄出現次數，Map 才適合。

### ArrayDeque：同一工具，兩種拿法

本教材明確匯入 `java.util.ArrayDeque`，避免和 Kotlin 同名類別混淆。Java 版本不能放 null，所以樹的空子節點不能直接入隊。

```kotlin
import java.util.ArrayDeque

fun demo() {
    val stack = ArrayDeque<Int>()
    stack.addLast(10)
    stack.addLast(20)
    val newest = stack.removeLast() // 20，後進先出

    val queue = ArrayDeque<Int>()
    queue.addLast(10)
    queue.addLast(20)
    val oldest = queue.removeFirst() // 10，先進先出
}
```

`peekFirst`／`peekLast` 只看不拿；`removeFirst`／`removeLast` 會拿走。先確認不是空的，才使用需要元素的操作。用 MutableList 每次 `removeAt(0)` 會搬動後面元素，BFS 題很容易變慢。

### PriorityQueue：會挑最小，並不是整份都排好

```kotlin
import java.util.PriorityQueue

fun demoHeap() {
    val minHeap = PriorityQueue<Int>()
    minHeap.offer(5)
    minHeap.offer(2)
    minHeap.offer(8)
    val smallest = minHeap.poll() // 2
    val maxHeap = PriorityQueue<Int>(compareByDescending<Int> { it })
    maxHeap.offer(5)
    maxHeap.offer(8)
    val largest = maxHeap.poll() // 8
}
```

`offer` 加入，`peek` 看頂端，`poll` 拿走頂端。每次只保證頂端符合排序優先順序，直接走訪整個 heap 不保證從小到大。比較器不要隨意用 `a-b`，兩個大整數相減可能溢位；使用 `compareTo` 或 `compareBy`。

「第 k 大」常用容量 k 的**最小堆**：裡面保留最大那 k 個，最小的那個恰好就是第 k 大。不是看到「大」就一定用最大堆。

### TreeSet：去重，同時維持大小順序

#2336 使用 `java.util.TreeSet<Int>()`。它跟 HashSet 一樣不留重複值，但會維持排序，因此 `pollFirst()` 可以拿走最小的值。`add` 與取最小通常 O(log n)，不是 HashSet 的平均 O(1)。這裡只需先會三個操作：`add(x)` 加入、`isNotEmpty()` 確認有值、`pollFirst()` 取走最小值。它很適合「放回的數不能重複，又要優先取最小」；底層的平衡搜尋樹可以之後再學。[Java TreeSet 官方 API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/TreeSet.html)

## 5. 鏈結串列：值之外，還帶下一站的地址

陣列可以直接讀第 100 格；鏈結串列必須從頭沿著 next 走，不能用索引瞬間跳過去。

```text
head
 ↓
[4 | next] → [7 | next] → [9 | null]
```

LeetCode 已提供節點，下面只解釋結構，不要和平台提供的定義重複貼上：

```kotlin
class ListNode(var `val`: Int) {
    var next: ListNode? = null
}
class TreeNode(var `val`: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}
```

反引號中的 `val` 是平台的欄位名稱；因為 val 同時是 Kotlin 關鍵字，所以用 ``node.`val` `` 讀取。`node.next` 是另一個節點的參考，不是下一格的數字。`a=b` 通常是指向同一物件，不是複製節點。

反轉一條鏈時，先存 `next`，再改箭頭，最後前進。如果先把箭頭反轉才找 next，就已經找不到原本的下一站。推薦先做 #206，再做刪中間與奇偶串列。

快慢指標：slow 一次走一站、fast 一次走兩站；fast 到底時 slow 約在中間。這是控制移動速度，和 Android 的多執行緒無關。

## 6. 樹、DFS、BFS：先畫再寫

```text
        6       ← root，根；第 1 層
       / \
      3   9     ← 第 2 層
     / \
    1   4       ← leaf，葉：沒有任何子節點
```

樹是沒有環的連接結構，二元樹每個節點最多兩個子節點。深度通常以節點層數算，上圖最大深度 3；有些題的路徑長度以**邊數**算，例如 6→3→1 是 2 條邊，要讀清楚。

DFS（深度優先）像走迷宮：先沿一條支線走到底，再退回岔路。用遞迴或自己準備 stack 都可以。BFS（廣度優先）像一圈圈傳消息：先處理這層所有人，再處理下一層，用 queue。

上圖左先 DFS 可得到 `6,3,1,4,9`；BFS 是 `6,3,9,1,4`。要找「最少走幾步」，且每一步成本一樣時，BFS 很自然，第一次到達就是最少步數。若邊有不同權重，就不能直接把普通 BFS 當成最短路解法。

遞迴的基本問題只有兩個：

1. 最小情況怎麼直接回答？例如空樹深度 0。
2. 假設孩子會回答，我如何組成自己的答案？深度就是 `1+max(左深度,右深度)`。

JVM 遞迴太深可能 StackOverflow，尤其樹退化成一條長鏈。教材部分樹題直接用顯式 stack／queue，演算法觀念和遞迴相同，但不依賴很深的函式呼叫。不要把任意遞迴加上 `tailrec` 就以為能解決；它只適用特定尾遞迴形式。

BST（二元搜尋樹）多一條規則：每個節點的左子樹值較小、右子樹值較大（本題組使用互異值）。因此找值可排除一側。但沒保證平衡時可能像一條鏈，搜尋最壞 O(n)，不能一律說 O(log n)。

## 7. 圖：節點與關係，不一定是畫面上的圖片

城市與道路、房間與鑰匙、使用者與朋友都能建成圖。`graph[u]` 通常存從 u 能走到哪些鄰居，叫鄰接串列。

```text
0 ─ 1 ─ 2
    │
    3
```

鄰接串列可寫成 `[[1],[0,2,3],[1],[1]]`。V 是節點數，E 是邊數，完整 DFS/BFS 通常 O(V+E)。圖可能有環：0→1→0，因此必須記 visited。**加入隊伍時就標記已拜訪**，可以避免同一節點被重複排隊。

網格也是圖，每格是節點，上下左右是鄰居。不一定要先建立所有邊；用四個方向計算座標即可。越界、牆壁、已走過都不能進。多源 BFS 是把所有起點一起入隊，像多處同時開始擴散。

## 8. 常見解題模式，用一句話先認識

| 模式 | 核心問題 | 代表題 |
|---|---|---|
| 雙指標 | 能不能用兩個位置避免枚舉所有配對？ | #283、#392、#11 |
| 固定視窗 | 這一段移一格，哪些東西進出？ | #643、#1456 |
| 可變視窗 | 超過限制時，左邊怎麼縮？ | #1004、#1493 |
| 前綴和 | 之前累積的總和能否重用？ | #1732、#724、#437 |
| 貪心 | 這一步選擇為何不會害到以後？ | #605、#435、#452 |
| 二分搜尋 | 哪一半可以確定丟掉？ | #374、#2300、#875 |
| 回溯 | 做選擇、往下試、撤銷選擇 | #17、#216 |
| 動態規劃 | 哪些小問題會重複出現，能不能存答案？ | #1137、#198、#1143 |
| 單調堆疊 | 哪些等待者可由新來的值一次解答？ | #739、#901 |
| Trie | 很多字共用的前綴能不能共用節點？ | #208、#1268 |

題目訊號只幫你產生候選方法，不是看到「連續」就一定能滑窗。例如任意正負數的區間總和，擴張或縮小不一定讓總和單調，不能隨便套「總和太大就縮左」。

## 9. 二分搜尋先理解「可排除」，再記左右邊界

在已排序的 `[2,5,8,11,14]` 找 11：看中間 8，太小；左邊全部更小，可以一起刪除，只找右半邊。二分的力量不是猜中間，而是一次確定排除一半。

另一種是對答案二分。#875 問吃香蕉速度：速度太慢做不到，速度夠快就做得到，形成 `false,false,...,true,true`；我們找第一個 true。每次判定本身可能要 O(n)，所以總時間是 O(n log M)，不是只有 O(log M)。

中點用 `left+(right-left)/2`，避免 `left+right` 溢位。先寫清楚區間包含哪些端點，別混用 `[left,right]` 和 `[left,right)` 模板。

## 10. 回溯與動態規劃不是魔法

回溯是有系統地試選擇。假設正在組 `[1,2]`，選 3 變 `[1,2,3]`，探索完必須刪掉 3 回到 `[1,2]`，才能試 4。存答案要 `path.toList()` 留快照，不能把會持續變動的同一清單一直放進答案。

動態規劃（DP）是記下重複小問題的答案。先不用背公式，依次回答：

1. `dp[i]` 到底代表什麼？一定要能講成一句完整中文。
2. 最小情況是多少？例如搶零間房最多得到 0。
3. 下一格依賴哪幾格？搶第 i 間，就不能搶第 i-1 間。
4. 要先算哪些格？依賴過去就從前往後算。
5. 最後要回傳哪格？是最後一格，還是全部最大值？

以房屋 `[2,7,4]` 為例：看第一間最多 2；看前兩間最多 7；看前三間，選「不搶第三間的 7」或「搶第三間 4，加第一間 2」的最大，所以仍 7。這不是相鄰兩間挑大的局部貪心，而是在比較完整前綴的最佳方案。

先用完整 dp 陣列手算懂，再學只留兩格的空間優化。程式短不代表適合先學。

## 11. Kotlin 考試常踩的坑

- `Int` 約正負 21 億；乘積、路徑累計、大量總和常要 Long。寫 `a.toLong()*b`，不是 `(a*b).toLong()`，後者溢位早已發生。
- Int 相除會丟小數。平均值用 `sum.toDouble()/k`。
- `String` 不能逐格改，改用 CharArray 或 StringBuilder。
- `sort()` 修改原陣列；`sorted()` 產生新清單。複雜度與是否改輸入要交代。
- List 當 HashMap key 有內容相等語義；Array／IntArray 預設沒有。不要把放進 Map 的可變 key 再修改。
- 每題的 `class Solution` 都獨立；不要把這 75 個同名類別一次貼入平台。
- LeetCode 的 `TreeNode`、`ListNode`、`GuessGame` 是平台支援型別；本機要自己準備，平台通常不用重寫。
- 不在解法中使用 Android 的 Context、Log、ViewModel 或 kotlinx.coroutines；這是一般 Kotlin/JVM 題目。

工具參考：[Kotlin Collections](https://kotlinlang.org/docs/collections-overview.html)、[Arrays](https://kotlinlang.org/docs/arrays.html)、[Numbers](https://kotlinlang.org/docs/numbers.html)、[Java ArrayDeque](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ArrayDeque.html)、[PriorityQueue](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/PriorityQueue.html)。以上為 API 參考；白話比喻、學習順序和解題教學為本教材自行編寫。
