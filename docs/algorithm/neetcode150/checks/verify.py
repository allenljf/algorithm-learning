from pathlib import Path
import re,json,subprocess,sys
root=Path(__file__).resolve().parent.parent
out=root/'checks'
first=[
(1,'Solution_1().twoSum(intArrayOf(6,1,8,4),5).toList()',[1,3]),
(1,'Solution_1().twoSum(intArrayOf(2,2),4).toList()',[0,1]),
(242,'Solution_242().isAnagram("aabc","caba")',True),(242,'Solution_242().isAnagram("aa","ab")',False),
(49,'Solution_49().groupAnagrams(arrayOf("arc","car","bat","rat","tar")).map { it.sorted().joinToString(",") }.sorted()',['arc,car','bat','rat,tar']),
(49,'Solution_49().groupAnagrams(arrayOf("", "")).map { it.size }',[2]),
(347,'Solution_347().topKFrequent(intArrayOf(4,4,4,7,7,9),2).sorted()',[4,7]),
(347,'Solution_347().topKFrequent(intArrayOf(-1,-1),1).toList()',[-1]),
(128,'Solution_128().longestConsecutive(intArrayOf(9,3,1,2,2,8))',3),(128,'Solution_128().longestConsecutive(intArrayOf())',0),
(125,'Solution_125().isPalindrome("No, on!")',True),(125,'Solution_125().isPalindrome("0P")',False),
(167,'Solution_167().twoSum(intArrayOf(1,3,6,8),9).toList()',[1,4]),(167,'Solution_167().twoSum(intArrayOf(-2,0,3),1).toList()',[1,3]),
(15,'Solution_15().threeSum(intArrayOf(-2,0,0,2,2))',[[-2,0,2]]),(15,'Solution_15().threeSum(intArrayOf(0,0,0,0))',[[0,0,0]]),
(42,'Solution_42().trap(intArrayOf(3,0,2,0,4))',7),(42,'Solution_42().trap(intArrayOf(1,2,3))',0),
(121,'Solution_121().maxProfit(intArrayOf(8,3,6,2,7))',5),(121,'Solution_121().maxProfit(intArrayOf(9,6,2))',0),
(3,'Solution_3().lengthOfLongestSubstring("abba")',2),(3,'Solution_3().lengthOfLongestSubstring("")',0),
(424,'Solution_424().characterReplacement("ABBBAC",1)',4),(424,'Solution_424().characterReplacement("ABCDE",0)',1),
(567,'Solution_567().checkInclusion("ac","zzcaxy")',True),(567,'Solution_567().checkInclusion("aa","ab")',False),
(76,'Solution_76().minWindow("XAABYC","ABC")','ABYC'),(76,'Solution_76().minWindow("A","AA")',''),
(239,'Solution_239().maxSlidingWindow(intArrayOf(4,2,5,1),2).toList()',[4,5,5]),(239,'Solution_239().maxSlidingWindow(intArrayOf(-2,-2,-3),1).toList()',[-2,-2,-3]),
(20,'Solution_20().isValid("{[]}")',True),(20,'Solution_20().isValid("([)]")',False),
(155,'run { val s=MinStack(); s.push(5);s.push(2);s.push(4); val a=s.getMin();s.pop();s.pop(); listOf(a,s.getMin(),s.top()) }',[2,5,5]),
(155,'run { val s=MinStack();s.push(Int.MIN_VALUE);s.push(Int.MIN_VALUE);s.pop();s.getMin() }',-2147483648),
(84,'Solution_84().largestRectangleArea(intArrayOf(2,4,4,1))',8),(84,'Solution_84().largestRectangleArea(intArrayOf(0))',0),
(704,'Solution_704().search(intArrayOf(2,5,8,11,14),11)',3),(704,'Solution_704().search(intArrayOf(2,5,8,11,14),10)',-1),
(33,'Solution_33().search(intArrayOf(6,8,10,1,3,5),3)',4),(33,'Solution_33().search(intArrayOf(1),2)',-1),
(74,'Solution_74().searchMatrix(arrayOf(intArrayOf(1,4,7),intArrayOf(9,12,15)),12)',True),(74,'Solution_74().searchMatrix(arrayOf(intArrayOf(2)),1)',False),
(153,'Solution_153().findMin(intArrayOf(7,9,1,3,5))',1),(153,'Solution_153().findMin(intArrayOf(1,2,3))',1),
(981,'run { val t=TimeMap();t.set("color","red",2);t.set("color","blue",6);listOf(t.get("color",1),t.get("color",5),t.get("color",6)) }',['','red','blue']),
(981,'TimeMap().get("unknown",100)','')]
(out/'first-tests.json').write_text(json.dumps([dict(id=i,call=c,expected=e) for i,c,e in first],ensure_ascii=False,indent=2))
imports=set();blocks=[];ids=[]
for f in sorted(root.glob('0[123]-*.md')):
    for section in re.split(r'(?=^## \d+\.)',f.read_text(),flags=re.M)[1:]:
        i=int(re.match(r'## (\d+)',section)[1]); ids.append(i)
        code=re.findall(r'```kotlin\n(.*?)\n```',section,re.S)
        assert len(code)==1,(i,len(code))
        body=code[0]
        imports.update(re.findall(r'^import .+$',body,re.M))
        for typ in ['ArrayDeque','PriorityQueue','TreeSet','BigInteger']:
            if re.search(r'\b'+typ+r'\b',body):
                group='java.math' if typ=='BigInteger' else 'java.util'
                assert f'import {group}.{typ}' in body,(i,typ)
        body=re.sub(r'^import .+\n?','',body,flags=re.M)
        blocks.append(re.sub(r'\bclass Solution\b',f'class Solution_{i}',body))
