#!/usr/bin/env python3
"""Idempotently import checked-in NeetCode 150 solutions through the REST API.

Credentials and bearer values stay in process memory and never enter output,
summaries, source control, or generated source files.
"""
from __future__ import annotations

import argparse
import json
import re
import sys
import urllib.error
import urllib.request
from collections import Counter
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BASE = "https://allenljf-algorithm.web.app/api/v1"
PROBLEMS = ROOT / "docs/algorithm/neetcode150/problems.json"
LEARNING_CONTENT = ROOT / "docs/algorithm/neetcode150/learning-content.json"
LESSON_FILES = sorted((ROOT / "docs/algorithm/neetcode150").glob("0[123]-*.md"))
REUSABLE_FILES = sorted((ROOT / "docs/algorithm/leetcode75-kotlin").glob("0[123]-*.md"))
EXTRA = ROOT / "docs/algorithm/neetcode150/checks/ExtraSolutions.kt"
SUMMARY = ROOT / "docs/algorithm/neetcode150/import-summary.json"
REUSED_IDS = {11, 17, 62, 72, 104, 136, 198, 199, 206, 208, 215, 238, 338,
              435, 739, 746, 875, 994, 1143, 1448}

COMMON_IMPORTS = "import java.util.*\nimport kotlin.math.*\n"
TYPE_STUBS = """
class ListNode(var `val`: Int) { var next: ListNode? = null }
class TreeNode(var `val`: Int) { var left: TreeNode? = null; var right: TreeNode? = null }
class Node(var `val`: Int) { var neighbors: ArrayList<Node?> = ArrayList() }
"""


def load_env():
    values = {}
    for raw in (ROOT / "infra/env/.env").read_text().splitlines():
        if "=" in raw and not raw.lstrip().startswith("#"):
            key, value = raw.split("=", 1)
            values[key.strip()] = value.strip().strip('"').strip("'")
    required = ["ALGORITHM_LEARNING_IMPORT_EMAIL", "ALGORITHM_LEARNING_IMPORT_PASSWORD"]
    missing = [key for key in required if not values.get(key)]
    if missing:
        raise RuntimeError("missing import credential variable(s): " + ", ".join(missing))
    return values


class Client:
    def __init__(self):
        self.token = None

    def login(self):
        env = load_env()
        self.token = None
        body = {
            "email": env["ALGORITHM_LEARNING_IMPORT_EMAIL"],
            "password": env["ALGORITHM_LEARNING_IMPORT_PASSWORD"],
        }
        response = self.raw("POST", "/auth/login", body, auth=False)
        self.token = response["accessToken"]

    def raw(self, method, path, body=None, auth=True):
        headers = {"Content-Type": "application/json"}
        if auth:
            headers["Authorization"] = "Bearer " + self.token
        request = urllib.request.Request(
            BASE + path,
            None if body is None else json.dumps(body, ensure_ascii=False).encode(),
            headers,
            method,
        )
        with urllib.request.urlopen(request, timeout=30) as response:
            text = response.read().decode()
            return json.loads(text) if text else None

    def request(self, method, path, body=None):
        try:
            return self.raw(method, path, body)
        except urllib.error.HTTPError as error:
            if error.code not in (401, 403):
                raise RuntimeError(f"HTTP {error.code} {method} {path}") from None
            self.login()
            try:
                return self.raw(method, path, body)
            except urllib.error.HTTPError as retry:
                raise RuntimeError(f"HTTP {retry.code} after re-login {method} {path}") from None


def load_learning_content(items):
    records = json.loads(LEARNING_CONTENT.read_text())
    required = {
        "id", "englishDescription", "chineseDescription", "hint", "approach",
        "timeComplexity", "spaceComplexity", "testMaterial", "provenance",
    }
    by_id = {record.get("id"): record for record in records}
    expected = {item["id"] for item in items}
    if len(records) != 150 or set(by_id) != expected or any(not required <= record.keys() for record in records):
        raise RuntimeError("learning content must contain one complete record for every NeetCode ID")
    if any(not isinstance(record[field], str) or not record[field].strip()
           for record in records for field in required - {"id"}):
        raise RuntimeError("learning content fields must be non-empty strings")
    return by_id


