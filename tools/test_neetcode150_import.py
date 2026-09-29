#!/usr/bin/env python3
import importlib.util
import json
from pathlib import Path
from unittest.mock import patch

path = Path(__file__).with_name("neetcode150_import.py")
spec = importlib.util.spec_from_file_location("neetcode150_import", path)
module = importlib.util.module_from_spec(spec); spec.loader.exec_module(module)
items = json.loads(module.PROBLEMS.read_text())
module.validate(items)
assert [item["id"] for item in module.select_items(items, {371})] == [371]
try:
    module.select_items(items, {999999})
    raise AssertionError("unknown IDs must be rejected")
except RuntimeError as error:
    assert "unknown NeetCode ID" in str(error)
content = module.load_learning_content(items)
example = module.material(next(item for item in items if item["id"] == 1), content[1])
assert example["platform"] == "leetcode"
assert example["externalProblemId"] == "1"
assert example["externalUrl"] == "https://leetcode.com/problems/two-sum/"
assert "given an array" in example["description"].lower()
assert "兩個不同位置" in example["notes"]
assert example["keyInsight"] == content[1]["hint"]
lesson_371 = content[371]
payload_371 = module.material(next(item for item in items if item["id"] == 371), lesson_371)
assert payload_371["externalUrl"] == "https://leetcode.com/problems/sum-of-two-integers/"
assert lesson_371["englishDescription"] in payload_371["description"]
assert lesson_371["englishExamples"] in payload_371["description"]
assert lesson_371["chineseDescription"] in payload_371["notes"]
assert lesson_371["chineseSummary"] in payload_371["notes"]
assert "xor" in lesson_371["approach"] and "二補數" in lesson_371["approach"]
assert "assertEquals(3" in lesson_371["testMaterial"]
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
required_lesson_fields = {
    "englishDescription", "englishExamples", "chineseDescription", "chineseSummary",
    "hint", "approach", "timeComplexity", "spaceComplexity", "testMaterial",
    "externalUrl", "sourceUrl", "sourceType",
}
assert all(record.get(field, "").strip() for record in content.values() for field in required_lesson_fields)
assert all(record["externalUrl"].startswith("https://leetcode.com/problems/") for record in content.values())
assert all("刻意只描述學習目標" not in record["chineseDescription"] for record in content.values())
assert all("先選擇能保留關鍵不變量" not in record["approach"] for record in content.values())


class FakeResponse:
    def __enter__(self):
        return self

    def __exit__(self, *_):
        return False

    def read(self):
        return b"{}"


captured = {}


def capture_request(request, timeout):
    captured["method"] = request.get_method()
    captured["timeout"] = timeout
    return FakeResponse()


client = module.Client()
client.token = "test-token"
with patch.object(module.urllib.request, "urlopen", capture_request):
    client.raw("PUT", "/problems/example", {"title": "example"})
assert captured == {"method": "PUT", "timeout": 30}
print("PASS: importer validates 150 complete learning records and Kotlin payloads")
