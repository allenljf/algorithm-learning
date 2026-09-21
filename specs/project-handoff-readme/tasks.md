# Project Handoff README — Task List

## Phase 1 — Final documentation

### PHR-001 — Publish bilingual project handoff documentation

- Deliverable: complete `README.md` and `README.zh-TW.md`, with source-backed
  backend/database/API/client/user/workflow content, valid Mermaid diagrams,
  workflow closeout records, a documentation commit, and push.
- Depends on: none
- Parallel group: `project-handoff-documentation`
- Spec refs: Goal, Scope, AC-HRD-01..05, Assumptions
- Execution contract: `single-agent` analysis; `rapid` test approach;
  `update-docs` documentation; `infer` ambiguity handling.
- Verification:
  - test -f README.md
  - test -f README.zh-TW.md
  - python3 -c "from pathlib import Path; import re; files=['README.md','README.zh-TW.md']; [Path(f).read_text() for f in files]; links=re.findall(r'(?<!! )\[[^]]+\]\((?!https?://|mailto:|#)([^)#]+)', '\n'.join(Path(f).read_text() for f in files)); missing=[p for p in links if not Path(p).exists()]; assert not missing, missing"
  - python3 -c "from pathlib import Path; a=Path('README.md').read_text(); b=Path('README.zh-TW.md').read_text(); marker=chr(96)*3+'mermaid'; assert a.count('## ') >= 12 and b.count('## ') >= 12; assert 'workflow-intake' in a and 'workflow-intake' in b; assert marker in a and marker in b"
  - git diff --check
- Status: completed