def material(problem, learning):
    return {
        "title": f"{problem['id']}. {problem['title']}",
        "platform": "leetcode",
        "externalProblemId": str(problem["id"]),
        "externalUrl": f"https://leetcode.com/problems/{problem['slug']}/",
        "difficulty": problem["difficulty"].lower(),
        "description": learning["englishDescription"],
        "notes": learning["chineseDescription"],
        "keyInsight": learning["hint"],
        "timeComplexity": learning["timeComplexity"],
        "spaceComplexity": learning["spaceComplexity"],
        "mistakes": "Use the included executable cases when reviewing.",
        "interviewNotes": learning["testMaterial"],
    }


def solution(source, learning):
    return {
        "language": "kotlin",
        "code": "// 中文學習註解：" + learning["hint"] + "\n" + source,
        "explanation": learning["approach"],
    }


def solution_matches(existing, payload):
    return (existing.get("language", "").lower() == payload["language"]
            and existing.get("code") == payload["code"]
            and existing.get("explanation") == payload["explanation"])


def problem_matches(detail, data, category):
    return (all(detail.get(key) == value for key, value in data.items() if key != "tagIds")
            and any(tag.get("name") == category for tag in detail.get("tags", [])))


def _with_supporting_types(code):
    body = re.sub(r"^import .+\n?", "", code, flags=re.M).strip()
    return COMMON_IMPORTS + TYPE_STUBS + "\n" + body + "\n"


def load_solution_sources():
    sources = {}
    for lesson in LESSON_FILES:
        for section in re.split(r"(?=^## \d+\.)", lesson.read_text(), flags=re.M)[1:]:
            match = re.match(r"## (\d+)", section)
            code = re.findall(r"\`\`\`kotlin\n(.*?)\n\`\`\`", section, re.S)
            if match is None or len(code) != 1:
                raise RuntimeError(f"invalid Kotlin lesson section in {lesson.name}")
            problem_id = int(match.group(1))
            if problem_id in sources:
                raise RuntimeError(f"duplicate Kotlin source for {problem_id}")
            sources[problem_id] = _with_supporting_types(code[0])

    for lesson in REUSABLE_FILES:
        for section in re.split(r"(?=^## \d+\.)", lesson.read_text(), flags=re.M)[1:]:
            match = re.match(r"## (\d+)", section)
            if match is None or int(match.group(1)) not in REUSED_IDS:
                continue
            code = re.findall(r"```kotlin\n(.*?)\n```", section, re.S)
            if len(code) != 1:
                raise RuntimeError(f"invalid reusable Kotlin section in {lesson.name}")
            problem_id = int(match.group(1))
            if problem_id in sources:
                raise RuntimeError(f"duplicate Kotlin source for {problem_id}")
            sources[problem_id] = _with_supporting_types(code[0])

    extra = EXTRA.read_text()
    markers = list(re.finditer(r"^class (?:X\d+|LNode|TNode)\b", extra, re.M))
    for index, marker in enumerate(markers):
        name = marker.group(0).split()[1]
        if not name.startswith("X"):
            continue
        problem_id = int(name[1:])
        end = markers[index + 1].start() if index + 1 < len(markers) else len(extra)
        body = extra[marker.start():end].strip()
        if problem_id in sources:
            raise RuntimeError(f"duplicate Kotlin source for {problem_id}")
        sources[problem_id] = _with_supporting_types(
            re.sub(rf"^class X{problem_id}\b", "class Solution", body, count=1)
        )
    return sources


def validate(items, sources=None):
    ids = [item["id"] for item in items]
    assert len(items) == 150 and len(set(ids)) == 150, "metadata must contain 150 unique IDs"
    assert all({"title", "slug", "category", "difficulty"} <= item.keys() for item in items)
    if sources is not None:
        assert set(sources) == set(ids), "Kotlin source must cover exactly the metadata IDs"
        assert all(0 < len(source) <= 100_000 for source in sources.values())


