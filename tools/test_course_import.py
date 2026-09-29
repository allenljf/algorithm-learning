#!/usr/bin/env python3
import importlib.util, unittest
from pathlib import Path
spec=importlib.util.spec_from_file_location("course_import",Path(__file__).with_name("course_import.py")); module=importlib.util.module_from_spec(spec); spec.loader.exec_module(module)
class CourseImportTest(unittest.TestCase):
 def test_payloads_are_lossless_and_unique(self):
  rows=module.payloads(); self.assertEqual(398,len(rows)); self.assertEqual(398,len({x['sourceIdentity'] for x in rows})); self.assertTrue(all(x['detail'] and x['tags'] and x['sourceUrl'] for x in rows))
 def test_import_endpoint_is_course_only(self):
  self.assertTrue(module.NAMES["neetcode150"][0]); self.assertNotIn("problems",module.NAMES)
if __name__ == "__main__": unittest.main()
