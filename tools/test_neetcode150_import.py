#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path

path = Path(__file__).with_name("neetcode150_import.py")
spec = importlib.util.spec_from_file_location("neetcode150_import", path)
module = importlib.util.module_from_spec(spec); spec.loader.exec_module(module)
items = json.loads(module.PROBLEMS.read_text())
module.validate(items)
content = module.load_learning_content(items)
example = module.material(next(item for item in items if item["id"] == 1), content[1])
assert example["platform"] == "leetcode"
assert example["externalProblemId"] == "1"
assert example["externalUrl"] == "https://leetcode.com/problems/two-sum/"
assert "Two Sum" in example["description"]
assert "兩個不同位置" in example["notes"]
assert example["keyInsight"] == content[1]["hint"]
payload = module.solution("class Solution {}", content[1])
assert payload["language"] == "kotlin"
assert payload["code"].startswith("// 中文學習註解：")
assert payload["explanation"] == content[1]["approach"]
assert module.solution_matches(payload, payload)
assert module.problem_matches(
    {**example, "tags": [{"name": "Arrays & Hashing"}]}, {**example, "tagIds": ["tag-id"]}, "Arrays & Hashing"
)
sources = module.load_solution_sources()
assert set(sources) == {item["id"] for item in items}
assert len(sources) == 150
assert "class Solution" in sources[1]
assert "class Solution" in sources[217]
assert "class X217" not in sources[217]
assert all(0 < len(source) <= 100_000 for source in sources.values())
assert len(content) == 150
assert all(record["testMaterial"].startswith("執行") for record in content.values())
print("PASS: importer validates 150 complete learning records and Kotlin payloads")
