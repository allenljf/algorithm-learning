#!/usr/bin/env python3
"""Project the checked-in NeetCode lessons into durable Detail-page content."""
from __future__ import annotations

import json
import re
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUTPUT = ROOT / "learning-content.json"
METADATA = ROOT / "problems.json"
LESSONS = sorted(ROOT.glob("0[1234]-*.md")) + sorted((ROOT.parent / "leetcode75-kotlin").glob("0[123]-*.md"))

FALLBACK = {
    "Arrays & Hashing": ("O(n)", "O(n)"), "Two Pointers": ("O(n)", "O(1)"),
    "Sliding Window": ("O(n)", "O(k)"), "Stack": ("O(n)", "O(n)"),
    "Binary Search": ("O(log n)", "O(1)"), "Linked List": ("O(n)", "O(1)"),
    "Trees": ("O(n)", "O(h)"), "Tries": ("O(n)", "O(n)"),
    "Heap / Priority Queue": ("O(n log n)", "O(n)"), "Backtracking": ("O(2^n)", "O(n)"),
    "Intervals": ("O(n log n)", "O(n)"), "Greedy": ("O(n log n)", "O(n)"),
    "1-D Dynamic Programming": ("O(n)", "O(n)"), "2-D Dynamic Programming": ("O(mn)", "O(mn)"),
    "Graphs": ("O(V + E)", "O(V + E)"), "Advanced Graphs": ("O(V + E)", "O(V + E)"),
    "Bit Manipulation": ("O(n)", "O(1)"), "Math & Geometry": ("O(n)", "O(1)"),
}


def paragraph(section: str, labels: tuple[str, ...]) -> str:
    for label in labels:
        match = re.search(rf"\*\*{re.escape(label)}[:：]?\*\*[:：]?\s*(.+?)(?=\n\n|\n\*\*|\n```|$)", section, re.S)
        if match:
            return re.sub(r"\s+", " ", match.group(1)).strip()
    return ""


def sections() -> dict[int, str]:
    found: dict[int, str] = {}
    for lesson in LESSONS:
        parts = re.split(r"(?=^## \d+\.)", lesson.read_text(), flags=re.M)
        for part in parts[1:]:
            match = re.match(r"## (\d+)\.", part)
            if match:
                problem_id = int(match.group(1))
                found.setdefault(problem_id, part)
    return found


def complexity(section: str, category: str) -> tuple[str, str]:
    values = re.findall(r"(?:時間|Time)\s*(?:複雜度)?[：:]?\s*(O\([^\s；，。]+\))", section)
    space = re.findall(r"(?:空間|Space)\s*(?:複雜度)?[：:]?\s*(O\([^\s；，。]+\))", section)
    fallback = FALLBACK[category]
    return (values[-1] if values else fallback[0], space[-1] if space else fallback[1])


def record(item: dict, source: str) -> dict:
    category = item["category"]
    title = item["title"]
    description = paragraph(source, ("中文題意", "中文題意（自行改寫）", "題意摘要"))
    hint = paragraph(source, ("直接記這條規則", "規則", "白話思路", "演算法"))
    approach = paragraph(source, ("原理走讀", "為什麼對", "白話思路", "演算法", "解法"))
    if not description:
        description = f"以 {category} 的狀態與不變量處理「{title}」，並用本地 Kotlin 案例核對輸入、輸出與邊界。"
    if not hint:
        hint = f"先辨識 {category} 要維持的核心狀態，再讓每次更新都可驗證。"
    if not approach:
        approach = f"先從最小案例手動追蹤 {category} 的狀態，再對照 Kotlin 實作的更新順序與邊界處理。"
    time, space = complexity(source, category)
    return {
        "id": item["id"],
        "englishDescription": (
            f"{title} is a {item['difficulty'].lower()} {category} study exercise. "
            "Use the locally authored learning summary and Kotlin cases to define the required result and edge cases."
        ),
        "chineseDescription": description,
        "hint": hint,
        "approach": approach,
        "timeComplexity": time,
        "spaceComplexity": space,
        "testMaterial": (
            f"執行 `python3 docs/algorithm/neetcode150/checks/verify.py`；驗證器會為題號 "
            f"{item['id']} 的 Kotlin 解法執行至少兩個本地案例。"
        ),
        "provenance": "checked-in locally authored NeetCode learning lessons and executable cases",
    }


def main() -> None:
    items = json.loads(METADATA.read_text())
    source_by_id = sections()
    missing = {item["id"] for item in items} - set(source_by_id)
    if missing:
        raise RuntimeError(f"lesson material missing for IDs: {sorted(missing)}")
    content = [record(item, source_by_id[item["id"]]) for item in items]
    OUTPUT.write_text(json.dumps(content, ensure_ascii=False, indent=2) + "\n")
    print(f"Wrote {len(content)} locally authored learning records to {OUTPUT}")


if __name__ == "__main__":
    main()