def list_all(client, path):
    page, result = 1, []
    while True:
        separator = "&" if "?" in path else "?"
        data = client.request("GET", f"{path}{separator}page={page}&pageSize=100")
        result.extend(data["items"])
        if page >= data["totalPages"]:
            return result
        page += 1


def run_import():
    items = json.loads(PROBLEMS.read_text())
    sources = load_solution_sources()
    validate(items, sources)
    learning = load_learning_content(items)
    client = Client()
    client.login()
    tags = {tag["name"]: tag["id"] for tag in client.request("GET", "/tags")}
    for category in sorted({item["category"] for item in items}):
        if category not in tags:
            tags[category] = client.request("POST", "/tags", {"name": category})["id"]
    existing = {
        row.get("externalProblemId"): row
        for row in list_all(client, "/problems")
        if row.get("platform") == "leetcode"
    }
    counts = Counter()
    for item in items:
        data = material(item, learning[item["id"]])
        data["tagIds"] = [tags[item["category"]]]
        old = existing.get(str(item["id"]))
        if old:
            detail = client.request("GET", "/problems/" + old["id"])
            if problem_matches(detail, data, item["category"]):
                counts["skipped"] += 1
            else:
                detail = client.request("PUT", "/problems/" + old["id"], data)
                counts["updated"] += 1
        else:
            detail = client.request("POST", "/problems", data)
            counts["created"] += 1
        kotlin = next((s for s in detail.get("solutions", []) if s["language"].lower() == "kotlin"), None)
        payload = solution(sources[item["id"]], learning[item["id"]])
        if kotlin:
            if not solution_matches(kotlin, payload):
                client.request("PUT", "/solutions/" + kotlin["id"], payload)
        else:
            client.request("POST", "/problems/" + detail["id"] + "/solutions", payload)
    verified = [row for row in list_all(client, "/problems") if row.get("platform") == "leetcode"]
    found = Counter(row.get("externalProblemId") for row in verified)
    missing = [item["id"] for item in items if found[str(item["id"])] != 1]
    if missing:
        raise RuntimeError("read-back count mismatch for IDs: " + ",".join(map(str, missing)))
    for item in items:
        row = next(x for x in verified if x.get("externalProblemId") == str(item["id"]))
        detail = client.request("GET", "/problems/" + row["id"])
        expected_url = f"https://leetcode.com/problems/{item['slug']}/"
        valid = (
            detail["title"] == f"{item['id']}. {item['title']}"
            and detail["difficulty"] == item["difficulty"].lower()
            and detail["externalUrl"] == expected_url
            and any(t["name"] == item["category"] for t in detail["tags"])
            and any(s["language"].lower() == "kotlin"
                    and s.get("code") == solution(sources[item["id"]], learning[item["id"]])["code"]
                    and s.get("explanation") == learning[item["id"]]["approach"]
                    for s in detail["solutions"])
        )
        if not valid:
            raise RuntimeError(f"detail verification failed for {item['id']}")
    summary = {"created": counts["created"], "updated": counts["updated"], "skipped": counts["skipped"],
               "failed": 0, "failedIds": [], "safeToRerun": True}
    SUMMARY.write_text(json.dumps(summary, ensure_ascii=False, indent=2) + "\n")
    print(json.dumps(summary, ensure_ascii=False))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--validate", action="store_true")
    parser.add_argument("--import-and-verify", action="store_true")
    args = parser.parse_args()
    try:
        items = json.loads(PROBLEMS.read_text())
        sources = load_solution_sources()
        validate(items, sources)
        load_learning_content(items)
        if args.validate:
            print("PASS: 150 unique metadata entries and independent Kotlin sources")
        elif args.import_and_verify:
            run_import()
        else:
            parser.error("choose --validate or --import-and-verify")
    except Exception as error:
        print("ERROR: " + str(error), file=sys.stderr)
        return 2
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
