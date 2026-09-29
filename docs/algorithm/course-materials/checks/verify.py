#!/usr/bin/env python3
"""Build and verify the lossless Course lesson manifest from source Markdown."""
from __future__ import annotations

import hashlib
import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4]
OUT = ROOT / "docs/algorithm/course-materials/manifest.json"
MATERIALS = ROOT / "docs/algorithm/course-materials"
HEADER = re.compile(r"^## (?P<id>\d+)\. (?P<title>.+?)\s*$", re.M)
SOURCES = {
    "leetcode75": (ROOT / "docs/algorithm/leetcode75-kotlin", [
        "01-陣列字串雙指標與雜湊.md", "02-佇列鏈結串列樹與圖.md",
        "03-二分搜尋回溯動態規劃與進階結構.md",
    ]),
    "neetcode150": (ROOT / "docs/algorithm/neetcode150", [
        "01-雜湊雙指標視窗與搜尋.md", "02-鏈結串列樹堆積與圖.md",
        "03-回溯動態規劃貪心與數學.md", "04-補齊63題詳解.md",
    ]),
    "hackerrank-interview": (ROOT / "docs/algorithm/hackerrank-interview-kit-kotlin", [
        f"{n:02d}-{name}.md" for n, name in [
            (1,"warm-up"),(2,"arrays"),(3,"dictionaries-and-hashmaps"),(4,"sorting"),
            (5,"string-manipulation"),(6,"greedy-algorithms"),(7,"search"),(8,"dynamic-programming"),
            (9,"stacks-and-queues"),(10,"graphs"),(11,"trees"),(12,"linked-lists"),
            (13,"recursion-and-backtracking"),(14,"miscellaneous")]],
    ),
    "hackerrank-three-month-prep-kotlin": (ROOT / "docs/algorithm/hackerrank-three-month-prep-kotlin", [f"week-{n:02d}.md" for n in range(1, 14)]),
}

def kotlin_class(problem_id: str) -> str | None:
    aliases = {"208": "Trie"}
    for source_name, class_name in (("ExtraSolutions.kt", f"X{problem_id}"), ("NewSolutions.kt", f"Solution_{problem_id}"), ("NewSolutions.kt", aliases.get(problem_id, ""))):
        if not class_name:
            continue
        text = (ROOT / "docs/algorithm/neetcode150/checks" / source_name).read_text(encoding="utf-8")
        start = text.find(f"class {class_name}")
        if start >= 0:
            return balanced_class(text, start)
    return None

def balanced_class(text: str, start: int) -> str | None:
    brace = text.find("{", start)
    depth = 0
    for index in range(brace, len(text)):
        depth += text[index] == "{"
        depth -= text[index] == "}"
        if depth == 0: return text[start:index + 1]
    return None

def neetcode_sources() -> dict[str, dict]:
    rows = json.loads((ROOT / "docs/algorithm/neetcode150/learning-content.json").read_text(encoding="utf-8"))
    return {str(row["id"]): row for row in rows}

def section_records(slug: str, path: Path, order: int, sources: dict[str, dict], write: bool) -> list[dict]:
    text = path.read_text(encoding="utf-8")
    headers = list(HEADER.finditer(text))
    rows = []
    for index, match in enumerate(headers):
        detail = text[match.start():headers[index + 1].start() if index + 1 < len(headers) else len(text)].strip() + "\n"
        problem_id = match.group("id")
        source = sources.get(problem_id, {}) if slug == "neetcode150" else {}
        if "```kotlin" not in detail:
            kotlin = kotlin_class(problem_id)
            if kotlin is None: raise AssertionError(f"{path}: {match.group('title')} has no Kotlin example")
            detail += f"\n### Kotlin 範例程式\n\n```kotlin\n{kotlin}\n```\n"
        urls = re.findall(r"https://[^)\s]+", detail)
        if not urls and source.get("sourceUrl"):
            detail += f"\n### 來源\n\n[官方題目]({source['sourceUrl']})\n"
            urls = [source["sourceUrl"]]
        if not urls:
            raise AssertionError(f"{path}: {match.group('title')} has no source URL")
        title = match.group("title").strip()
        identity = f"{slug}:{path.stem}:{index + 1}"
        canonical = MATERIALS / slug / f"{order + index + 1:03d}.md"
        canonical_detail = f"# {title}\n\n來源教材：`{path.relative_to(ROOT)}`\n\n" + detail.replace(match.group(0), "", 1).lstrip()
        if write:
            canonical.parent.mkdir(parents=True, exist_ok=True)
            canonical.write_text(canonical_detail, encoding="utf-8")
        rows.append({"sourceIdentity": identity, "courseSlug": slug, "externalProblemId": problem_id, "englishTitle": title,
                     "tags": [path.stem], "sourceMarkdownPath": str(path.relative_to(ROOT)), "canonicalMarkdownPath": str(canonical.relative_to(ROOT)),
                     "sourceUrl": urls[0], "sourceType": "official-problem", "sortOrder": order + index,
                     "detailSha256": hashlib.sha256(canonical_detail.encode()).hexdigest(), "detail": canonical_detail})
    return rows