assert len(ids)==67 and len(set(ids))==67,len(ids)
old_ids={p['id'] for p in json.loads((root.parent/'leetcode75-kotlin'/'problems.json').read_text())}
assert not set(ids)&old_ids
nc_ids={p['id'] for p in json.loads((root/'problems.json').read_text())}
learning=json.loads((root/'learning-content.json').read_text())
learning_by_id={record['id']:record for record in learning}
required_learning={'id','englishDescription','chineseDescription','hint','approach','timeComplexity','spaceComplexity','testMaterial','provenance'}
assert len(learning)==150 and set(learning_by_id)==nc_ids
assert all(required_learning <= record.keys() for record in learning)
assert all(isinstance(record[field],str) and record[field].strip() for record in learning for field in required_learning-{'id'})
assert all(record['testMaterial'].startswith('執行') for record in learning)
assert set(ids)<=nc_ids
reused_ids={11,17,62,72,104,136,198,199,206,208,215,238,338,435,739,746,875,994,1143,1448}
for f in sorted((root.parent/'leetcode75-kotlin').glob('0[123]-*.md')):
    for section in re.split(r'(?=^## \d+\.)',f.read_text(),flags=re.M)[1:]:
        i=int(re.match(r'## (\d+)',section)[1])
        if i not in reused_ids: continue
        code=re.findall(r'```kotlin\n(.*?)\n```',section,re.S)
        assert len(code)==1,(i,len(code))
        body=code[0]
        imports.update(re.findall(r'^import .+$',body,re.M))
        body=re.sub(r'^import .+\n?','',body,flags=re.M)
        blocks.append(re.sub(r'\bclass Solution\b',f'class Solution_{i}',body))
assert reused_ids <= nc_ids
alltests=[]
for file in ['first-tests.json','middle-tests.json','advanced-tests.json']: alltests+=json.loads((out/file).read_text())
assert {t['id'] for t in alltests}==set(ids)
assert all(sum(t['id']==i for t in alltests)>=2 for i in ids)
def ktstr(v):
    if isinstance(v,list):return '['+', '.join(ktstr(x) for x in v)+']'
    if isinstance(v,bool):return str(v).lower()
    if v is None:return 'null'
    return str(v)
