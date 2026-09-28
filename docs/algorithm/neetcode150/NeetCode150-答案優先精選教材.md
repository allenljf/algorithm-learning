# NeetCode150：Kotlin答案優先精選教材

本合訂本收錄67題新增詳解＋20題沿用詳解，共87題；另含完整150題索引，其餘63題明列為延伸。先看答案，再走讀原理，最後重寫與比較相似題。

## 導覽

- [8週計畫](#plan)
- [18類題型與DP分類](#map)
- [完整150題索引](#index)
- [23題：雜湊到搜尋](#first)
- [20題：鏈結串列到圖](#middle)
- [24題：回溯、DP與數學](#advanced)
- [熟練追蹤](#tracker)
- [官方來源](#sources)
- [本機驗證](#validation)
- [沿用原75教材的20題](#reused)

---

<a id="plan"></a>

# NeetCode 150：Kotlin 答案優先學習與 8 週計畫

更新：2026-09-21。依你的新偏好調整：**先看完整答案，理解每一步的理由，反覆走讀與重寫，最後才練類似題的變形。第一遍不安排自行苦想或先寫暴力解。** 你預留 1–2 個月以上，本計畫以 8 週、每天 60–90 分鐘、每週約6天為起點；有更多時間就延長熟練階段。

## 本次補上什麼

- NeetCode 150 的**完整150題索引、18種分類、官方原題連結與難度**。
- **67題新增經典題詳細教學**，與原 LeetCode75 不重複；每題附Kotlin答案、白話原理、原創推演、工具選擇理由、複雜度與相似題差異。
- 原教材20題也在NeetCode150中，可直接沿用：合計 **87／150題有本地詳解**。其餘63題列為延伸閱讀，提供官方入口，尚未撰寫本地逐題詳解。
- 原75題全部保留，所以兩套教材目前有 **142道不同題目的詳解**。兩份完整題單合併則是205題，請勿把它們全部列成短期必做。

**閱讀入口**：[完整150題索引與優先級](#index)｜[題型選擇與相似題地圖](#map)｜[精選詳解合訂本](#plan)｜[原75題工具箱](../00-零基礎與Kotlin工具箱.md)

| 分冊 | 新增題數 | 內容 |
|---|---:|---|
| [01 雜湊、雙指標、視窗與搜尋](#first) | 23 | Two Sum、3Sum、最長不重複、最小覆蓋窗、單調容器、旋轉搜尋 |
| [02 鏈結串列、樹、Heap與圖](#middle) | 20 | LRU、樹重建、路徑、序列化、拓撲、DSU、Dijkstra、MST |
| [03 回溯、DP、貪心與數學](#advanced) | 24 | 子集／排列、背包、硬幣、買賣狀態、子序列、區間與矩陣 |
| [熟練追蹤表](#tracker) | 87 | 已讀答案→可走讀→可重寫→可解釋差異 |
| [來源查證](#sources) | 150題來源 | 官方資料快照、與原題單重複統計、常見面試題型依據 |
| [驗證紀錄](#validation) | — | 本機編譯與測試範圍 |

完整原始題目透過官方連結閱讀；中文為自行改寫的教學題意，不是逐字翻譯全文。NeetCode150 = Blind75加75，**Blind75和LeetCode75是不同題單**。本次150題來自可重現的NeetCode現站公開資料、官方repo及LeetCode官方metadata核對。[來源](#sources)

## 優先級：學得少而可解釋，再逐步擴大

| 優先級 | 用途 | 你怎麼讀 |
|---|---|---|
| P0 核心28題 | 第一輪主要目標；含23題新增與5題原教材 | 按下面週計畫；每天一題可拆兩天，不省略理解與重寫 |
| P1 補強 | 擴到各類變形；包含其餘可沿用舊題 | 同類P0已能走讀後，挑1–2題比較；不必一口氣全做 |
| P2 進階／較後順位 | 選讀Hard或較低順位技巧 | 先把前置題做熟；每週至多一個難題主題，必要時拆兩天 |
| E 延伸索引 | NeetCode150其餘63題 | 有官方入口但本地無詳解；本輪不排必做，可後續擴充 |

P0/P1/P2是為你配置的**學習優先級**，不是官方難度，也不是任何公司出題頻率排序。P2有少數Medium或Easy，是因它們的前置知識與本次時間安排，不代表它們變成Hard。

## 8 週主線

右欄是補強選項，不是每週額外必須完成的全部作業。前6週先完成28題主線；每題先看答案，不要求第一次獨立推出解法。較長的一題可拆兩天，早已熟悉的題則縮短。第7–8週把時間留給回想、對照、平台輸入與有限的進階題。

| 週 | P0主線：依序看答案理解 | 同類熟練後的P1／P2選讀 | 這週的口頭目標 |
|---|---|---|---|
| 1 | #1、#242、#49、#125、#167 | #347、#128、#15；舊#238 | Map存什麼、何時可排序、雙指標為何能排除 |
| 2 | #3、#20、#704、#33 | #424、#567、#155、#74、#153、#981 | 合法視窗、stack順序、二分左右區間 |
| 3 | 舊#206、#21、#19、舊#104、#102、#98 | #143、#146、#226、#105 | 指標先存再改、DFS/BFS、BST整體上下界 |
| 4 | #200、#207、舊#994 | #133、#210、#684、#743、#1584 | visited、入度、多源BFS；最短路與連通成本差別 |
| 5 | #78、#39、#70、舊#746、舊#198 | #46、#79、#211；舊#208 | 回溯撤銷、保存快照、dp狀態和最小情況 |
| 6 | #322、#300、#53、#56、#73 | #416、#518、#494、#309、#5、#55/#45、#57 | 最少／計數／可行性差別；貪心的證明；標記技巧 |
| 7 | 不加P0，重寫薄弱題、做成對比較 | #48/#54；舊#136/#338；時間足夠選#190/#371 | 看題意能說明和母題相比改哪個條件；補數學位元分類 |
| 8 | 每週2–3次45–75分鐘混合重寫，其餘時間修錯 | 從#42/#76/#239/#84/#124/#297/#295/#332/#115挑1–2題；#621可後補 | 在沒有分類提示時，說出工具選擇與限制，再完成程式 |

涵蓋各分類的經典題已備好，不代表8週內每題都應同樣熟。第一輪以P0為完成目標；P1用於形成變形能力；Hard採有前置題的少量選讀。完成28題後仍有很多可補知識，請依追蹤表的理解程度擴大。

## 每次60–90分鐘的答案優先流程

| 步驟 | 時間 | 具體動作 |
|---|---:|---|
| 直接看題意與答案 | 10–15分鐘 | 看完整Kotlin，先知道輸入輸出、最終方法和用到的容器，不先自己找解法 |
| 對照原理走讀 | 15–20分鐘 | 看著答案逐輪寫下關鍵變數；不懂某個API就回工具箱查 |
| 看著答案輸入一次 | 10–15分鐘 | 每段旁用中文註明用途，讓手與眼熟悉API；這次可以照看 |
| 部分遮住重建 | 10–15分鐘 | 遮掉條件或更新式，從剛理解的規則填回；卡住直接查看再說明原因 |
| 重跑＋整理差異 | 10分鐘 | 跑原創例和一個邊界，寫「這題和母題差在哪」 |
| 有餘裕再完整重寫 | 10–15分鐘 | 不求第一次就完全記住；完整重寫也可留隔天 |

第一天的成功標準是**對著答案能說明**，不是從空白獨立解題。隔天再不看答案重寫同一題；這是熟練已學內容，不是要求你先發明新解法。仍卡住就看提示補回，並記下卡在哪個概念。

第1、3、7、14天各做短複習，間隔可調整。**重寫不只背字面**：每次至少換一個例子或說明一個邊界。相似題第一輪也可先看答案，把它和母題並排看，指出資料結構、條件、轉移式的異同；等同類熟了再選一次不提示分類的練習。

## 你要記住的是「規則＋適用條件」，不是整份程式

例如#1記「先查補數再存索引」；#167記「資料已排序，和小移左、和大移右」；#322記「dp是最少枚數，轉移用min」；#518記「dp是組合數，外層幣種避免把順序當不同」。這幾句話能帶回程式，也能提醒何時不能照套。

對照題推薦順序：

1. #1→#167→#15：同樣湊和，排序與索引要求不同。
2. 舊#643→#3→#567→#76：固定窗、禁止重複、頻率相等、最短覆蓋。
3. 舊#198→#322→#416→#518→#494：最佳收益、最少枚數、是否可行、組合計數、符號選擇。
4. #200→舊#994→#207→#743：走遍、同時擴散、依賴順序、加權最短路。
5. 舊#739→#239→#84：等更大、窗內最大、遇更小結算。

母題與對照題不限同一週全部完成；完整差異說明見 [題型地圖](#map)。

## 若最後只剩4週

仍採答案優先，將8週的P0按兩週合一週走，先暫緩所有P2。每日90分鐘最多一題新母題，另一題只走讀，隔天交換；若進度落後就保留#1/#3/#20/#704/#206/#104/#200/#207/#198/#322等母題，把其他題放後面。這是取捨方案，不要求零基礎四週掌握全部18類。

## 關於「常考」與正式考試

HackerRank 官方面試準備頁涵蓋陣列、雜湊、排序、字串、搜尋、DP、圖、樹等，可作為主題選擇依據；它的概括比例未明示統計期間與方法，不當成台積電出題機率。NeetCode150也不是台積電題庫。[來源與限制](#sources)

Kotlin是否開放、題數、時間與模板仍依考試通知。沿用 [HackerRank輸入輸出與平台注意事項](../04-HackerRank實戰與複習.md)；正式限時練習放在看懂、重寫已熟之後，先把答案轉成自己的理解。


---

<a id="map"></a>

# 題型選擇與相似題地圖

[回答案優先計畫](#plan)｜[150 題逐題索引](#index)

這裡的「選型」指解題時選資料結構與演算法。先讀已知答案，再用這份地圖理解為什麼選它；熟練後才拿來辨識陌生題。題號連結與是否已有詳解，請查索引。**經典題型**依 NeetCode、HackerRank 與 LeetCode 官方面試準備清單選取，不是台積電公司出題次數排名。[查證依據](#sources)

## 一張表先看懂 18 類

「起手」是先看的代表；「對照」是用來理解改動；「進階」不是一開始就必須完成。舊表示可以沿用上一套教材。

| 分類 | 看見什麼問題 | 常用工具與理由 | 起手 → 對照 → 進階 | 不要誤套 |
|---|---|---|---|---|
| Arrays & Hashing | 配對、計次、去重、分組 | HashMap記對應、Set查存在；頻率有界可用桶 | #1 → #242/#49/#347 → #128；舊#238 | 要有序不一定適合HashSet；key需正確內容相等 |
| Two Pointers | 排序配對、兩端比較 | 排序讓移動可排除一整批候選 | #125/#167 → #15、舊#11 → #42 | 未排序不能照「和小移左」 |
| Sliding Window | 連續區段、固定長度或可維護限制 | 只更新進出元素，避免重算 | 舊#643 → #3/#424/#567 → #76/#239 | 正負和不單調；子序列不是連續窗 |
| Stack | 巢狀、撤銷、下一個更大或更小 | 最近未完成工作先處理；單調stack淘汰無用候選 | #20/#155 → 舊#739 → #84 | Queue的先來先走不同；只計括號數不夠 |
| Binary Search | 排序、單調真假門檻 | 每輪用證據排除半邊 | #704/#74 → #33/#153/#981 → 舊#875 | 不是任何答案範圍都可二分，必須證明單調 |
| Linked List | 改節點順序、串接、倒數位置 | dummy簡化頭節點；快慢指標；改next | 舊#206 → #21/#19/#143 → #146 | 不先存next會斷鏈；Map不等於有最近使用順序 |
| Trees | 父子關係、子樹、路徑 | DFS處理子樹，BFS按層；BST用上下界 | 舊#104 → #226/#102/#98/#105 → #124/#297 | 一般二元樹不是BST；路徑可回傳的形狀有限 |
| Heap / Priority Queue | 重複拿最小／最大、維持前k | Heap只保證頂端最值，動態插入比每次全排序合適 | 舊#215 → #295/#621 | 不保證遍歷時有序；一次性頻率有界可桶排 |
| Backtracking | 列出所有組合、排列、路徑 | 選→深入→撤銷；保存答案快照 | #78 → #46/#39 → #79；舊#17 | 所有解通常本來就多，不能假裝可O(n)列完 |
| Tries | 前綴查找、字典共用開頭 | 共用字元路徑；遇萬用字元時展開分支 | 舊#208 → #211 | 只查完整字是否存在，HashSet可能更簡單 |
| Graphs | 網路連通、格子擴散、先修關係 | DFS/BFS、visited；DSU分群；拓撲排序排依賴 | #200 → #133/#207/#210/#684、舊#994 | 有環不能用沒有visited的樹走訪；建邊方向要對 |
| Advanced Graphs | 加權最短路、全點連接、用完每條邊 | 非負邊Dijkstra；MST最便宜連全點；Euler用完邊 | #743 → #1584 → #332 | 最短路≠最小生成樹；負權不套Dijkstra |
| 1-D DP | 前綴最佳、選不選、最少步數 | 記已算過的子問題；狀態是一句精確中文 | #70、舊#746/#198 → #322/#300/#416/#5 | 問最優不必然DP；局部選最好不一定正確 |
| 2-D DP | 比較兩串、容量×物品、時間×狀態 | 保存多個維度；按依賴順序計算 | 舊#62/#1143/#72 → #518/#494/#309 → #115 | 程式只用一維陣列，也可能是壓縮後二維DP |
| Greedy | 當下選擇可證明不傷後續 | 記最佳前綴、可達最遠；需有交換或支配理由 | #53 → #55/#45 | 不能只因「看起來最好」就選；#198需比較完整前綴 |
| Intervals | 合併區間、排程、重疊 | 先排序，再維持目前區間或最早終點 | #56 → #57、舊#435/#452 | 端點相接算不算重疊依題意決定 |
| Math & Geometry | 矩陣轉向、座標、標記 | 座標映射、逐層邊界、借首列首欄做標記 | #48/#54 → #73 | 一看到零立刻清列欄，會把新零當原始零 |
| Bit Manipulation | 二進位逐位、成對抵銷、進位 | xor消成對、and查位、ushr無符號右移 | 舊#136/#338 → #190/#371 | xor不是一般加法；負數要按固定位元看 |

NeetCode 的分類是整理方式，不是唯一解法。例如 #53 既可用DP解釋，也可視為丟掉負前綴的貪心；#42本教材先用前後綴最高表，沒有硬要求用O(1)雙指標；#2336舊教材用TreeSet，雖收在Heap分類仍合理。重點是成本、條件與不漏答案的理由。

## DP 必須拆開學，不能只背一個模板

動態規劃最先記的是**dp的中文意思**。同名 `dp[j]` 在不同題可能是「最少幾枚」「是否可行」「有幾種」，抄迴圈之前先確認它在回答哪個問題。

| 子型 | 代表題 | 狀態怎麼說 | 轉移重點與最常見混淆 |
|---|---|---|---|
| 固定前幾格遞推 | #70、舊#1137 | 到第i格的方法數／第i項數值 | 起點定義不同，不能把所有dp[0]都設0 |
| 路徑最小成本 | 舊#746 | 到達第i階且未支付本階的最低費用 | 到達與離開的成本定義不要混用 |
| 選或不選 | 舊#198 | 看完前i間房的最佳收益 | 不選接前一格；選接前兩格；不是奇數總和vs偶數總和 |
| 無限重用，最小數量 | #322 | 湊出金額a最少幾枚硬幣 | min；dp[0]=0；不可達初始化為大值 |
| 無限重用，組合數 | #518 | 用已處理幣種湊出金額a的方式數 | sum；dp[0]=1；外層幣種、內層容量正向，避免把順序當不同 |
| 每項只用一次，是否可行 | #416 | 用前面元素能否湊出容量j | Boolean；容量倒著走，避免當輪重用同一元素 |
| 每項只用一次，計數 | #494 | 有多少子集合可湊指定容量 | 計數；倒著走；值0會讓選／不選都成立，方法數需加倍 |
| 最長遞增子序列 | #300 | 傳統DP：以i結尾的最佳長度；本冊優化版tails：各長度的最小結尾 | 傳統版接更小的前面元素；tails版用二分改善結尾，答案是有效長度，tails不一定是真實路徑 |
| 兩串配對／編輯 | 舊#1143/#72、#115 | 前i字與前j字的最佳長度、成本或方法數 | 相等時與不等時的分支不同；#115是計數，不能用max |
| 交易狀態機 | #309、舊#714 | 今天結束持有／剛賣出／可休息的最佳收益 | 一天的轉移只能讀昨天狀態；冷卻與手續費是不同限制 |
| 回文區間 | #5 | 子字串[l..r]是否回文（本教材採中心擴張） | 可由更短區間遞推，也可每個中心擴張；分奇偶中心 |
| 網格／覆蓋 | 舊#62/#790 | 到格子的路徑數／填到第i欄的形狀狀態 | 單一長度不一定描述缺口，需額外形狀狀態 |

### 先對照 #322 與 #518

硬幣 `[1,2]`、金額3。

- #322問最少幾枚：1+2需要2枚，所以答案2。1+1+1雖合法但不是最少。
- #518問幾種組合：`1+1+1`、`1+2`，答案2。2+1與1+2是同組合，不能重複算。

這次兩題剛好都回2，不代表同一演算法。把金額改成4：最少2枚（2+2）；組合3種（1111、112、22）。**用能區分兩種規則的例子，才能知道自己理解了什麼。**

### 正向與反向容量究竟差在哪？

只有一個數字2，目標4。容量由小到大時，先更新dp[2]；更新dp[4]又讀到剛更新的dp[2]，等於同一個2用了兩次。若每個元素只能用一次，容量由大到小，就會讀到「上一輪尚未用本次元素」的狀態。

硬幣可以無限使用時，這種同輪重用正是想要的行為；0/1背包則要避免。迴圈方向不是背誦規定，是在控制讀「上一輪」還是「這一輪」。

### Long 不一定能保證所有計數都安全

最後答案能放Int，不代表任何寫法的中間dp都安全。#115教材採BigInteger做完整計數，避免大量無用前綴配對在中間就爆掉。實務上可再研究只算必要狀態或安全飽和值，但先把狀態與依賴學會。不要把BigInteger運算的成本當成永遠O(1)。

## 圖論的工具怎麼分？

| 問題 | 工具 | 代表題 | 為何不同 |
|---|---|---|---|
| 去得到嗎／有幾群？ | DFS/BFS | #200、舊#841/#547 | 不一定需要最短路，只需走訪 |
| 同時從多點擴散幾步？ | 多源BFS | 舊#994 | 一開始全部源頭入隊，每一步成本相同 |
| 先修能不能完成？ | 拓撲排序 | #207/#210 | 入度0表示目前沒有未完成前置條件 |
| 加邊會不會成環？ | DSU並查集 | #684 | 兩端已在同群，再連就成環；這是無向圖問題 |
| 不同非負成本的最短路？ | Dijkstra+Heap | #743 | 下一個要確定的是目前累積距離最小者，不是最早入隊者 |
| 最低費用把所有點連在一起？ | 最小生成樹 | #1584 | 最小化全部連線總成本，不是每人到起點的路程 |
| 每張票恰好用一次？ | Euler路徑／Hierholzer | #332 | 訪問的是邊；城市可以重複，不可用visited城市來禁止回訪 |

DSU像分公司群組名冊：`find(x)`找群組代表、`union(a,b)`合併兩群；路徑壓縮讓查找沿路直接記代表。拓撲排序的入度是尚未解決的前置條件數，不是節點值。

## 幾組特別容易看錯的相似題

| 題對 | 外表像 | 真正要換的部分 |
|---|---|---|
| #1 vs #167 | 兩數和 | 未排序Map vs 已排序雙指標；0-based vs 1-based |
| 舊#11 vs #42 | 柱子和水 | 選兩根的容器面積 vs 每格積水總和 |
| #3 vs #424 | 最長字串窗 | 不可重複 vs 可改k個變同字 |
| #567 vs #76 | 包含指定字母 | 固定長排列 vs 最短可變長覆蓋；重複字次數都重要 |
| #704 vs #981 | 二分查找 | 等於即可 vs 最後一筆不晚於查詢時間 |
| 舊#104 vs #124 | 左右子樹 | 深度可取max；完整最大路徑能分叉，但回傳父層只能單側 |
| #207 vs #210 | 課程依賴 | 只問可不可完成 vs 回傳可行順序；可共用Kahn演算法 |
| #55 vs #45 | 跳躍 | 能否到達 vs 最少跳幾次；後者按可達範圍分層 |
| #56 vs 舊#435 | 區間 | 合併所有覆蓋 vs 刪最少讓不重疊；排序依據可不同 |
| 舊#739 vs #239 vs #84 | 單調容器 | 等更暖／固定窗最大／遇矮柱算面積；出隊理由不同 |

## 答案看完後，用這五句話檢查理解

1. 這個變數／格子存的是什麼？例如不是「dp」，而是「湊到金額a的最少硬幣數」。
2. 為什麼這些舊資料可以丟？例如新值更大而且更晚過期，舊小值不可能再贏。
3. 哪個假設讓它成立？例如排序、非負權重、可重用硬幣、每步同成本。
4. 改哪個條件就不能原樣套？例如把組合改成排列、把固定窗改成最短窗。
5. 最小反例是什麼？例如abba會揭露左指標倒退的錯誤。

第一輪可以看著答案說，第二輪只看變數名說，第三輪才不看答案重寫。你現在不需要先耗時間獨立發明解法；需要把「為什麼這樣寫」與程式逐行連起來。


---

<a id="index"></a>

# NeetCode150完整索引與優先級

[回8週計畫](#plan)｜[題型地圖](#map)

查證2026-09-21。150題完整收錄；87題可讀本地詳解（67新增＋20沿用），63題為延伸索引。P0/P1/P2是本教材學習安排，不是公司題頻。每題有官方原題連結；Premium標记表示LeetCode完整原題可能需要訂閱，可先使用NeetCode官方練習頁尋找对应題目，未保證兩站所有題面限制完全相同。

[NeetCode150官方入口](https://neetcode.io/practice/practice/neetcode150)｜[可重現來源](#sources)

| 分類 | 全題數 | 有本地詳解 | 延伸 |
|---|---:|---:|---:|
| Arrays & Hashing | 9 | 6 | 3 |
| Two Pointers | 5 | 5 | 0 |
| Sliding Window | 6 | 6 | 0 |
| Stack | 6 | 4 | 2 |
| Binary Search | 7 | 6 | 1 |
| Linked List | 11 | 5 | 6 |
| Trees | 15 | 9 | 6 |
| Heap / Priority Queue | 7 | 3 | 4 |
| Backtracking | 10 | 5 | 5 |
| Tries | 3 | 2 | 1 |
| Graphs | 13 | 6 | 7 |
| Advanced Graphs | 6 | 3 | 3 |
| 1-D Dynamic Programming | 12 | 7 | 5 |
| 2-D Dynamic Programming | 11 | 7 | 4 |
| Greedy | 8 | 3 | 5 |
| Intervals | 6 | 3 | 3 |
| Math & Geometry | 8 | 3 | 5 |
| Bit Manipulation | 7 | 4 | 3 |

## Arrays & Hashing

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [217. Contains Duplicate](https://leetcode.com/problems/contains-duplicate/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [242. Valid Anagram](https://leetcode.com/problems/valid-anagram/) | Easy | P0 核心 | [新增詳解](#242-valid-anagram) |
| [1. Two Sum](https://leetcode.com/problems/two-sum/) | Easy | P0 核心 | [新增詳解](#1-two-sum) |
| [49. Group Anagrams](https://leetcode.com/problems/group-anagrams/) | Medium | P0 核心 | [新增詳解](#49-group-anagrams) |
| [347. Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/) | Medium | P1 補強 | [新增詳解](#347-top-k-frequent-elements) |
| [271. Encode and Decode Strings](https://leetcode.com/problems/encode-and-decode-strings/)（Premium） | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [238. Product of Array Except Self](https://leetcode.com/problems/product-of-array-except-self/) | Medium | P1 補強 | [沿用75題詳解](#238-product-of-array-except-self) |
| [36. Valid Sudoku](https://leetcode.com/problems/valid-sudoku/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [128. Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/) | Medium | P1 補強 | [新增詳解](#128-longest-consecutive-sequence) |

## Two Pointers

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [125. Valid Palindrome](https://leetcode.com/problems/valid-palindrome/) | Easy | P0 核心 | [新增詳解](#125-valid-palindrome) |
| [167. Two Sum II - Input Array Is Sorted](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | Medium | P0 核心 | [新增詳解](#167-two-sum-ii---input-array-is-sorted) |
| [15. 3Sum](https://leetcode.com/problems/3sum/) | Medium | P1 補強 | [新增詳解](#15-3sum) |
| [11. Container With Most Water](https://leetcode.com/problems/container-with-most-water/) | Medium | P1 補強 | [沿用75題詳解](#11-container-with-most-water) |
| [42. Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/) | Hard | P2 選讀 | [新增詳解](#42-trapping-rain-water) |

## Sliding Window

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [121. Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | Easy | P1 補強 | [新增詳解](#121-best-time-to-buy-and-sell-stock) |
| [3. Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | Medium | P0 核心 | [新增詳解](#3-longest-substring-without-repeating-characters) |
| [424. Longest Repeating Character Replacement](https://leetcode.com/problems/longest-repeating-character-replacement/) | Medium | P1 補強 | [新增詳解](#424-longest-repeating-character-replacement) |
| [567. Permutation in String](https://leetcode.com/problems/permutation-in-string/) | Medium | P1 補強 | [新增詳解](#567-permutation-in-string) |
| [76. Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring/) | Hard | P2 選讀 | [新增詳解](#76-minimum-window-substring) |
| [239. Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/) | Hard | P2 選讀 | [新增詳解](#239-sliding-window-maximum) |

## Stack

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [20. Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) | Easy | P0 核心 | [新增詳解](#20-valid-parentheses) |
| [155. Min Stack](https://leetcode.com/problems/min-stack/) | Medium | P1 補強 | [新增詳解](#155-min-stack) |
| [150. Evaluate Reverse Polish Notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [739. Daily Temperatures](https://leetcode.com/problems/daily-temperatures/) | Medium | P1 補強 | [沿用75題詳解](#739-daily-temperatures) |
| [853. Car Fleet](https://leetcode.com/problems/car-fleet/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [84. Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | Hard | P2 選讀 | [新增詳解](#84-largest-rectangle-in-histogram) |

## Binary Search

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [704. Binary Search](https://leetcode.com/problems/binary-search/) | Easy | P0 核心 | [新增詳解](#704-binary-search) |
| [74. Search a 2D Matrix](https://leetcode.com/problems/search-a-2d-matrix/) | Medium | P1 補強 | [新增詳解](#74-search-a-2d-matrix) |
| [875. Koko Eating Bananas](https://leetcode.com/problems/koko-eating-bananas/) | Medium | P1 補強 | [沿用75題詳解](#875-koko-eating-bananas) |
| [153. Find Minimum in Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | Medium | P1 補強 | [新增詳解](#153-find-minimum-in-rotated-sorted-array) |
| [33. Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/) | Medium | P0 核心 | [新增詳解](#33-search-in-rotated-sorted-array) |
| [981. Time Based Key-Value Store](https://leetcode.com/problems/time-based-key-value-store/) | Medium | P1 補強 | [新增詳解](#981-time-based-key-value-store) |
| [4. Median of Two Sorted Arrays](https://leetcode.com/problems/median-of-two-sorted-arrays/) | Hard | E 延伸 | 延伸索引，尚無本地詳解 |

## Linked List

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [206. Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/) | Easy | P0 核心 | [沿用75題詳解](#206-reverse-linked-list) |
| [21. Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists/) | Easy | P0 核心 | [新增詳解](#21-merge-two-sorted-lists) |
| [141. Linked List Cycle](https://leetcode.com/problems/linked-list-cycle/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [143. Reorder List](https://leetcode.com/problems/reorder-list/) | Medium | P1 補強 | [新增詳解](#143-reorder-list) |
| [19. Remove Nth Node From End of List](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) | Medium | P0 核心 | [新增詳解](#19-remove-nth-node-from-end-of-list) |
| [138. Copy List with Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [2. Add Two Numbers](https://leetcode.com/problems/add-two-numbers/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [287. Find the Duplicate Number](https://leetcode.com/problems/find-the-duplicate-number/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [146. LRU Cache](https://leetcode.com/problems/lru-cache/) | Medium | P1 補強 | [新增詳解](#146-lru-cache) |
| [23. Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) | Hard | E 延伸 | 延伸索引，尚無本地詳解 |
| [25. Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/) | Hard | E 延伸 | 延伸索引，尚無本地詳解 |

## Trees

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [226. Invert Binary Tree](https://leetcode.com/problems/invert-binary-tree/) | Easy | P1 補強 | [新增詳解](#226-invert-binary-tree) |
| [104. Maximum Depth of Binary Tree](https://leetcode.com/problems/maximum-depth-of-binary-tree/) | Easy | P0 核心 | [沿用75題詳解](#104-maximum-depth-of-binary-tree) |
| [543. Diameter of Binary Tree](https://leetcode.com/problems/diameter-of-binary-tree/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [110. Balanced Binary Tree](https://leetcode.com/problems/balanced-binary-tree/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [100. Same Tree](https://leetcode.com/problems/same-tree/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [572. Subtree of Another Tree](https://leetcode.com/problems/subtree-of-another-tree/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [235. Lowest Common Ancestor of a Binary Search Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [102. Binary Tree Level Order Traversal](https://leetcode.com/problems/binary-tree-level-order-traversal/) | Medium | P0 核心 | [新增詳解](#102-binary-tree-level-order-traversal) |
| [199. Binary Tree Right Side View](https://leetcode.com/problems/binary-tree-right-side-view/) | Medium | P1 補強 | [沿用75題詳解](#199-binary-tree-right-side-view) |
| [1448. Count Good Nodes in Binary Tree](https://leetcode.com/problems/count-good-nodes-in-binary-tree/) | Medium | P1 補強 | [沿用75題詳解](#1448-count-good-nodes-in-binary-tree) |
| [98. Validate Binary Search Tree](https://leetcode.com/problems/validate-binary-search-tree/) | Medium | P0 核心 | [新增詳解](#98-validate-binary-search-tree) |
| [230. Kth Smallest Element in a BST](https://leetcode.com/problems/kth-smallest-element-in-a-bst/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [105. Construct Binary Tree from Preorder and Inorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/) | Medium | P1 補強 | [新增詳解](#105-construct-binary-tree-from-preorder-and-inorder-traversal) |
| [124. Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/) | Hard | P2 選讀 | [新增詳解](#124-binary-tree-maximum-path-sum) |
| [297. Serialize and Deserialize Binary Tree](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/) | Hard | P2 選讀 | [新增詳解](#297-serialize-and-deserialize-binary-tree) |

## Heap / Priority Queue

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [703. Kth Largest Element in a Stream](https://leetcode.com/problems/kth-largest-element-in-a-stream/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [1046. Last Stone Weight](https://leetcode.com/problems/last-stone-weight/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [973. K Closest Points to Origin](https://leetcode.com/problems/k-closest-points-to-origin/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [215. Kth Largest Element in an Array](https://leetcode.com/problems/kth-largest-element-in-an-array/) | Medium | P1 補強 | [沿用75題詳解](#215-kth-largest-element-in-an-array) |
| [621. Task Scheduler](https://leetcode.com/problems/task-scheduler/) | Medium | P2 選讀 | [新增詳解](#621-task-scheduler) |
| [355. Design Twitter](https://leetcode.com/problems/design-twitter/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [295. Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/) | Hard | P2 選讀 | [新增詳解](#295-find-median-from-data-stream) |

## Backtracking

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [78. Subsets](https://leetcode.com/problems/subsets/) | Medium | P0 核心 | [新增詳解](#78-subsets) |
| [39. Combination Sum](https://leetcode.com/problems/combination-sum/) | Medium | P0 核心 | [新增詳解](#39-combination-sum) |
| [40. Combination Sum II](https://leetcode.com/problems/combination-sum-ii/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [46. Permutations](https://leetcode.com/problems/permutations/) | Medium | P1 補強 | [新增詳解](#46-permutations) |
| [90. Subsets II](https://leetcode.com/problems/subsets-ii/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [22. Generate Parentheses](https://leetcode.com/problems/generate-parentheses/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [79. Word Search](https://leetcode.com/problems/word-search/) | Medium | P1 補強 | [新增詳解](#79-word-search) |
| [131. Palindrome Partitioning](https://leetcode.com/problems/palindrome-partitioning/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [17. Letter Combinations of a Phone Number](https://leetcode.com/problems/letter-combinations-of-a-phone-number/) | Medium | P1 補強 | [沿用75題詳解](#17-letter-combinations-of-a-phone-number) |
| [51. N-Queens](https://leetcode.com/problems/n-queens/) | Hard | E 延伸 | 延伸索引，尚無本地詳解 |

## Tries

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [208. Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/) | Medium | P1 補強 | [沿用75題詳解](#208-implement-trie-prefix-tree) |
| [211. Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) | Medium | P1 補強 | [新增詳解](#211-design-add-and-search-words-data-structure) |
| [212. Word Search II](https://leetcode.com/problems/word-search-ii/) | Hard | E 延伸 | 延伸索引，尚無本地詳解 |

## Graphs

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [200. Number of Islands](https://leetcode.com/problems/number-of-islands/) | Medium | P0 核心 | [新增詳解](#200-number-of-islands) |
| [695. Max Area of Island](https://leetcode.com/problems/max-area-of-island/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [133. Clone Graph](https://leetcode.com/problems/clone-graph/) | Medium | P1 補強 | [新增詳解](#133-clone-graph) |
| [286. Walls and Gates](https://leetcode.com/problems/walls-and-gates/)（Premium） | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [994. Rotting Oranges](https://leetcode.com/problems/rotting-oranges/) | Medium | P0 核心 | [沿用75題詳解](#994-rotting-oranges) |
| [417. Pacific Atlantic Water Flow](https://leetcode.com/problems/pacific-atlantic-water-flow/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [130. Surrounded Regions](https://leetcode.com/problems/surrounded-regions/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [207. Course Schedule](https://leetcode.com/problems/course-schedule/) | Medium | P0 核心 | [新增詳解](#207-course-schedule) |
| [210. Course Schedule II](https://leetcode.com/problems/course-schedule-ii/) | Medium | P1 補強 | [新增詳解](#210-course-schedule-ii) |
| [261. Graph Valid Tree](https://leetcode.com/problems/graph-valid-tree/)（Premium） | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [323. Number of Connected Components in an Undirected Graph](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/)（Premium） | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [684. Redundant Connection](https://leetcode.com/problems/redundant-connection/) | Medium | P1 補強 | [新增詳解](#684-redundant-connection) |
| [127. Word Ladder](https://leetcode.com/problems/word-ladder/) | Hard | E 延伸 | 延伸索引，尚無本地詳解 |

## Advanced Graphs

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [743. Network Delay Time](https://leetcode.com/problems/network-delay-time/) | Medium | P1 補強 | [新增詳解](#743-network-delay-time) |
| [332. Reconstruct Itinerary](https://leetcode.com/problems/reconstruct-itinerary/) | Hard | P2 選讀 | [新增詳解](#332-reconstruct-itinerary) |
| [1584. Min Cost to Connect All Points](https://leetcode.com/problems/min-cost-to-connect-all-points/) | Medium | P1 補強 | [新增詳解](#1584-min-cost-to-connect-all-points) |
| [778. Swim in Rising Water](https://leetcode.com/problems/swim-in-rising-water/) | Hard | E 延伸 | 延伸索引，尚無本地詳解 |
| [269. Alien Dictionary](https://leetcode.com/problems/alien-dictionary/)（Premium） | Hard | E 延伸 | 延伸索引，尚無本地詳解 |
| [787. Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |

## 1-D Dynamic Programming

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [70. Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | Easy | P0 核心 | [新增詳解](#70-climbing-stairs) |
| [746. Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | Easy | P0 核心 | [沿用75題詳解](#746-min-cost-climbing-stairs) |
| [198. House Robber](https://leetcode.com/problems/house-robber/) | Medium | P0 核心 | [沿用75題詳解](#198-house-robber) |
| [213. House Robber II](https://leetcode.com/problems/house-robber-ii/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [5. Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/) | Medium | P1 補強 | [新增詳解](#5-longest-palindromic-substring) |
| [647. Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [91. Decode Ways](https://leetcode.com/problems/decode-ways/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [322. Coin Change](https://leetcode.com/problems/coin-change/) | Medium | P0 核心 | [新增詳解](#322-coin-change) |
| [152. Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [139. Word Break](https://leetcode.com/problems/word-break/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [300. Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/) | Medium | P0 核心 | [新增詳解](#300-longest-increasing-subsequence) |
| [416. Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) | Medium | P1 補強 | [新增詳解](#416-partition-equal-subset-sum) |

## 2-D Dynamic Programming

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [62. Unique Paths](https://leetcode.com/problems/unique-paths/) | Medium | P1 補強 | [沿用75題詳解](#62-unique-paths) |
| [1143. Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | Medium | P1 補強 | [沿用75題詳解](#1143-longest-common-subsequence) |
| [309. Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/) | Medium | P1 補強 | [新增詳解](#309-best-time-to-buy-and-sell-stock-with-cooldown) |
| [518. Coin Change II](https://leetcode.com/problems/coin-change-ii/) | Medium | P1 補強 | [新增詳解](#518-coin-change-ii) |
| [494. Target Sum](https://leetcode.com/problems/target-sum/) | Medium | P1 補強 | [新增詳解](#494-target-sum) |
| [97. Interleaving String](https://leetcode.com/problems/interleaving-string/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [329. Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) | Hard | E 延伸 | 延伸索引，尚無本地詳解 |
| [115. Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/) | Hard | P2 選讀 | [新增詳解](#115-distinct-subsequences) |
| [72. Edit Distance](https://leetcode.com/problems/edit-distance/) | Medium | P1 補強 | [沿用75題詳解](#72-edit-distance) |
| [312. Burst Balloons](https://leetcode.com/problems/burst-balloons/) | Hard | E 延伸 | 延伸索引，尚無本地詳解 |
| [10. Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/) | Hard | E 延伸 | 延伸索引，尚無本地詳解 |

## Greedy

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [53. Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) | Medium | P0 核心 | [新增詳解](#53-maximum-subarray) |
| [55. Jump Game](https://leetcode.com/problems/jump-game/) | Medium | P1 補強 | [新增詳解](#55-jump-game) |
| [45. Jump Game II](https://leetcode.com/problems/jump-game-ii/) | Medium | P1 補強 | [新增詳解](#45-jump-game-ii) |
| [134. Gas Station](https://leetcode.com/problems/gas-station/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [846. Hand of Straights](https://leetcode.com/problems/hand-of-straights/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [1899. Merge Triplets to Form Target Triplet](https://leetcode.com/problems/merge-triplets-to-form-target-triplet/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [763. Partition Labels](https://leetcode.com/problems/partition-labels/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [678. Valid Parenthesis String](https://leetcode.com/problems/valid-parenthesis-string/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |

## Intervals

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [57. Insert Interval](https://leetcode.com/problems/insert-interval/) | Medium | P1 補強 | [新增詳解](#57-insert-interval) |
| [56. Merge Intervals](https://leetcode.com/problems/merge-intervals/) | Medium | P0 核心 | [新增詳解](#56-merge-intervals) |
| [435. Non-overlapping Intervals](https://leetcode.com/problems/non-overlapping-intervals/) | Medium | P1 補強 | [沿用75題詳解](#435-non-overlapping-intervals) |
| [252. Meeting Rooms](https://leetcode.com/problems/meeting-rooms/)（Premium） | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [253. Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/)（Premium） | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [1851. Minimum Interval to Include Each Query](https://leetcode.com/problems/minimum-interval-to-include-each-query/) | Hard | E 延伸 | 延伸索引，尚無本地詳解 |

## Math & Geometry

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [48. Rotate Image](https://leetcode.com/problems/rotate-image/) | Medium | P1 補強 | [新增詳解](#48-rotate-image) |
| [54. Spiral Matrix](https://leetcode.com/problems/spiral-matrix/) | Medium | P1 補強 | [新增詳解](#54-spiral-matrix) |
| [73. Set Matrix Zeroes](https://leetcode.com/problems/set-matrix-zeroes/) | Medium | P0 核心 | [新增詳解](#73-set-matrix-zeroes) |
| [202. Happy Number](https://leetcode.com/problems/happy-number/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [66. Plus One](https://leetcode.com/problems/plus-one/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [50. Pow(x, n)](https://leetcode.com/problems/powx-n/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [43. Multiply Strings](https://leetcode.com/problems/multiply-strings/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |
| [2013. Detect Squares](https://leetcode.com/problems/detect-squares/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |

## Bit Manipulation

| 題號與英文原題 | 難度 | 優先級 | 本地教學 |
|---|---|---|---|
| [136. Single Number](https://leetcode.com/problems/single-number/) | Easy | P1 補強 | [沿用75題詳解](#136-single-number) |
| [191. Number of 1 Bits](https://leetcode.com/problems/number-of-1-bits/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [338. Counting Bits](https://leetcode.com/problems/counting-bits/) | Easy | P1 補強 | [沿用75題詳解](#338-counting-bits) |
| [190. Reverse Bits](https://leetcode.com/problems/reverse-bits/) | Easy | P2 選讀 | [新增詳解](#190-reverse-bits) |
| [268. Missing Number](https://leetcode.com/problems/missing-number/) | Easy | E 延伸 | 延伸索引，尚無本地詳解 |
| [371. Sum of Two Integers](https://leetcode.com/problems/sum-of-two-integers/) | Medium | P2 選讀 | [新增詳解](#371-sum-of-two-integers) |
| [7. Reverse Integer](https://leetcode.com/problems/reverse-integer/) | Medium | E 延伸 | 延伸索引，尚無本地詳解 |


---

<a id="first"></a>

# 答案優先詳解：雜湊、雙指標、視窗、堆疊與搜尋

[回衝刺計畫](#plan)｜[題型選擇地圖](#map)

每題先讀中文題意，**直接看程式與規則**，再逐行走讀後面的例子。第一輪不用自己發明解法。每個 Kotlin 區塊獨立提交；中文為自行改寫的教學摘要，完整題目與限制以官方連結為準。熟練步驟統一為：對著答案手算一次 → 邊看邊輸入一次 → 隔天關閉答案重寫 → 對照相似題的改動。

## 1. Two Sum

Easy｜[英文原題](https://leetcode.com/problems/two-sum/)｜[官方中文](https://leetcode.cn/problems/two-sum/)

**中文題意**：從陣列找兩個不同位置，數字加總等於 target，回傳兩個索引。保證恰有一組解，不能重用自己。

**直接記這條規則**：每讀一個 x，先問字典「之前有沒有 target−x？」有就配對，沒有才記錄自己。

```kotlin
class Solution {
    fun twoSum(nums: IntArray, target: Int): IntArray {
        val seen = HashMap<Int, Int>() // 數值 -> 之前的索引
        for (i in nums.indices) {
            val other = seen[target - nums[i]]
            if (other != null) return intArrayOf(other, i)
            seen[nums[i]] = i
        }
        return intArrayOf() // 題目保證有解
    }
}
```

**原理走讀**：`[6,1,8,4]`、target=5。看 6，缺 -1，記 6→0；看 1，缺 4，記 1→1；看 8，缺 -3；看 4，缺 1，字典回索引 1，配現在索引 3。答案 `[1,3]`，不是 `[1,4]` 兩個值。

Map 消除「往前整段重找」；每對合法答案中較晚的那個被讀到時，前一個已在字典，所以不漏。先查再存防止 `[3,3]` 中第一個 3 與自己配對。平均 O(n) 時間、O(n) 空間；題目數值範圍下差值可用 Int。

**相似題改哪裡**：#167 已排序，可改雙指標省空間；舊教材 #1679 要最多組數，value 改存剩餘次數。不能直接排序本題再回排序後索引，因為題目要原位置。

**走讀自測**：為何 Map 的 value 不是 Boolean？答案：需要回傳位置，而不只是知道有沒有。

## 242. Valid Anagram

Easy｜[英文原題](https://leetcode.com/problems/valid-anagram/)｜[官方中文](https://leetcode.cn/problems/valid-anagram/)

**中文題意**：兩個小寫英文字串能否只靠重新排列變成彼此？字母及每種字母的個數都必須相同。

**規則**：第一串各字母加一，第二串各字母減一，最後全部歸零才相同。

```kotlin
class Solution {
    fun isAnagram(s: String, t: String): Boolean {
        if (s.length != t.length) return false
        val counts = IntArray(26)
        for (c in s) counts[c - 'a']++
        for (c in t) counts[c - 'a']--
        return counts.all { it == 0 }
    }
}
```

**原理走讀**：`"aabc"` 與 `"caba"`。讀完第一串，a=2,b=1,c=1；扣 c→0、a→1、b→0、a→0，全部清帳成功。`c-'a'` 是把字母轉成 0–25 的抽屜編號。

順序可任意調整，所以次數完全描述了答案。O(a+b) 時間、O(26)=O(1) 空間。固定陣列適用題目限定的小寫字母；若改成一般 Unicode 字元，不可仍用 26 格，需按新的字元定義計數。

**相似題**：#49 是把很多字串按同一張「字母帳單」分組；#567 是一邊滑動一邊維護帳單。自測：`"aa"`、`"ab"`？false，長度相同不夠。

## 49. Group Anagrams

Medium｜[英文原題](https://leetcode.com/problems/group-anagrams/)｜[官方中文](https://leetcode.cn/problems/group-anagrams/)

**中文題意**：將多個小寫單字分組，同組的字能互相重排得到。可能有空字串；各組輸出順序不限。

**規則**：每個單字變成「26 格字母次數簽名」，相同簽名放同一個袋子。

```kotlin
class Solution {
    fun groupAnagrams(strs: Array<String>): List<List<String>> {
        val groups = HashMap<List<Int>, MutableList<String>>()
        for (word in strs) {
            val counts = IntArray(26)
            for (c in word) counts[c - 'a']++
            val key = counts.toList()
            groups.getOrPut(key) { mutableListOf() }.add(word)
        }
        return groups.values.toList()
    }
}
```

**原理走讀**：`["arc","car","bat","rat","tar"]`。arc/car 都 a1,c1,r1，同袋；rat/tar 都 a1,r1,t1，同袋；bat 獨立。`getOrPut` 表示「已有袋子就拿來；沒有就建立空袋子並存進字典」。

List 的比較會看每格內容；不能直接拿 IntArray 作 key，否則兩個內容相同的陣列不會因此被當成同一把鑰匙。key 建立後不再修改。令 S 為全部字元數、n 為單字數，平均時間 O(S+26n)，額外結構及結果參照 O(26n+n)，原字串未複製。

**相似題**：#242 比兩份帳單；本題把帳單當分組 key；舊 #2352 把一整排數字當 key。若題目變成近似字而非完全同字母，這個簽名就不足。自測：兩個空字串？都進同一個全零簽名的袋子。

## 347. Top K Frequent Elements

Medium｜[英文原題](https://leetcode.com/problems/top-k-frequent-elements/)｜[官方中文](https://leetcode.cn/problems/top-k-frequent-elements/)

**中文題意**：找出現次數最高的 k 種數值，順序不限，保證結果唯一。k 不超過不同數值數量。

**規則**：先計次；再把數值放進「頻率為幾次」的桶子，由高頻桶往下拿。頻率最多 n，所以不必排序所有元素。

```kotlin
class Solution {
    fun topKFrequent(nums: IntArray, k: Int): IntArray {
        val counts = HashMap<Int, Int>()
        for (x in nums) counts[x] = (counts[x] ?: 0) + 1
        val buckets = Array(nums.size + 1) { mutableListOf<Int>() }
        for ((value, frequency) in counts) buckets[frequency].add(value)
        val answer = IntArray(k)
        var write = 0
        for (frequency in nums.size downTo 1) {
            for (value in buckets[frequency]) {
                answer[write++] = value
                if (write == k) return answer
            }
        }
        return answer
    }
}
```

**原理走讀**：`[4,4,4,7,7,9]`、k=2。次數 4→3、7→2、9→1。桶3放4，桶2放7，桶1放9；先拿桶3的4，再拿桶2的7，完成。

**選型理由**：頻率是範圍有限的整數，桶子能直接代表名次。平均 O(n) 時間、O(n) 空間。若資料持續串流而非一次給完，或頻率範圍太大，需另評估 heap 等方式，不照搬固定桶。

**相似題**：舊 #215 找的是數值第 k 大，這題比的是次數。誤用 Set 去重後就丟掉所有頻率。自測：最大值一定被選嗎？不一定，9 比4大但只出現一次。

## 128. Longest Consecutive Sequence

Medium｜[英文原題](https://leetcode.com/problems/longest-consecutive-sequence/)｜[官方中文](https://leetcode.cn/problems/longest-consecutive-sequence/)

**中文題意**：陣列未排序，找其中可組成連續整數的最長長度。可以不按原位置，重複數字不加長。可為空；要求線性時間。

**規則**：全部放 Set；只有 x−1 不存在，才從 x 開始一路數 x+1、x+2。

```kotlin
class Solution {
    fun longestConsecutive(nums: IntArray): Int {
        val values = nums.toHashSet()
        var best = 0
        for (x in values) {
            if (x - 1 in values) continue // 不是這條連續段的起點
            var end = x
            while (end + 1 in values) end++
            best = maxOf(best, end - x + 1)
        }
        return best
    }
}
```

**原理走讀**：`[9,3,1,2,2,8]`，名冊 `{1,2,3,8,9}`。1 沒有前輩0，數到3，長3；2、3 有前輩就不重數；8 沒有7，數到9，長2。答案3。

每條段只由最小值啟動一次，所以即使有巢狀 while，所有值合計只被連續走訪 O(n) 次。平均 O(n) 時間、O(n) 空間。數值限定 ±10⁹，x±1 在 Int 內。

**相似題**：舊 #334 要保留原順序且嚴格遞增，不是本題；#300 的遞增也不要求數值差1。自測：為何遍歷 Set 而非原陣列？避免重複起點造成反覆掃同一長段。

## 125. Valid Palindrome

Easy｜[英文原題](https://leetcode.com/problems/valid-palindrome/)｜[官方中文](https://leetcode.cn/problems/valid-palindrome/)

**中文題意**：忽略非英數字元與英文字母大小寫後，字串由前讀、由後讀是否相同？輸入為可列印 ASCII；沒有英數字元也視為回文。

**規則**：左右手指跳過不算的字元，再比較轉小寫後是否相同。

```kotlin
class Solution {
    fun isPalindrome(s: String): Boolean {
        var left = 0
        var right = s.lastIndex
        while (left < right) {
            while (left < right && !s[left].isLetterOrDigit()) left++
            while (left < right && !s[right].isLetterOrDigit()) right--
            if (s[left].lowercaseChar() != s[right].lowercaseChar()) return false
            left++
            right--
        }
        return true
    }
}
```

**原理走讀**：`"No, on!"`，N對n相同、o對o相同，中間逗號空格不算，true。每對外側相同後，剩下只需檢查內側。時間 O(n)，額外空間 O(1)，沒有建立清理過的新字串。

**相似題**：舊 #345 只交換母音，本題只比較英數字元；#5 要找最長回文子字串，不可以直接用整串左右一對一檢查取代。自測：`"0P"`？false，數字0與字母p不同。

## 167. Two Sum II - Input Array Is Sorted

Medium｜[英文原題](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/)｜[官方中文](https://leetcode.cn/problems/two-sum-ii-input-array-is-sorted/)

**中文題意**：已非遞減排序的陣列，找兩個不同位置加總為 target，答案保證唯一；回傳從 1 開始的索引，要求常數額外空間。

**規則**：兩端相加；太小移左邊讓值變大，太大移右邊讓值變小。

```kotlin
class Solution {
    fun twoSum(numbers: IntArray, target: Int): IntArray {
        var left = 0
        var right = numbers.lastIndex
        while (left < right) {
            val sum = numbers[left] + numbers[right]
            when {
                sum == target -> return intArrayOf(left + 1, right + 1)
                sum < target -> left++
                else -> right--
            }
        }
        return intArrayOf()
    }
}
```

**原理走讀**：`[1,3,6,8]`，target=9。兩端1+8=9，回傳 `[1,4]`。若 target=7，先1+8太大移右，1+6命中 `[1,3]`。

當最小值連最大值都不夠，最小值與其他值更不夠，所以可整個排除左端；太大時對稱。O(n) 時間、O(1) 額外空間。**未排序時這個排除理由不成立**，改用 #1。

自測：為何回傳要 +1？因題目特別指定 1-based，不是 Kotlin 陣列真的從1開始。

## 15. 3Sum

Medium｜[英文原題](https://leetcode.com/problems/3sum/)｜[官方中文](https://leetcode.cn/problems/3sum/)

**中文題意**：找所有三個不同位置，數字加總為0的組合，輸出數值；相同三數組合只能出現一次。

**規則**：先排序，固定第一個數，把剩下兩數交給雙指標；每層跳過相同值避免重複。

```kotlin
class Solution {
    fun threeSum(nums: IntArray): List<List<Int>> {
        val a = nums.sortedArray()
        val answer = mutableListOf<List<Int>>()
        for (i in 0 until a.size - 2) {
            if (i > 0 && a[i] == a[i - 1]) continue
            if (a[i] > 0) break
            var left = i + 1
            var right = a.lastIndex
            while (left < right) {
                val sum = a[i] + a[left] + a[right]
                when {
                    sum < 0 -> left++
                    sum > 0 -> right--
                    else -> {
                        answer.add(listOf(a[i], a[left], a[right]))
                        left++
                        right--
                        while (left < right && a[left] == a[left - 1]) left++
                        while (left < right && a[right] == a[right + 1]) right--
                    }
                }
            }
        }
        return answer
    }
}
```

**原理走讀**：`[-2,0,0,2,2]` 已排序。固定-2，左右0、2加總0，記 `[-2,0,2]`；左右移入後跳過相同0、2，不再重報。固定第一個0，只剩正數與0，沒有其他解。

第一個位置依序固定涵蓋所有組合；剩下沿用 #167 的排除理由。時間 O(n²)，複製排序陣列 O(n)，另加輸出 O(r)。本題值範圍 ±10⁵，三數和可用Int。

**相似題**：#1 回原索引不能丟位置，本題回數值可排序；四數和可再多固定一層，但複雜度也增加。自測：`[0,0,0,0]` 只輸出一組 `[0,0,0]`，不是四組。

## 42. Trapping Rain Water

Hard｜[英文原題](https://leetcode.com/problems/trapping-rain-water/)｜[官方中文](https://leetcode.cn/problems/trapping-rain-water/)

**中文題意**：每根柱寬1，高度非負，下雨後所有凹槽總共能留多少水？計算每格水量的總和。

**規則**：每格水位＝左右最高牆中較矮者；水量＝水位−自己高度。先用兩張表存左右最高，這版比省空間的雙指標更容易直接理解。

```kotlin
class Solution {
    fun trap(height: IntArray): Int {
        val n = height.size
        val leftMax = IntArray(n)
        val rightMax = IntArray(n)
        leftMax[0] = height[0]
        for (i in 1 until n) leftMax[i] = maxOf(leftMax[i - 1], height[i])
        rightMax[n - 1] = height[n - 1]
        for (i in n - 2 downTo 0) rightMax[i] = maxOf(rightMax[i + 1], height[i])
        var water = 0
        for (i in height.indices) water += minOf(leftMax[i], rightMax[i]) - height[i]
        return water
    }
}
```

**原理走讀**：`[3,0,2,0,4]`。

| 格子 | 高度 | 左最高 | 右最高 | 可留水 |
|---|---:|---:|---:|---:|
| 0 | 3 | 3 | 4 | 0 |
| 1 | 0 | 3 | 4 | 3 |
| 2 | 2 | 3 | 4 | 1 |
| 3 | 0 | 3 | 4 | 3 |
| 4 | 4 | 4 | 4 | 0 |

總共7。水會從矮牆漏掉，因此不可能高於左右最高牆中較小者；達到這高度以前則兩側都有牆擋住。O(n) 時間、O(n) 空間。表內最高值包含自己，所以差不會負。

**相似題**：舊 #11 選「兩根」算一個容器，本題把「每格上方」加總；不要因圖相像就共用面積公式。熟練後才學左右最高的 O(1) 空間版。自測：只有上坡 `[1,2,3]`？每格都留不住，0。

## 121. Best Time to Buy and Sell Stock

Easy｜[英文原題](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/)｜[官方中文](https://leetcode.cn/problems/best-time-to-buy-and-sell-stock/)

**中文題意**：按天給股價，只能先買一次再在以後某天賣一次，最多賺多少？可不交易，回傳0；不能先賣後買。

**規則**：一路記之前最低價，今天賣的收益是今天價減最低價。

```kotlin
class Solution {
    fun maxProfit(prices: IntArray): Int {
        var cheapest = prices[0]
        var best = 0
        for (price in prices) {
            cheapest = minOf(cheapest, price)
            best = maxOf(best, price - cheapest)
        }
        return best
    }
}
```

**原理走讀**：`[8,3,6,2,7]`：最低8、3；6賣可賺3；最低更新2；7賣可賺5。每一天若決定賣出，最好的買入點一定是它之前最便宜的一天，所以只需保留最低值。更新後同一天買賣只得到0，不會製造正的假收益。

O(n) 時間、O(1) 空間。**選型邊界**：只能一次交易才如此簡單；#309 有冷卻期、舊 #714 有手續費與多次交易，需買賣狀態DP，不能直接相減套用。自測：`[9,6,2]`？0，不強迫虧錢。

## 3. Longest Substring Without Repeating Characters

Medium｜[英文原題](https://leetcode.com/problems/longest-substring-without-repeating-characters/)｜[官方中文](https://leetcode.cn/problems/longest-substring-without-repeating-characters/)

**中文題意**：找最長連續子字串，使裡面沒有任何重複字元，回傳長度。不是可以跳過字元的子序列。

**規則**：記每個字上次的位置；若重複在目前窗內，左端跳到該位置後一格。左端不能倒退。

```kotlin
class Solution {
    fun lengthOfLongestSubstring(s: String): Int {
        val lastSeen = HashMap<Char, Int>()
        var left = 0
        var best = 0
        for (right in s.indices) {
            val previous = lastSeen[s[right]]
            if (previous != null) left = maxOf(left, previous + 1)
            lastSeen[s[right]] = right
            best = maxOf(best, right - left + 1)
        }
        return best
    }
}
```

**原理走讀**：`"abba"`。a→長1；b→長2；第二個b的位置2，看見上一個b在1，把left跳2；末尾a上次在0，已在窗外，left保持2，窗為ba。若寫 `left=previous+1` 就退到1，錯收bba。

以每個right結尾的合法最長窗都算過，平均 O(n) 時間、O(min(n,字元種類數)) 空間。題目一般字元可用Char；若產品需求是emoji等視覺字素，Char不等於一個完整顯示字，需另設計。

**相似題**：#424 容許改字後重複；舊 #1004 容許k個零；先說清楚合法窗條件。自測：空字串？0，迴圈不進。

## 424. Longest Repeating Character Replacement

Medium｜[英文原題](https://leetcode.com/problems/longest-repeating-character-replacement/)｜[官方中文](https://leetcode.cn/problems/longest-repeating-character-replacement/)

**中文題意**：大寫英文字串可修改至多k個位置，使某一段全部相同，求最長長度。

**規則**：窗內保留最多的那種字，其餘改掉；需要改的數量＝視窗長度−最高字頻。超過k就縮左。

```kotlin
class Solution {
    fun characterReplacement(s: String, k: Int): Int {
        val counts = IntArray(26)
        var left = 0
        var best = 0
        for (right in s.indices) {
            counts[s[right] - 'A']++
            while (right - left + 1 - counts.maxOrNull()!! > k) {
                counts[s[left] - 'A']--
                left++
            }
            best = maxOf(best, right - left + 1)
        }
        return best
    }
}
```

**原理走讀**：`"ABBBAC"`、k=1。窗ABBB有4字，B最多3個，只改1個A，合法長4；加入A變5−3=2，縮掉左端A，BBBA又合法；加入C再超標，繼續縮。最大4。

此版每次查26格的**當前最高字頻**，避免一開始就引入「歷史最大字頻不下調」的較難證明技巧。左右指標各至多n步，每步掃26，O(26n)=O(n) 時間、O(26) 空間。

**相似題**：#3 是窗內不得重複；本題希望變成全部相同，條件幾乎相反。自測：為何改最多的字反而不划算？保留最常見者，改動數才最少。

## 567. Permutation in String

Medium｜[英文原題](https://leetcode.com/problems/permutation-in-string/)｜[官方中文](https://leetcode.cn/problems/permutation-in-string/)

**中文題意**：s2 是否含一段連續字串，是s1某種排列？都只有小寫英文，回傳Boolean，不必列出所有排列。

**規則**：排列的長度固定為s1長度，沿s2滑同樣大小的窗，比26格字母頻率。

```kotlin
class Solution {
    fun checkInclusion(s1: String, s2: String): Boolean {
        val k = s1.length
        if (k > s2.length) return false
        val need = IntArray(26)
        val window = IntArray(26)
        for (c in s1) need[c - 'a']++
        for (right in s2.indices) {
            window[s2[right] - 'a']++
            if (right >= k) window[s2[right - k] - 'a']--
            if (right >= k - 1 && window.contentEquals(need)) return true
        }
        return false
    }
}
```

**原理走讀**：s1=`"ac"`、s2=`"zzcaxy"`，長2視窗是zz、zc、ca，ca的a1/c1與需求相同，true。不用生成ac、ca；頻率本身就描述所有排列共同特徵。

O(a+26b) 時間、O(26) 空間，a、b是兩長度。**相似題**：#242 整串頻率；#76 允許更長且找最短包含窗，不能固定長度。自測：s1有兩個a時，只含一個a的窗不夠。

## 76. Minimum Window Substring

Hard｜[英文原題](https://leetcode.com/problems/minimum-window-substring/)｜[官方中文](https://leetcode.cn/problems/minimum-window-substring/)

**中文題意**：從s找最短的連續片段，包含t要求的每個字元及數量，多餘字元可存在；大小寫不同。無解回空字串，題目保證最短答案唯一。

**規則**：右端擴到需求全部滿足，再不斷縮左端嘗試更短；`missing` 記「還缺幾個字元實例」，不是還缺幾種。

```kotlin
class Solution {
    fun minWindow(s: String, t: String): String {
        val need = IntArray(128) // 題目為大小寫英文字母
        for (c in t) need[c.code]++
        var missing = t.length
        var left = 0
        var bestStart = 0
        var bestLength = Int.MAX_VALUE
        for (right in s.indices) {
            val r = s[right].code
            if (need[r] > 0) missing--
            need[r]-- // 負數代表這種字有多餘
            while (missing == 0) {
                val length = right - left + 1
                if (length < bestLength) {
                    bestLength = length
                    bestStart = left
                }
                val l = s[left].code
                need[l]++
                if (need[l] > 0) missing++
                left++
            }
        }
        return if (bestLength == Int.MAX_VALUE) ""
        else s.substring(bestStart, bestStart + bestLength)
    }
}
```

**原理走讀**：s=`"XAABYC"`、t=`"ABC"`。開始缺3；X不需要；第一A缺2，第二A只是多餘；B缺1；Y無關；C缺0。現在左縮：刪X仍滿足；刪第一A仍有另一A；得到ABYC長4；再刪A就缺A，因此停。答案ABYC。

**為何對**：固定right，所有可移除的左端都嘗試過；一旦刪到需求不足，再縮只會更缺，必須等右端補貨。兩端只前進，O(|s|+|t|) 時間；計數額外O(128)，另加回傳字串長度。t按題意非空。

**相似題**：#567 要剛好同長排列，本題是至少覆蓋需求；舊 #1004 是違規才縮，本題則在滿足時努力縮小。自測：t=`"AA"` 只看到一個A，missing還是1，不能接受。

## 239. Sliding Window Maximum

Hard｜[英文原題](https://leetcode.com/problems/sliding-window-maximum/)｜[官方中文](https://leetcode.cn/problems/sliding-window-maximum/)

**中文題意**：固定長度k的窗每次往右移一格，回傳每個窗的最大值。1≤k≤n。

**規則**：用deque保留可能當最大值的**索引**，對應數值由大到小。過期的從頭丟；比新來的還小的從尾丟。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun maxSlidingWindow(nums: IntArray, k: Int): IntArray {
        val queue = ArrayDeque<Int>()
        val answer = IntArray(nums.size - k + 1)
        for (right in nums.indices) {
            while (queue.isNotEmpty() && queue.peekFirst() <= right - k) queue.removeFirst()
            while (queue.isNotEmpty() && nums[queue.peekLast()] <= nums[right]) queue.removeLast()
            queue.addLast(right)
            if (right >= k - 1) answer[right - k + 1] = nums[queue.peekFirst()]
        }
        return answer
    }
}
```

**原理走讀**：`[4,2,5,1]`、k=2。索引0進隊；索引1較小留在尾，窗[4,2]最大4；索引2值5比尾2大，丟2，再把舊4移掉（已過期），隊首5，窗[2,5]最大5；加1後窗[5,1]最大5。結果 `[4,5,5]`。

新的大值比舊的小值更大且更晚過期，因此舊小值未來永不可能贏，可以刪掉。每個索引最多進出各一次，O(n) 時間、O(k) deque空間，另加O(n−k+1)結果。

**相似題**：舊 #739 單調stack找下一個更大；本題多了「隨時間過期」，必須能從頭部移除，因此用deque。自測：只存數值為何不夠方便？無法直接知道哪個位置已離窗。

## 20. Valid Parentheses

Easy｜[英文原題](https://leetcode.com/problems/valid-parentheses/)｜[官方中文](https://leetcode.cn/problems/valid-parentheses/)

**中文題意**：字串只有三種括號，判斷能否正確成對且巢狀順序正確。只看左右數量相同不夠。

**規則**：遇開括號，直接把「未來期待的關括號」放stack；遇關括號，要和最上面期待相同。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun isValid(s: String): Boolean {
        val expected = ArrayDeque<Char>()
        for (c in s) {
            when (c) {
                '(' -> expected.addLast(')')
                '[' -> expected.addLast(']')
                '{' -> expected.addLast('}')
                else -> if (expected.isEmpty() || expected.removeLast() != c) return false
            }
        }
        return expected.isEmpty()
    }
}
```

**原理走讀**：`"{[]}"`，期待依序放`}`、`]`；看到`]`先滿足最近要求；看到`}`再滿足外層。`"([)]"` 看到`)`時目前期待`]`，立即false。

巢狀結構最後打開的要最先結束，正是後進先出。O(n) 時間、O(n) 空間。**相似題**：舊 #394 也是遇左括號保存外層工作；普通計數只適用不需區分這些順序的較簡單問題。自測：全部開括號走完仍有期待，false。

## 155. Min Stack

Medium｜[英文原題](https://leetcode.com/problems/min-stack/)｜[官方中文](https://leetcode.cn/problems/min-stack/)

**中文題意**：設計stack，支援push、pop、top、getMin，每個操作都要O(1)。pop/top/getMin呼叫時保證非空。

**規則**：每格同時保存「自己的值」與「到這格為止的最小值」。拿掉頂端，下一格仍記得當時最小。

```kotlin
class MinStack() {
    private class Entry(val value: Int, val minimum: Int, val previous: Entry?)
    private var topEntry: Entry? = null
    fun push(`val`: Int) {
        val minimum = minOf(`val`, topEntry?.minimum ?: `val`)
        topEntry = Entry(`val`, minimum, topEntry)
    }
    fun pop() { topEntry = topEntry!!.previous }
    fun top(): Int = topEntry!!.value
    fun getMin(): Int = topEntry!!.minimum
}
```

**原理走讀**：push5→(5,5)；push2→(2,2)；push4→(4,2)。getMin是2；pop4後仍2；pop2後頂(5,5)，最小恢復5。這是「每個歷史深度的快照」，不是每次重新掃stack。

每個Entry的previous指向先前的頂端，形成鏈式stack；pop只把頂端改指向上一格。每個操作O(1)，空間O(n)。這裡採一般資料結構分析，節點配置視為O(1)。

**相似題**：舊 #901 每格也可濃縮一段歷史。自測：重複最小值2 push兩次再pop一次？剩下那格仍記2，不會丟失最小值。

## 84. Largest Rectangle in Histogram

Hard｜[英文原題](https://leetcode.com/problems/largest-rectangle-in-histogram/)｜[官方中文](https://leetcode.cn/problems/largest-rectangle-in-histogram/)

**中文題意**：柱狀圖每柱寬1，找可以完整放在柱子範圍內的最大長方形面積。高度非負。

**規則**：單調stack保留非遞減高度的索引。新柱較矮時，替被彈出的高柱結算「以它為高度」的最大寬。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun largestRectangleArea(heights: IntArray): Int {
        val stack = ArrayDeque<Int>()
        var best = 0
        for (i in 0..heights.size) {
            val current = if (i == heights.size) 0 else heights[i]
            while (stack.isNotEmpty() && heights[stack.peekLast()] > current) {
                val height = heights[stack.removeLast()]
                val leftBoundary = if (stack.isEmpty()) -1 else stack.peekLast()
                val width = i - leftBoundary - 1
                best = maxOf(best, height * width)
            }
            stack.addLast(i)
        }
        return best
    }
}
```

**原理走讀**：`[2,4,4,1]`。前3柱進stack；遇索引3高度1，先彈第二個4，寬1面積4；再彈第一個4，此時左邊界索引0，寬3−0−1=2，面積8；再彈2，左邊界−1，寬3，面積6。最後補虛擬高度0結算尾部，最大8。

被彈出的柱遇到右側第一根更矮柱，不能再往右延伸；stack剩餘頂端給左界。相等高度暫時互相擋住，但更早的同高柱之後會取得完整寬，因此不漏最大矩形。每柱進出一次，O(n)時間、O(n)空間；本題上界乘積可用Int。

**相似題**：#239 是窗最大、#739 找下一個更大，這題以「較矮」觸發結算；改比較方向前先說清楚在等什麼。自測：寬為何要−1？兩個界線本身不屬於可用區段。

## 704. Binary Search

Easy｜[英文原題](https://leetcode.com/problems/binary-search/)｜[官方中文](https://leetcode.cn/problems/binary-search/)

**中文題意**：互異且升冪排序的陣列中找target的索引，找不到回−1，要求O(log n)。

**規則**：維持含頭含尾 `[left,right]` 是尚未排除的範圍；比中間小丟右半，比中間大丟左半。

```kotlin
class Solution {
    fun search(nums: IntArray, target: Int): Int {
        var left = 0
        var right = nums.lastIndex
        while (left <= right) {
            val mid = left + (right - left) / 2
            when {
                nums[mid] == target -> return mid
                nums[mid] < target -> left = mid + 1
                else -> right = mid - 1
            }
        }
        return -1
    }
}
```

**原理走讀**：`[2,5,8,11,14]`找11，中間索引2值8太小，left變3；中間索引3命中。若找10，第二輪11太大right變2，left>right表示候選已用完。

排序讓被排除的一半全都不可能，且每輪至少排除mid。O(log n)時間、O(1)空間。**相似題**：#153找轉折，#875二分答案而非查陣列；每次「丟半邊」的理由各不同。自測：用left=mid會怎樣？範圍剩兩格時可能不縮小，陷入迴圈。

## 33. Search in Rotated Sorted Array

Medium｜[英文原題](https://leetcode.com/problems/search-in-rotated-sorted-array/)｜[官方中文](https://leetcode.cn/problems/search-in-rotated-sorted-array/)

**中文題意**：原本互異升冪的陣列被旋轉過，例如尾部移到前頭。找target索引，沒有回−1，要求O(log n)。

**規則**：切中間後，至少有一半仍然完整排序；先認出哪半，再判斷target是否在它的值域內。

```kotlin
class Solution {
    fun search(nums: IntArray, target: Int): Int {
        var left = 0
        var right = nums.lastIndex
        while (left <= right) {
            val mid = left + (right - left) / 2
            if (nums[mid] == target) return mid
            if (nums[left] <= nums[mid]) {
                if (target >= nums[left] && target < nums[mid]) right = mid - 1
                else left = mid + 1
            } else {
                if (target > nums[mid] && target <= nums[right]) left = mid + 1
                else right = mid - 1
            }
        }
        return -1
    }
}
```

**原理走讀**：`[6,8,10,1,3,5]`找3。mid=2值10，左半6到10有序，但3不在裡面，丟左半；剩1,3,5，中間直接找到索引4。

只有一個旋轉斷點，因此左右不可能都內含斷點。O(log n)時間、O(1)空間。**適用條件**：本題數值互異；若允許大量重複，`left==mid==right`可能無法判哪半有序，需另處理，最壞不再保證O(log n)。

**相似題**：#704的單一比較，變成「認出有序半邊＋值域判斷」；#153不找任意值，只找最小值。自測：沒旋轉的陣列？仍適用。

## 74. Search a 2D Matrix

Medium｜[英文原題](https://leetcode.com/problems/search-a-2d-matrix/)｜[官方中文](https://leetcode.cn/problems/search-a-2d-matrix/)

**中文題意**：每列升序，下一列第一個值大於上一列最後一個值，判斷target是否存在。矩陣非空，要求O(log(mn))。

**規則**：把矩陣想成攤平的一條排序陣列，但不用真的複製；一維索引mid對應列mid/cols、欄mid%cols。

```kotlin
class Solution {
    fun searchMatrix(matrix: Array<IntArray>, target: Int): Boolean {
        val cols = matrix[0].size
        var left = 0
        var right = matrix.size * cols - 1
        while (left <= right) {
            val mid = left + (right - left) / 2
            val value = matrix[mid / cols][mid % cols]
            when {
                value == target -> return true
                value < target -> left = mid + 1
                else -> right = mid - 1
            }
        }
        return false
    }
}
```

**原理走讀**：`[[1,4,7],[9,12,15]]`視作 `[1,4,7,9,12,15]`。mid=4代表row=4/3=1、col=4%3=1，值12；其餘流程同#704。O(log(mn))時間、O(1)空間。

**選型邊界**：只有每行與每列各自有序、卻沒有「下一列首>上一列尾」時，攤平不一定排序，不能用本解。自測：一維索引3對應哪格？第1列第0欄，值9。

## 153. Find Minimum in Rotated Sorted Array

Medium｜[英文原題](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/)｜[官方中文](https://leetcode.cn/problems/find-minimum-in-rotated-sorted-array/)

**中文題意**：互異升冪陣列旋轉後，回傳最小值，要求O(log n)，陣列非空。

**規則**：mid比right大，最小值在mid右側；否則mid可能就是最小，保留mid並丟掉它右側。

```kotlin
class Solution {
    fun findMin(nums: IntArray): Int {
        var left = 0
        var right = nums.lastIndex
        while (left < right) {
            val mid = left + (right - left) / 2
            if (nums[mid] > nums[right]) left = mid + 1
            else right = mid
        }
        return nums[left]
    }
}
```

**原理走讀**：`[7,9,1,3,5]`，mid值1≤右5，最小一定不在mid後面，right=mid；剩7,9,1，mid9>右1，left=mid+1，停在1。

每次保留最小值所在的半邊，O(log n)時間、O(1)空間。**相似題**：#33是找target；此題找斷點最小。沒有重複是重要條件；全部相等時同樣的二分證明不能照搬。自測：為何不是right=mid−1？mid本身可能正是最小值。

## 981. Time Based Key-Value Store

Medium｜[英文原題](https://leetcode.com/problems/time-based-key-value-store/)｜[官方中文](https://leetcode.cn/problems/time-based-key-value-store/)

**中文題意**：set(key,value,time)記錄版本；get(key,time)找該key在不晚於指定時間的最新版本，沒有就回空字串。set的時間戳按題目保證嚴格遞增。

**規則**：每個key存按時間遞增的歷史清單，get用二分找最後一筆time≤查詢時間。

```kotlin
class TimeMap() {
    private data class Record(val time: Int, val value: String)
    private val history = HashMap<String, MutableList<Record>>()
    fun set(key: String, value: String, timestamp: Int) {
        history.getOrPut(key) { mutableListOf() }.add(Record(timestamp, value))
    }
    fun get(key: String, timestamp: Int): String {
        val records = history[key] ?: return ""
        var left = 0
        var right = records.lastIndex
        var answer = ""
        while (left <= right) {
            val mid = left + (right - left) / 2
            if (records[mid].time <= timestamp) {
                answer = records[mid].value
                left = mid + 1 // 這筆能用，但再找更晚的
            } else right = mid - 1
        }
        return answer
    }
}
```

**原理走讀**：key=color，歷史(2,red)、(6,blue)。查5，2合法，先記red再往右找；6太晚排除，回red。查1空字串，查6回blue。

時間遞增使append後仍排序，所以set不用排序。忽略字串key雜湊成本時，set平均／攤銷O(1)，get平均O(log m)，m是該key版本數；總記錄空間O(N)，另計字串儲存。若時間可能亂序，不能直接append再假設有序，需排序或有序結構。

**相似題**：#704找等於目標，本題找不超過目標的最右解；舊 #2300 找達門檻的最左解。自測：找到合法就立刻return可行嗎？不行，可能有更晚且仍合法的版本。


---

<a id="middle"></a>

# NeetCode 150 補充：鏈結串列、樹、堆積與圖（答案優先）

這一冊的使用方式是先看答案，再把每一個變數在小例子上走一遍。你已經有 Android 開發經驗，現在補的是「資料之間怎麼連、演算法如何利用這些連線」，不是重新學寫程式。完整原題與官方中文版在每題連結；中文題意是自行摘要，解法、例子與說明為原創教學。官方英文頁於 2026-09-21 查證。程式碼未宣稱已在 LeetCode 提交通過。

**共用小字典：** ListNode 是一個數字加上下一個物件的參照；TreeNode 是一個數字加左右孩子；133 題的 Node 則可有多位鄰居。平台提供這些類別，值存於 `` `val` ``。`===` 問的是「是否同一個物件」。`java.util.ArrayDeque` 的 `addLast/removeFirst` 是先進先出的佇列，`addLast/removeLast` 是後進先出的堆疊。Heap（堆積）能快速拿最小或最大值，但內部並非全部排好。

每題 Kotlin 程式獨立使用。為了避免 JVM 在很深的鏈狀樹或圖上耗盡呼叫堆疊，本冊所有走訪都用迴圈與明確容器。這不代表 O(n) 記憶體消失，而是把待辦事項放在一般記憶體中，避免一個節點對應一層函式呼叫。

## 21. Merge Two Sorted Lists

**Easy** · [英文原題](https://leetcode.com/problems/merge-two-sorted-lists/) · [官方中文](https://leetcode.cn/problems/merge-two-sorted-lists/)

**中文題意：** 兩條各自由小到大、可包含重複值的鏈結串列，重新接成一條有序串列。各可為空，最多 50 個節點，值在 ±100。回傳合併後的 head。

### 先記核心規則，再看完整答案

**每次接兩個開頭中較小的那個。** 用一個假的開頭 dummy，省掉「第一次接節點」的特殊情況。

```kotlin
class Solution {
    fun mergeTwoLists(list1: ListNode?, list2: ListNode?): ListNode? {
        val dummy = ListNode(0)
        var tail = dummy
        var a = list1
        var b = list2
        while (a != null && b != null) {
            if (a.`val` <= b.`val`) {
                tail.next = a
                a = a.next
            } else {
                tail.next = b
                b = b.next
            }
            tail = tail.next!!
        }
        tail.next = a ?: b
        return dummy.next
    }
}
```

### 對著答案走讀

原創輸入 A=`[-3,4]`、B=`[-2,4,7]`：

| 比較的開頭 | 接上誰 | 已完成部分 |
|---|---|---|
| -3、-2 | A 的 -3 | -3 |
| 4、-2 | B 的 -2 | -3→-2 |
| 4、4 | A 的 4 | -3→-2→4 |
| A 已空 | 整串接上 B | -3→-2→4→4→7 |

tail 永遠站在已完成串列的最後，a、b 站在尚未接走的位置。因為兩串本來就排序，開頭就是各自剩餘最小值；選較小開頭，不會跳過更小的數。剩一串時可以整段接上，無需逐個複製。

**為何選／不適用：** 已排序輸入可以利用兩個指標；未排序就不能只看頭。這版會重新接原節點，若業務要求保留原串列，需複製節點，額外空間會增加。

**複雜度：** m、n 為兩串長度，時間 O(m+n)，額外空間 O(1)。**相似題怎麼改：** 合併 k 串時可用最小 Heap 比較 k 個開頭。**提醒：** 回傳 dummy.next，不是 dummy。

## 19. Remove Nth Node From End of List

**Medium** · [英文原題](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) · [官方中文](https://leetcode.cn/problems/remove-nth-node-from-end-of-list/)

**中文題意：** 刪除倒數第 n 個節點，回傳新 head。串列長度 1–30、值 0–100，n 保證在合法範圍。

### 先記核心規則，再看完整答案

**快指標先走 n 格，再一起前進；快指標到最後一個時，慢指標站在待刪節點前面。** 兩人都從 dummy 出發。

```kotlin
class Solution {
    fun removeNthFromEnd(head: ListNode?, n: Int): ListNode? {
        val dummy = ListNode(0)
        dummy.next = head
        var fast: ListNode = dummy
        var slow: ListNode = dummy
        repeat(n) { fast = fast.next!! }
        while (fast.next != null) {
            fast = fast.next!!
            slow = slow.next!!
        }
        slow.next = slow.next?.next
        return dummy.next
    }
}
```

### 對著答案走讀

原創輸入 `8→6→3→1`、n=3。快先到值 3，慢在 dummy；兩人再同步一步：快到 1，慢到 8。fast.next 已空，於是把 `8.next` 從 6 改成 3，得到 `8→3→1`。快慢距離一直是 3 條邊，因此快到尾巴時，慢的下一個就是倒數第 3 個。

**為何選／不適用：** 不知道長度又希望一趟完成時，固定間隔很好用。若 n 不合法，這份平台版不負責驗證，直接使用在一般 App API 要加輸入檢查。dummy 讓刪除原 head 也等同修改前一個節點的 next。

**複雜度：** L 為長度，時間 O(L)，額外空間 O(1)，修改輸入。**相似題怎麼改：** 找倒數第 n 個而不刪，可以調整停止位置直接回傳節點。**提醒：** 本版停止條件是 fast.next，而不是 fast==null，不能混用其他版本的起始距離。

## 143. Reorder List

**Medium** · [英文原題](https://leetcode.com/problems/reorder-list/) · [官方中文](https://leetcode.cn/problems/reorder-list/)

**中文題意：** 將串列改為第一個、最後一個、第二個、倒數第二個……交錯排列，只改節點連結，不能用改值冒充。節點 1–50,000，值 1–1,000；函式原地修改，沒有回傳值。

### 先記核心規則，再看完整答案

**找中點 → 切斷 → 反轉後半 → 交錯接回。** 這是把已學過的小操作組合，不是新的神奇技巧。

```kotlin
class Solution {
    fun reorderList(head: ListNode?) {
        if (head?.next == null) return
        var slow: ListNode = head
        var fast: ListNode? = head
        while (fast?.next?.next != null) {
            slow = slow.next!!
            fast = fast.next?.next
        }
        var current = slow.next
        slow.next = null
        var reversed: ListNode? = null
        while (current != null) {
            val next = current.next
            current.next = reversed
            reversed = current
            current = next
        }
        var left: ListNode? = head
        var right = reversed
        while (right != null) {
            val leftNext = left!!.next
            val rightNext = right.next
            left.next = right
            right.next = leftNext
            left = leftNext
            right = rightNext
        }
    }
}
```

### 對著答案走讀

原創輸入 `2→4→6→8→10`。slow 最後在 6，切成 `2→4→6` 和 `8→10`；反轉第二串成 `10→8`。第一輪把 10 放在 2 後，第二輪把 8 放在 4 後，成為 `2→10→4→8→6`。leftNext、rightNext 在改箭頭前先保存，才不會丟失剩下的路。

左半長度等於右半或多一，因此右半走完就結束。左邊按原順序、右邊按倒序，每輪各取一個，正是要求的頭尾交錯。

**為何選／不適用：** 鏈結串列無法 O(1) 取最後一個，反轉後半將「從尾取」變成「從頭取」。若輸入不允許修改，應另建輸出。**複雜度：** L 為節點數，三次線性工作仍是 O(L)，空間 O(1)。**相似題怎麼改：** 2130 反轉後半後比較成對總和，不需要交錯。**提醒：** 一定先切斷，否則接回時可能形成環。

## 146. LRU Cache

**Medium** · [英文原題](https://leetcode.com/problems/lru-cache/) · [官方中文](https://leetcode.cn/problems/lru-cache/)

**中文題意：** 容量固定的快取提供 get/put；get 不存在回 -1。成功讀取、新增、更新都算最近使用。超過容量刪最久沒用的 key。容量 1–3,000，最多 200,000 次操作，要求 get/put 平均 O(1)。

### 先記核心規則，再看完整答案

**HashMap 負責瞬間找到節點，雙向串列負責瞬間搬到最前面。** head 後是最新、tail 前是最舊。這不是照加入時間刪除。

```kotlin
class LRUCache(private val capacity: Int) {
    private class Entry(val key: Int, var value: Int) {
        var prev: Entry? = null
        var next: Entry? = null
    }
    private val index = HashMap<Int, Entry>()
    private val head = Entry(0, 0)
    private val tail = Entry(0, 0)
    init { head.next = tail; tail.prev = head }
    private fun unlink(node: Entry) {
        node.prev!!.next = node.next
        node.next!!.prev = node.prev
    }
    private fun putFirst(node: Entry) {
        node.prev = head
        node.next = head.next
        head.next!!.prev = node
        head.next = node
    }
    private fun touch(node: Entry) {
        unlink(node)
        putFirst(node)
    }
    fun get(key: Int): Int {
        val node = index[key] ?: return -1
        touch(node)
        return node.value
    }
    fun put(key: Int, value: Int) {
        val existing = index[key]
        if (existing != null) {
            existing.value = value
            touch(existing)
            return
        }
        val node = Entry(key, value)
        index[key] = node
        putFirst(node)
        if (index.size > capacity) {
            val oldest = tail.prev!!
            unlink(oldest)
            index.remove(oldest.key)
        }
    }
}
```

### 對著答案走讀

想像 Android 圖片快取，容量 2，從左到右「新→舊」：

| 操作 | 次序 | 發生什麼 |
|---|---|---|
| put(5,50) | 5 | 加入 |
| put(7,70) | 7→5 | 7 最新 |
| get(5) | 5→7 | 讀到 50，5 搬前面 |
| put(9,90) | 9→5 | 7 最久沒用，被刪 |

如果只有 HashMap，找得到 7 卻不知道誰最舊；只有串列，更新 5 前必須一路找。兩者合用，Map 存的是**同一個串列節點的參照**。unlink 只接起前後鄰居，不掃描；putFirst 只改固定幾根箭頭。每次操作保持一個 key 恰有一個 Map 項目與一個真節點。

**為何選／不適用：** 適用「最近使用」政策；若要依使用次數 LFU，這個次序不夠。這是單執行緒練習，不能直接當具執行緒安全保證的 Android 共用快取。

**複雜度：** 容量 C，get/put 平均 O(1)，空間 O(C)。**相似題怎麼改：** FIFO 不需在 get 時 touch；TTL 另外需要時間過期規則。**提醒：** 更新既有 key 不能新增第二個節點；淘汰要同步刪 Map。

## 226. Invert Binary Tree

**Easy** · [英文原題](https://leetcode.com/problems/invert-binary-tree/) · [官方中文](https://leetcode.cn/problems/invert-binary-tree/)

**中文題意：** 將二元樹左右鏡像，回傳 root。最多 100 節點，可空；值在 ±100。

### 先記核心規則，再看完整答案

**每個節點都交換左右孩子，整棵樹就鏡像。** 不只交換根的左右。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun invertTree(root: TreeNode?): TreeNode? {
        if (root == null) return null
        val stack = ArrayDeque<TreeNode>()
        stack.addLast(root)
        while (stack.isNotEmpty()) {
            val node = stack.removeLast()
            val oldLeft = node.left
            node.left = node.right
            node.right = oldLeft
            node.left?.let { stack.addLast(it) }
            node.right?.let { stack.addLast(it) }
        }
        return root
    }
}
```

### 對著答案走讀

原創樹根 6，左孩 2、右孩 9，2 只有右孩 4。交換根之後，9 到左、2 到右；處理 2 時，把 4 從右移左。結果根 6 左孩 9、右孩 2，2 左孩 4。整棵樹的每一步「往左」都變「往右」，所以任意節點的位置都正好鏡像。

**為何選／不適用：** 不要求先處理誰，DFS 堆疊簡單；本題沒有共享孩子或環。一般圖不能不做 seen 就套用。**複雜度：** N 節點、H 高度，時間 O(N)，堆疊 O(H)，最壞 O(N)，原地修改。**相似題怎麼改：** 判斷兩棵樹是否鏡像，改成同時比較 A.left 與 B.right，不需修改。**提醒：** 先存 oldLeft，避免交換後兩邊指向同一棵子樹。

## 102. Binary Tree Level Order Traversal

**Medium** · [英文原題](https://leetcode.com/problems/binary-tree-level-order-traversal/) · [官方中文](https://leetcode.cn/problems/binary-tree-level-order-traversal/)

**中文題意：** 依層由上到下、每層由左到右，回傳二維值清單。最多 2,000 節點，可空；值在 ±1,000。

### 先記核心規則，再看完整答案

**每輪先固定佇列人數，這些就是同一層。** 新加入的孩子下一輪再看。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun levelOrder(root: TreeNode?): List<List<Int>> {
        val answer = mutableListOf<List<Int>>()
        if (root == null) return answer
        val queue = ArrayDeque<TreeNode>()
        queue.addLast(root)
        while (queue.isNotEmpty()) {
            val count = queue.size
            val level = mutableListOf<Int>()
            repeat(count) {
                val node = queue.removeFirst()
                level.add(node.`val`)
                node.left?.let { queue.addLast(it) }
                node.right?.let { queue.addLast(it) }
            }
            answer.add(level)
        }
        return answer
    }
}
```

### 對著答案走讀

原創樹根 5、孩子 2 與 8，2 右孩 3、8 左孩 7。每輪取出的整批為 `[5]`、`[2,8]`、`[3,7]`，輸出 `[[5],[2,8],[3,7]]`。queue 就像下一班要處理的工作排隊。左孩先進隊，因此同層左邊先出來；父層處理完，剛好只剩下一層。

**為何選／不適用：** 需要按距離或層分批，BFS 最直接；若是帶不同權重的最短距離，單純先進先出不一定正確。**複雜度：** N 節點、W 最大層寬，時間 O(N)，額外佇列 O(W)，輸出 O(N)。**相似題怎麼改：** 每層取最後一個是 199；每層加總是 1161。**提醒：** 不要在同一輪把剛加入的孩子也全取光，否則層界線消失。

## 98. Validate Binary Search Tree

**Medium** · [英文原題](https://leetcode.com/problems/validate-binary-search-tree/) · [官方中文](https://leetcode.cn/problems/validate-binary-search-tree/)

**中文題意：** 判斷整棵樹是否滿足 BST：左子樹全部嚴格小於根、右子樹全部嚴格大於根，每棵子樹也一樣。不允許相等值。節點 1–10,000，值可到 Int 的上下限。

### 先記核心規則，再看完整答案

**每個節點繼承祖先給它的允許範圍。** 往左縮上限，往右縮下限。範圍用 Long，才能容納 Int 極值。

```kotlin
import java.util.ArrayDeque
class Solution {
    private data class Check(val node: TreeNode, val low: Long, val high: Long)
    fun isValidBST(root: TreeNode?): Boolean {
        if (root == null) return true
        val stack = ArrayDeque<Check>()
        stack.addLast(Check(root, Long.MIN_VALUE, Long.MAX_VALUE))
        while (stack.isNotEmpty()) {
            val check = stack.removeLast()
            val value = check.node.`val`.toLong()
            if (value <= check.low || value >= check.high) return false
            check.node.left?.let { stack.addLast(Check(it, check.low, value)) }
            check.node.right?.let { stack.addLast(Check(it, value, check.high)) }
        }
        return true
    }
}
```

### 對著答案走讀

原創樹根 10，右孩 16，16 左孩 8。8 比父親 16 小，看起來符合局部規則，但它在 10 的右子樹，必須大於 10。傳給它的範圍為 `(10,16)`，8 不在其中，回傳 false。範圍保留所有祖先限制，而不只上一層。

**為何選／不適用：** 範圍適合驗證整體 BST 規則；若某變體允許重複值，要先確認重複可以放哪側，再調整端點是否包含。**複雜度：** N 節點、H 高度，時間 O(N)，空間 O(H)。**相似題怎麼改：** 中序讀值必須嚴格遞增，也能驗證，需保存前一值。**提醒：** 不要用 value±1 縮範圍，Int 邊界可能溢位。

## 105. Construct Binary Tree from Preorder and Inorder Traversal

**Medium** · [英文原題](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/) · [官方中文](https://leetcode.cn/problems/construct-binary-tree-from-preorder-and-inorder-traversal/)

**中文題意：** 已知同一棵樹的前序與中序陣列，建立樹。前序是「根、左、右」，中序是「左、根、右」。長度 1–3,000，值唯一，輸入保證有效。

### 先記核心規則，再看完整答案

**前序的第一個是這棵子樹根；用它在中序的位置切出左右子樹。** Map 讓找位置平均 O(1)，索引區間讓切割不複製陣列；工作堆疊取代深遞迴。

```kotlin
import java.util.ArrayDeque
class Solution {
    private data class Job(val preStart: Int, val inLeft: Int, val inRight: Int,
                           val parent: TreeNode?, val isLeft: Boolean)
    fun buildTree(preorder: IntArray, inorder: IntArray): TreeNode? {
        if (preorder.isEmpty()) return null
        val position = HashMap<Int, Int>()
        for (i in inorder.indices) position[inorder[i]] = i
        val jobs = ArrayDeque<Job>()
        jobs.addLast(Job(0, 0, inorder.lastIndex, null, false))
        var root: TreeNode? = null
        while (jobs.isNotEmpty()) {
            val job = jobs.removeLast()
            if (job.inLeft > job.inRight) continue
            val node = TreeNode(preorder[job.preStart])
            val parent = job.parent
            if (parent == null) root = node
            else if (job.isLeft) parent.left = node else parent.right = node
            val middle = position.getValue(node.`val`)
            val leftSize = middle - job.inLeft
            jobs.addLast(Job(job.preStart + 1 + leftSize, middle + 1,
                             job.inRight, node, false))
            jobs.addLast(Job(job.preStart + 1, job.inLeft, middle - 1, node, true))
        }
        return root
    }
}
```

### 對著答案走讀

原創 preorder=`[8,3,5,11]`，inorder=`[3,5,8,11]`。

| 子問題 | 根 | 中序左半 | 中序右半 |
|---|---|---|---|
| 整棵樹 | 8 | 3,5（2 個） | 11 |
| 8 的左樹 | 3 | 空 | 5 |
| 8 的右樹 | 11 | 空 | 空 |

`leftSize=2`，所以右子樹的根在前序索引 `0+1+2=3`，是 11。左子樹根只往後一格，是 3。每個 Job 保存「要處理哪個區間、建好掛在哪個父親哪側」，等同手寫待辦便條。子區間互不重疊，每個節點只建立一次，因此平均 O(N)。若每次 indexOf 搜根或 copyOfRange 切新陣列，長鏈容易退化 O(N²)。

**為何選／不適用：** 前序決定根，中序決定左右邊界；值重複時位置 Map 無法唯一定位，這份解法不適用。**複雜度：** N 為節點數，平均時間 O(N)，Map 與工作記憶體 O(N)，輸出樹 O(N)。**相似題怎麼改：** 後序+中序的根在後序區間末尾，要重算左右區間。**提醒：** `inRight` 是包含端點，空區間是 left>right。

## 124. Binary Tree Maximum Path Sum

**Hard** · [英文原題](https://leetcode.com/problems/binary-tree-maximum-path-sum/) · [官方中文](https://leetcode.cn/problems/binary-tree-maximum-path-sum/)

**中文題意：** 在非空二元樹選一條不重複節點的連續路徑，不必經過根，求最大總和。路徑至少有一個節點，可能有負數；節點 1–30,000，值在 ±1,000。

### 先記核心規則，再看完整答案

先補一個觀念：**「目前能成為完整答案的路徑」可以接左右兩側；「要交給父親繼續延長的路徑」只能選一側。** 否則會變三岔，不再是一條路。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun maxPathSum(root: TreeNode?): Int {
        if (root == null) return 0
        val stack = ArrayDeque<Pair<TreeNode, Boolean>>()
        val gain = HashMap<TreeNode, Int>()
        stack.addLast(root to false)
        var best = Int.MIN_VALUE
        while (stack.isNotEmpty()) {
            val (node, ready) = stack.removeLast()
            if (!ready) {
                stack.addLast(node to true)
                node.right?.let { stack.addLast(it to false) }
                node.left?.let { stack.addLast(it to false) }
            } else {
                val left = maxOf(0, gain[node.left] ?: 0)
                val right = maxOf(0, gain[node.right] ?: 0)
                best = maxOf(best, node.`val` + left + right)
                gain[node] = node.`val` + maxOf(left, right)
            }
        }
        return best
    }
}
```

### 對著答案走讀

原創樹根 -5、左孩 4、右孩 7，7 的孩子是 2、3。葉子各回報 4、2、3；在 7 能成為完整答案的是 `2→7→3`，和 12，但交給父親的只能是 `7→3`，和 10。根處候選為 `4→-5→7→3`，和 9，因此全域最好仍是 12。

ready=false 先排孩子；ready=true 時孩子結果已寫在 gain，稱為「後序」——事情從下面算回來。負的孩子貢獻改成 0，表示不走那側；但是 best 初始化為 Int.MIN_VALUE，仍允許全負樹選一個最不負的節點。每條路都有一個最高節點，在該節點用左右最佳貢獻計算時必被涵蓋。

**為何選／不適用：** 樹沒有環，子樹能獨立回報最佳延伸；一般有環圖的最長簡單路徑不能直接套這個式子。官方節點數和值限制使 Int 路徑和足夠；自訂更大值需改 Long。**複雜度：** N 節點，平均時間 O(N)，gain 與堆疊合計 O(N)。**相似題怎麼改：** 最長路徑長度題，把節點值貢獻改成邊數。**提醒：** 不可把 `node+left+right` 交給父親。

## 297. Serialize and Deserialize Binary Tree

**Hard** · [英文原題](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/) · [官方中文](https://leetcode.cn/problems/serialize-and-deserialize-binary-tree/)

**中文題意：** 設計 Codec，將樹轉字串，再由字串還原同樣的結構與值。格式可自行設計，不必照 LeetCode 畫面的陣列格式；要能處理空樹與負值；最多 10,000 節點，值在 ±1,000。

### 先記核心規則，再看完整答案

**不能只記數值，還要記哪個孩子缺席。** 這版用 BFS，每個真節點固定輸出左右兩個欄位，空孩子記 `#`，逗號分隔。空樹就是 `#`。

```kotlin
import java.util.ArrayDeque
class Codec {
    fun serialize(root: TreeNode?): String {
        if (root == null) return "#"
        val tokens = mutableListOf(root.`val`.toString())
        val queue = ArrayDeque<TreeNode>()
        queue.addLast(root)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val left = node.left
            val right = node.right
            tokens.add(left?.`val`?.toString() ?: "#")
            tokens.add(right?.`val`?.toString() ?: "#")
            if (left != null) queue.addLast(left)
            if (right != null) queue.addLast(right)
        }
        return tokens.joinToString(",")
    }
    fun deserialize(data: String): TreeNode? {
        if (data == "#") return null
        val tokens = data.split(',')
        val root = TreeNode(tokens[0].toInt())
        val queue = ArrayDeque<TreeNode>()
        queue.addLast(root)
        var index = 1
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val leftToken = tokens[index++]
            val rightToken = tokens[index++]
            if (leftToken != "#") {
                val left = TreeNode(leftToken.toInt())
                node.left = left
                queue.addLast(left)
            }
            if (rightToken != "#") {
                val right = TreeNode(rightToken.toInt())
                node.right = right
                queue.addLast(right)
            }
        }
        return root
    }
}
```

### 對著答案走讀

原創樹根 -4，只有右孩 12，12 只有左孩 -7。輸出 `-4,#,12,-7,#,#,#`。第一個是根；處理 -4 時讀兩欄 `#,12`；處理 12 讀 `-7,#`；處理 -7 讀 `#,#`。如此每個父親對應固定兩欄，左右位置不會混淆。`toInt()` 處理多位數與負號，不要逐字元當成一個數。

先補 Hard 前置觀念：序列化本質是雙方約好的檔案格式。你在 Android 傳 JSON 也在做類似事；這題只是自己決定欄位順序。ArrayDeque 不收 null，所以只將真孩子排隊，把缺席記在字串中。

**為何選／不適用：** 樹可用孩子欄位重建；若物件有共享參照或環，還要加入節點 ID，否則無法保留物件關係。deserialize 只接受本 Codec 產生的有效格式，不是一般不可信輸入解析器。

**複雜度：** N 為節點數、S 為字串長度，時間與總空間 O(N+S)，固定 Int 範圍下可寫 O(N)。**相似題怎麼改：** BST 有大小規則，有時可不存所有空位，但一般樹不能省掉結構資訊。**提醒：** 本版保留尾端 #，不能拿其他格式字串直接測 deserialize。

## 295. Find Median from Data Stream

**Hard** · [英文原題](https://leetcode.com/problems/find-median-from-data-stream/) · [官方中文](https://leetcode.cn/problems/find-median-from-data-stream/)

**中文題意：** 持續 addNum 加入數字，findMedian 回傳目前排序後的中位數。奇數筆取正中間，偶數筆取中間兩數平均；查詢時保證至少有一筆。輸入值在 ±100,000，總操作至多 50,000 次。

### 先記核心規則，再看完整答案

先補 Heap 觀念：只需看「較小半邊的最大」和「較大半邊的最小」，不必把全部排好。**左邊最大 Heap、右邊最小 Heap；左邊人數等於右邊，或只多一個。**

```kotlin
import java.util.PriorityQueue
class MedianFinder() {
    private val lower = PriorityQueue<Int>(compareByDescending<Int> { it })
    private val upper = PriorityQueue<Int>()
    fun addNum(num: Int) {
        lower.offer(num)
        upper.offer(lower.poll())
        if (upper.size > lower.size) lower.offer(upper.poll())
    }
    fun findMedian(): Double {
        return if (lower.size > upper.size) lower.peek().toDouble()
        else (lower.peek().toLong() + upper.peek().toLong()) / 2.0
    }
}
```

### 對著答案走讀

原創輸入依序 6、-2、10、4。表內為了易讀把兩邊排好，Heap 內部不保證整體排序。

| 加入 | 小半 lower | 大半 upper | 中位數 |
|---|---|---|---|
| 6 | [6] | [] | 6 |
| -2 | [-2] | [6] | 2 |
| 10 | [-2,6] | [10] | 6 |
| 4 | [-2,4] | [6,10] | 5 |

先放 lower，再把 lower 最大搬到 upper，可以讓兩邊仍滿足「左邊每個≤右邊每個」。如果右邊比較多人，就把右邊最小搬回來，恢復人數規則。這兩條規則成立，中間位置就一定在兩個箱口。

**為何選／不適用：** 適合不斷新增並查中位數。若還要任意刪除舊元素，例如滑動視窗中位數，普通 PriorityQueue 刪任意值是線性，需延遲刪除或其他結構。**複雜度：** N 為目前資料筆數，新增 O(log(N+1))、查詢 O(1)、空間 O(N)。**相似題怎麼改：** 固定第 k 小可讓小半維持 k 個。**提醒：** 平均前先轉 Long，再除 2.0，避免整數截斷與加法溢位。

## 621. Task Scheduler

**Medium** · [英文原題](https://leetcode.com/problems/task-scheduler/) · [官方中文](https://leetcode.cn/problems/task-scheduler/)

**中文題意：** 每個大寫英文字母代表一種任務，一個時間格做一件或閒置；相同字母兩次之間至少隔 n 格。任務可重排，求全部做完最少格數。任務數 1–10,000，n 為 0–100。

### 先記核心規則，再看完整答案

**答案是 `max(總件數, (最高頻率−1)×(n+1)+最高頻率的種類數)`。** 先看程式，再用排空格理解公式，不用死記符號。

```kotlin
class Solution {
    fun leastInterval(tasks: CharArray, n: Int): Int {
        val count = IntArray(26)
        for (task in tasks) count[task - 'A']++
        val maximum = count.maxOrNull() ?: 0
        val tied = count.count { it == maximum }
        val frame = (maximum - 1) * (n + 1) + tied
        return maxOf(tasks.size, frame)
    }
}
```

### 對著答案走讀

原創任務 `A,A,A,B,C`、n=2。A 最多共 3 次，先排骨架：

```text
A _ _ | A _ _ | A
A B C | A _ _ | A  → 7 格
```

前三段開頭的 A 至少隔 3 格，所以最後一個 A 不可能早於第 7 格。若 A、B 都同為最多次，末段還要容納最後的 B，因此公式最後加的是「並列最多種類數」。若其他任務多得填滿甚至超過這些空格，就不需要閒置，答案是任務總數。

為何下界能達成：把出現最多的種類按輪排列，每輪同一種類至多一件；其餘種類的次數不超過最高頻率，可分散填入輪間空位。空位不足時把輪拉長，增加的是實際工作而非閒置；空位有剩時才閒置。因此「必需的冷卻骨架」與「總工作量」取大值足夠。本題只問長度，不用真的建排程。

**為何選／不適用：** 所有任務耗時相同、冷卻相同、可自由重排，才有這個公式。不同任務各有不同冷卻、到達時間或優先依賴時不適用。**複雜度：** T 為任務數、字母固定 26 種，時間 O(T)，額外空間 O(1)。**相似題怎麼改：** 要實際排程，可用最大 Heap 選剩最多次的可用任務，冷卻佇列追蹤解禁時間。**提醒：** n 是中間需隔的格數，不是兩次開始的距離；距離是 n+1。

## 200. Number of Islands

**Medium** · [英文原題](https://leetcode.com/problems/number-of-islands/) · [官方中文](https://leetcode.cn/problems/number-of-islands/)

**中文題意：** grid 的字元 '1' 是陸地、'0' 是水，只有上下左右相連算同一座島，求島數。列欄各 1–300。

### 先記核心規則，再看完整答案

**看到還沒走過的陸地，答案加一，接著把整座島都標記。** 這裡直接把已走的 '1' 改成 '0'。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun numIslands(grid: Array<CharArray>): Int {
        val rows = grid.size
        val cols = grid[0].size
        val directions = intArrayOf(-1, 0, 1, 0, -1)
        val stack = ArrayDeque<Pair<Int, Int>>()
        var answer = 0
        for (r in 0 until rows) for (c in 0 until cols) {
            if (grid[r][c] != '1') continue
            answer++
            grid[r][c] = '0'
            stack.addLast(r to c)
            while (stack.isNotEmpty()) {
                val (row, col) = stack.removeLast()
                for (d in 0 until 4) {
                    val nr = row + directions[d]
                    val nc = col + directions[d + 1]
                    if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc] == '1') {
                        grid[nr][nc] = '0'
                        stack.addLast(nr to nc)
                    }
                }
            }
        }
        return answer
    }
}
```

### 對著答案走讀

原創地圖：

```text
1 0 1
1 0 1
0 1 0
```

掃到左上，數第一島，一路走左欄兩格都改 0。掃到右上，數第二島，右欄兩格改 0。最後底部中間自己一座，答案 3；斜角沒有連接。整座走完後才繼續外層掃描，因此同島不會加第二次。

**為何選／不適用：** 格子就是圖，四個鄰居可現算，不需另建所有邊。若要求保留地圖，改用 BooleanArray visited；若陸地會逐次新增，Union Find 更適合動態維護群數。**複雜度：** R、C 為列欄數，時間 O(RC)，額外堆疊最壞 O(RC)。**相似題怎麼改：** 數最大島面積，在一次走訪中另計格數。**提醒：** 入堆疊就標記，避免多個鄰居把同一格重複加入；全陸地時也不使用深遞迴。

## 133. Clone Graph

**Medium** · [英文原題](https://leetcode.com/problems/clone-graph/) · [官方中文](https://leetcode.cn/problems/clone-graph/)

**中文題意：** 給無向連通圖的一個起點，建立完全獨立的新圖，保留值和鄰居關係。可空圖；節點由 val 和 neighbors 構成，原圖可能有環。最多 100 個節點，值唯一且為 1–100；沒有自環或重複邊。

### 先記核心規則，再看完整答案

**每個舊物件只對應一個新物件，第一次遇到就先登記，再探索鄰居。** Map 同時是複製對照表與已發現紀錄。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun cloneGraph(node: Node?): Node? {
        if (node == null) return null
        val copies = HashMap<Node, Node>()
        val queue = ArrayDeque<Node>()
        copies[node] = Node(node.`val`)
        queue.addLast(node)
        while (queue.isNotEmpty()) {
            val original = queue.removeFirst()
            for (neighborOrNull in original.neighbors) {
                val neighbor = neighborOrNull ?: continue
                if (!copies.containsKey(neighbor)) {
                    copies[neighbor] = Node(neighbor.`val`)
                    queue.addLast(neighbor)
                }
                copies.getValue(original).neighbors.add(copies.getValue(neighbor))
            }
        }
        return copies.getValue(node)
    }
}
```

### 對著答案走讀

原創圖是三角形 `1—2—3—1`。開始先建立 1′並登記 `1→1′`；讀 1 的鄰居，建立 2′、3′，接上 `1′→2′,3′`。處理 2 遇到 1 時查到已有 1′，直接接回 1′，不再建立第二個 1，也不再排隊。最後新圖仍有環，但所有鄰居都是新節點。

如果每次看到鄰居都 new，一個舊節點會變很多副本；如果先深入探索後才登記，遇到環會永遠回到同一點。這兩個問題都由「先登記唯一副本」解決。nullable 鄰居的寫法相容某些 Kotlin 平台 stub，正式圖邊本身不包含 null。

**為何選／不適用：** 普通 `copy()` 或複製 List 只複製容器，裡面的 Node 還是舊物件，不是深拷貝。對有額外可變欄位的實務物件，還要逐項複製那些欄位。**複雜度：** V 節點、E 鄰接項目總數，平均時間 O(V+E)，輔助 O(V)，新圖 O(V+E)。**相似題怎麼改：** 帶 random 指標的串列也可用「舊→新」對照表。**提醒：** 測試不能只比值，還要確認新圖不引用原物件。

## 207. Course Schedule

**Medium** · [英文原題](https://leetcode.com/problems/course-schedule/) · [官方中文](https://leetcode.cn/problems/course-schedule/)

**中文題意：** 課程編號 0 到 numCourses−1，prerequisites 中 `[a,b]` 表示 a 前必須先修 b。判斷是否能修完全部課，包括沒有任何依賴的課。最多 2,000 門課、5,000 對不同依賴。

### 先記核心規則，再看完整答案

**先修課 → 後修課；還欠 0 門先修的課先排隊，修完就替下游扣 1。** 最後修完數量等於總課數才成功。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun canFinish(numCourses: Int, prerequisites: Array<IntArray>): Boolean {
        val nextCourses = Array(numCourses) { mutableListOf<Int>() }
        val needed = IntArray(numCourses)
        for (pair in prerequisites) {
            nextCourses[pair[1]].add(pair[0])
            needed[pair[0]]++
        }
        val queue = ArrayDeque<Int>()
        for (course in 0 until numCourses) if (needed[course] == 0) queue.addLast(course)
        var completed = 0
        while (queue.isNotEmpty()) {
            val course = queue.removeFirst()
            completed++
            for (next in nextCourses[course]) {
                needed[next]--
                if (needed[next] == 0) queue.addLast(next)
            }
        }
        return completed == numCourses
    }
}
```

### 對著答案走讀

原創 4 門課、`[[2,0],[2,1],[3,2]]`。needed=`[0,0,2,1]`；0、1 先排隊。修 0，2 還欠 1；修 1，2 欠 0 可排隊；修 2，3 才能排隊，最後修完 4 門，true。

「入度」就是一門課還欠幾個直接先修條件。每門只在欠數變 0 時排隊，因此不會提早修。有環時，環內每門至少欠環中另一門，誰都無法先開始；若已處理完所有可修課但還有剩下，就代表被環卡住。這種依賴順序叫拓撲排序，不是按課號大小排序。

**為何選／不適用：** 適合有向依賴與判斷有環；無向圖的環不能直接拿入度判斷。**複雜度：** V 課數、E 先修對數，時間空間 O(V+E)。**相似題怎麼改：** 210 把出隊順序保存即是課表。**提醒：** `[a,b]` 不是 a→b；方向寫反雖可能仍判出有環，卻會讓輸出的修課順序錯誤。

## 210. Course Schedule II

**Medium** · [英文原題](https://leetcode.com/problems/course-schedule-ii/) · [官方中文](https://leetcode.cn/problems/course-schedule-ii/)

**中文題意：** 先修規則與 207 相同，這次回傳一個完整合法修課順序；可能多解，任一合法順序皆可。做不到回傳空 IntArray。最多 2,000 門課，依賴對不重複且不包含課程對自己。

### 先記核心規則，再看完整答案

**把 207 每次「確定可以修」的出隊時刻，記成答案。** 不要記剛讀到邊的順序。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun findOrder(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
        val graph = Array(numCourses) { mutableListOf<Int>() }
        val needed = IntArray(numCourses)
        for (pair in prerequisites) {
            graph[pair[1]].add(pair[0])
            needed[pair[0]]++
        }
        val queue = ArrayDeque<Int>()
        for (course in 0 until numCourses) if (needed[course] == 0) queue.addLast(course)
        val answer = IntArray(numCourses)
        var size = 0
        while (queue.isNotEmpty()) {
            val course = queue.removeFirst()
            answer[size++] = course
            for (next in graph[course]) {
                needed[next]--
                if (needed[next] == 0) queue.addLast(next)
            }
        }
        return if (size == numCourses) answer else intArrayOf()
    }
}
```

### 對著答案走讀

原創 4 門、依賴 `[[1,0],[3,1],[2,0]]`。初始只 0 可修，記 0；它解鎖 1、2，按本輸入建邊順序排隊；取 1 後解鎖 3，但 2 已在隊伍前，輸出 `[0,1,2,3]`。`[0,2,1,3]` 也合法，因為 1、2 沒有互相先修限制。

每個輸出課程的 needed 是 0，所有先修都已出現，所以整串符合每條依賴。如果只輸出部分，代表有環阻住某些課，不能把部分課表當完整答案。

**為何選／不適用：** 一般佇列提供某一個合法順序，不保證所有合法順序中字典序最小。若要求最小課號優先，改 PriorityQueue，但時間會增加。**複雜度：** V 課數、E 依賴數，時間與總空間 O(V+E)。**相似題怎麼改：** 要算每課最早完成時間，在解鎖過程維護先修完成時間最大值。**提醒：** 測一般解答時應驗證每門出現一次且每條先修在前，不應只接受唯一排列。

## 684. Redundant Connection

**Medium** · [英文原題](https://leetcode.com/problems/redundant-connection/) · [官方中文](https://leetcode.cn/problems/redundant-connection/)

**中文題意：** 無向圖原本是一棵樹，額外多一條邊。刪一條使它回到樹；可刪多條時，選輸入中最晚出現那條。n 個節點編號 1–n，恰有 n 條邊，n 為 3–1,000，沒有重複邊。

### 先記核心規則，再看完整答案

**加入一條邊前，若兩端早已在同一群，這條就把路繞成環。** Union Find（並查集）只管理「誰和誰同群」，不保存實際路徑。

```kotlin
class Solution {
    fun findRedundantConnection(edges: Array<IntArray>): IntArray {
        val parent = IntArray(edges.size + 1) { it }
        val size = IntArray(edges.size + 1) { 1 }
        fun find(start: Int): Int {
            var x = start
            while (parent[x] != x) {
                parent[x] = parent[parent[x]]
                x = parent[x]
            }
            return x
        }
        for (edge in edges) {
            var a = find(edge[0])
            var b = find(edge[1])
            if (a == b) return edge
            if (size[a] < size[b]) { val temp = a; a = b; b = temp }
            parent[b] = a
            size[a] += size[b]
        }
        return intArrayOf()
    }
}
```

### 對著答案走讀

原創邊 `[[1,2],[2,4],[3,4],[1,3]]`。開始四個群各一人；前 3 條逐步把全體接成一群。最後 1、3 已同群，表示原本就有 `1→2→4→3` 的路，再接 1—3 就成環，答案 `[1,3]`。

parent 是每人的群組代表鏈，find 一路找到大代表。`parent[x]=parent[parent[x]]` 是順便縮短代表鏈，讓下次少走。把小群掛大群降低高度。題目保證只有一個多出的環，所以依輸入順序第一次接到「已同群」的邊，正好是那個環中最晚出現的邊；刪它符合要求。

**為何選／不適用：** 很適合逐條新增無向邊的連通性；有向先修環要用拓撲或有向 DFS。**複雜度：** n 為節點數，時間 O(n α(n))，α 是成長極慢的反 Ackermann 函數，實務可視為接近常數但不寫成嚴格 O(1)；空間 O(n)。**相似題怎麼改：** 每成功合併群數減一，可做省份群數。**提醒：** 必須合併代表 a、b，不是任意改原端點的 parent。

## 743. Network Delay Time

**Medium** · [英文原題](https://leetcode.com/problems/network-delay-time/) · [官方中文](https://leetcode.cn/problems/network-delay-time/)

**中文題意：** times 的 `[u,v,w]` 是 u 到 v 的單向傳輸，耗時 w。從 k 發訊號，求所有 n 個節點都收到的時間，任何點收不到則 -1。邊權非負，節點編號 1–n；n 至多 100、邊數至多 6,000、權重 0–100。

### 先記核心規則，再看完整答案

**先處理目前已知到達時間最小的節點，再嘗試更新鄰居。** 這是 Dijkstra；min Heap 取最早到達的待辦。

```kotlin
import java.util.PriorityQueue
class Solution {
    fun networkDelayTime(times: Array<IntArray>, n: Int, k: Int): Int {
        val graph = Array(n + 1) { mutableListOf<Pair<Int, Int>>() }
        for (edge in times) graph[edge[0]].add(edge[1] to edge[2])
        val distance = LongArray(n + 1) { Long.MAX_VALUE }
        val heap = PriorityQueue<Pair<Long, Int>>(compareBy<Pair<Long, Int>> { it.first })
        distance[k] = 0L
        heap.offer(0L to k)
        while (heap.isNotEmpty()) {
            val (time, node) = heap.poll()
            if (time != distance[node]) continue // 舊候選已被更短路取代
            for ((next, cost) in graph[node]) {
                val candidate = time + cost.toLong()
                if (candidate < distance[next]) {
                    distance[next] = candidate
                    heap.offer(candidate to next)
                }
            }
        }
        var answer = 0L
        for (node in 1..n) {
            if (distance[node] == Long.MAX_VALUE) return -1
            answer = maxOf(answer, distance[node])
        }
        return answer.toInt()
    }
}
```

### 對著答案走讀

原創 `1→2(8)、1→3(2)、3→2(1)`，起點 1。先得 distance[2]=8、distance[3]=2；Heap 先取 3，把到 2 改為 2+1=3。之後取 2 的 3，剩在 Heap 的舊 8 要跳過。所有點到達時間 `[0,3,2]`，最後一點在 3 收到，答案 3，不是三個時間相加。

非負權重保證：目前最小候選如果還能被未處理點繞路縮短，那個未處理點必須先以更小時間到達；但它理應先被 Heap 取出，矛盾。因此最早候選可放心用來往外更新。只在嚴格縮短時加入，0 權重環也不會無限重複。

**為何選／不適用：** 不同邊時間不能只靠普通 BFS；負權重則不適用此最早確定原理。官方輸入沒有同一有向點對的多重邊，本版鄰接清單仍可保存多條邊，逐條比較選較快者，不會覆寫漏邊。

**複雜度：** V=n、E=邊數，此懶刪除 Heap 版時間 O(V+E log(E+1))，空間 O(V+E)；距離用 Long 避免加法溢位，官方範圍下結果可轉 Int。**相似題怎麼改：** 只問到單一終點，可在其最小有效候選出隊時提早回傳。**提醒：** 入 Heap 不等於已確定，不能那時就永久標記 seen。

## 1584. Min Cost to Connect All Points

**Medium** · [英文原題](https://leetcode.com/problems/min-cost-to-connect-all-points/) · [官方中文](https://leetcode.cn/problems/min-cost-to-connect-all-points/)

**中文題意：** 平面上每對點都可拉線，費用是 `|x1−x2|+|y1−y2|`。求讓全部點連成一棵樹的最小總費用。這是曼哈頓距離，不是開根號直線距離。點數 1–1,000，座標在 ±1,000,000，點不重複。

### 先記核心規則，再看完整答案

**維護已接通的一群，每次選把群外一點接進來的最便宜線。** 這是 Prim 最小生成樹。不是求根到每點的最短路。

```kotlin
import kotlin.math.abs
class Solution {
    fun minCostConnectPoints(points: Array<IntArray>): Int {
        val n = points.size
        val connected = BooleanArray(n)
        val cheapest = IntArray(n) { Int.MAX_VALUE }
        cheapest[0] = 0
        var total = 0L
        repeat(n) {
            var chosen = -1
            for (i in 0 until n) {
                if (!connected[i] && (chosen == -1 || cheapest[i] < cheapest[chosen])) chosen = i
            }
            connected[chosen] = true
            total += cheapest[chosen].toLong()
            for (i in 0 until n) {
                if (!connected[i]) {
                    val cost = abs(points[chosen][0] - points[i][0]) +
                               abs(points[chosen][1] - points[i][1])
                    cheapest[i] = minOf(cheapest[i], cost)
                }
            }
        }
        return total.toInt()
    }
}
```

### 對著答案走讀

原創點 A=(0,0)、B=(2,0)、C=(2,3)。先接 A，其他點最便宜費用 B=2、C=5；選 B 花 2，C 可以改由 B 連，費用降到 3；最後接 C 花 3，總和 5。cheapest[C] 表示「接到已完成群體」最便宜，不是「從 A 走到 C」的累積距離。

為何每次最便宜邊安全：任何能連通全體的方案，都要跨過「已連群／未連群」這個分界。假設最佳方案沒用目前這條最便宜邊，加入它會形成環；拿掉環上另一條跨分界邊，其費用不會更便宜，所以總價不增加。逐次這樣選，仍能保留某個最佳方案。

**為何選／不適用：** 每對點都有邊，直接建 n² 條邊很占空間；此版現算距離、線性掃最小，O(n²) 很適合最多 1,000 點。若很稀疏且很大，可用鄰接表 + Heap Prim。**複雜度：** n 點，時間 O(n²)，額外空間 O(n)。官方座標範圍內單邊 Int 足夠，總和先用 Long；官方限制內答案可用 Int。**相似題怎麼改：** 已有道路清單，可按邊價排序配 Union Find 做 Kruskal。**提醒：** 更新的是單條連線 cost，不是 cheapest[chosen]+cost。

## 332. Reconstruct Itinerary

**Hard** · [英文原題](https://leetcode.com/problems/reconstruct-itinerary/) · [官方中文](https://leetcode.cn/problems/reconstruct-itinerary/)

**中文題意：** 從 JFK 出發，使用每張機票恰好一次，回傳完整機場路線；有多解取字典序最小。題目保證至少存在一條完整路線。相同起訖的不同機票仍是不同張，不能去重。機票 1–300 張，機場碼為 3 個大寫英文字母。

### 先記核心規則，再看完整答案

先補一個前置概念：這題要走完所有**邊（機票）**，不是每個機場只能去一次，稱為 Euler 路徑。**能走就取字典序最小的票繼續；走不動時才把機場排到答案前面。** 這個「走不動才定案」處理了先走進死路的情況。

```kotlin
import java.util.ArrayDeque
import java.util.PriorityQueue
class Solution {
    fun findItinerary(tickets: List<List<String>>): List<String> {
        val flights = HashMap<String, PriorityQueue<String>>()
        for (ticket in tickets) {
            flights.getOrPut(ticket[0]) { PriorityQueue<String>() }.offer(ticket[1])
        }
        val path = ArrayDeque<String>()
        val answer = ArrayDeque<String>()
        path.addLast("JFK")
        while (path.isNotEmpty()) {
            val airport = path.peekLast()
            val outgoing = flights[airport]
            if (outgoing != null && outgoing.isNotEmpty()) {
                path.addLast(outgoing.poll())
            } else {
                answer.addFirst(path.removeLast())
            }
        }
        return answer.toList()
    }
}
```

### 對著答案走讀

原創機票 `JFK→AAA、JFK→BBB、BBB→JFK`。直覺先取 AAA，卻沒票離開，不能當作已完成完整路線。演算法如何補救：

| 動作 | 探索堆疊 path | 目前定案 answer |
|---|---|---|
| 起點 | JFK | 空 |
| 用最小票到 AAA | JFK,AAA | 空 |
| AAA 沒票，倒放 | JFK | AAA |
| JFK 還有票到 BBB | JFK,BBB | AAA |
| BBB 回 JFK | JFK,BBB,JFK | AAA |
| 依序退回定案 | 空 | JFK,BBB,JFK,AAA |

AAA 只能當最後一站，所以先退回時放在答案尾段；JFK 其餘可走的迴圈被插到它前面。這是 Hierholzer 的拼接方式：堆疊保留尚未完成的路，沒有剩餘出邊才確定該站在剩餘路線中的尾部位置。每張票 poll 一次，重複機票在 Heap 裡有多個相同字串，仍會各用一次。

字典序為何成立：可選出邊按小到大探索，在仍能回來接續的部分，小的選擇會排在前面；若小邊通往無法回來的尾段，它會先倒放並留到尾部，因為把它放前面根本無法用完其他票。倒序拼接保留最小**可完成整條行程**的前綴，不是天真地把探索順序直接當答案。

**為何選／不適用：** 適用「每條邊用一次且保證存在 Euler 路徑」；不是最短飛行距離，也不是每個點只拜訪一次。任意無效票表不能只拿此輸出長度就宣稱路線有效，要另驗證相鄰票的使用次數與起點。

**複雜度：** E 票數，機場碼固定長度時平均建圖與處理時間 O(E log(E+1))，空間 O(E)。**相似題怎麼改：** 不要求字典序時，出邊可用一般 List 從尾拿；仍必須保留每張票。**提醒：** 不可用 Set 存機票；不可用 visited[機場] 阻止再次到訪；答案前插用 ArrayDeque，避免 MutableList.add(0) 每次搬動。

## 熟練步驟：先讀答案，再自己重寫

1. 先讀該題的粗體核心規則，直接看完整答案，不要求你先自行苦想。
2. 打開原創例子，對照每一行，手寫指標、佇列、Map 或 Heap 當下內容。先能說清楚「這行改了什麼」。
3. 說出容器保存的意思，例如「needed 是還欠幾門」或「gain 只能交一條路給父親」，不要只背變數名。
4. 暫時蓋住答案，在同一個小例子上重寫；卡住就回看對應幾行，再完成它。
5. 跑原創案例與空輸入、單節點、重複值、環或負數等該題適用邊界，解釋為何得到這個結果。
6. 隔 1 天與 1 週再重寫，最後才練「相似題怎麼改」。目標是看懂規則能改用，不是第一次就憑空發明演算法。


---

<a id="advanced"></a>

# 補充第三冊：回溯、動態規劃、貪心與矩陣位元

這是給已有 Android 開發經驗、正在建立資料結構與演算法觀念的答案優先教材，共24題。每題先給一份可提交的 Kotlin 答案和要記的規則，再解釋它怎麼運作。各題是獨立提交；不要把多個 `class Solution` 貼進同一個原始碼檔案。

**練習方式：** 先讀答案與核心規則 → 用例子逐行追蹤 → 對照解說說明每個變數 → 蓋住程式重寫 → 第二天再寫一次。卡住時可以直接看對應段落，不要求在不知道工具的情況下苦想很久。目標是能說出「為什麼用這個工具、哪個條件一改就不能照抄」。

本冊中文題意為自行改寫的短摘要；完整題目請用每題英文原題與官方中文連結閱讀。24題英文官方頁均於2026-09-21查閱；中文連結供閱讀，並非宣稱已逐字翻譯官方全文。解說與例子為本教材原創。題目190官方目前的signed/偶數版本與舊版unsigned表示差異，另在該題說明。

先記住三種不同的DP格子：`min`格子是「最少花多少」；`Boolean`格子是「能不能」；`+=`格子是「有幾種」。三者的起點不一定相同，迴圈方向也由能否重複使用同一元素決定。

## 78. Subsets

**Medium** · [英文原題](https://leetcode.com/problems/subsets/) · [官方中文](https://leetcode.cn/problems/subsets/)

**中文題意（自行摘要）：** 給一組互不重複的整數，列出所有子集合，包含空集合。順序不重要，每個元素最多選一次。長度 1–10，值為 -10 到 10。

### 先看答案：要記住的核心規則

每個位置只有「不選、選」兩條路；走到最後才存答案。存入結果時必須複製 `path.toList()`。

```kotlin
class Solution {
    fun subsets(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        val path = mutableListOf<Int>()
        fun dfs(index: Int) {
            if (index == nums.size) {
                result.add(path.toList())
                return
            }
            dfs(index + 1) // 不選目前元素
            path.add(nums[index])
            dfs(index + 1) // 選目前元素
            path.removeAt(path.lastIndex) // 回來就撤銷
        }
        dfs(0)
        return result
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選回溯：** 題目真的要全部答案，不能只保留最好的那一個。`index` 表示正在決定哪個元素，`path` 是目前選到的清單。回溯不是神祕技巧，就是「選擇 → 往下走 → 撤銷」。

**為何正確：** 每個子集合都能唯一表示成一串選／不選決定，這棵分岔樹把所有決定走過一次，因此不漏也不重複。每層返回時撤銷自己的更動，兄弟分支不會污染彼此。

**不適用處：** 如果輸入允許重複數字，兩條不同決策可能得到同一組值，必須另加去重；如果只問集合數量，可直接算 2ⁿ，不用列舉。

### 原創例子：把變數走一遍

輸入 `[2,4]`：

```text
不選2 → 不選4 → []
不選2 → 選4   → [4]
選2   → 不選4 → [2]
選2   → 選4   → [2,4]
```

先看到這四個答案，再蓋住程式重寫三行「選、遞迴、撤銷」，不必先逼自己憑空發明回溯。

**複雜度：** n 是元素數。時間 O(n·2ⁿ)，包含複製答案；工作空間 O(n)，輸出空間 O(n·2ⁿ)。

**相似題差異與易錯處：** 46 的排列在乎順序，所以 `[2,4]` 和 `[4,2]` 是兩個答案；本題它們是同一個集合。直接把可變 path 加入 result，會使已存答案一起被後续撤銷改掉。


## 46. Permutations

**Medium** · [英文原題](https://leetcode.com/problems/permutations/) · [官方中文](https://leetcode.cn/problems/permutations/)

**中文題意（自行摘要）：** 給互不相同的整數，列出所有排列；每個答案必須用完所有數字，順序不同算不同。長度 1–6，值 -10 到 10。

### 先看答案：要記住的核心規則

排列要在每一層試「所有尚未使用」的位置。用 `used[i]` 記錄目前路徑用過誰，回來時設回 false。

```kotlin
class Solution {
    fun permute(nums: IntArray): List<List<Int>> {
        val result = mutableListOf<List<Int>>()
        val path = mutableListOf<Int>()
        val used = BooleanArray(nums.size)
        fun dfs() {
            if (path.size == nums.size) {
                result.add(path.toList())
                return
            }
            for (i in nums.indices) {
                if (used[i]) continue
                used[i] = true
                path.add(nums[i])
                dfs()
                path.removeAt(path.lastIndex)
                used[i] = false
            }
        }
        dfs()
        return result
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選回溯＋BooleanArray：** 每次在下一個空位安排一個人，就需要知道哪些人已入座。used 是這一張排列的座位表，不是整個搜尋永遠不能再用的黑名單。

**為何正確：** 第 1 格試全部 n 個值，第 2 格試其餘 n−1 個，以此類推。每個完整排列都有唯一選擇路徑；used 保證不重複用同一元素。

**不適用處：** 若輸入包含重複值，光靠位置 used 不足以避免相同排列；如果 n 很大且要求全部排列，輸出本身的 n! 成長就不可避免。

### 原創例子：把變數走一遍

輸入 `[2,5,8]`：

```text
先放2 → 放5 → [2,5,8]；退回後放8 → [2,8,5]
先放5 → [5,2,8]、[5,8,2]
先放8 → [8,2,5]、[8,5,2]
```

從 `[2,5,8]` 回到 `[2]` 時，5 和 8 都要解除 used，才能試另一種順序。

**複雜度：** n 是元素數。時間 O(n·n!)，工作空間 O(n)，輸出 O(n·n!)。

**相似題差異與易錯處：** 78 的下一層只需處理下一個位置；排列每層都要從所有位置挑人。若下一層只從 i+1 開始，就只能列出依原索引遞增的組合，會漏排列。


## 39. Combination Sum

**Medium** · [英文原題](https://leetcode.com/problems/combination-sum/) · [官方中文](https://leetcode.cn/problems/combination-sum/)

**中文題意（自行摘要）：** 從不同的正整數 candidates 中選數字湊成 target，每種可無限次使用。回傳不重複組合，順序不算差異。候選數 1–30，值 2–40，target 為 1–40。

### 先看答案：要記住的核心規則

選了索引 i，下一層仍從 i 開始，表示可重複；不能回頭到更小索引，表示不同順序不重算。

```kotlin
class Solution {
    fun combinationSum(candidates: IntArray, target: Int): List<List<Int>> {
        val sorted = candidates.sortedArray()
        val result = mutableListOf<List<Int>>()
        val path = mutableListOf<Int>()
        fun dfs(start: Int, remaining: Int) {
            if (remaining == 0) {
                result.add(path.toList())
                return
            }
            for (i in start until sorted.size) {
                val value = sorted[i]
                if (value > remaining) break
                path.add(value)
                dfs(i, remaining - value)
                path.removeAt(path.lastIndex)
            }
        }
        dfs(0, target)
        return result
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選回溯：** 需要輸出每一組組成內容，而不只要數量。排序讓數字超過剩餘額度時可以停止，因為後面只會更大。

**為何正確：** 任一合法組合排序後，都對應一條不下降索引的路徑；我們恰好走過這些路徑。remaining 每次減少正整數，所以一定會終止。

**不適用處：** 允許 0 且可重複時，remaining 不減會無限遞迴；有負數時也不能用「超過剩餘就停止」。只問組合個數時，518 的 DP 更適合。

### 原創例子：把變數走一遍

輸入 `candidates=[3,4],target=10`：

```text
選3 → 剩7 → 選3 → 剩4 → 選4 → 剩0，得到[3,3,4]
以4開頭 → 剩6 → 再選4剩2 → 無法完成
```

不會另外輸出 `[3,4,3]`，因為選4之後不能回頭選3。

**複雜度：** 令 n 為候選數、D=target/最小候選值的整數商。搜尋深度最多 D；保守時間上界 O(n log n＋(n+1)^(D+1)＋K·D)，K 是輸出組合數；實際排序剪枝會小很多。工作空間 O(n+D)，輸出 O(KD)。

**相似題差異與易錯處：** 216 每個數字最多一次且要剛好 k 個，遞迴用 i+1；本題無限次用 i。322 問最少幾枚，518 問幾種，39 問每一種內容，不能把三種回傳值混在一起。


## 79. Word Search

**Medium** · [英文原題](https://leetcode.com/problems/word-search/) · [官方中文](https://leetcode.cn/problems/word-search/)

**中文題意（自行摘要）：** 在字母格子中找一條路拼出 word，每步只能上下左右走，同一格在一條路中不能重用。回傳是否存在。行、列各 1–6，word 長度 1–15，字母有大小寫。

### 先看答案：要記住的核心規則

走進格子時標記 used，離開時取消；used 屬於「目前這條路」，不是整張圖永久拜訪一次。

```kotlin
class Solution {
    fun exist(board: Array<CharArray>, word: String): Boolean {
        val rows = board.size
        val cols = board[0].size
        val used = Array(rows) { BooleanArray(cols) }
        fun dfs(r: Int, c: Int, index: Int): Boolean {
            if (r !in 0 until rows || c !in 0 until cols) return false
            if (used[r][c] || board[r][c] != word[index]) return false
            if (index == word.lastIndex) return true
            used[r][c] = true
            val found = dfs(r + 1, c, index + 1) ||
                dfs(r - 1, c, index + 1) ||
                dfs(r, c + 1, index + 1) ||
                dfs(r, c - 1, index + 1)
            used[r][c] = false
            return found
        }
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (dfs(r, c, 0)) return true
            }
        }
        return false
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選回溯：** 同一格可以出現在不同候選路徑，卻不能在同一條路重複。一般圖形搜尋永久 visited 會錯誤剪掉其他合法路徑。程式使用額外 used 陣列，避免暫時改字母讓初學者難追蹤。

**為何正確：** 從每格當起點，下一步列舉四個合法方向；逐字比對避免走錯字，used 避免重用。所有合法單字路線都在這些嘗試中。即使 found=true，也先恢復 used 再返回。

**不適用處：** 如果要一次找成千上萬個不同單字，逐字重搜會重工，可考慮 Trie＋DFS 的 Word Search II；對角線移動不是本題規則。

### 原創例子：把變數走一遍

格子 `[[A,B],[D,C]]`、word=`"ABCD"`：

```text
A → B
    ↓
D ← C
```

得到 true。若 word=`"ABA"`，從 A 到 B 後不能回原本 A，那個格子仍在 used 中，答案 false。

**複雜度：** R、C 是行列數，L 是單字長度。保守時間 O(RC·4^L)；排除走回上一格後可更緊估為 O(RC·3^L)。空間 O(RC+L)。

**相似題差異與易錯處：** 一般島嶼搜尋拜訪過可永久標記；本題必須撤銷。短路 `||` 找到一條就不再試其他方向，但撤銷仍必須執行。


## 211. Design Add and Search Words Data Structure

**Medium** · [英文原題](https://leetcode.com/problems/design-add-and-search-words-data-structure/) · [官方中文](https://leetcode.cn/problems/design-add-and-search-words-data-structure/)

**中文題意（自行摘要）：** 實作 WordDictionary，addWord 加入小寫單字，search 查完整單字；查詢中的點號 . 可以匹配任意一個字母。長度 1–25，每次查詢最多 2 個點，總操作最多 10,000。

### 先看答案：要記住的核心規則

一般字母走 Trie 的一條路；點號試所有存在的子節點。字串走完仍要檢查 end，不能只因路徑存在就成功。

```kotlin
class WordDictionary() {
    private class Node {
        val children = arrayOfNulls<Node>(26)
        var end = false
    }
    private val root = Node()

    fun addWord(word: String) {
        var node = root
        for (ch in word) {
            val index = ch - 'a'
            if (node.children[index] == null) node.children[index] = Node()
            node = node.children[index]!!
        }
        node.end = true
    }

    fun search(word: String): Boolean {
        fun dfs(node: Node, index: Int): Boolean {
            if (index == word.length) return node.end
            val ch = word[index]
            if (ch != '.') {
                val next = node.children[ch - 'a'] ?: return false
                return dfs(next, index + 1)
            }
            for (next in node.children) {
                if (next != null && dfs(next, index + 1)) return true
            }
            return false
        }
        return dfs(root, 0)
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選 Trie＋分支搜尋：** Trie 把共同字首共用一段路。遇到點號不知道該走哪個字母，只好把所有候選路試過；其他明確字母則只走一條。

**為何正確：** 每一個點恰好消耗一個節點、每個普通字母必須相等。到字串末尾時只有 end=true 的完整單字才合格。這正好對應查詢規則。

**不適用處：** 點號不是正規表示式的 `.*`，不能吃零個或很多字元；若只查完整單字且沒有萬用字元，HashSet 可以更簡單。

### 原創例子：把變數走一遍

加入 `"sun"`、`"sea"`：

```text
root → s ┬→ u → n(end)
         └→ e → a(end)
查s.n：s → .試u/e → n → sun成功
查s. ：只走兩個字元，沒有end → false
```

**複雜度：** L 是字串長度、d 是點號數。新增 O(L)，查詢保守 O(L·26^d)，也不會超過搜尋到的既有節點總量；查詢遞迴 O(L)。儲存空間 O(S)，S 是加入單字總字元數，字母表26視為常數。

**相似題差異與易錯處：** 208 的 search 每層只有一條路；本題多了 .，因此查詢不能一律說是 O(L)。重複 addWord 不需要新增重複字串，end 留 true 即可。


## 70. Climbing Stairs

**Easy** · [英文原題](https://leetcode.com/problems/climbing-stairs/) · [官方中文](https://leetcode.cn/problems/climbing-stairs/)

**中文題意（自行摘要）：** 一次可走 1 或 2 階，從平地到第 n 階共有多少種走法？順序不同算不同。n 為 1–45。

### 先看答案：要記住的核心規則

到第 i 階，只可能最後走一階或兩階：`dp[i]=dp[i-1]+dp[i-2]`。從最小問題往上填表。

```kotlin
class Solution {
    fun climbStairs(n: Int): Int {
        val dp = IntArray(n + 1)
        dp[0] = 1
        dp[1] = 1
        for (i in 2..n) dp[i] = dp[i - 1] + dp[i - 2]
        return dp[n]
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選 DP：** 很多路線都會先到同一階，無須重新列出之前怎麼走，只要記住「到這階有幾種」。

**狀態：** dp[i] 是到第 i 階的方法數。**起點：** dp[0]=1 代表不走也是到達平地的一種方式，dp[1]=1。**轉移：** 最後一步若走 1 階就來自 i−1，若走 2 階就來自 i−2，兩群相加。**迴圈順序：** i 遞增，先算前兩格。**答案：** dp[n]。

**為何正確：** 按最後一步分群，兩群互不重疊，且涵蓋所有走法。**不適用處：** 有壞階梯、費用或可走三階時，轉移要跟著改。

### 原創例子：把變數走一遍

n=4：

```text
i   0 1 2 3 4
dp  1 1 2 3 5
到4：從3來有3種，從2來有2種 → 共5種
```

五種是 1111、112、121、211、22。這裡的 112 和 211 是不同順序，所以不同方法。

**複雜度：** 時間 O(n)，空間 O(n)；理解表格後只留前兩格可變 O(1)。n<=45，所有前綴值都不超過最後值，Int 足夠。

**相似題差異與易錯處：** 746 問最小花費，用 min；本題問方法數，用加法。518 硬幣組合不計順序，本題爬階梯計順序，不能直接照搬硬幣外迴圈。


## 322. Coin Change

**Medium** · [英文原題](https://leetcode.com/problems/coin-change/) · [官方中文](https://leetcode.cn/problems/coin-change/)

**中文題意（自行摘要）：** 用給定面額的硬幣湊 amount，各面額無限枚，回傳最少幾枚，湊不出回傳 -1。面額數 1–12、面額為正 Int，amount 為 0–10000。

### 先看答案：要記住的核心規則

`dp[x]` 記最少枚數，初始「湊不到」不是 0；每次拿一枚 coin，候選值是 `dp[x-coin]+1`。

```kotlin
class Solution {
    fun coinChange(coins: IntArray, amount: Int): Int {
        val unreachable = amount + 1
        val dp = IntArray(amount + 1) { unreachable }
        dp[0] = 0
        for (value in 1..amount) {
            for (coin in coins) {
                if (coin <= value) {
                    dp[value] = minOf(dp[value], dp[value - coin] + 1)
                }
            }
        }
        return if (dp[amount] == unreachable) -1 else dp[amount]
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選 DP：** 貪心一直拿最大面額對任意面額不可靠。例如面額 1、3、4 湊 6，4+1+1 要三枚，3+3 只要兩枚。

**狀態：** dp[x] 是湊 x 的最少枚數。**起點：** dp[0]=0；其他設 amount+1，因為正整數硬幣若可湊出，最多只要 amount 枚。**轉移：** 試最後一枚 coin，取 min(dp[x],dp[x−coin]+1)。**順序：** 金額由小到大，所有較小金額先完成，因此可重複使用面額。**答案：** dp[amount]，仍是哨兵則 -1。

**為何正確：** 最佳解必有最後一枚，拿走後的剩餘金額也必須用最少枚數，否則可再改善。列舉所有最後一枚就不會漏最佳解。**不適用處：** 硬幣各只有一枚時需要 0/1 模式；問幾種組合則應改為518。

### 原創例子：把變數走一遍

coins=`[1,3,4]`、amount=6：

```text
金額 0 1 2 3 4 5 6
最少 0 1 2 1 1 2 2
算6：拿1→dp5+1=3；拿3→dp3+1=2；拿4→dp2+1=3
```

**複雜度：** A 是 amount、C 是面額數，時間 O(AC)，空間 O(A)。哨兵最多10001，+1安全；沒有用 Int.MAX_VALUE+1。

**相似題差異與易錯處：** 518 是 `+=` 計數，本題是 min 最小化。dp[0] 在本題是0枚，但518是1種空組合。不要硬記「DP初始都設0」。


## 300. Longest Increasing Subsequence

**Medium** · [英文原題](https://leetcode.com/problems/longest-increasing-subsequence/) · [官方中文](https://leetcode.cn/problems/longest-increasing-subsequence/)

**中文題意（自行摘要）：** 回傳最長嚴格遞增子序列長度，可以跳過元素但不能改順序。長度 1–2500，數值 -10000 到 10000。

### 先看答案：要記住的核心規則

`tails[k]` 是目前「長度 k+1 的遞增子序列，最小可能結尾」。用二分找第一個 >=x 的位置替換；找不到才延長。

```kotlin
class Solution {
    fun lengthOfLIS(nums: IntArray): Int {
        val tails = IntArray(nums.size)
        var size = 0
        for (value in nums) {
            var left = 0
            var right = size
            while (left < right) {
                val mid = left + (right - left) / 2
                if (tails[mid] < value) left = mid + 1
                else right = mid
            }
            tails[left] = value
            if (left == size) size++
        }
        return size
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選最小結尾＋二分：** 同樣長度的序列，結尾越小，後面能接的數字只會更多。因此只保存每種長度最有利的結尾，避免比較所有前面元素。

**狀態：** tails[k] 的定義如上；size 是目前最長長度。**起點：** size=0，沒有有效格子。**轉移：** 找第一個 >=x 的位置 k，用 x 改善該長度的結尾；若 k=size 就增長。**迴圈順序：** 按輸入順序，維持子序列不能重排。**答案：** size，不是 tails 內容。

**為何正確：** k 前面的結尾都小於 x，可以把 x 接上長度 k 的序列；更長的舊序列結尾不小於 x，無法以 x 延長。替換為更小或相等結尾不損失將來機會。**不適用處：** 想輸出真正的子序列，需要額外保存前驅索引；tails 本身不一定來自同一條路徑。

### 原創例子：把變數走一遍

輸入 `[4,2,3,1,5]`：

```text
4 → tails=[4]
2 → tails=[2]     長度沒變，但結尾更好
3 → tails=[2,3]
1 → tails=[1,3]   不代表輸入中的1在3前面！
5 → tails=[1,3,5] size=3，真正可用序列是[2,3,5]
```

**複雜度：** n 是長度，時間 O(n log n)，空間 O(n)。

**相似題差異與易錯處：** 1143 是兩字串共同子序列；本題只處理一個數列。相同數不能延長「嚴格」遞增，所以找第一個 >=，不是 >。若仍想先學傳統DP，dp[i]=以i結尾的最佳長度，枚舉j<i且nums[j]<nums[i]，可得O(n²)版本。


## 309. Best Time to Buy and Sell Stock with Cooldown

**Medium** · [英文原題](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/) · [官方中文](https://leetcode.cn/problems/best-time-to-buy-and-sell-stock-with-cooldown/)

**中文題意（自行摘要）：** 可多次買賣、同時最多持有一股；賣出後隔天不能買，需冷卻一天。求最大獲利。天數 1–5000，每天價格 0–1000。

### 先看答案：要記住的核心規則

把空手分成「今天剛賣出 sold」與「可以休息／買入的 rest」。今天買入只能由昨天 rest 轉來，不能由昨天 sold 轉來。

```kotlin
class Solution {
    fun maxProfit(prices: IntArray): Int {
        var hold = -prices[0]
        var sold = Int.MIN_VALUE / 2
        var rest = 0
        for (i in 1 until prices.size) {
            val oldHold = hold
            val oldSold = sold
            val oldRest = rest
            hold = maxOf(oldHold, oldRest - prices[i])
            sold = oldHold + prices[i]
            rest = maxOf(oldRest, oldSold)
        }
        return maxOf(rest, sold)
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選狀態 DP：** 是否能買入，不只取決於有沒有股票，還取決於昨天是否賣出。把這種歷史濃縮成三個狀態即可。

**狀態：** hold=今天結束持股的最高淨收入；sold=今天剛賣出的最高淨收入；rest=今天結束空手且今天沒有賣出的最高淨收入。**起點（第0天）：** hold=-price[0]、rest=0、sold=不可能。**轉移：** hold=max(舊hold,舊rest−price)；sold=舊hold+price；rest=max(舊rest,舊sold)。**順序：** 日期遞增，先保留三個昨天值，再一起更新。**答案：** max(rest,sold)，庫存要賣掉才算完成。

**為何正確：** 昨天 sold 今天只能進 rest，最快明天才可從 rest 買入，恰好隔一天。其他合法「不動、買、賣」全都包含。**不適用處：** 714 有手續費沒有冷卻，不能直接套同一限制；冷卻多天也要額外狀態。

### 原創例子：把變數走一遍

prices=`[2,5,1,4]`：

```text
價格   hold  sold  rest
2      -2    不可能 0
5      -2     3    0
1      -1    -1    3
4      -1     3    3
```

答案3：2買5賣之後，1那天不能立刻買。hold=-1代表另一條「之前沒交易、今天才買1」的路，不是把違法交易接上去。

**複雜度：** n 是天數，時間 O(n)，空間 O(1)。收益上界5000×1000，Int足夠；不可能狀態用MIN/2保留運算餘裕。

**相似題差異與易錯處：** 不能先更新 rest 再拿新 rest 更新 hold，否則可能把昨天 sold 在今天直接變成買入。三個狀態是不同候選歷史的最佳值，不代表同一個人同時做三件事。


## 5. Longest Palindromic Substring

**Medium** · [英文原題](https://leetcode.com/problems/longest-palindromic-substring/) · [官方中文](https://leetcode.cn/problems/longest-palindromic-substring/)

**中文題意（自行摘要）：** 找字串中最長的連續回文片段，回文是左右反著讀一樣。長度 1–1000，只含英文字母及數字。若最長答案不唯一，任一個即可。

### 先看答案：要記住的核心規則

每個位置試兩種中心：一個字元的奇數中心 `(i,i)`，兩個字元中間的偶數中心 `(i,i+1)`；相同就向外擴張。

```kotlin
class Solution {
    fun longestPalindrome(s: String): String {
        var start = 0
        var bestLength = 1
        fun expand(leftStart: Int, rightStart: Int) {
            var left = leftStart
            var right = rightStart
            while (left >= 0 && right < s.length && s[left] == s[right]) {
                val length = right - left + 1
                if (length > bestLength) {
                    start = left
                    bestLength = length
                }
                left--
                right++
            }
        }
        for (i in s.indices) {
            expand(i, i)
            expand(i, i + 1)
        }
        return s.substring(start, start + bestLength)
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選中心擴張：** 回文內層已成立時，只需看新加的左右兩字是否相同，不必每次重查整段。比列出全部片段再檢查少一層迴圈，而且不用二維表。

**為何正確：** 每個回文都有奇數或偶數中心，所有中心都試過，就會找到最長那一段。遇到不相同的兩字，該中心更大的範圍也不可能是回文，可立即停止。

**不適用處：** 子序列允許跳字，本題是 substring 必須連續；要在超長字串達到線性時間可再學 Manacher，但此題限制可用中心擴張。

### 原創例子：把變數走一遍

輸入 `"xabbay"`：

```text
索引 0 1 2 3 4 5
字元 x a b b a y
中心(2,3)：bb → abba → xabbay不是回文，停止
輸出abba
```

只試 `(i,i)` 會漏掉這個偶數長度答案。

**複雜度：** n 是字串長度。時間 O(n²)，工作空間 O(1)，回傳子字串最多佔 O(n)。

**相似題差異與易錯處：** 125 判斷整段是否回文，本題要找最長片段；1143 的子序列可以跳字，本題不行。多個答案同長時程式保留先遇到的，不影響正確性。


## 416. Partition Equal Subset Sum

**Medium** · [英文原題](https://leetcode.com/problems/partition-equal-subset-sum/) · [官方中文](https://leetcode.cn/problems/partition-equal-subset-sum/)

**中文題意（自行摘要）：** 能否把所有數字分成兩組，兩組總和相同？每個元素只能放到其中一組。長度 1–200，數值 1–100，回傳 Boolean。

### 先看答案：要記住的核心規則

總和是奇數直接 false；否則只需找是否能湊到總和一半。每個數字只用一次，容量必須倒著更新。

```kotlin
class Solution {
    fun canPartition(nums: IntArray): Boolean {
        val total = nums.sum()
        if (total % 2 != 0) return false
        val target = total / 2
        val possible = BooleanArray(target + 1)
        possible[0] = true
        for (value in nums) {
            for (sum in target downTo value) {
                possible[sum] = possible[sum] || possible[sum - value]
            }
        }
        return possible[target]
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選 0/1 背包 DP：** 每個元素是「選進第一組」或「不選進第一組」，不選的自然在第二組。只記能否湊出某個和，無須列出所有集合。

**狀態：** 處理完目前元素後，possible[x] 表示能否用已處理元素的一部分湊 x。**起點：** possible[0]=true，其他false。**轉移：** 舊possible[x]（不選）OR 舊possible[x−value]（選）。**順序：** 元素外層，sum由target降到value，確保x−value還是上一輪的值。**答案：** possible[target]。

**為何正確：** 每個元素只有選／不選兩種，轉移完整涵蓋。倒序避免同一元素在一輪被重複使用。**不適用處：** 含負數時不能直接用0..target索引；無限次選同一面額則要正序，見518。

### 原創例子：把變數走一遍

輸入 `[2,2,3,5]`，總和12，target=6：

```text
起始可湊 {0}
處理第一個2 → {0,2}
處理第二個2 → {0,2,4}
處理3       → {0,2,3,4,5}
處理5       → {0,2,3,4,5}
沒有6 → false
```

如果處理第一個2就正序更新，會從2立刻造出4、再造出6，等於偷用同一個2三次。

**複雜度：** n 是元素數、T=total/2。時間 O(nT)，空間 O(T)，total最多20000，Int安全。

**相似題差異與易錯處：** 494 是計數，要相加；本題是存在性，用OR。416官方不含0；若延伸允許0，OR同一個值不改變可達性。494的0則有+0/-0兩種不同指派，會使計數加倍。


## 518. Coin Change II

**Medium** · [英文原題](https://leetcode.com/problems/coin-change-ii/) · [官方中文](https://leetcode.cn/problems/coin-change-ii/)

**中文題意（自行摘要）：** 用無限枚不同面額硬幣湊 amount，回傳有幾種組合；拿的順序不算不同。面額數最多300，面額1–5000，amount=0–5000。最終答案保證放得進有號32位元。

### 先看答案：要記住的核心規則

硬幣外層、金額正序，`ways[x] += ways[x-coin]`。用 BigInteger 保護可能很大的中間計數，最後依題目保證轉回 Int。

```kotlin
import java.math.BigInteger

class Solution {
    fun change(amount: Int, coins: IntArray): Int {
        val ways = Array(amount + 1) { BigInteger.ZERO }
        ways[0] = BigInteger.ONE
        for (coin in coins) {
            for (value in coin..amount) {
                ways[value] = ways[value].add(ways[value - coin])
            }
        }
        return ways[amount].intValueExact()
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選組合計數 DP：** 不需輸出每一組，只需數量。每輪把一種新硬幣納入，避免把不同拿取順序重算。

**狀態：** 處理某些面額後，ways[x] 是只用這些面額湊 x 的組合數。**起點：** ways[0]=1，代表空組合；其他0。**轉移：** 原ways[x]是不拿目前面額，ways[x−coin]再加一枚目前硬幣是至少拿一枚的情況，兩者相加。**順序：** 硬幣外層，金額由coin往amount增加；因此本輪算過的ways[x−coin]可以再用目前硬幣，實現無限枚。**答案：** ways[amount]。

**為何正確：** 按「是否使用目前最後一種面額」分群，不重不漏。**不適用處：** 若不同順序算不同，金額外層才是另一種計數；若每枚最多一次，金額需反序。

**數值安全：** 最終答案小，不表示較小金額的組合數都小。例如大量偶數面額對奇數目標完全湊不到，偶數中間格卻可能巨大；Long也不保證足夠。BigInteger是JVM標準大整數，不會因固定64位元溢位。[Java BigInteger 官方說明](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/math/BigInteger.html)

### 原創例子：把變數走一遍

coins=`[2,3]`、amount=6：

```text
金額       0 1 2 3 4 5 6
還沒面額   1 0 0 0 0 0 0
處理2      1 0 1 0 1 0 1
處理3      1 0 1 1 1 1 2
兩組：[2,2,2]、[3,3]
```

**複雜度：** C是面額數、A是目標、B是中間計數最大位元長度。O(CA)次大整數加法，按位元成本約O(CAB)時間、O(AB)空間。這裡不把BigInteger加法假裝成永遠O(1)。

**相似題差異與易錯處：** 322問最少枚數，這個例子答案是2枚；518問組合數，碰巧也是2，但意義完全不同。amount=0時322回0枚，518回1種空組合。


## 494. Target Sum

**Medium** · [英文原題](https://leetcode.com/problems/target-sum/) · [官方中文](https://leetcode.cn/problems/target-sum/)

**中文題意（自行摘要）：** 為 nums 中每個位置選一個+或-，全部加總後等於target，問有幾種符號指派。長度1–20，元素非負，總和最多1000，target在-1000到1000。

### 先看答案：要記住的核心規則

正號組的總和 P 滿足 `2P=total+target`。轉成「每個位置最多選一次」的組合計數；容量倒序。零元素也必須處理，不能跳過。

```kotlin
class Solution {
    fun findTargetSumWays(nums: IntArray, target: Int): Int {
        val total = nums.sum()
        if (target < -total || target > total) return 0
        val combined = total + target
        if (combined % 2 != 0) return 0
        val positive = combined / 2
        val ways = IntArray(positive + 1)
        ways[0] = 1
        for (value in nums) {
            for (sum in positive downTo value) {
                ways[sum] += ways[sum - value]
            }
        }
        return ways[positive]
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選0/1計數背包：** 正號組總和P、負號組總和N，有P+N=total且P−N=target，相加得到2P=total+target。於是只需要數哪些位置被放進正號組。

**狀態：** ways[x]是已處理位置中，選一部分使總和x的選法數。**起點：** ways[0]=1，其他0。**轉移：** 不選value的舊ways[x]，加上選value的舊ways[x−value]。**順序：** 逐元素、容量降序，每個位置最多一次。**答案：** ways[positive]；範圍或奇偶不符先回0。

**為何正確：** 每個正號子集合唯一對應一種完整符號指派。**零的特別之處：** value=0時公式變ways[x]+=ways[x]，正好加倍：+0與-0總和值相同，但題目把符號選擇當作不同表達式。

**不適用處：** 輸入允許負數時，這份0..positive的容量表不能照抄；可改用總和到次數的Map。

### 原創例子：把變數走一遍

nums=`[0,1,2]`、target=1，total=3，所以P=2：

```text
容量      0 1 2
初始      1 0 0
處理0     2 0 0
處理1     2 2 0
處理2     2 2 2
```

兩種是 `+0-1+2`、`-0-1+2`。

**複雜度：** n是元素數、P是正號組容量。時間O(nP+n)，空間O(P+1)。每個位置只有兩種符號，所有計數不超過2ⁿ；n<=20，所以Int的安全性是由所有中間狀態上界推得。

**相似題差異與易錯處：** 416只問能否，零不改布林結果；494問次數，零會加倍。518硬幣可無限用，正序；494每個位置只用一次，反序。相同數值出現在不同索引仍是不同選擇，不能先去重。


## 115. Distinct Subsequences

**Hard** · [英文原題](https://leetcode.com/problems/distinct-subsequences/) · [官方中文](https://leetcode.cn/problems/distinct-subsequences/)

**中文題意（自行摘要）：** 從s刪去部分字元但保持剩下順序，能形成t的索引選法有幾種？兩種選法即使文字相同，只要選到不同來源位置就不同。s、t長度各1–1000，英文字母大小寫都可能出現；最後答案保證放得進Int。

### 先看答案：要記住的核心規則

來源字元由左到右；目標長度由右到左。字元相等時 `ways[j] += ways[j-1]`；使用 BigInteger，避免「最後答案很小，但中間前綴計數爆掉」。

```kotlin
import java.math.BigInteger

class Solution {
    fun numDistinct(s: String, t: String): Int {
        val ways = Array(t.length + 1) { BigInteger.ZERO }
        ways[0] = BigInteger.ONE
        for (ch in s) {
            for (j in t.length downTo 1) {
                if (ch == t[j - 1]) {
                    ways[j] = ways[j].add(ways[j - 1])
                }
            }
        }
        return ways[t.length].intValueExact()
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選前綴計數DP：** 同一段來源前綴可能用許多索引方式形成相同目標前綴，只需累積次數，無須列出所有刪法。

**狀態：** 處理s前i個字元後，ways[j]是形成t前j個字元的選法數。**起點：** ways[0]=1，空目標可全部不選；其他0。**轉移：** s目前字元不等於t[j−1]，只能不用它，保留ways[j]；相等則「不用」舊ways[j]＋「使用」舊ways[j−1]。**順序：** s字元正序，j反序；避免剛處理的同一來源字元被拿來匹配多個目標位置。**答案：** ways[t.length]。

**為何正確：** 按是否使用目前來源位置分群，兩群互斥；使用時必須剛好接到目標下一字，所以轉移完整。**不適用處：** 想找最長共同長度是1143，不是此處的選法計數。

**為什麼Long還不夠：** s是100個a，t是50個a再接b時，最終答案0，但形成前50個a的方式是C(100,50)，約10²⁹，超過Long。BigInteger讓每格精確；最後才用intValueExact依官方最終答案保證轉換。

### 原創例子：把變數走一遍

s=`"aaba"`、t=`"aa"`：

```text
目前來源  ways[0] ways[1] ways[2]
空          1       0       0
a           1       1       0
aa          1       2       1
aab         1       2       1
aaba        1       3       3
```

三種來源索引為(0,1)、(0,3)、(1,3)。最後a先更新ways[2]，才更新ways[1]，所以不會把它用兩次。

**複雜度：** m、n為s、t長度，B為最大中間整數位元數。做O(mn)次操作，大整數加法成本下時間O(mnB)、空間O(nB)；選法最多2^m，故B=O(m)。

**相似題差異與易錯處：** 1143用max保留最佳長度，本題用加法保留所有選法。錯把j正序更新時，來源只有一個a也可能湊出aa。字元大小寫必須精確匹配。


## 53. Maximum Subarray

**Medium** · [英文原題](https://leetcode.com/problems/maximum-subarray/) · [官方中文](https://leetcode.cn/problems/maximum-subarray/)

**中文題意（自行摘要）：** 找一段非空、連續的子陣列，使總和最大，回傳這個和。長度1–100000，數值-10000到10000。

### 先看答案：要記住的核心規則

以今天結尾的最佳連續和，只有「從今天重開」或「接昨天那段」：`ending=max(x,ending+x)`；全域答案另外保存。

```kotlin
class Solution {
    fun maxSubArray(nums: IntArray): Int {
        var ending = nums[0]
        var best = nums[0]
        for (i in 1 until nums.size) {
            ending = maxOf(nums[i], ending + nums[i])
            best = maxOf(best, ending)
        }
        return best
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選DP（Kadane）：** 連續片段的最後一格若是i，前一格只能是i−1；不需要記住所有起點，只保留最佳前段。

**狀態：** ending是「必須以目前位置結尾」的最大非空連續和，best是截至目前全域最大值。**起點：** 兩者都等於nums[0]，不是0。**轉移：** ending=max(nums[i],舊ending+nums[i])；best=max(best,ending)。**順序：** 索引遞增。**答案：** best。

**為何正確：** 以i結尾的片段要嘛只有自己，要嘛接在一段以i−1結尾的片段後；後者選最大舊ending即可。**不適用處：** 不連續、環狀、或要求至少k長度，條件改變需另設狀態。

### 原創例子：把變數走一遍

輸入 `[-4,3,-1,2,-6]`：

```text
x       -4 3 -1 2 -6
ending  -4 3  2 4 -2
best    -4 3  3 4  4
```

答案4來自[3,-1,2]。最後ending=-2，不代表之前最佳4消失了。

**複雜度：** n是長度，時間O(n)，空間O(1)；最大和不超過10⁹，Int足夠。

**相似題差異與易錯處：** 198可跳過房屋但不能選相鄰；本題要求連續，不能挑完3跳過-1再選2。若best初始為0，全負輸入就會錯誤選到不存在的空片段。


## 55. Jump Game

**Medium** · [英文原題](https://leetcode.com/problems/jump-game/) · [官方中文](https://leetcode.cn/problems/jump-game/)

**中文題意（自行摘要）：** 從索引0開始，每個nums[i]是當下最多可以往前跳幾格，可少跳。判斷能否到最後索引。長度1–10000，跳長0–100000。

### 先看答案：要記住的核心規則

只維護已知可到的最遠位置 farthest。掃描索引i若超過farthest，就已經斷路；可到時才拿它更新最遠距離。

```kotlin
class Solution {
    fun canJump(nums: IntArray): Boolean {
        var farthest = 0
        for (i in nums.indices) {
            if (i > farthest) return false
            farthest = maxOf(farthest, i + nums[i])
            if (farthest >= nums.lastIndex) return true
        }
        return true
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選貪心邊界：** 不需要枚舉每條跳躍路線。因為可以少跳，所有已知可達的位置形成從0開始的連續區間，只記右邊界就足夠。

**為何正確：** 若i在可達區間內，從i能把可達範圍擴到i+nums[i]；若i已在範圍外，更後面的位置也沒有任何已知跳法能跨過去。這個過程保留了所有可能路線的聯集，不是在盲目選某一次實際跳躍。

**不適用處：** 若規則是「必須恰好跳nums[i]格」或中間有禁止落地格，可達集合未必連續，不能只存最遠邊界。

### 原創例子：把變數走一遍

輸入 `[2,0,1,0]`：

```text
i=0 → 可到0..2
i=1 → nums1=0，不擴張但仍可繼續
i=2 → 可到0..3 → 成功
```

遇到0不一定失敗，因為可以從其他位置跳過它。

**複雜度：** n是長度，時間O(n)，空間O(1)。本題i+nums[i]最大約110000，不溢位。

**相似題差異與易錯處：** 45問最少幾次，需要分層；本題只問可達。不要把「每次實際跳到最遠落點」誤認成這個演算法，它保留的是所有可達位置的右界。


## 45. Jump Game II

**Medium** · [英文原題](https://leetcode.com/problems/jump-game-ii/) · [官方中文](https://leetcode.cn/problems/jump-game-ii/)

**中文題意（自行摘要）：** 與Jump Game相同的最多跳長規則，求到最後索引的最少跳躍次數。題目保證可到達。長度1–10000，跳長0–1000。

### 先看答案：要記住的核心規則

把可用相同跳數到達的區間當一層。掃完目前層右界end，才增加一次jumps，並用farthest當下一層右界。最後一格不需再起跳。

```kotlin
class Solution {
    fun jump(nums: IntArray): Int {
        var jumps = 0
        var end = 0
        var farthest = 0
        for (i in 0 until nums.lastIndex) {
            farthest = maxOf(farthest, i + nums[i])
            if (i == end) {
                jumps++
                end = farthest
            }
        }
        return jumps
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選區間分層：** 這其實是把BFS「每一層多一步」壓縮成幾個整數。可達範圍連續，所以不需要真的放入所有鄰居到Queue。

**為何正確：** 在相同跳數能到達的全部位置中，下一跳能到多遠取最大值，得到下一層完整範圍。第一次擴張覆蓋終點的層數，自然是最少跳數。

**不適用處：** 本題保證可達；拿去解允許斷路的題，必須在邊界不再推進時返回失敗，而不能照樣累加次數。

### 原創例子：把變數走一遍

輸入 `[2,1,2,1,1]`：

```text
初始：0跳可到索引0
i=0 掃完第0層 → jumps=1，end=2
i=1 下一層最遠仍2
i=2 下一層最遠4 → jumps=2，end=4
掃描不用處理最後索引4 → 答案2（0→2→4）
```

**複雜度：** n是長度，時間O(n)，空間O(1)。

**相似題差異與易錯處：** 55只維護farthest就夠；45另外需要end和jumps。長度1時已在終點，迴圈為空、答案0，不能先把jumps設1。


## 56. Merge Intervals

**Medium** · [英文原題](https://leetcode.com/problems/merge-intervals/) · [官方中文](https://leetcode.cn/problems/merge-intervals/)

**中文題意（自行摘要）：** 把所有有交集的區間合併，回傳覆蓋同樣範圍且互不重疊的區間。端點相同也算重疊。區間數1–10000，0<=start<=end<=10000，未保證排序。

### 先看答案：要記住的核心規則

先按起點排序，再與最後一個合併結果比較：起點<=目前終點就延伸；否則開新區間。

```kotlin
class Solution {
    fun merge(intervals: Array<IntArray>): Array<IntArray> {
        val sorted = intervals.sortedBy { it[0] }
        val result = mutableListOf<IntArray>()
        for (interval in sorted) {
            if (result.isEmpty() || interval[0] > result.last()[1]) {
                result.add(interval.copyOf())
            } else {
                result.last()[1] = maxOf(result.last()[1], interval[1])
            }
        }
        return result.toTypedArray()
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選排序＋掃描：** 原本不相鄰的輸入也可能重疊。按起點排好後，一段是否能接上只需看目前最後一段，不需和所有舊區間逐一比較。

**為何正確：** 新起點大於目前終點時，後面起點只會更大，所以目前區間可以定案；有交集則合併成最小起點到最大終點，覆蓋範圍完全相同。

**不適用處：** 435是刪除最少區間，需考慮最早結束，不能直接拿合併後的數量當刪除答案；若是半開區間，端點相同是否重疊要重新確認。

### 原創例子：把變數走一遍

輸入 `[[5,7],[1,3],[3,6]]`：

```text
排序 [1,3] [3,6] [5,7]
先留[1,3] → 3接觸3，變[1,6]
5<=6，變[1,7]
```

**複雜度：** n是區間數，時間O(n log n)，額外排序空間O(n)，輸出O(n)。

**相似題差異與易錯處：** 57輸入已排序且互不重疊，只插一段，不需重排。使用copyOf避免修改結果時順便改到原輸入內層IntArray；只複製外層不會複製裡面每一段。


## 57. Insert Interval

**Medium** · [英文原題](https://leetcode.com/problems/insert-interval/) · [官方中文](https://leetcode.cn/problems/insert-interval/)

**中文題意（自行摘要）：** 已有按起點排序、互不重疊的區間，插入一個newInterval，必要時合併交集，回傳仍排序且不重疊的結果。可有0個原區間，最多10000個；端點0–100000。

### 先看答案：要記住的核心規則

三段式：先收完全在左邊的，再合併所有有交集的，最後收完全在右邊的。

```kotlin
class Solution {
    fun insert(intervals: Array<IntArray>, newInterval: IntArray): Array<IntArray> {
        val result = mutableListOf<IntArray>()
        var index = 0
        var start = newInterval[0]
        var end = newInterval[1]
        while (index < intervals.size && intervals[index][1] < start) {
            result.add(intervals[index].copyOf())
            index++
        }
        while (index < intervals.size && intervals[index][0] <= end) {
            start = minOf(start, intervals[index][0])
            end = maxOf(end, intervals[index][1])
            index++
        }
        result.add(intArrayOf(start, end))
        while (index < intervals.size) {
            result.add(intervals[index].copyOf())
            index++
        }
        return result.toTypedArray()
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選線性掃描：** 已知舊區間有序且不重疊，插入位置與可能合併範圍都是連續的一段，不必再付排序成本。

**為何正確：** 第一段end<start保證完全不碰；第二段start<=end表示相交，更新終點後再看下一段，連鎖重疊也不漏；退出第二段後剩餘起點都更大，因此全部在右邊。

**不適用處：** 若原區間沒有排序或本來彼此重疊，三段式的前提不成立，應先按56處理。

### 原創例子：把變數走一遍

原區間 `[[1,2],[5,6],[9,10]]`，插入 `[2,9]`：

```text
[1,2]與新區間接觸 → 合併為[1,9]
[5,6]被包含       → 仍[1,9]
[9,10]端點接觸    → 合併為[1,10]
```

**複雜度：** n是原區間數，時間O(n)，輸出空間O(n)，除了輸出額外工作空間O(1)。

**相似題差異與易錯處：** 56必須先排序；57利用已排序前提省去這一步。第一段判斷用嚴格<，第二段用<=，才能把接觸端點正確合併。


## 48. Rotate Image

**Medium** · [英文原題](https://leetcode.com/problems/rotate-image/) · [官方中文](https://leetcode.cn/problems/rotate-image/)

**中文題意（自行摘要）：** 將n×n方形矩陣原地順時針轉90度，不可另開一張矩陣。n為1–20，元素-1000到1000。函式直接修改輸入、不回傳新矩陣。

### 先看答案：要記住的核心規則

先對主對角線轉置，再把每一列左右反轉。座標 `(r,c)→(c,r)→(c,n-1-r)` 就是順時針90度。

```kotlin
class Solution {
    fun rotate(matrix: Array<IntArray>) {
        val n = matrix.size
        for (r in 0 until n) {
            for (c in r + 1 until n) {
                val temp = matrix[r][c]
                matrix[r][c] = matrix[c][r]
                matrix[c][r] = temp
            }
        }
        for (row in matrix) row.reverse()
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選轉置＋反轉：** 直接逐個搬到新位置會覆蓋還沒搬走的值。兩次對稱變換都可用交換完成，避免額外矩陣，也比四角同時搬移更容易記。

**為何正確：** 原(r,c)經轉置到(c,r)，再左右反轉到(c,n−1−r)，這正是順時針座標公式。轉置只掃主對角線一側，讓每對格子交換一次。

**不適用處：** 長方形旋轉後行列數會互換，不能直接沿用這份方形原地程式；逆時針需更改變換順序或反轉方向。

### 原創例子：把變數走一遍

輸入 `[[2,4],[6,8]]`：

```text
原圖       轉置       每列反轉
2 4        2 6        6 2
6 8        4 8        8 4
```

**複雜度：** n是邊長，時間O(n²)，額外空間O(1)。row.reverse()原地修改IntArray，不是建立反向清單。

**相似題差異與易錯處：** 54是讀取螺旋順序，沒有旋轉矩陣。轉置若掃全部(r,c)又掃(c,r)，會交換兩次回到原狀。


## 54. Spiral Matrix

**Medium** · [英文原題](https://leetcode.com/problems/spiral-matrix/) · [官方中文](https://leetcode.cn/problems/spiral-matrix/)

**中文題意（自行摘要）：** 由左上角起，以向右、向下、向左、向上的順序一圈圈往內，回傳矩陣所有元素。行列各1–10，值-100到100，可為長方形。

### 先看答案：要記住的核心規則

維護top、bottom、left、right四條尚未讀取的邊界。讀完一邊就向內縮；讀下邊與左邊前再次檢查是否還有格子。

```kotlin
class Solution {
    fun spiralOrder(matrix: Array<IntArray>): List<Int> {
        val result = mutableListOf<Int>()
        var top = 0
        var bottom = matrix.lastIndex
        var left = 0
        var right = matrix[0].lastIndex
        while (top <= bottom && left <= right) {
            for (c in left..right) result.add(matrix[top][c])
            top++
            for (r in top..bottom) result.add(matrix[r][right])
            right--
            if (top <= bottom) {
                for (c in right downTo left) result.add(matrix[bottom][c])
                bottom--
            }
            if (left <= right) {
                for (r in bottom downTo top) result.add(matrix[r][left])
                left++
            }
        }
        return result
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選四邊界：** 不必另外維護visited矩陣。剩下未讀格子始終是一個矩形，四個數字就能描述它。

**為何正確：** 依序取出最外圈四邊；每讀完一邊立刻縮界，所以角落不會重複。剩下一排或一欄時，額外檢查避免反向再讀一次。

**不適用處：** 若矩陣是不規則缺角或有障礙物，未讀範圍不是矩形，需改成方向模擬加visited。

### 原創例子：把變數走一遍

輸入 `[[2,4,6],[8,10,12]]`：

```text
上邊：2,4,6 → top=1
右邊：12    → right=1
下邊：10,8  → bottom=0
左邊：已無剩餘行
輸出[2,4,6,12,10,8]
```

**複雜度：** R、C是行列數。時間O(RC)，額外工作空間O(1)，輸出O(RC)。

**相似題差異與易錯處：** 48真正改變矩陣內容，本題只讀。Kotlin `a..b` 在a>b時為空，但`downTo`是另一個方向，不能靠直覺省掉全部邊界判斷。


## 73. Set Matrix Zeroes

**Medium** · [英文原題](https://leetcode.com/problems/set-matrix-zeroes/) · [官方中文](https://leetcode.cn/problems/set-matrix-zeroes/)

**中文題意（自行摘要）：** 原矩陣中某格是0，就要把該整行、整列都設0；依原始0的位置決定，不能讓新設的0繼續傳染。原地修改矩陣。行列各1–200，值可能是任意Int。

### 先看答案：要記住的核心規則

借第一行、第一列當標記區。先記住它們原本是否含0，再標記內部、清內部，最後才清第一行與第一列。

```kotlin
class Solution {
    fun setZeroes(matrix: Array<IntArray>) {
        val rows = matrix.size
        val cols = matrix[0].size
        var firstRowZero = false
        var firstColZero = false
        for (c in 0 until cols) if (matrix[0][c] == 0) firstRowZero = true
        for (r in 0 until rows) if (matrix[r][0] == 0) firstColZero = true
        for (r in 1 until rows) {
            for (c in 1 until cols) {
                if (matrix[r][c] == 0) {
                    matrix[r][0] = 0
                    matrix[0][c] = 0
                }
            }
        }
        for (r in 1 until rows) {
            for (c in 1 until cols) {
                if (matrix[r][0] == 0 || matrix[0][c] == 0) matrix[r][c] = 0
            }
        }
        if (firstRowZero) for (c in 0 until cols) matrix[0][c] = 0
        if (firstColZero) for (r in 0 until rows) matrix[r][0] = 0
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選矩陣內部標記：** 直覺可用兩組BooleanArray記錄哪些行列要清零，但需要O(R+C)空間。第一行／列本來就在矩陣中，可當可重用便條紙，達到O(1)。

**為何正確：** 標記階段只讀內部原值，寫第一行／列，不把內部改掉，因此不會把人工0誤認成原始0。清內部時讀標記；第一行／列自己的原始狀態已保存在兩個Boolean中，最後處理不會破壞尚需使用的標記。

**不適用處：** 若不能修改輸入就不能借空間；如果題目真的要連鎖傳染，則是不同規則。不能用某個特殊Int當暫存哨兵，因為所有Int都可能是合法輸入。

### 原創例子：把變數走一遍

輸入 `[[2,0,4],[5,6,7]]`：

```text
第一行原本有0 → firstRowZero=true
第一列沒有0 → firstColZero=false
內部依matrix[0][1]==0 → 把6變0
最後清第一行
輸出 [[0,0,0],[5,0,7]]
```

**複雜度：** R、C是行列數。時間O(RC)，額外空間O(1)。

**相似題差異與易錯處：** 不要看到0就立刻把整行整列清掉，再繼續找0，否則很容易把整張圖全變0。matrix[0][0]一格無法同時獨立記兩個布林條件，所以先保存firstRowZero與firstColZero。


## 190. Reverse Bits

**Easy** · [英文原題](https://leetcode.com/problems/reverse-bits/) · [官方中文](https://leetcode.cn/problems/reverse-bits/)

**中文題意（自行摘要）：** 將32個位元的左右順序反轉，回傳反轉後的32位元值。2026-09-21查閱英文原題寫signed integer，限制0<=n<=2³¹−2且n為偶數；網路舊版常寫unsigned。下面Kotlin程式以Int承載位元，亦可處理任意32位元圖樣。

### 先看答案：要記住的核心規則

固定做32次：把答案左移一格，塞入來源最低位，來源用`ushr 1`右移補0。不是只反轉目前看得到的有效位數。

```kotlin
class Solution {
    fun reverseBits(n: Int): Int {
        var remaining = n
        var result = 0
        repeat(32) {
            result = (result shl 1) or (remaining and 1)
            remaining = remaining ushr 1
        }
        return result
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選逐位搬移：** 32位元很短，直接搬32次最清楚，不需要用字串補零。`and 1`抽最低位，`shl 1`替下一位留位置，`or`放進去。

**為何正確：** 來源從右向左取，答案從左移累積，處理完32次後，每個原始第k位恰好出現在第31−k位。前導0也算位元，不能漏做。

**signed／unsigned呈現：** Kotlin Int是有號32位元，但位元圖樣本身仍有32格。最高位是1時Int顯示負數，並不表示位元反轉失敗。若要看成0..2³²−1的正十進位，可用結果`.toLong() and 0xFFFF_FFFFL`，這只是顯示轉換。現行官方偶數輸入反轉後最高位為0；任意位元延伸測試才可能得到負Int。

**不適用處：** 64位元題目要改Long及64次；反轉十進位數字是另一題。

### 原創例子：把變數走一遍

現行限制內的原創例n=6：

```text
來源：00000000 00000000 00000000 00000110
反轉：01100000 00000000 00000000 00000000
結果：1610612736
```

延伸理解：n=1（不在現行偶數限制內）反轉為最高位1，其餘0；Int顯示-2147483648，unsigned視角是2147483648。兩者是同一個32位元圖樣。

**複雜度：** 固定32次，所以時間O(1)、空間O(1)；以位寬B泛化則時間O(B)。

**相似題差異與易錯處：** 338數1的個數，位置不重要；190要求每一位換位置。Kotlin用ushr避免負數右移時符號擴展。[Kotlin ushr 官方說明](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/ushr.html)測試區前兩例符合現行限制，第三例明確是signed/unsigned延伸驗證。


## 371. Sum of Two Integers

**Medium** · [英文原題](https://leetcode.com/problems/sum-of-two-integers/) · [官方中文](https://leetcode.cn/problems/sum-of-two-integers/)

**中文題意（自行摘要）：** 不用+或-運算子，求整數a與b的和。a、b各在-1000到1000，可為負數。

### 先看答案：要記住的核心規則

`xor`算不含進位的和，`and`找兩邊都是1的位置，再左移一格成進位；把和與進位反覆合併，直到沒有進位。

```kotlin
class Solution {
    fun getSum(a: Int, b: Int): Int {
        var value = a
        var carry = b
        while (carry != 0) {
            val nextCarry = (value and carry) shl 1
            value = value xor carry
            carry = nextCarry
        }
        return value
    }
}
```

### 再拆原理：工具理由、成立原因與使用界線

**為何選位元運算：** 二進位加法只有四種單位情況：0+0→0；0+1或1+0→1；1+1→本位0、向左進1。前半就是xor，進位就是and再左移。

**為何正確：** 每輪把原本待相加兩數，分成「不含進位的和」與「還沒加入的進位」，總值按32位元運算保持相同。進位不斷往左傳，最多跨過32個位置便消失；若carry=0，value就是結果。負數使用二補數表示，同樣位元加法可用。

**不適用處：** 若數學結果超過Int，這只保留32位元，不能當作任意精度加法；本題結果介於-2000到2000，不會超出。

### 原創例子：把變數走一遍

a=5、b=7，以低4位示意：

```text
value carry
0101  0111
0010  1010  ← xor=0010，and=0101左移得1010
1000  0100
1100  0000  → 12
```

先算nextCarry再改value，避免進位使用到更新後的資料。

**複雜度：** 固定32位元下，時間O(1)、空間O(1)，最壞約32輪；泛化位寬B時迴圈次數O(B)。

**相似題差異與易錯處：** 136利用xor把重複值抵消，並不是整數加法；本題xor還需要搭配進位。若只回a xor b，遇到兩邊同一位都是1就會錯。



---

<a id="reused"></a>

# 沿用原LeetCode75的20題

以下保留原詳解內容；採新的閱讀順序：看題意後先跳到Kotlin答案，再回看推演，不要求先自行想解法。

## 238. Product of Array Except Self

難度：Medium｜[英文原題](https://leetcode.com/problems/product-of-array-except-self/)｜[官方中文](https://leetcode.cn/problems/product-of-array-except-self/)

**中文題意（自行改寫）**：每個位置的答案是「其他所有位置的數相乘」。不能用除法，要求線性時間。可能有零或負數；題目保證相關乘積可用 32 位元整數表示。

**白話思路**：每格重新乘一次是 O(n²)。改把「除了自己」拆成「左邊全部 × 右邊全部」。第一趟把左乘積存入答案，第二趟把右乘積乘進去。空的一邊乘積是 1，因為乘 1 不改結果。

**圖解**：輸入 `[2,3,4]`。

| 位置 | 左邊的乘積 | 右邊的乘積 | 答案 |
|---|---:|---:|---:|
| 0 | 1 | 3×4=12 | 12 |
| 1 | 2 | 4 | 8 |
| 2 | 2×3=6 | 1 | 6 |

```kotlin
class Solution {
    fun productExceptSelf(nums: IntArray): IntArray {
        val answer = IntArray(nums.size)
        var leftProduct = 1
        for (i in nums.indices) {
            answer[i] = leftProduct // 先寫，才不會乘進自己
            leftProduct *= nums[i]
        }
        var rightProduct = 1
        for (i in nums.lastIndex downTo 0) {
            answer[i] *= rightProduct
            rightProduct *= nums[i]
        }
        return answer
    }
}
```

**為什麼對**：寫入時累積值只包含索引以左或以右的元素，自己永遠沒進自己的結果。時間 O(n)；結果 O(n)，排除結果的額外空間 O(1)。

**常見錯誤**：先更新乘積才寫答案；用總乘積除自己，遇零失敗也違反要求。

**理解自測**：`[2,0,4]` 結果？答案：`[0,8,0]`，不需要為零另外寫分支。

## 11. Container With Most Water

難度：Medium｜[英文原題](https://leetcode.com/problems/container-with-most-water/)｜[官方中文](https://leetcode.cn/problems/container-with-most-water/)

**中文題意（自行改寫）**：每格是一根垂直線的高度，選兩根和地面形成容器，求最大面積。寬是索引差，高只能取較矮的一根；不是計算雨水積在每個凹槽的總量。

**白話思路**：暴力枚舉兩根是 O(n²)。雙指標先選最外兩根，算面積，再移動較矮的一側。因為固定矮邊、只往內移高邊，寬縮短而水位不可能超過矮邊，沒有改善機會。

**手算**：`[2,5,4,3]`：兩端寬 3、高 2，面積 6；左邊較矮，移左到 1。此時寬 2、高 3，面積 6；移右到 2，寬 1、高 4，面積 4。最大 6。

```kotlin
class Solution {
    fun maxArea(height: IntArray): Int {
        var left = 0
        var right = height.lastIndex
        var best = 0
        while (left < right) {
            val area = minOf(height[left], height[right]) * (right - left)
            best = maxOf(best, area)
            if (height[left] <= height[right]) left++ else right--
        }
        return best
    }
}
```

**為什麼對**：每次丟掉的一側，與其內側任何另一邊搭配都不會勝過剛算過的面積；所以不會跳過尚未計算的更好答案。時間 O(n)，空間 O(1)。題目範圍下乘積可用 Int。

**常見錯誤**：面積用較高邊；寬寫成 `right-left+1`；誤以為應保留短邊。

**理解自測**：兩邊一樣高要移哪邊？答案：任一邊皆可，上面選左邊。

## 739. Daily Temperatures

**難度：Medium｜觀念：單調堆疊、下一個更大值**
[英文原題](https://leetcode.com/problems/daily-temperatures/) · [官方中文](https://leetcode.cn/problems/daily-temperatures/)

### 中文題意與輸入輸出

輸入每天溫度，回傳每一天要再等幾天才會遇到**嚴格更熱**的一天，之後沒有更熱就填 0。天數 1–100,000，溫度 30–100。

### 白話思路與手算

每一天都往後找，最壞 O(n²)。換個角度：把還沒遇到更熱天氣的日期放進堆疊。新的一天到來，如果比頂端那天熱，那天終於等到了，答案就是今天索引減那天索引。

原創例 `[60,55,55,65]`：

```text
第0天60 → 等待[0]
第1天55 → 等待[0,1]
第2天55 → 一樣熱不算 → 等待[0,1,2]
第3天65 → 解決2，等1天；解決1，等2天；解決0，等3天
輸出[3,2,1,0]
```

堆疊裡對應的溫度由底到頂不增加。頂端較冷，解決後可以繼續往下看；頂端都不比今天冷，下面更熱的更不可能被今天解決。每個等待者被移除那天，就是它第一次遇到更熱天，因為若之前有更熱天，它早已被移除了。

### Kotlin 解答

```kotlin
import java.util.ArrayDeque

class Solution {
    fun dailyTemperatures(temperatures: IntArray): IntArray {
        val answer = IntArray(temperatures.size)
        val waiting = ArrayDeque<Int>() // 存日期索引，不是溫度
        for (today in temperatures.indices) {
            while (waiting.isNotEmpty() && temperatures[today] > temperatures[waiting.peekLast()]) {
                val earlier = waiting.removeLast()
                answer[earlier] = today - earlier
            }
            waiting.addLast(today)
        }
        return answer
    }
}
```

**複雜度：** n 是天數；總時間 `O(n)`，因為每個索引最多加入、移除一次，不是看見 while 包在 for 裡就必定 O(n²)。堆疊額外空間 `O(n)`，輸出也 `O(n)`。

**常見錯誤：** 存溫度而忘記日期，無法算距離；使用 `>=` 把相同溫度也當成更熱；最後還等不到的日期不用改，IntArray 預設 0 即是答案。

**理解自測：** `[70,70]` 的第一個答案是多少？
**答案：** 0，第二天沒有嚴格比較熱。

## 875. Koko Eating Bananas

**難度：Medium｜觀念：對答案二分搜尋**
[英文原題](https://leetcode.com/problems/koko-eating-bananas/) · [官方中文](https://leetcode.cn/problems/koko-eating-bananas/)

### 中文題意與輸入輸出

輸入每堆香蕉數 `piles` 與時限 h。每小時只吃一堆，最多吃 k 根；該堆不足 k 根也要用完整一小時，不能把剩下時間拿去吃另一堆。求能在 h 小時內吃完的最小整數速度 k。堆數 1–10,000，每堆最多 10⁹；h 至少等於堆數，最多 10⁹。

### 白話思路與手算

先別問「最佳速度是多少」，先問「給定速度，能吃完嗎？」每堆花 `ceil(數量/k)` 小時。速度越快只會越容易完成，所以可行性排列成「不行、不行、可以、可以」，適合找第一個可以。

原創例 `piles=[4,8]`，`h=4`：

```text
k=2：2+4=6小時，不行
k=3：2+3=5小時，不行
k=4：1+2=3小時，可以 → 最小速度4
```

最大一堆的數量一定是可行上限：這個速度每堆只花一小時。使用整數公式 `(p+k-1)/k` 向上取整；加法與總時數都用 Long，避免大資料溢位。

### Kotlin 解答

```kotlin
class Solution {
    fun minEatingSpeed(piles: IntArray, h: Int): Int {
        var left = 1
        var right = piles.maxOrNull()!!
        while (left < right) {
            val mid = left + (right - left) / 2
            var hours = 0L
            for (pile in piles) {
                hours += (pile.toLong() + mid - 1) / mid
            }
            if (hours <= h.toLong()) right = mid
            else left = mid + 1
        }
        return left
    }
}
```

**複雜度：** n 是堆數、M 是最大一堆數量；時間 `O(n log M)`，額外空間 `O(1)`。

**常見錯誤：** 用香蕉總數除以 h，忽略每堆各自佔用整小時；把可行時的 mid 排除，錯過最小速度。

**理解自測：** 當 h 就等於堆數，答案是多少？
**答案：** 最大一堆數量。每堆都只能花一小時。

## 206. Reverse Linked List

**難度：Easy** · [英文原題](https://leetcode.com/problems/reverse-linked-list/) · [官方中文](https://leetcode.cn/problems/reverse-linked-list/)

### 中文題意（自行改寫，非逐字翻譯）

輸入一條可能為空的串列，把順序反轉並回傳新開頭。節點最多 5,000，值在 ±5,000。

### 白話思路與手算

把數字放陣列再倒著建節點可以，但會多用記憶體。只改箭頭就好：previous 是已經反轉完成的開頭，current 是尚未處理的位置。先保存下一張紙條位置，再把箭頭轉回去，否則會找不到後半串。

原創例子：

```text
原本：null    7 → 3 → 9 → null
第一步：null ← 7    3 → 9 → null
第二步：null ← 7 ← 3    9 → null
完成：null ← 7 ← 3 ← 9（新 head 是 9）
```

```kotlin
class Solution {
    fun reverseList(head: ListNode?): ListNode? {
        var previous: ListNode? = null
        var current = head
        while (current != null) {
            val next = current.next // 改箭頭前，先保存剩下的路
            current.next = previous
            previous = current
            current = next
        }
        return previous
    }
}
```

**複雜度：** n 為節點數，時間 O(n)，額外空間 O(1)，修改輸入串列。**常見錯誤：** 改完 current.next 才讀 next。**自測：** 為何 previous 初始是 null？**答案：** 原本第一個節點最後會變尾巴，尾巴必須指向 null。

## 104. Maximum Depth of Binary Tree

**難度：Easy** · [英文原題](https://leetcode.com/problems/maximum-depth-of-binary-tree/) · [官方中文](https://leetcode.cn/problems/maximum-depth-of-binary-tree/)

### 中文題意（自行改寫，非逐字翻譯）

求樹最深有幾層，根算第 1 層；空樹為 0。最多 10,000 個節點，值在 ±100。

### 白話思路與手算

可以問左右子樹各多深，再加 1，這就是遞迴。不過樹若像長鏈，Kotlin 呼叫可能太深。本版用 BFS：先把根排隊，整批處理完一層再加一。必須先記住本層人數，新加入的孩子留到下一輪。

原創樹：`4` 左孩 `2`，`2` 左孩 `1`，沒有其他節點。佇列每層依序 `[4] → [2] → [1] → []`，答案 3。每輪只放下一層，所以輪數等於深度。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun maxDepth(root: TreeNode?): Int {
        if (root == null) return 0
        val queue = ArrayDeque<TreeNode>()
        queue.addLast(root)
        var depth = 0
        while (queue.isNotEmpty()) {
            val count = queue.size
            repeat(count) {
                val node = queue.removeFirst()
                node.left?.let { queue.addLast(it) }
                node.right?.let { queue.addLast(it) }
            }
            depth++
        }
        return depth
    }
}
```

**複雜度：** n 為節點數、w 為最大層寬，時間 O(n)，空間 O(w)，最壞 O(n)。**常見錯誤：** 把深度當邊數，單節點回傳 0。**自測：** 只有根節點多深？**答案：** 1。

## 199. Binary Tree Right Side View

**難度：Medium** · [英文原題](https://leetcode.com/problems/binary-tree-right-side-view/) · [官方中文](https://leetcode.cn/problems/binary-tree-right-side-view/)

### 中文題意（自行改寫，非逐字翻譯）

從右邊看樹，回傳每層最右邊那個值，由上往下。最多 100 個節點，可為空；值在 ±100。

### 白話思路與手算

不是一路沿 right 走到底，因為右子樹可能比較矮。用 BFS 一層一層看，孩子左先右後排隊，每層最後拿出來的就是最右邊。

原創例子根 8，左右孩為 4、9，只有 4 有左孩 2。三層依序 `[8]`、`[4,9]`、`[2]`，答案 `[8,9,2]`。右側空了，左子樹深處仍看得到。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun rightSideView(root: TreeNode?): List<Int> {
        val answer = mutableListOf<Int>()
        if (root == null) return answer
        val queue = ArrayDeque<TreeNode>()
        queue.addLast(root)
        while (queue.isNotEmpty()) {
            val count = queue.size
            repeat(count) { index ->
                val node = queue.removeFirst()
                if (index == count - 1) answer.add(node.`val`)
                node.left?.let { queue.addLast(it) }
                node.right?.let { queue.addLast(it) }
            }
        }
        return answer
    }
}
```

**複雜度：** n 為節點數、w 為層寬，時間 O(n)，額外佇列 O(w)，輸出 O(h)，h 為高度。**常見錯誤：** 右孩子不存在就結束搜尋。**自測：** 只有左孩子的長鏈會看到誰？**答案：** 全部節點。

## 1448. Count Good Nodes in Binary Tree

**難度：Medium** · [英文原題](https://leetcode.com/problems/count-good-nodes-in-binary-tree/) · [官方中文](https://leetcode.cn/problems/count-good-nodes-in-binary-tree/)

### 中文題意（自行改寫，非逐字翻譯）

從根走到某節點，若途中沒有比它更大的值，它就是 good node。回傳個數。節點數 1–100,000，值在 ±10,000。

### 白話思路與手算

每個節點回頭重查祖先很慢。往下走時隨身攜帶「到目前最大的值」就好。每個分支拿自己的紀錄，兄弟分支互不影響。相等也合格。

原創例子：根 5，左孩 3，3 的左孩 6，根右孩 5。走 `5→3→6` 的最大紀錄為 5、5、6，合格為 5、6；右邊的 5 也合格，答案 3。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun goodNodes(root: TreeNode?): Int {
        if (root == null) return 0
        val stack = ArrayDeque<Pair<TreeNode, Int>>()
        stack.addLast(root to Int.MIN_VALUE)
        var count = 0
        while (stack.isNotEmpty()) {
            val (node, maximum) = stack.removeLast()
            if (node.`val` >= maximum) count++
            val nextMaximum = maxOf(maximum, node.`val`)
            node.left?.let { stack.addLast(it to nextMaximum) }
            node.right?.let { stack.addLast(it to nextMaximum) }
        }
        return count
    }
}
```

**複雜度：** n 為節點數、h 為高度，時間 O(n)，堆疊空間 O(h)，最壞 O(n)。**常見錯誤：** 用整棵樹共用的一個最大值，讓左邊污染右邊。**自測：** 全樹值都等於 7 呢？**答案：** 每一個都合格。

## 215. Kth Largest Element in an Array

**難度：Medium** · [英文原題](https://leetcode.com/problems/kth-largest-element-in-an-array/) · [官方中文](https://leetcode.cn/problems/kth-largest-element-in-an-array/)

### 中文題意（自行改寫，非逐字翻譯）

找降冪排列第 k 個數，重複數字也佔名次。`1 ≤ k ≤ n ≤ 100,000`，數值在 ±10,000。題目希望不靠完整排序。

### 白話思路與手算

完整排序是 O(n log n)。只保留目前最大的 k 個就夠；最小 Heap 的箱口是這 k 個中最小的，也就是第 k 大。每讀一數先放入，超過 k 就拿掉最小的。被拿掉的已有至少 k 個數不小於它，不可能再擠進前 k。

原創例子 `[8,2,8,5]`、k=2：保留集合依序 `[8]`、`[2,8]`、`[8,8]`、`[8,8]`，答案 8。這裡列出的集合為方便閱讀排序過，PriorityQueue 內部本身不保證全部有序。

```kotlin
import java.util.PriorityQueue
class Solution {
    fun findKthLargest(nums: IntArray, k: Int): Int {
        val heap = PriorityQueue<Int>()
        for (number in nums) {
            heap.offer(number)
            if (heap.size > k) heap.poll()
        }
        return heap.peek()
    }
}
```

**複雜度：** n 為元素數，時間 O(n log(k+1))，空間 O(k)。heap 的 offer/poll 是 O(log 大小)，peek 是 O(1)。**常見錯誤：** 用 Set 去重，改變名次。**自測：** k=1，箱口是最大還最小？**答案：** 全部輸入的最大值，因為其他較小值都已被丟掉。

## 17. Letter Combinations of a Phone Number

**難度：Medium｜觀念：回溯列舉**
[英文原題](https://leetcode.com/problems/letter-combinations-of-a-phone-number/) · [官方中文](https://leetcode.cn/problems/letter-combinations-of-a-phone-number/)

### 中文題意與輸入輸出

輸入由 2–9 組成的字串，依電話鍵盤把每個數字換成對應的一個英文字母，列出所有組合。2 對 abc、3 對 def、4 對 ghi、5 對 jkl、6 對 mno、7 對 pqrs、8 對 tuv、9 對 wxyz。查閱時英文頁限制長度 1–4；程式亦兼容舊題面可能出現的空字串，回傳空清單。

### 白話思路與手算

先替第一格選字母，再替第二格選，直到每格都有字母就記錄。回來時要撤銷剛才的選擇，否則下一個分支會夾帶上一個答案。

原創例 `digits="24"`：

```text
空字串
├─a → ag、ah、ai
├─b → bg、bh、bi
└─c → cg、ch、ci
```

`index` 表示正在填第幾格，`path` 是目前填好的字母。每層只處理自己的一格，因此不漏掉組合，也不會重複。這題要求輸出全部答案，指數級答案量無法靠神奇技巧消除。

### Kotlin 解答

```kotlin
class Solution {
    fun letterCombinations(digits: String): List<String> {
        if (digits.isEmpty()) return emptyList()
        val letters = arrayOf("", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz")
        val result = mutableListOf<String>()
        val path = StringBuilder()
        fun dfs(index: Int) {
            if (index == digits.length) {
                result.add(path.toString())
                return
            }
            for (ch in letters[digits[index] - '0']) {
                path.append(ch) // 選
                dfs(index + 1) // 往下一格
                path.deleteCharAt(path.length - 1) // 撤銷
            }
        }
        dfs(0)
        return result
    }
}
```

**複雜度：** d 是數字長度；最多 4ᵈ 個答案，每個答案複製 d 個字元，時間 `O(d·4ᵈ)`；排除輸出，遞迴與路徑空間 `O(d)`，輸出 `O(d·4ᵈ)`。

**常見錯誤：** 忘記撤銷；把 `digits[index].toInt()` 當成數字 2，它其實是字元編碼。減 `'0'` 才會得到對應數字。

**理解自測：** `"79"` 有幾個答案？
**答案：** 4×4=16 個，因為兩個鍵各有四個字母。

## 208. Implement Trie (Prefix Tree)

**難度：Medium｜觀念：前綴樹**
[英文原題](https://leetcode.com/problems/implement-trie-prefix-tree/) · [官方中文](https://leetcode.cn/problems/implement-trie-prefix-tree/)

### 中文題意與輸入輸出

實作 `Trie()`、`insert(word)`、`search(word)`、`startsWith(prefix)`。insert 加入單字；search 判斷完整單字是否曾加入；startsWith 判斷是否有單字以指定字首開頭。只有小寫英文，字串長度 1–2000，總操作次數最多 30,000。

### 白話思路與圖解

用 HashSet 存完整字串可以快速查單字，但查共同字首不方便。Trie 像共用資料夾路徑：共同字首只存一份，每個字母是一條路。每個節點有 26 個可能的下一站，`ch-'a'` 決定走哪條。

原創例，先插入 `"car"`、`"cat"`：

```text
root → c → a ┬→ r (end=true)
             └→ t (end=true)
search("ca") = false     // 路走得到，但不是完整單字
startsWith("ca") = true  // 路走得到即可
search("car") = true
```

`end` 像資料夾上寫著「這裡也是一個完整答案」。不能只看有沒有走到節點，否則會把所有字首都當作已插入單字。

### Kotlin 解答

```kotlin
class Trie() {
    private class Node {
        val children = arrayOfNulls<Node>(26)
        var end = false
    }
    private val root = Node()

    fun insert(word: String) {
        var node = root
        for (ch in word) {
            val index = ch - 'a'
            if (node.children[index] == null) node.children[index] = Node()
            node = node.children[index]!!
        }
        node.end = true
    }

    private fun walk(text: String): Node? {
        var node = root
        for (ch in text) {
            node = node.children[ch - 'a'] ?: return null
        }
        return node
    }

    fun search(word: String): Boolean = walk(word)?.end == true

    fun startsWith(prefix: String): Boolean = walk(prefix) != null
}
```

`arrayOfNulls<Node>(26)` 一開始 26 條路都不存在；需要時才建立節點。`?: return null` 表示某一步沒路就立刻查詢失敗。

**複雜度：** 每次操作時間 `O(L)`，L 是這次字串長度。若所有插入字元總數為 S，節點數最多 S+1，空間 `O(26S)=O(S)`；共享前綴能減少實際節點。

**常見錯誤：** 不記 end；每次 insert 都重新建立 root，洗掉舊資料；把英文大小寫混用而索引越界，本題限定小寫。

**理解自測：** 插入 car 後再插入 ca，會破壞 car 嗎？
**答案：** 不會，只把 a 節點的 end 設為 true，r 的路和 end 都保留。

## 994. Rotting Oranges

**難度：Medium** · [英文原題](https://leetcode.com/problems/rotting-oranges/) · [官方中文](https://leetcode.cn/problems/rotting-oranges/)

### 中文題意（自行改寫，非逐字翻譯）

格子 0 是空地、1 是新鮮橘子、2 是爛橘子。每分鐘所有爛橘子同時感染上下左右新鮮橘子，求全部沒有新鮮橘子的最少分鐘；做不到為 -1。列欄各 1–10。

### 白話思路與手算

不能每顆爛橘子各自完整傳染後才處理下一顆，因為現實是同時發生。把一開始所有爛橘子一起排隊，稱為多起點 BFS。每輪只處理本分鐘已爛的，新感染的留下一輪。

原創例子 `[2,1,1,1,2]`：0 分鐘左右兩端爛；1 分鐘變 `[2,2,1,2,2]`；2 分鐘全部爛，答案 2。另計 fresh，感染就扣一，避免每分鐘重掃整張圖。

```kotlin
import java.util.ArrayDeque
class Solution {
    fun orangesRotting(grid: Array<IntArray>): Int {
        val rows = grid.size
        val cols = grid[0].size
        val queue = ArrayDeque<Pair<Int, Int>>()
        var fresh = 0
        for (r in 0 until rows) for (c in 0 until cols) {
            if (grid[r][c] == 1) fresh++
            if (grid[r][c] == 2) queue.addLast(r to c)
        }
        val directions = intArrayOf(-1, 0, 1, 0, -1)
        var minutes = 0
        while (queue.isNotEmpty() && fresh > 0) {
            repeat(queue.size) {
                val (r, c) = queue.removeFirst()
                for (d in 0 until 4) {
                    val nr = r + directions[d]
                    val nc = c + directions[d + 1]
                    if (nr in 0 until rows && nc in 0 until cols && grid[nr][nc] == 1) {
                        grid[nr][nc] = 2
                        fresh--
                        queue.addLast(nr to nc)
                    }
                }
            }
            minutes++
        }
        return if (fresh == 0) minutes else -1
    }
}
```

**複雜度：** R、C 為列欄數，時間與額外空間 O(RC)，會修改 grid。**常見錯誤：** 沒新鮮橘子還回傳 1；空地也傳染。**自測：** `[2,0,1]`？**答案：** -1，感染不能跨空地。

## 746. Min Cost Climbing Stairs

**難度：Easy｜觀念：最小成本 DP**
[英文原題](https://leetcode.com/problems/min-cost-climbing-stairs/) · [官方中文](https://leetcode.cn/problems/min-cost-climbing-stairs/)

### 中文題意與輸入輸出

第 i 階離開時要付 `cost[i]`，付完可向上走一或兩階。可以直接從索引 0 或 1 開始；目標是越過最後一階，來到索引 n 的頂端。回傳最少總花費。階數 2–1000，每階費用 0–999。

### 白話思路與手算

不能只看眼前哪一階便宜，因為今天的選擇會改變明天能跳去哪裡。改問「要到第 i 階，最後一步從哪裡來？」只可能來自 i−1 或 i−2。

**狀態：** `dp[i]` 是「到達 i，但還沒付 i 的費用」的最低成本。**初始：** `dp[0]=dp[1]=0`，因為允許直接站上去。**轉移：** `min(dp[i−1]+cost[i−1], dp[i−2]+cost[i−2])`。**方向：** 2 到 n，由低到高；答案是 `dp[n]`，不是 `dp[n−1]`。

原創例 `cost=[5,2,4,1]`：

```text
dp[0]=0, dp[1]=0
dp[2]=min(0+2,0+5)=2
dp[3]=min(2+4,0+2)=2
dp[4]=min(2+1,2+4)=3 → 從1走到3，再到頂端
```

### Kotlin 解答

```kotlin
class Solution {
    fun minCostClimbingStairs(cost: IntArray): Int {
        val n = cost.size
        val dp = IntArray(n + 1)
        for (i in 2..n) {
            dp[i] = minOf(dp[i - 1] + cost[i - 1], dp[i - 2] + cost[i - 2])
        }
        return dp[n]
    }
}
```

**複雜度：** n 是階數；時間 `O(n)`，空間 `O(n)`。理解後可像 1137 一樣只存前兩項，降為 `O(1)`，此處保留表格方便對照。

**常見錯誤：** 把頂端當成最後一個付費階梯；混用「到達的成本」與「已付本階的成本」兩種狀態定義。

**理解自測：** 只有兩階 `[9,3]`，答案是多少？
**答案：** 3。直接從索引 1 開始，付 3 後走到頂端。

## 198. House Robber

**難度：Medium｜觀念：選或不選 DP**
[英文原題](https://leetcode.com/problems/house-robber/) · [官方中文](https://leetcode.cn/problems/house-robber/)

### 中文題意與輸入輸出

一排房屋各有 `nums[i]` 元，不能同時選相鄰兩間，求最多可以取得多少。輸入長度 1–100，每個金額 0–400；房屋排列是一直線，不是環形。

### 白話思路與手算

「每次挑最大間」可能卡住左右兩間，合計反而更少。對前 i 間的最後一間，只有兩種可能：不選它，沿用前 i−1 間最佳值；選它，則前一間不能選，接上前 i−2 間最佳值。

**狀態：** `dp[i]` 是前 i 間能取得的最大金額。**初始：** `dp[0]=0`、`dp[1]=nums[0]`。**轉移：** `max(dp[i−1], dp[i−2]+nums[i−1])`。**方向：** i 遞增。程式用 `previous` 存前一項、`twoBack` 存前兩項，先算 current 再更新。

原創例 `[4,1,2,7]`：

```text
前0間 → 0
前1間 → 4
前2間 → max(4,0+1)=4
前3間 → max(4,4+2)=6
前4間 → max(6,4+7)=11 → 選4和7
```

兩個分支已包含所有合法可能，取最大值不會漏掉最佳選法。

### Kotlin 解答

```kotlin
class Solution {
    fun rob(nums: IntArray): Int {
        var twoBack = 0
        var previous = 0
        for (money in nums) {
            val current = maxOf(previous, twoBack + money)
            twoBack = previous
            previous = current
        }
        return previous
    }
}
```

**複雜度：** n 是房屋數；時間 `O(n)`、額外空間 `O(1)`。

**常見錯誤：** 只算奇數位置和偶數位置兩種選法，但最佳答案不一定隔一間選到底；更新順序使 twoBack 變成今天的答案。

**理解自測：** `[5,1,1,5]` 的答案是 6 嗎？
**答案：** 是 10，第一間和第四間不相鄰，可以一起選。

## 62. Unique Paths

**難度：Medium｜觀念：二維 DP**
[英文原題](https://leetcode.com/problems/unique-paths/) · [官方中文](https://leetcode.cn/problems/unique-paths/)

### 中文題意與輸入輸出

在 m 排、n 欄的格子中，從左上角出發，每次只能向右或向下走一格，求到右下角的路線數。`1<=m,n<=100`；測資保證答案最多 2×10⁹。沒有障礙物。

### 白話思路與手算

列出所有路會指數成長。不要記住每條路長什麼樣，只要記住到每格有幾條。

**狀態：** `dp[r][c]` 是到第 r 排、第 c 欄的方法數。**初始：** 第一排與第一欄都是 1，因為只能一路向右或一路向下。**轉移：** 其他格是「上面的方法數＋左邊的方法數」。**方向：** 由上到下、每排由左到右，確保依賴已算好。

原創例 `m=3,n=3`：

```text
1 1 1
1 2 3
1 3 6 ← 3(上面)+3(左邊)
```

最後一步來自上面與左邊是互斥的兩群路線，兩群加起來恰好是全部，因此可以直接相加。

### Kotlin 解答

```kotlin
class Solution {
    fun uniquePaths(m: Int, n: Int): Int {
        val dp = Array(m) { IntArray(n) { 1 } }
        for (r in 1 until m) {
            for (c in 1 until n) {
                dp[r][c] = dp[r - 1][c] + dp[r][c - 1]
            }
        }
        return dp[m - 1][n - 1]
    }
}
```

**複雜度：** 時間、空間皆 `O(mn)`；之後可只留一排而降空間至 `O(n)`，因為上一排用過就不必保留。

**常見錯誤：** 把格子數和移動步數混淆；第一排全部設 0；忘記 m 或 n 可以是 1。

**理解自測：** 1×5 有幾條路？
**答案：** 1 條，只能一直向右。

## 1143. Longest Common Subsequence

**難度：Medium｜觀念：兩個字串的前綴 DP**
[英文原題](https://leetcode.com/problems/longest-common-subsequence/) · [官方中文](https://leetcode.cn/problems/longest-common-subsequence/)

### 中文題意與輸入輸出

輸入兩個小寫英文字串，找同時出現在兩者中的最長「子序列」長度。子序列可以跳過字元，但不能改變順序，也不需要連續。兩個長度都是 1–1000；沒有共同字元就回傳 0。

### 白話思路與手算

每個字元都猜要留或刪會爆炸。我們改記「兩個字串各看到某個位置時，最佳答案是多少」。

**狀態：** `dp[i][j]` 是 text1 前 i 個字元與 text2 前 j 個字元的 LCS 長度。注意 i 是**數量**，目前字元是 `text1[i−1]`。**初始：** 任一字串長度為 0，答案為 0。**轉移：** 最後字元相同，接上這一對，`dp[i−1][j−1]+1`；不同，最後兩個字元不能互相配對，至少略過一邊，取 `max(dp[i−1][j],dp[i][j−1])`。**方向：** i、j 都由小到大。

原創例 `text1="abca",text2="aca"`：

```text
      空 a c a
空     0 0 0 0
a      0 1 1 1
b      0 1 1 1
c      0 1 2 2
a      0 1 2 3 → 共同子序列aca，長度3
```

### Kotlin 解答

```kotlin
class Solution {
    fun longestCommonSubsequence(text1: String, text2: String): Int {
        val m = text1.length
        val n = text2.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 1..m) {
            for (j in 1..n) {
                dp[i][j] = if (text1[i - 1] == text2[j - 1]) {
                    dp[i - 1][j - 1] + 1
                } else {
                    maxOf(dp[i - 1][j], dp[i][j - 1])
                }
            }
        }
        return dp[m][n]
    }
}
```

**複雜度：** m、n 是兩字串長度；時間、空間都是 `O(mn)`。空間可壓成一排，但需要保存左上角舊值，初學先用完整表。

**常見錯誤：** 誤解成必須連續的 substring；字元不同就把格子歸零，那是在解另一道問題。

**理解自測：** `"abc"` 與 `"cba"` 的答案為什麼不是 3？
**答案：** 共同字元雖有三種，但順序相反，最多只能選一個，答案是 1。

## 72. Edit Distance

**難度：Medium｜觀念：編輯操作 DP**
[英文原題](https://leetcode.com/problems/edit-distance/) · [官方中文](https://leetcode.cn/problems/edit-distance/)

### 中文題意與輸入輸出

把 word1 變成 word2，每次可插入、刪除或替換一個字元，求最少操作次數。字串是小寫英文，長度各為 0–500，可以是空字串。

### 白話思路與手算

一口氣考慮整個字串很難，先問較短的前綴要幾次操作。

**狀態：** `dp[i][j]` 表示 word1 前 i 個字元，轉成 word2 前 j 個字元的最少次數。**初始：** `dp[i][0]=i`（全刪）、`dp[0][j]=j`（全插）。**轉移：** 尾字元相同就不用多做，沿用左上 `dp[i−1][j−1]`；不同則以下三者取最小再加 1：上方 `dp[i−1][j]` 對應刪掉來源最後一字；左方 `dp[i][j−1]` 對應補上目標最後一字；左上 `dp[i−1][j−1]` 對應替換。**方向：** 左上到右下，依賴先算完。

原創例 `word1="cat",word2="cut"`：

```text
       空 c u t
空      0 1 2 3
c       1 0 1 2
a       2 1 1 2
t       3 2 2 1
```

最後一格是 1：把 a 改成 u。看格子 `[2][2]`：`"ca"→"cu"` 只要替換，因此由左上 0 加 1 得到。

### Kotlin 解答

```kotlin
class Solution {
    fun minDistance(word1: String, word2: String): Int {
        val m = word1.length
        val n = word2.length
        val dp = Array(m + 1) { IntArray(n + 1) }
        for (i in 0..m) dp[i][0] = i
        for (j in 0..n) dp[0][j] = j
        for (i in 1..m) {
            for (j in 1..n) {
                dp[i][j] = if (word1[i - 1] == word2[j - 1]) {
                    dp[i - 1][j - 1]
                } else {
                    1 + minOf(dp[i - 1][j], dp[i][j - 1], dp[i - 1][j - 1])
                }
            }
        }
        return dp[m][n]
    }
}
```

**複雜度：** m、n 是字串長度；時間 `O(mn)`，空間 `O((m+1)(n+1))`，非空情況慣寫 `O(mn)`。

**常見錯誤：** 第一排和第一欄全部留 0；把兩字元交換算一步，題目並沒有交換操作；把索引與前綴長度混淆。

**理解自測：** `""` 變成 `"go"` 需要幾次？
**答案：** 2 次，分別插入 g 和 o。

## 435. Non-overlapping Intervals

**難度：Medium｜觀念：貪心、最早結束**
[英文原題](https://leetcode.com/problems/non-overlapping-intervals/) · [官方中文](https://leetcode.cn/problems/non-overlapping-intervals/)

### 中文題意與輸入輸出

輸入多個 `[start,end]`，移除最少幾個才能使剩下的區間互不重疊？端點接觸不算重疊，例如一個剛好結束時另一個開始可以保留。區間數 1–100,000，端點在 -50,000 到 50,000，start 嚴格小於 end。

### 白話思路與手算

「刪最少」等同「留最多」。先按結束時間排序，能選時就選最早結束的，替後面留下最多空間。

為何安全？假設某個最佳安排的第一段比我們選的結束更晚，把它換成最早結束的這段，後面原本能接上的區間仍能接上，數量不會變少。對後面重複同樣道理即可。

原創例 `[[1,4],[2,3],[3,5],[6,8]]`：

```text
按end排序：[2,3] [1,4] [3,5] [6,8]
留[2,3] → [1,4]起點1<3，刪掉
留[3,5] → 端點3接觸可以
留[6,8] → 共留3段，刪1段
```

### Kotlin 解答

```kotlin
class Solution {
    fun eraseOverlapIntervals(intervals: Array<IntArray>): Int {
        val sorted = intervals.sortedBy { it[1] }
        var end = Int.MIN_VALUE
        var kept = 0
        for (interval in sorted) {
            if (interval[0] >= end) {
                kept++
                end = interval[1]
            }
        }
        return intervals.size - kept
    }
}
```

**複雜度：** n 是區間數；時間 `O(n log n)`，額外空間 `O(n)`，本程式使用排序副本以保留輸入。

**常見錯誤：** 先挑最早開始，可能被一個超長區間佔住；接觸端點時用 `>`，錯刪本來可共存的區間。

**理解自測：** `[1,10]` 與 `[2,3]` 只能留一個，優先留哪個？
**答案：** `[2,3]`，它較早結束，後面選擇只會更多。

## 136. Single Number

**難度：Easy｜觀念：XOR 抵消**
[英文原題](https://leetcode.com/problems/single-number/) · [官方中文](https://leetcode.cn/problems/single-number/)

### 中文題意與輸入輸出

非空整數陣列中，只有一個值出現一次，其他值恰好各出現兩次。找出單獨的值。長度最多 30,000，值介於 -30,000 到 30,000。要求線性時間、常數額外空間。

### 白話思路與手算

用 Map 數次數很直覺，但需要額外空間。`xor` 是逐位「不同才得到 1」：同一個值 XOR 自己得到 0；任何值 XOR 0 等於自己；順序可以調換。所以把所有值 XOR 在一起，成雙的就消掉，剩下單獨那個。

原創例 `[7,3,7]`：

```text
  111 (7)
xor011 (3)
= 100 (4)
xor111 (7)
= 011 (3) → 答案3
```

中途的 4 不需要有題目意義，它只是還沒抵消完的位元狀態。負數也能用，因為相同的固定寬度位元一樣會抵消。

### Kotlin 解答

```kotlin
class Solution {
    fun singleNumber(nums: IntArray): Int {
        var answer = 0
        for (num in nums) answer = answer xor num
        return answer
    }
}
```

**複雜度：** n 是元素數；時間 `O(n)`，額外空間 `O(1)`。

**常見錯誤：** 在「其他值出現三次」的變形題直接照抄；把 XOR 寫成 OR，OR 沒有成對抵消的性質。

**理解自測：** 如果陣列只有 `[-8]` 能用嗎？
**答案：** 可以，0 XOR -8 就是 -8。

## 338. Counting Bits

**難度：Easy｜觀念：位元、DP**
[英文原題](https://leetcode.com/problems/counting-bits/) · [官方中文](https://leetcode.cn/problems/counting-bits/)

### 中文題意與輸入輸出

輸入 n，輸出長度 n+1 的陣列；第 i 格放 i 的二進位有幾個 1。`0<=n<=100000`。本解不用內建計算 1 位元數的函式，並達到線性時間。

### 白話思路與手算

把數字右移一位，就像把二進位最右邊的字拿掉。拿掉的字若是 1，再加回一個就好。Kotlin 的 `shr` 是右移，`and 1` 用來看最右一位。

**狀態：** `answer[i]` 是 i 的 1 位元數。**初始：** `answer[0]=0`。**轉移：** `answer[i]=answer[i shr 1]+(i and 1)`。**方向：** 1 到 n，因為 i/2 一定比 i 小，已經算完。

原創例 `n=4`：

```text
0=000 → 0
1=001 → answer[0]+1=1
2=010 → answer[1]+0=1
3=011 → answer[1]+1=2
4=100 → answer[2]+0=1
輸出 [0,1,1,2,1]
```

### Kotlin 解答

```kotlin
class Solution {
    fun countBits(n: Int): IntArray {
        val answer = IntArray(n + 1)
        for (i in 1..n) {
            answer[i] = answer[i shr 1] + (i and 1)
        }
        return answer
    }
}
```

**複雜度：** 時間 `O(n)`，輸出空間 `O(n)`，除此之外額外空間 `O(1)`。

**常見錯誤：** 配置 n 格而非 n+1；把位元 `and` 和布林 `&&` 混淆。

**理解自測：** 10 的二進位是 1010，不重新數全部位元，如何算？
**答案：** `answer[5]+0`；5 是 101，有兩個 1，所以答案 2。

---

<a id="tracker"></a>

# 答案優先熟練追蹤表

[回8週計畫](#plan)｜[150題索引](#index)

這張表只列有本地詳解的87題。先記閱讀與走讀，不把第一遍獨立解出當門檻。各欄填日期；「可比較」填能指出的相似題差異。日+1/+3/+7/+14可在複習欄記錄，卡住時直接回看原理。

| 優先 | 題目與詳解 | 已讀答案 | 可走讀 | 可重寫 | 可比較／差異 | 複習日期 |
|---|---|---|---|---|---|---|
| P0 核心 | [242. Valid Anagram](#242-valid-anagram) |  |  |  |  |  |
| P0 核心 | [1. Two Sum](#1-two-sum) |  |  |  |  |  |
| P0 核心 | [49. Group Anagrams](#49-group-anagrams) |  |  |  |  |  |
| P0 核心 | [125. Valid Palindrome](#125-valid-palindrome) |  |  |  |  |  |
| P0 核心 | [167. Two Sum II - Input Array Is Sorted](#167-two-sum-ii---input-array-is-sorted) |  |  |  |  |  |
| P0 核心 | [3. Longest Substring Without Repeating Characters](#3-longest-substring-without-repeating-characters) |  |  |  |  |  |
| P0 核心 | [20. Valid Parentheses](#20-valid-parentheses) |  |  |  |  |  |
| P0 核心 | [704. Binary Search](#704-binary-search) |  |  |  |  |  |
| P0 核心 | [33. Search in Rotated Sorted Array](#33-search-in-rotated-sorted-array) |  |  |  |  |  |
| P0 核心 | [206. Reverse Linked List](#206-reverse-linked-list) |  |  |  |  |  |
| P0 核心 | [21. Merge Two Sorted Lists](#21-merge-two-sorted-lists) |  |  |  |  |  |
| P0 核心 | [19. Remove Nth Node From End of List](#19-remove-nth-node-from-end-of-list) |  |  |  |  |  |
| P0 核心 | [104. Maximum Depth of Binary Tree](#104-maximum-depth-of-binary-tree) |  |  |  |  |  |
| P0 核心 | [102. Binary Tree Level Order Traversal](#102-binary-tree-level-order-traversal) |  |  |  |  |  |
| P0 核心 | [98. Validate Binary Search Tree](#98-validate-binary-search-tree) |  |  |  |  |  |
| P0 核心 | [78. Subsets](#78-subsets) |  |  |  |  |  |
| P0 核心 | [39. Combination Sum](#39-combination-sum) |  |  |  |  |  |
| P0 核心 | [200. Number of Islands](#200-number-of-islands) |  |  |  |  |  |
| P0 核心 | [994. Rotting Oranges](#994-rotting-oranges) |  |  |  |  |  |
| P0 核心 | [207. Course Schedule](#207-course-schedule) |  |  |  |  |  |
| P0 核心 | [70. Climbing Stairs](#70-climbing-stairs) |  |  |  |  |  |
| P0 核心 | [746. Min Cost Climbing Stairs](#746-min-cost-climbing-stairs) |  |  |  |  |  |
| P0 核心 | [198. House Robber](#198-house-robber) |  |  |  |  |  |
| P0 核心 | [322. Coin Change](#322-coin-change) |  |  |  |  |  |
| P0 核心 | [300. Longest Increasing Subsequence](#300-longest-increasing-subsequence) |  |  |  |  |  |
| P0 核心 | [53. Maximum Subarray](#53-maximum-subarray) |  |  |  |  |  |
| P0 核心 | [56. Merge Intervals](#56-merge-intervals) |  |  |  |  |  |
| P0 核心 | [73. Set Matrix Zeroes](#73-set-matrix-zeroes) |  |  |  |  |  |
| P1 補強 | [347. Top K Frequent Elements](#347-top-k-frequent-elements) |  |  |  |  |  |
| P1 補強 | [238. Product of Array Except Self](#238-product-of-array-except-self) |  |  |  |  |  |
| P1 補強 | [128. Longest Consecutive Sequence](#128-longest-consecutive-sequence) |  |  |  |  |  |
| P1 補強 | [15. 3Sum](#15-3sum) |  |  |  |  |  |
| P1 補強 | [11. Container With Most Water](#11-container-with-most-water) |  |  |  |  |  |
| P1 補強 | [121. Best Time to Buy and Sell Stock](#121-best-time-to-buy-and-sell-stock) |  |  |  |  |  |
| P1 補強 | [424. Longest Repeating Character Replacement](#424-longest-repeating-character-replacement) |  |  |  |  |  |
| P1 補強 | [567. Permutation in String](#567-permutation-in-string) |  |  |  |  |  |
| P1 補強 | [155. Min Stack](#155-min-stack) |  |  |  |  |  |
| P1 補強 | [739. Daily Temperatures](#739-daily-temperatures) |  |  |  |  |  |
| P1 補強 | [74. Search a 2D Matrix](#74-search-a-2d-matrix) |  |  |  |  |  |
| P1 補強 | [875. Koko Eating Bananas](#875-koko-eating-bananas) |  |  |  |  |  |
| P1 補強 | [153. Find Minimum in Rotated Sorted Array](#153-find-minimum-in-rotated-sorted-array) |  |  |  |  |  |
| P1 補強 | [981. Time Based Key-Value Store](#981-time-based-key-value-store) |  |  |  |  |  |
| P1 補強 | [143. Reorder List](#143-reorder-list) |  |  |  |  |  |
| P1 補強 | [146. LRU Cache](#146-lru-cache) |  |  |  |  |  |
| P1 補強 | [226. Invert Binary Tree](#226-invert-binary-tree) |  |  |  |  |  |
| P1 補強 | [199. Binary Tree Right Side View](#199-binary-tree-right-side-view) |  |  |  |  |  |
| P1 補強 | [1448. Count Good Nodes in Binary Tree](#1448-count-good-nodes-in-binary-tree) |  |  |  |  |  |
| P1 補強 | [105. Construct Binary Tree from Preorder and Inorder Traversal](#105-construct-binary-tree-from-preorder-and-inorder-traversal) |  |  |  |  |  |
| P1 補強 | [215. Kth Largest Element in an Array](#215-kth-largest-element-in-an-array) |  |  |  |  |  |
| P1 補強 | [46. Permutations](#46-permutations) |  |  |  |  |  |
| P1 補強 | [79. Word Search](#79-word-search) |  |  |  |  |  |
| P1 補強 | [17. Letter Combinations of a Phone Number](#17-letter-combinations-of-a-phone-number) |  |  |  |  |  |
| P1 補強 | [208. Implement Trie (Prefix Tree)](#208-implement-trie-prefix-tree) |  |  |  |  |  |
| P1 補強 | [211. Design Add and Search Words Data Structure](#211-design-add-and-search-words-data-structure) |  |  |  |  |  |
| P1 補強 | [133. Clone Graph](#133-clone-graph) |  |  |  |  |  |
| P1 補強 | [210. Course Schedule II](#210-course-schedule-ii) |  |  |  |  |  |
| P1 補強 | [684. Redundant Connection](#684-redundant-connection) |  |  |  |  |  |
| P1 補強 | [743. Network Delay Time](#743-network-delay-time) |  |  |  |  |  |
| P1 補強 | [1584. Min Cost to Connect All Points](#1584-min-cost-to-connect-all-points) |  |  |  |  |  |
| P1 補強 | [5. Longest Palindromic Substring](#5-longest-palindromic-substring) |  |  |  |  |  |
| P1 補強 | [416. Partition Equal Subset Sum](#416-partition-equal-subset-sum) |  |  |  |  |  |
| P1 補強 | [62. Unique Paths](#62-unique-paths) |  |  |  |  |  |
| P1 補強 | [1143. Longest Common Subsequence](#1143-longest-common-subsequence) |  |  |  |  |  |
| P1 補強 | [309. Best Time to Buy and Sell Stock with Cooldown](#309-best-time-to-buy-and-sell-stock-with-cooldown) |  |  |  |  |  |
| P1 補強 | [518. Coin Change II](#518-coin-change-ii) |  |  |  |  |  |
| P1 補強 | [494. Target Sum](#494-target-sum) |  |  |  |  |  |
| P1 補強 | [72. Edit Distance](#72-edit-distance) |  |  |  |  |  |
| P1 補強 | [55. Jump Game](#55-jump-game) |  |  |  |  |  |
| P1 補強 | [45. Jump Game II](#45-jump-game-ii) |  |  |  |  |  |
| P1 補強 | [57. Insert Interval](#57-insert-interval) |  |  |  |  |  |
| P1 補強 | [435. Non-overlapping Intervals](#435-non-overlapping-intervals) |  |  |  |  |  |
| P1 補強 | [48. Rotate Image](#48-rotate-image) |  |  |  |  |  |
| P1 補強 | [54. Spiral Matrix](#54-spiral-matrix) |  |  |  |  |  |
| P1 補強 | [136. Single Number](#136-single-number) |  |  |  |  |  |
| P1 補強 | [338. Counting Bits](#338-counting-bits) |  |  |  |  |  |
| P2 選讀 | [42. Trapping Rain Water](#42-trapping-rain-water) |  |  |  |  |  |
| P2 選讀 | [76. Minimum Window Substring](#76-minimum-window-substring) |  |  |  |  |  |
| P2 選讀 | [239. Sliding Window Maximum](#239-sliding-window-maximum) |  |  |  |  |  |
| P2 選讀 | [84. Largest Rectangle in Histogram](#84-largest-rectangle-in-histogram) |  |  |  |  |  |
| P2 選讀 | [124. Binary Tree Maximum Path Sum](#124-binary-tree-maximum-path-sum) |  |  |  |  |  |
| P2 選讀 | [297. Serialize and Deserialize Binary Tree](#297-serialize-and-deserialize-binary-tree) |  |  |  |  |  |
| P2 選讀 | [621. Task Scheduler](#621-task-scheduler) |  |  |  |  |  |
| P2 選讀 | [295. Find Median from Data Stream](#295-find-median-from-data-stream) |  |  |  |  |  |
| P2 選讀 | [332. Reconstruct Itinerary](#332-reconstruct-itinerary) |  |  |  |  |  |
| P2 選讀 | [115. Distinct Subsequences](#115-distinct-subsequences) |  |  |  |  |  |
| P2 選讀 | [190. Reverse Bits](#190-reverse-bits) |  |  |  |  |  |
| P2 選讀 | [371. Sum of Two Integers](#371-sum-of-two-integers) |  |  |  |  |  |


---

<a id="sources"></a>

# NeetCode 150 來源查證

查證日期：2026-09-21。這份文件提供題單來源、分類差異與教材定位；不是台積電題庫或公司出題機率表。

## 查證結論

本次取得完整 **150 個唯一 LeetCode 題號、18 個分類、28 Easy／101 Medium／21 Hard**，符合 [NeetCode 150 官方頁](https://neetcode.io/practice/practice/neetcode150) 的總數與分類數。

官方頁說明 NeetCode 150 是 **Blind 75 加上另外 75 題**，適合已有基本資料結構與演算法知識的人。注意：**Blind 75 與 LeetCode 75 是兩份不同的題單**，不是名稱寫法不同而已。零基礎學習者仍需先補基礎，不能把 150 題當成直接照順序就一定學得會的課程。[官方定位](https://neetcode.io/practice/practice/neetcode150)

## 可重現資料來源

1. **收錄清單與分類：NeetCode 現行網站公開資料**。從官方頁的 HTML 取得 script URL，再讀取 [網站公開 JavaScript bundle](https://neetcode.io/main.f39af0c52a4e9fb5.js) 中 `neetcode150:!0` 的題目資料物件。未執行下載的程式碼，只解析字串。共取得 150 筆。網站部署後 bundle URL 可能更新，舊檔案未必永久保存。
2. **獨立核對收錄：NeetCode 官方解答儲存庫**。[固定 commit 的 .problemSiteData.json](https://github.com/neetcode-gh/leetcode/blob/3186ede2ea4c4788e87be4b509bf2b66d5eba0e9/.problemSiteData.json) 以 `neetcode150 == true` 篩選出相同 150 個題目代碼。這份 commit 連結可供後續重現。
3. **LeetCode 正式題名、題號、slug、難度：官方 API**。[LeetCode 公開題庫 metadata](https://leetcode.com/api/problems/all/) 的 `stat_status_pairs` 提供題目資料，以 `stat.frontend_question_id` 對應題號。這不是內部 `question_id`。150 題的 slug 與難度全部和 NeetCode 資料吻合；英文標題採用 LeetCode 正式拼字與標點。
4. 本地保留 `sources/manifest.json`（來源與 bundle SHA-256）、`sources/neetcode150-site-metadata.json`（所選 metadata）、`sources/leetcode-api-metadata.json`（LeetCode metadata），以及 `sources/comparison.json`（與原 LeetCode 75 題單差集）。這些是查證快照，不是整份原題或解答的複製。

**分類差異已處理：** GitHub repo 將 #22 Generate Parentheses 放在 Stack，造成 Stack 7 題、Backtracking 9 題；現行網站資料將它放在 Backtracking，為 Stack 6 題、Backtracking 10 題。本教材採現行網站分類。其他分類題數相同。現行網站與 repo 的 150 個題號集合完全一致，並未發現題目增刪。

JSON 陣列按現行網站題目資料順序輸出，各筆只含 `id,title,slug,category,difficulty`；教材可以依先備知識另排學習順序。

## 和現有 LeetCode 75 的關係

以同目錄上一層的 `problems.json` 題號作集合比對：

| 比較項目 | 題數 |
| --- | ---: |
| 兩份都有 | 20 |
| NeetCode 150 新增、原 LeetCode 75 沒有 | 130 |
| 只在原 LeetCode 75 | 55 |
| 兩份合計去重 | 205 |

重複的 20 題可以當作複習，不必當成 20 題全新內容。題號：11、17、62、72、104、136、198、199、206、208、215、238、338、435、739、746、875、994、1143、1448。此統計是本次檔案比對結果，不是官方宣傳數字。

## 為何把這些視為經典面試題型

這裡的「經典」是指廣泛的面試準備分類，不能解讀為某家公司必考或某題出現頻率很高。

- [HackerRank Interview Preparation Kit](https://www.hackerrank.com/interview/interview-preparation-kit) 用陣列、Hashmap、排序、字串、貪心、搜尋、DP、Stack／Queue、圖、樹、Linked List、遞迴／回溯等主題組織練習。這支持先學題型與基礎操作的安排。
- [LeetCode Top Interview 150](https://leetcode.com/studyplan/top-interview-150/) 官方定位為 150 題經典面試練習，涵蓋多類面試主題，建議三個月以上準備。它與 NeetCode 150 是不同題單，不能混用總數或當成同一份課綱。
- [NeetCode Roadmap](https://neetcode.io/roadmap) 可作為後續查看學習路線的官方入口；本次 web 文字擷取未取得其互動圖的節點內容，因此這份文件**沒有**宣稱逐條核對 roadmap 的依賴順序。實際的 18 分類依據是上面成功取得的官方 Practice 頁與其公開資料。

HackerRank 頁面在查證日顯示以下平台概括比例：陣列 70%、Dictionaries／Hashmaps 40%、排序 40%、字串 40%、貪心 31%、搜尋 30%、DP 27%、Stack／Queue 17%、圖 15%、樹 12%、Linked List 8%、遞迴／回溯 5%。**這不是台積電資料，也不是你這場考試的機率。**該頁未交代這些數字的統計期間、樣本與方法，故只能當作平台展示的背景資訊，不能做精確預測；2026-09-21 是讀取日期，不是統計資料的更新日期。[比例原頁](https://www.hackerrank.com/interview/interview-preparation-kit)

## 18 分類

| 分類 | 題數 |
| --- | ---: |
| Arrays & Hashing | 9 |
| Two Pointers | 5 |
| Sliding Window | 6 |
| Stack | 6 |
| Binary Search | 7 |
| Linked List | 11 |
| Trees | 15 |
| Heap / Priority Queue | 7 |
| Backtracking | 10 |
| Tries | 3 |
| Graphs | 13 |
| Advanced Graphs | 6 |
| 1-D Dynamic Programming | 12 |
| 2-D Dynamic Programming | 11 |
| Greedy | 8 |
| Intervals | 6 |
| Math & Geometry | 8 |
| Bit Manipulation | 7 |

## 完整 150 題索引

難度為查證日官方 metadata。原題連結若需要 LeetCode Premium，可改到 NeetCode 官網搜尋該題對應練習；NeetCode 自有題名或限制可能不同，以實際開啟頁面為準。

| 序號 | 題號 | LeetCode 正式英文題名／原題 | 難度 | 分類 | 與原 75 比較 | LeetCode 存取 |
| --- | --- | --- | --- | --- | --- | --- |
| 1 | 217 | [Contains Duplicate](https://leetcode.com/problems/contains-duplicate/) | Easy | Arrays & Hashing | 新增 | 公開 |
| 2 | 242 | [Valid Anagram](https://leetcode.com/problems/valid-anagram/) | Easy | Arrays & Hashing | 新增 | 公開 |
| 3 | 1 | [Two Sum](https://leetcode.com/problems/two-sum/) | Easy | Arrays & Hashing | 新增 | 公開 |
| 4 | 49 | [Group Anagrams](https://leetcode.com/problems/group-anagrams/) | Medium | Arrays & Hashing | 新增 | 公開 |
| 5 | 347 | [Top K Frequent Elements](https://leetcode.com/problems/top-k-frequent-elements/) | Medium | Arrays & Hashing | 新增 | 公開 |
| 6 | 271 | [Encode and Decode Strings](https://leetcode.com/problems/encode-and-decode-strings/) | Medium | Arrays & Hashing | 新增 | Premium |
| 7 | 238 | [Product of Array Except Self](https://leetcode.com/problems/product-of-array-except-self/) | Medium | Arrays & Hashing | 複習 | 公開 |
| 8 | 36 | [Valid Sudoku](https://leetcode.com/problems/valid-sudoku/) | Medium | Arrays & Hashing | 新增 | 公開 |
| 9 | 128 | [Longest Consecutive Sequence](https://leetcode.com/problems/longest-consecutive-sequence/) | Medium | Arrays & Hashing | 新增 | 公開 |
| 10 | 125 | [Valid Palindrome](https://leetcode.com/problems/valid-palindrome/) | Easy | Two Pointers | 新增 | 公開 |
| 11 | 167 | [Two Sum II - Input Array Is Sorted](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/) | Medium | Two Pointers | 新增 | 公開 |
| 12 | 15 | [3Sum](https://leetcode.com/problems/3sum/) | Medium | Two Pointers | 新增 | 公開 |
| 13 | 11 | [Container With Most Water](https://leetcode.com/problems/container-with-most-water/) | Medium | Two Pointers | 複習 | 公開 |
| 14 | 42 | [Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/) | Hard | Two Pointers | 新增 | 公開 |
| 15 | 121 | [Best Time to Buy and Sell Stock](https://leetcode.com/problems/best-time-to-buy-and-sell-stock/) | Easy | Sliding Window | 新增 | 公開 |
| 16 | 3 | [Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | Medium | Sliding Window | 新增 | 公開 |
| 17 | 424 | [Longest Repeating Character Replacement](https://leetcode.com/problems/longest-repeating-character-replacement/) | Medium | Sliding Window | 新增 | 公開 |
| 18 | 567 | [Permutation in String](https://leetcode.com/problems/permutation-in-string/) | Medium | Sliding Window | 新增 | 公開 |
| 19 | 76 | [Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring/) | Hard | Sliding Window | 新增 | 公開 |
| 20 | 239 | [Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/) | Hard | Sliding Window | 新增 | 公開 |
| 21 | 20 | [Valid Parentheses](https://leetcode.com/problems/valid-parentheses/) | Easy | Stack | 新增 | 公開 |
| 22 | 155 | [Min Stack](https://leetcode.com/problems/min-stack/) | Medium | Stack | 新增 | 公開 |
| 23 | 150 | [Evaluate Reverse Polish Notation](https://leetcode.com/problems/evaluate-reverse-polish-notation/) | Medium | Stack | 新增 | 公開 |
| 24 | 739 | [Daily Temperatures](https://leetcode.com/problems/daily-temperatures/) | Medium | Stack | 複習 | 公開 |
| 25 | 853 | [Car Fleet](https://leetcode.com/problems/car-fleet/) | Medium | Stack | 新增 | 公開 |
| 26 | 84 | [Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/) | Hard | Stack | 新增 | 公開 |
| 27 | 704 | [Binary Search](https://leetcode.com/problems/binary-search/) | Easy | Binary Search | 新增 | 公開 |
| 28 | 74 | [Search a 2D Matrix](https://leetcode.com/problems/search-a-2d-matrix/) | Medium | Binary Search | 新增 | 公開 |
| 29 | 875 | [Koko Eating Bananas](https://leetcode.com/problems/koko-eating-bananas/) | Medium | Binary Search | 複習 | 公開 |
| 30 | 153 | [Find Minimum in Rotated Sorted Array](https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/) | Medium | Binary Search | 新增 | 公開 |
| 31 | 33 | [Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/) | Medium | Binary Search | 新增 | 公開 |
| 32 | 981 | [Time Based Key-Value Store](https://leetcode.com/problems/time-based-key-value-store/) | Medium | Binary Search | 新增 | 公開 |
| 33 | 4 | [Median of Two Sorted Arrays](https://leetcode.com/problems/median-of-two-sorted-arrays/) | Hard | Binary Search | 新增 | 公開 |
| 34 | 206 | [Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/) | Easy | Linked List | 複習 | 公開 |
| 35 | 21 | [Merge Two Sorted Lists](https://leetcode.com/problems/merge-two-sorted-lists/) | Easy | Linked List | 新增 | 公開 |
| 36 | 141 | [Linked List Cycle](https://leetcode.com/problems/linked-list-cycle/) | Easy | Linked List | 新增 | 公開 |
| 37 | 143 | [Reorder List](https://leetcode.com/problems/reorder-list/) | Medium | Linked List | 新增 | 公開 |
| 38 | 19 | [Remove Nth Node From End of List](https://leetcode.com/problems/remove-nth-node-from-end-of-list/) | Medium | Linked List | 新增 | 公開 |
| 39 | 138 | [Copy List with Random Pointer](https://leetcode.com/problems/copy-list-with-random-pointer/) | Medium | Linked List | 新增 | 公開 |
| 40 | 2 | [Add Two Numbers](https://leetcode.com/problems/add-two-numbers/) | Medium | Linked List | 新增 | 公開 |
| 41 | 287 | [Find the Duplicate Number](https://leetcode.com/problems/find-the-duplicate-number/) | Medium | Linked List | 新增 | 公開 |
| 42 | 146 | [LRU Cache](https://leetcode.com/problems/lru-cache/) | Medium | Linked List | 新增 | 公開 |
| 43 | 23 | [Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/) | Hard | Linked List | 新增 | 公開 |
| 44 | 25 | [Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/) | Hard | Linked List | 新增 | 公開 |
| 45 | 226 | [Invert Binary Tree](https://leetcode.com/problems/invert-binary-tree/) | Easy | Trees | 新增 | 公開 |
| 46 | 104 | [Maximum Depth of Binary Tree](https://leetcode.com/problems/maximum-depth-of-binary-tree/) | Easy | Trees | 複習 | 公開 |
| 47 | 543 | [Diameter of Binary Tree](https://leetcode.com/problems/diameter-of-binary-tree/) | Easy | Trees | 新增 | 公開 |
| 48 | 110 | [Balanced Binary Tree](https://leetcode.com/problems/balanced-binary-tree/) | Easy | Trees | 新增 | 公開 |
| 49 | 100 | [Same Tree](https://leetcode.com/problems/same-tree/) | Easy | Trees | 新增 | 公開 |
| 50 | 572 | [Subtree of Another Tree](https://leetcode.com/problems/subtree-of-another-tree/) | Easy | Trees | 新增 | 公開 |
| 51 | 235 | [Lowest Common Ancestor of a Binary Search Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/) | Medium | Trees | 新增 | 公開 |
| 52 | 102 | [Binary Tree Level Order Traversal](https://leetcode.com/problems/binary-tree-level-order-traversal/) | Medium | Trees | 新增 | 公開 |
| 53 | 199 | [Binary Tree Right Side View](https://leetcode.com/problems/binary-tree-right-side-view/) | Medium | Trees | 複習 | 公開 |
| 54 | 1448 | [Count Good Nodes in Binary Tree](https://leetcode.com/problems/count-good-nodes-in-binary-tree/) | Medium | Trees | 複習 | 公開 |
| 55 | 98 | [Validate Binary Search Tree](https://leetcode.com/problems/validate-binary-search-tree/) | Medium | Trees | 新增 | 公開 |
| 56 | 230 | [Kth Smallest Element in a BST](https://leetcode.com/problems/kth-smallest-element-in-a-bst/) | Medium | Trees | 新增 | 公開 |
| 57 | 105 | [Construct Binary Tree from Preorder and Inorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/) | Medium | Trees | 新增 | 公開 |
| 58 | 124 | [Binary Tree Maximum Path Sum](https://leetcode.com/problems/binary-tree-maximum-path-sum/) | Hard | Trees | 新增 | 公開 |
| 59 | 297 | [Serialize and Deserialize Binary Tree](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/) | Hard | Trees | 新增 | 公開 |
| 60 | 703 | [Kth Largest Element in a Stream](https://leetcode.com/problems/kth-largest-element-in-a-stream/) | Easy | Heap / Priority Queue | 新增 | 公開 |
| 61 | 1046 | [Last Stone Weight](https://leetcode.com/problems/last-stone-weight/) | Easy | Heap / Priority Queue | 新增 | 公開 |
| 62 | 973 | [K Closest Points to Origin](https://leetcode.com/problems/k-closest-points-to-origin/) | Medium | Heap / Priority Queue | 新增 | 公開 |
| 63 | 215 | [Kth Largest Element in an Array](https://leetcode.com/problems/kth-largest-element-in-an-array/) | Medium | Heap / Priority Queue | 複習 | 公開 |
| 64 | 621 | [Task Scheduler](https://leetcode.com/problems/task-scheduler/) | Medium | Heap / Priority Queue | 新增 | 公開 |
| 65 | 355 | [Design Twitter](https://leetcode.com/problems/design-twitter/) | Medium | Heap / Priority Queue | 新增 | 公開 |
| 66 | 295 | [Find Median from Data Stream](https://leetcode.com/problems/find-median-from-data-stream/) | Hard | Heap / Priority Queue | 新增 | 公開 |
| 67 | 78 | [Subsets](https://leetcode.com/problems/subsets/) | Medium | Backtracking | 新增 | 公開 |
| 68 | 39 | [Combination Sum](https://leetcode.com/problems/combination-sum/) | Medium | Backtracking | 新增 | 公開 |
| 69 | 40 | [Combination Sum II](https://leetcode.com/problems/combination-sum-ii/) | Medium | Backtracking | 新增 | 公開 |
| 70 | 46 | [Permutations](https://leetcode.com/problems/permutations/) | Medium | Backtracking | 新增 | 公開 |
| 71 | 90 | [Subsets II](https://leetcode.com/problems/subsets-ii/) | Medium | Backtracking | 新增 | 公開 |
| 72 | 22 | [Generate Parentheses](https://leetcode.com/problems/generate-parentheses/) | Medium | Backtracking | 新增 | 公開 |
| 73 | 79 | [Word Search](https://leetcode.com/problems/word-search/) | Medium | Backtracking | 新增 | 公開 |
| 74 | 131 | [Palindrome Partitioning](https://leetcode.com/problems/palindrome-partitioning/) | Medium | Backtracking | 新增 | 公開 |
| 75 | 17 | [Letter Combinations of a Phone Number](https://leetcode.com/problems/letter-combinations-of-a-phone-number/) | Medium | Backtracking | 複習 | 公開 |
| 76 | 51 | [N-Queens](https://leetcode.com/problems/n-queens/) | Hard | Backtracking | 新增 | 公開 |
| 77 | 208 | [Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/) | Medium | Tries | 複習 | 公開 |
| 78 | 211 | [Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/) | Medium | Tries | 新增 | 公開 |
| 79 | 212 | [Word Search II](https://leetcode.com/problems/word-search-ii/) | Hard | Tries | 新增 | 公開 |
| 80 | 200 | [Number of Islands](https://leetcode.com/problems/number-of-islands/) | Medium | Graphs | 新增 | 公開 |
| 81 | 695 | [Max Area of Island](https://leetcode.com/problems/max-area-of-island/) | Medium | Graphs | 新增 | 公開 |
| 82 | 133 | [Clone Graph](https://leetcode.com/problems/clone-graph/) | Medium | Graphs | 新增 | 公開 |
| 83 | 286 | [Walls and Gates](https://leetcode.com/problems/walls-and-gates/) | Medium | Graphs | 新增 | Premium |
| 84 | 994 | [Rotting Oranges](https://leetcode.com/problems/rotting-oranges/) | Medium | Graphs | 複習 | 公開 |
| 85 | 417 | [Pacific Atlantic Water Flow](https://leetcode.com/problems/pacific-atlantic-water-flow/) | Medium | Graphs | 新增 | 公開 |
| 86 | 130 | [Surrounded Regions](https://leetcode.com/problems/surrounded-regions/) | Medium | Graphs | 新增 | 公開 |
| 87 | 207 | [Course Schedule](https://leetcode.com/problems/course-schedule/) | Medium | Graphs | 新增 | 公開 |
| 88 | 210 | [Course Schedule II](https://leetcode.com/problems/course-schedule-ii/) | Medium | Graphs | 新增 | 公開 |
| 89 | 261 | [Graph Valid Tree](https://leetcode.com/problems/graph-valid-tree/) | Medium | Graphs | 新增 | Premium |
| 90 | 323 | [Number of Connected Components in an Undirected Graph](https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/) | Medium | Graphs | 新增 | Premium |
| 91 | 684 | [Redundant Connection](https://leetcode.com/problems/redundant-connection/) | Medium | Graphs | 新增 | 公開 |
| 92 | 127 | [Word Ladder](https://leetcode.com/problems/word-ladder/) | Hard | Graphs | 新增 | 公開 |
| 93 | 743 | [Network Delay Time](https://leetcode.com/problems/network-delay-time/) | Medium | Advanced Graphs | 新增 | 公開 |
| 94 | 332 | [Reconstruct Itinerary](https://leetcode.com/problems/reconstruct-itinerary/) | Hard | Advanced Graphs | 新增 | 公開 |
| 95 | 1584 | [Min Cost to Connect All Points](https://leetcode.com/problems/min-cost-to-connect-all-points/) | Medium | Advanced Graphs | 新增 | 公開 |
| 96 | 778 | [Swim in Rising Water](https://leetcode.com/problems/swim-in-rising-water/) | Hard | Advanced Graphs | 新增 | 公開 |
| 97 | 269 | [Alien Dictionary](https://leetcode.com/problems/alien-dictionary/) | Hard | Advanced Graphs | 新增 | Premium |
| 98 | 787 | [Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/) | Medium | Advanced Graphs | 新增 | 公開 |
| 99 | 70 | [Climbing Stairs](https://leetcode.com/problems/climbing-stairs/) | Easy | 1-D Dynamic Programming | 新增 | 公開 |
| 100 | 746 | [Min Cost Climbing Stairs](https://leetcode.com/problems/min-cost-climbing-stairs/) | Easy | 1-D Dynamic Programming | 複習 | 公開 |
| 101 | 198 | [House Robber](https://leetcode.com/problems/house-robber/) | Medium | 1-D Dynamic Programming | 複習 | 公開 |
| 102 | 213 | [House Robber II](https://leetcode.com/problems/house-robber-ii/) | Medium | 1-D Dynamic Programming | 新增 | 公開 |
| 103 | 5 | [Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/) | Medium | 1-D Dynamic Programming | 新增 | 公開 |
| 104 | 647 | [Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/) | Medium | 1-D Dynamic Programming | 新增 | 公開 |
| 105 | 91 | [Decode Ways](https://leetcode.com/problems/decode-ways/) | Medium | 1-D Dynamic Programming | 新增 | 公開 |
| 106 | 322 | [Coin Change](https://leetcode.com/problems/coin-change/) | Medium | 1-D Dynamic Programming | 新增 | 公開 |
| 107 | 152 | [Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/) | Medium | 1-D Dynamic Programming | 新增 | 公開 |
| 108 | 139 | [Word Break](https://leetcode.com/problems/word-break/) | Medium | 1-D Dynamic Programming | 新增 | 公開 |
| 109 | 300 | [Longest Increasing Subsequence](https://leetcode.com/problems/longest-increasing-subsequence/) | Medium | 1-D Dynamic Programming | 新增 | 公開 |
| 110 | 416 | [Partition Equal Subset Sum](https://leetcode.com/problems/partition-equal-subset-sum/) | Medium | 1-D Dynamic Programming | 新增 | 公開 |
| 111 | 62 | [Unique Paths](https://leetcode.com/problems/unique-paths/) | Medium | 2-D Dynamic Programming | 複習 | 公開 |
| 112 | 1143 | [Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/) | Medium | 2-D Dynamic Programming | 複習 | 公開 |
| 113 | 309 | [Best Time to Buy and Sell Stock with Cooldown](https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/) | Medium | 2-D Dynamic Programming | 新增 | 公開 |
| 114 | 518 | [Coin Change II](https://leetcode.com/problems/coin-change-ii/) | Medium | 2-D Dynamic Programming | 新增 | 公開 |
| 115 | 494 | [Target Sum](https://leetcode.com/problems/target-sum/) | Medium | 2-D Dynamic Programming | 新增 | 公開 |
| 116 | 97 | [Interleaving String](https://leetcode.com/problems/interleaving-string/) | Medium | 2-D Dynamic Programming | 新增 | 公開 |
| 117 | 329 | [Longest Increasing Path in a Matrix](https://leetcode.com/problems/longest-increasing-path-in-a-matrix/) | Hard | 2-D Dynamic Programming | 新增 | 公開 |
| 118 | 115 | [Distinct Subsequences](https://leetcode.com/problems/distinct-subsequences/) | Hard | 2-D Dynamic Programming | 新增 | 公開 |
| 119 | 72 | [Edit Distance](https://leetcode.com/problems/edit-distance/) | Medium | 2-D Dynamic Programming | 複習 | 公開 |
| 120 | 312 | [Burst Balloons](https://leetcode.com/problems/burst-balloons/) | Hard | 2-D Dynamic Programming | 新增 | 公開 |
| 121 | 10 | [Regular Expression Matching](https://leetcode.com/problems/regular-expression-matching/) | Hard | 2-D Dynamic Programming | 新增 | 公開 |
| 122 | 53 | [Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) | Medium | Greedy | 新增 | 公開 |
| 123 | 55 | [Jump Game](https://leetcode.com/problems/jump-game/) | Medium | Greedy | 新增 | 公開 |
| 124 | 45 | [Jump Game II](https://leetcode.com/problems/jump-game-ii/) | Medium | Greedy | 新增 | 公開 |
| 125 | 134 | [Gas Station](https://leetcode.com/problems/gas-station/) | Medium | Greedy | 新增 | 公開 |
| 126 | 846 | [Hand of Straights](https://leetcode.com/problems/hand-of-straights/) | Medium | Greedy | 新增 | 公開 |
| 127 | 1899 | [Merge Triplets to Form Target Triplet](https://leetcode.com/problems/merge-triplets-to-form-target-triplet/) | Medium | Greedy | 新增 | 公開 |
| 128 | 763 | [Partition Labels](https://leetcode.com/problems/partition-labels/) | Medium | Greedy | 新增 | 公開 |
| 129 | 678 | [Valid Parenthesis String](https://leetcode.com/problems/valid-parenthesis-string/) | Medium | Greedy | 新增 | 公開 |
| 130 | 57 | [Insert Interval](https://leetcode.com/problems/insert-interval/) | Medium | Intervals | 新增 | 公開 |
| 131 | 56 | [Merge Intervals](https://leetcode.com/problems/merge-intervals/) | Medium | Intervals | 新增 | 公開 |
| 132 | 435 | [Non-overlapping Intervals](https://leetcode.com/problems/non-overlapping-intervals/) | Medium | Intervals | 複習 | 公開 |
| 133 | 252 | [Meeting Rooms](https://leetcode.com/problems/meeting-rooms/) | Easy | Intervals | 新增 | Premium |
| 134 | 253 | [Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/) | Medium | Intervals | 新增 | Premium |
| 135 | 1851 | [Minimum Interval to Include Each Query](https://leetcode.com/problems/minimum-interval-to-include-each-query/) | Hard | Intervals | 新增 | 公開 |
| 136 | 48 | [Rotate Image](https://leetcode.com/problems/rotate-image/) | Medium | Math & Geometry | 新增 | 公開 |
| 137 | 54 | [Spiral Matrix](https://leetcode.com/problems/spiral-matrix/) | Medium | Math & Geometry | 新增 | 公開 |
| 138 | 73 | [Set Matrix Zeroes](https://leetcode.com/problems/set-matrix-zeroes/) | Medium | Math & Geometry | 新增 | 公開 |
| 139 | 202 | [Happy Number](https://leetcode.com/problems/happy-number/) | Easy | Math & Geometry | 新增 | 公開 |
| 140 | 66 | [Plus One](https://leetcode.com/problems/plus-one/) | Easy | Math & Geometry | 新增 | 公開 |
| 141 | 50 | [Pow(x, n)](https://leetcode.com/problems/powx-n/) | Medium | Math & Geometry | 新增 | 公開 |
| 142 | 43 | [Multiply Strings](https://leetcode.com/problems/multiply-strings/) | Medium | Math & Geometry | 新增 | 公開 |
| 143 | 2013 | [Detect Squares](https://leetcode.com/problems/detect-squares/) | Medium | Math & Geometry | 新增 | 公開 |
| 144 | 136 | [Single Number](https://leetcode.com/problems/single-number/) | Easy | Bit Manipulation | 複習 | 公開 |
| 145 | 191 | [Number of 1 Bits](https://leetcode.com/problems/number-of-1-bits/) | Easy | Bit Manipulation | 新增 | 公開 |
| 146 | 338 | [Counting Bits](https://leetcode.com/problems/counting-bits/) | Easy | Bit Manipulation | 複習 | 公開 |
| 147 | 190 | [Reverse Bits](https://leetcode.com/problems/reverse-bits/) | Easy | Bit Manipulation | 新增 | 公開 |
| 148 | 268 | [Missing Number](https://leetcode.com/problems/missing-number/) | Easy | Bit Manipulation | 新增 | 公開 |
| 149 | 371 | [Sum of Two Integers](https://leetcode.com/problems/sum-of-two-integers/) | Medium | Bit Manipulation | 新增 | 公開 |
| 150 | 7 | [Reverse Integer](https://leetcode.com/problems/reverse-integer/) | Medium | Bit Manipulation | 新增 | 公開 |


---

<a id="validation"></a>

# NeetCode 精選補充教材驗證紀錄

日期：2026-09-21。範圍是本次新增67題，原LeetCode75的結果另見 [原驗證紀錄](../程式碼驗證紀錄.md)。

| 檢查 | 結果 |
|---|---|
| NeetCode150題單 | 150唯一題號、18分類，與現站及官方repo核對；細節見來源查證 |
| 新增詳解 | 67唯一題號，全部在NeetCode150內，與原75題無重複 |
| 每題程式 | 一個完整Kotlin區塊；每題必要imports獨立檢查 |
| 編譯 | 67題全部通過 |
| 原創與邊界測試 | 146個全部通過，每題至少2個 |
| 案例分布 | 第一冊46、第二冊47、第三冊53 |

## 實際做了什麼

從Markdown直接抽出Kotlin，只將同名Solution改成Solution_題號供合併驗證。設計題保留原類名；補上平台通常提供的ListNode、TreeNode與Clone Graph的Node，沒有修改題目邏輯來配合測試。

工具鏈：既有Kotlin compiler 2.2.21，language-version 1.9、api-version 1.9、JVM target 17；stdlib 2.0.21；Android Studio JBR 25。這是本機1.9語言／API相容模式，不是HackerRank原生Kotlin1.9.0容器。編譯器有原生存取與棄用警告，教材解答無編譯錯誤。

測試涵蓋原創手算例、空值與邊界、相等元素、負数、LRU讀取和更新後的淘汰次序、拓撲順序合法性、Clone Graph環與物件身分、Dijkstra過時候選、重複機票與死路拼接。另實測#105的3000節點單鏈、#124的30000節點單鏈、#297的10000節點序列化還原；這些採顯式容器避免極深函式呼叫。

#115、#518含「最終答案為0，但中間計數超出Long」案例；採BigInteger保持中間值安全。#190有一個標明額外延伸的奇數輸入案例，不將其當成現行官方偶數輸入限制內案例。#743的某些額外案例亦用來檢查實作對多重邊等延伸情況的行為。

## 證據與範圍

- [本次測試結果](checks/run.log)、[編譯紀錄](checks/compile.log)
- [第一冊案例](checks/first-tests.json)、[第二冊案例](checks/middle-tests.json)、[第三冊案例](checks/advanced-tests.json)
- [抽取與驗證工具](checks/verify.py)：依賴這台電腦已有的Gradle工具鏈快取，換電腦需調整路徑。

這些是本機驗證，未登入LeetCode／HackerRank提交，也未跑官方隱藏測資；不宣稱已取得平台Accepted。測試通過不取代每題適用條件、正確性推理與實際測驗的時間／記憶體限制。
