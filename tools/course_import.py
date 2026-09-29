#!/usr/bin/env python3
"""Validate or explicitly import checked-in Course Markdown without persisting secrets."""
from __future__ import annotations
import argparse, hashlib, json, os, urllib.request
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
MANIFEST = ROOT / "docs/algorithm/course-materials/manifest.json"
NAMES = {"leetcode75": ("LeetCode 75", 1), "neetcode150": ("NeetCode 150", 2), "hackerrank-interview": ("HackerRank Interview Kit", 3), "hackerrank-three-month-prep-kotlin": ("HackerRank Three Month Prep", 4)}

def payloads() -> list[dict]:
    manifest = json.loads(MANIFEST.read_text(encoding="utf-8")); lessons = manifest["lessons"]
    assert manifest["lessonCount"] == len(lessons) and set(manifest["courses"]) == set(NAMES)
    ids = set(); rows = []
    for lesson in lessons:
        detail = (ROOT / lesson["canonicalMarkdownPath"]).read_text(encoding="utf-8")
        assert detail == lesson["detail"] and hashlib.sha256(detail.encode()).hexdigest() == lesson["detailSha256"]
        assert lesson["sourceIdentity"] not in ids; ids.add(lesson["sourceIdentity"])
        name, sort = NAMES[lesson["courseSlug"]]
        rows.append({"categorySlug": lesson["courseSlug"], "categoryDisplayName": name, "categorySortOrder": sort,
                     "sourceIdentity": lesson["sourceIdentity"], "sourceMarkdownPath": lesson["sourceMarkdownPath"],
                     "englishTitle": lesson["englishTitle"], "tags": lesson["tags"], "detail": detail,
                     "sourceUrl": lesson["sourceUrl"], "sourceType": lesson["sourceType"], "sortOrder": lesson["sortOrder"]})
    return rows

def post(base_url: str, token: str, lessons: list[dict]) -> dict:
    request = urllib.request.Request(base_url.rstrip("/") + "/api/v1/courses/import", data=json.dumps({"lessons": lessons}).encode(), headers={"Content-Type":"application/json", "X-Course-Ingestion-Token":token}, method="POST")
    with urllib.request.urlopen(request, timeout=30) as response:
        if response.status != 200: raise RuntimeError(f"import failed: HTTP {response.status}")
        return json.loads(response.read())

def main() -> None:
    parser=argparse.ArgumentParser(); parser.add_argument("--validate",action="store_true"); parser.add_argument("--import",dest="do_import",action="store_true"); parser.add_argument("--base-url",default=os.getenv("COURSE_API_BASE_URL","http://localhost:8080")); args=parser.parse_args()
    rows=payloads(); print(f"validated {len(rows)} Course payloads; no network write performed")
    if args.do_import:
        token=os.getenv("APP_COURSE_INGESTION_TOKEN")
        if not token: raise SystemExit("APP_COURSE_INGESTION_TOKEN is required for --import")
        result=post(args.base_url,token,rows)
        if len(result)!=len(rows): raise RuntimeError("API response count mismatch")
        print(f"imported {len(result)} Course payloads")
if __name__ == "__main__": main()