lines=[]
for n,t in enumerate(alltests,1):
    expected=json.dumps(ktstr(t['expected']),ensure_ascii=False).replace('$','\\$')
    lines.append(f'run {{ val actual=({t["call"]}).toString(); check(actual=={expected}) {{ "Case {n} #{t["id"]}: expected="+{expected}+", actual="+actual }} }}')
stub='''
class ListNode(var `val`: Int) { var next: ListNode? = null }
class TreeNode(var `val`: Int) { var left: TreeNode? = null; var right: TreeNode? = null }
class Node(var `val`: Int) { var neighbors: ArrayList<Node?> = ArrayList() }
'''
extra=re.sub(r'^import .+\n', '', (out/'ExtraSolutions.kt').read_text(), flags=re.M)
extra_ids={int(x) for x in re.findall(r'^class X(\d+)',extra,re.M)}
assert len(extra_ids)==63, len(extra_ids)
extra_cases=json.loads((out/'extra-tests.json').read_text())
assert {x['id'] for x in extra_cases}==extra_ids
assert all(sum(x['id']==i for x in extra_cases)>=2 for i in extra_ids)
assert all({'call','expected'} <= x.keys() for x in extra_cases), 'every supplemental case must execute a Kotlin call with an expected result'
for n,t in enumerate(extra_cases, len(lines)+1):
    expected=json.dumps(ktstr(t['expected']),ensure_ascii=False).replace('$','\\$')
    lines.append(f'run {{ val actual=({t["call"]}).toString(); check(actual=={expected}) {{ "Case {n} #{t["id"]}: expected="+{expected}+", actual="+actual }} }}')
reused_cases=json.loads((out/'reused-tests.json').read_text())
assert {x['id'] for x in reused_cases}==reused_ids
assert all(sum(x['id']==i for x in reused_cases)>=2 for i in reused_ids)
assert all({'call','expected'} <= x.keys() for x in reused_cases)
for n,t in enumerate(reused_cases, len(lines)+1):
    expected=json.dumps(ktstr(t['expected']),ensure_ascii=False).replace('$','\\$')
    lines.append(f'run {{ val actual=({t["call"]}).toString(); check(actual=={expected}) {{ "Case {n} #{t["id"]}: expected="+{expected}+", actual="+actual }} }}')
assert set(ids) | extra_ids | reused_ids == nc_ids
source='import kotlin.math.*\n'+'\n'.join(sorted(imports))+'\n'+stub+'\n\n'.join(blocks)+'\n'+extra+'\nfun main() {\n'+'\n'.join(lines)+f'\nprintln("PASS: {len(lines)} executable cases across 150 Kotlin solutions")\n}}\n'
(out/'NewSolutions.kt').write_text(source)
kotlinc='/Applications/Android Studio.app/Contents/plugins/Kotlin/kotlinc/bin/kotlinc'
java='/Applications/Android Studio.app/Contents/jbr/Contents/Home/bin/java'
cmd=[kotlinc,str(out/'NewSolutions.kt'),'-jvm-target','17','-include-runtime','-d',str(out/'new-solutions.jar')]
run=subprocess.run(cmd,text=True,capture_output=True)
(out/'compile.log').write_text(run.stdout+run.stderr)
if run.returncode: print(run.stdout+run.stderr);sys.exit(run.returncode)
run=subprocess.run([java,'-jar',str(out/'new-solutions.jar')],text=True,capture_output=True)
(out/'run.log').write_text(run.stdout+run.stderr)
print(run.stdout+run.stderr)
if run.returncode:sys.exit(run.returncode)
print('PASS: 150 unique lessons, each with >=2 executable Kotlin cases.')
