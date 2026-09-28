from pathlib import Path
import json,re,collections
r=Path(__file__).resolve().parent.parent
nc=json.loads((r/'problems.json').read_text()); old=json.loads((r.parent/'problems.json').read_text())
pmap={p['id']:p for p in nc}; ncids=set(pmap); oldids={p['id'] for p in old}
p0={1,242,49,125,167,3,20,704,33,21,19,206,104,102,98,200,207,994,78,39,70,746,198,322,300,53,56,73}
p2={42,76,239,84,124,297,295,332,115,190,371,621}
premium={252,253,261,269,271,286,323}
assert len(p0)==28
new_sections={};old_sections={};paths={}
def anchor(title):return re.sub(r'[^\w\- ]','',title.lower()).replace(' ','-')
for folder,target in [(r,new_sections),(r.parent,old_sections)]:
    for f in sorted(folder.glob('0[123]-*.md')):
        for section in re.split(r'(?=^## \d+\.)',f.read_text(),flags=re.M)[1:]:
            i=int(re.match(r'## (\d+)',section)[1]);target[i]=section.rstrip()
            if i in ncids:
                heading=section.splitlines()[0][3:]
                relative=f.name if folder==r else '../'+f.name
                paths[i]=relative+'#'+anchor(heading)
newids=set(new_sections); covered=(oldids|newids)&ncids
assert len(newids)==67 and not newids&oldids and len(covered)==87
assert p0<=covered and p2<=newids
cats=list(dict.fromkeys(p['category'] for p in nc))
assert len(cats)==18
stats={cat:sum(p['category']==cat and p['id'] in covered for p in nc) for cat in cats}
assert all(stats.values())
def priority(i):return 'P0 核心' if i in p0 else 'P2 選讀' if i in p2 else 'P1 補強' if i in covered else 'E 延伸'
lines=['# NeetCode150完整索引與優先級','', '[回8週計畫](README.md)｜[題型地圖](00-題型選擇與相似題地圖.md)','', '查證2026-09-21。150題完整收錄；87題可讀本地詳解（67新增＋20沿用），63題為延伸索引。P0/P1/P2是本教材學習安排，不是公司題頻。每題有官方原題連結；Premium標记表示LeetCode完整原題可能需要訂閱，可先使用NeetCode官方練習頁尋找对应題目，未保證兩站所有題面限制完全相同。','', '[NeetCode150官方入口](https://neetcode.io/practice/practice/neetcode150)｜[可重現來源](來源查證.md)','', '| 分類 | 全題數 | 有本地詳解 | 延伸 |','|---|---:|---:|---:|']
for cat in cats:
    total=sum(p['category']==cat for p in nc);c=stats[cat];lines.append(f'| {cat} | {total} | {c} | {total-c} |')
for cat in cats:
    lines += ['',f'## {cat}','','| 題號與英文原題 | 難度 | 優先級 | 本地教學 |','|---|---|---|---|']
    for p in nc:
        if p['category']!=cat:continue
        i=p['id'];access='（Premium）' if i in premium else ''
        lesson=f'[{"新增詳解" if i in newids else "沿用75題詳解"}]({paths[i]})' if i in covered else '延伸索引，尚無本地詳解'
        lines.append(f'| [{i}. {p["title"]}](https://leetcode.com/problems/{p["slug"]}/){access} | {p["difficulty"]} | {priority(i)} | {lesson} |')
(r/'150題索引與優先級.md').write_text('\n'.join(lines)+'\n')
track=['# 答案優先熟練追蹤表','', '[回8週計畫](README.md)｜[150題索引](150題索引與優先級.md)','', '這張表只列有本地詳解的87題。先記閱讀與走讀，不把第一遍獨立解出當門檻。各欄填日期；「可比較」填能指出的相似題差異。日+1/+3/+7/+14可在複習欄記錄，卡住時直接回看原理。','', '| 優先 | 題目與詳解 | 已讀答案 | 可走讀 | 可重寫 | 可比較／差異 | 複習日期 |','|---|---|---|---|---|---|---|']
for tier in ['P0 核心','P1 補強','P2 選讀']:
    for p in nc:
        i=p['id']
        if i not in covered or priority(i)!=tier:continue
        track.append(f'| {tier} | [{i}. {p["title"]}]({paths[i]}) |  |  |  |  |  |')
