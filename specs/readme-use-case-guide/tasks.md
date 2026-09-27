# README Use-case Guide Tasks

## Phase 1 — Bilingual user documentation

### RUG-001 — Add practical website-use scenarios to both READMEs

- Deliverable: `README.md` and `README.zh-TW.md` include equivalent getting-
  started guidance and three concrete use cases for recording, organizing, and
  reviewing algorithm problems.
- Depends on: none
- Parallel group: `readme-use-case-documentation`
- Spec refs: Goal; Scope; AC-RUG-01..04; Technical constraints; Decisions and
  assumptions.
- Execution contract: `single-agent` analysis; `rapid` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification:
  - python3 -c "from pathlib import Path; a=Path('README.md').read_text(); b=Path('README.zh-TW.md').read_text(); required=['Use case 1: Capture a solved problem','Use case 2: Find and organize a study set','Use case 3: Run a focused review session','使用情境 1：記錄已解出的題目','使用情境 2：找出並整理學習清單','使用情境 3：完成一次專注複習']; assert all(item in a or item in b for item in required)"
  - python3 -c "from pathlib import Path; a=Path('README.md').read_text(); b=Path('README.zh-TW.md').read_text(); assert 'Getting started on the website' in a and '開始使用網站' in b; assert a.count('Use case ') == 3 and b.count('使用情境 ') == 3"
  - git diff --check
- Status: completed