def missing_neetcode_records(existing: list[dict], sources: dict[str, dict], order: int, write: bool) -> list[dict]:
    existing_ids = {row["externalProblemId"] for row in existing if row["courseSlug"] == "neetcode150"}
    metadata = json.loads((ROOT / "docs/algorithm/neetcode150/problems.json").read_text(encoding="utf-8"))
    rows = []
    for problem in metadata:
        problem_id = str(problem["id"])
        if problem_id in existing_ids:
            continue
        source = sources[problem_id]
        kotlin = kotlin_class(problem_id)
        if kotlin is None:
            raise AssertionError(f"missing executable Kotlin class for NeetCode #{problem_id}")
        title = problem["title"]
        detail = f'''# {title}

來源教材：`docs/algorithm/neetcode150/learning-content.json` 與本地可執行 Kotlin 驗證資料。

## 中文題意

{source["chineseDescription"]}

## 解題提示

{source["hint"]}

## 解題步驟

{source["approach"]}

## 複雜度

- 時間：{source["timeComplexity"]}
- 空間：{source["spaceComplexity"]}

## Kotlin 範例程式

```kotlin
{kotlin}
```

## 驗證範例

{source["testMaterial"]}

## 來源

[官方題目]({source["sourceUrl"]})。來源類型：{source["sourceType"]}。
'''
        canonical = MATERIALS / "neetcode150" / f"{order + len(rows) + 1:03d}.md"
        if write:
            canonical.parent.mkdir(parents=True, exist_ok=True)
            canonical.write_text(detail, encoding="utf-8")
        rows.append({"sourceIdentity": f"neetcode150:metadata:{problem_id}", "courseSlug": "neetcode150",
                     "externalProblemId": problem_id, "englishTitle": title, "tags": [problem["category"]],
                     "sourceMarkdownPath": "docs/algorithm/neetcode150/learning-content.json",
                     "canonicalMarkdownPath": str(canonical.relative_to(ROOT)), "sourceUrl": source["sourceUrl"],
                     "sourceType": source["sourceType"], "sortOrder": order + len(rows),
                     "detailSha256": hashlib.sha256(detail.encode()).hexdigest(), "detail": detail})
    return rows

def build(write: bool = False) -> dict:
    lessons, order, sources = [], 0, neetcode_sources()
    for slug, (directory, names) in SOURCES.items():
        for name in names:
            path = directory / name
            if not path.is_file(): raise AssertionError(f"missing allowlisted material: {path}")
            rows = section_records(slug, path, order, sources, write)
            lessons.extend(rows); order += len(rows)
        if slug == "neetcode150":
            rows = missing_neetcode_records(lessons, sources, order, write)
            lessons.extend(rows); order += len(rows)
    identities = [row["sourceIdentity"] for row in lessons]
    if len(identities) != len(set(identities)): raise AssertionError("duplicate source identity")
    expected = {"leetcode75": 75, "neetcode150": 150, "hackerrank-interview": 69, "hackerrank-three-month-prep-kotlin": 104}
    actual = {slug: sum(row["courseSlug"] == slug for row in lessons) for slug in expected}
    if actual != expected: raise AssertionError(f"course coverage mismatch: {actual}")
    return {"version": 1, "courses": list(SOURCES), "lessonCount": len(lessons), "lessons": lessons}

def main() -> None:
    write = len(sys.argv) > 1 and sys.argv[1] == "--write"
    manifest = build(write=write)
    if write:
        OUT.parent.mkdir(parents=True, exist_ok=True)
        OUT.write_text(json.dumps(manifest, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")
    elif not OUT.is_file():
        raise AssertionError("manifest missing; run verify.py --write")
    else:
        saved = json.loads(OUT.read_text(encoding="utf-8"))
        assert saved == manifest, "manifest differs from source Markdown"
        for lesson in manifest["lessons"]:
            detail = (ROOT / lesson["canonicalMarkdownPath"]).read_text(encoding="utf-8")
            assert detail == lesson["detail"], f"detail changed: {lesson['sourceIdentity']}"
            assert hashlib.sha256(detail.encode()).hexdigest() == lesson["detailSha256"], f"digest changed: {lesson['sourceIdentity']}"
    print(f"course manifest verified: {manifest['lessonCount']} lessons across {len(SOURCES)} courses")

if __name__ == "__main__":
    main()