(r/'熟練追蹤表.md').write_text('\n'.join(track)+'\n')
# Single-file supplement: 67 new lessons plus 20 reusable existing lessons.
order=[('README.md','plan','8週計畫'),('00-題型選擇與相似題地圖.md','map','18類題型與DP分類'),('150題索引與優先級.md','index','完整150題索引'),('01-雜湊雙指標視窗與搜尋.md','first','23題：雜湊到搜尋'),('02-鏈結串列樹堆積與圖.md','middle','20題：鏈結串列到圖'),('03-回溯動態規劃貪心與數學.md','advanced','24題：回溯、DP與數學'),('熟練追蹤表.md','tracker','熟練追蹤'),('來源查證.md','sources','官方來源'),('驗證紀錄.md','validation','本機驗證')]
alias={name:a for name,a,_ in order}
parts=['# NeetCode150：Kotlin答案優先精選教材','', '本合訂本收錄67題新增詳解＋20題沿用詳解，共87題；另含完整150題索引，其餘63題明列為延伸。先看答案，再走讀原理，最後重寫與比較相似題。','', '## 導覽','']+[f'- [{label}](#{a})' for _,a,label in order]+['- [沿用原75教材的20題](#reused)','']
for name,a,label in order:
    body=(r/name).read_text()
    def relink(m):
        file,frag=m.group(1),m.group(2)
        if file in alias:return '('+(frag or '#'+alias[file])+')'
        if file.startswith('../0') and frag:
            match=re.match(r'#(\d+)-',frag)
            if match and int(match[1]) in covered:return '('+frag+')'
        if file=='NeetCode150-答案優先精選教材.md':return '(#plan)'
        return m.group(0)
    body=re.sub(r'\(([^()\s]+\.md)(#[^)]*)?\)',relink,body)
    parts+=['---','',f'<a id="{a}"></a>','',body,'']
    if name=='03-回溯動態規劃貪心與數學.md':
        parts+=['---','','<a id="reused"></a>','','# 沿用原LeetCode75的20題','','以下保留原詳解內容；採新的閱讀順序：看題意後先跳到Kotlin答案，再回看推演，不要求先自行想解法。','']
        for p in nc:
            if p['id'] in oldids:parts+=[old_sections[p['id']],'']
combined='\n'.join(parts)
(r/'NeetCode150-答案優先精選教材.md').write_text(combined)
# Validate local links, chapter anchors and count exact official problem headings.
for p in r.glob('*.md'):
    text=p.read_text();assert sum(line.startswith('```') for line in text.splitlines())%2==0,p.name
    for dest in re.findall(r'\]\(([^)]+)\)',text):
        if dest.startswith(('https://','http://','#')):continue
        file=dest.split('#')[0];assert (p.parent/file).exists(),(p.name,file)
        if '#' in dest and file.endswith('.md'):
            wanted=dest.split('#',1)[1]
            target=(p.parent/file).read_text()
            headings={anchor(h) for h in re.findall(r'^#+ (.+)$',target,re.M)}|set(re.findall(r'<a id="([^"]+)"',target))
            assert wanted in headings,(p.name,file,wanted)
headings={anchor(h) for h in re.findall(r'^#+ (.+)$',combined,re.M)}|set(re.findall(r'<a id="([^"]+)"',combined))
for dest in re.findall(r'\]\(#([^)]*)\)',combined):assert dest in headings,dest
for i in covered:
    expected=f'## {i}. {pmap[i]["title"]}'
    assert combined.splitlines().count(expected)==1,(expected,combined.splitlines().count(expected))
for section in new_sections.values():
    for code in re.findall(r'```kotlin\n(.*?)\n```',section,re.S):assert code in combined
report={'new_lessons':len(newids),'covered_neetcode150':len(covered),'extension_only':len(ncids-covered),'p0':len(p0),'p1':len(covered-p0-p2),'p2':len(p2),'category_coverage':stats,'new_tests':sum(len(json.loads((r/'checks'/f).read_text())) for f in ['first-tests.json','middle-tests.json','advanced-tests.json'])}
(r/'checks/coverage.json').write_text(json.dumps(report,ensure_ascii=False,indent=2))
print('PASS: 150 index rows; 87 detailed lessons; 18 categories covered; 28 core lessons; links, anchors, fences and code blocks verified.')
print(json.dumps(report,ensure_ascii=False))
