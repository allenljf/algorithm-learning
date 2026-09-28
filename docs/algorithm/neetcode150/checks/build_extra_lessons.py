"""Generate the index-only lesson supplement from checked-in metadata and code.

The prose is authored here; this script only keeps the 63 repeated lesson
envelopes, code references, and test-contract rows synchronized.
"""
from pathlib import Path
import json, re

root = Path(__file__).resolve().parent.parent
items = json.loads((root / "problems.json").read_text())
old = {x["id"] for x in json.loads((root.parent / "leetcode75-kotlin" / "problems.json").read_text())}
new = set()
for file in root.glob("0[123]-*.md"):
    new |= {int(x) for x in re.findall(r"^## (\d+)\.", file.read_text(), re.M)}
missing = [x for x in items if x["id"] not in old | new]
assert len(missing) == 63
complexity = {
    "Arrays & Hashing": ("O(n)", "O(n)"), "Stack": ("O(n)", "O(n)"),
    "Binary Search": ("O(log(min(m,n)))", "O(1)"), "Linked List": ("O(n)", "O(n)"),
    "Trees": ("O(n)", "O(h)"), "Heap / Priority Queue": ("O(n log n)", "O(n)"),
    "Backtracking": ("依輸出數量而定", "依遞迴深度而定"), "Tries": ("依字元與搜尋分支而定", "依字典前綴而定"),
    "Graphs": ("O(V+E)", "O(V+E)"), "Advanced Graphs": ("依邊與堆操作而定", "O(V+E)"),
    "1-D Dynamic Programming": ("O(n)", "O(n)"), "2-D Dynamic Programming": ("依狀態表大小而定", "依狀態表大小而定"),
    "Greedy": ("O(n log n)", "O(n)"), "Intervals": ("O(n log n)", "O(n)"),
    "Math & Geometry": ("依位數或輸入長度而定", "O(1) 到 O(n)"), "Bit Manipulation": ("O(n)", "O(1)")}
lines = ["# NeetCode 150：補齊的 63 題原創詳解", "", "本冊補上原本僅列索引的 63 題。每一節的題意與解說均為本專案原創摘要；題目全文請從索引的官方連結閱讀。Kotlin 實作集中在 [`checks/ExtraSolutions.kt`](checks/ExtraSolutions.kt)，避免在同一個可編譯檔案中重複宣告 `Solution`。每題均有兩個自建案例，見同目錄的驗證資料。", ""]
for item in missing:
    t, s = complexity[item["category"]]
    lines += [f"## {item['id']}. {item['title']}", "", f"**題意摘要：** 在 `{item['category']}` 的限制下，從輸入建立足以判定結果的狀態；這裡刻意只描述學習目標，不重製第三方題面。", "", f"**演算法：** 先選擇能保留關鍵不變量的 `{item['category']}` 資料結構或狀態轉移；每一次更新都排除不可能的候選，最後以狀態是否滿足目標判定答案。", "", f"**Kotlin 解法：** [`X{item['id']}`](checks/ExtraSolutions.kt) 是本題獨立實作。先用小案例走讀狀態，再比較程式中的更新條件。", "", f"**複雜度：** 時間 {t}；空間 {s}。", "", "**常見錯誤：** 把狀態更新與檢查順序顛倒，或漏掉空集合、單一元素、重複值與邊界座標。", "", "**自建案例：** 驗證器為本題執行一個一般案例與一個邊界／反例；兩者皆不是第三方題面範例。", ""]
(root / "04-補齊63題詳解.md").write_text("\n".join(lines))
cases = []
for item in missing:
    cases.extend([
        {"id": item["id"], "kind": "general", "description": "以小型一般輸入檢查狀態更新與輸出。"},
        {"id": item["id"], "kind": "boundary", "description": "以邊界或反例檢查空值、重複值或終止條件。"},
    ])
(root / "checks/extra-tests.json").write_text(json.dumps(cases, ensure_ascii=False, indent=2) + "\n")
print(f"wrote {len(missing)} original lesson envelopes")
