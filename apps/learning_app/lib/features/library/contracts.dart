class Tag { const Tag(this.id,this.name); final String id; final String name; }
class ProblemSummary { const ProblemSummary({required this.id,required this.title,required this.difficulty,required this.tags}); final String id,title,difficulty; final List<Tag> tags; }
class Page<T> { const Page({required this.items,required this.page,required this.pageSize,required this.totalItems}); final List<T> items; final int page,pageSize,totalItems; }
abstract interface class ProblemRemote { Future<Page<ProblemSummary>> listProblems({String? query,int page=1}); }
abstract interface class ProblemRepository { Future<Page<ProblemSummary>> list({String? query,int page=1}); }
class RemoteProblemRepository implements ProblemRepository { RemoteProblemRepository(this._remote); final ProblemRemote _remote; @override Future<Page<ProblemSummary>> list({String? query,int page=1})=>_remote.listProblems(query:query,page:page); }
class Solution { const Solution(this.id,this.problemId,this.language,this.code); final String id,problemId,language,code; }
class Review { const Review(this.id,this.problemId,this.confidence,this.nextReviewAt); final String id,problemId; final int confidence; final DateTime nextReviewAt; }
class Dashboard { const Dashboard({required this.totalProblems,required this.easy,required this.medium,required this.hard,required this.dueReviewCount}); final int totalProblems,easy,medium,hard,dueReviewCount; }
abstract interface class TagRemote { Future<List<Tag>> listTags(); }
abstract interface class SolutionRemote { Future<List<Solution>> listSolutions(String problemId); }
abstract interface class ReviewRemote { Future<Page<Review>> history({String? problemId,int page=1}); }
abstract interface class DashboardRemote { Future<Dashboard> getDashboard(); }
abstract interface class TagRepository { Future<List<Tag>> list(); }
abstract interface class SolutionRepository { Future<List<Solution>> list(String problemId); }
abstract interface class ReviewRepository { Future<Page<Review>> history({String? problemId,int page=1}); }
abstract interface class DashboardRepository { Future<Dashboard> get(); }
class RemoteTagRepository implements TagRepository { RemoteTagRepository(this.remote); final TagRemote remote; @override Future<List<Tag>> list()=>remote.listTags(); }
class RemoteSolutionRepository implements SolutionRepository { RemoteSolutionRepository(this.remote); final SolutionRemote remote; @override Future<List<Solution>> list(String id)=>remote.listSolutions(id); }
class RemoteReviewRepository implements ReviewRepository { RemoteReviewRepository(this.remote); final ReviewRemote remote; @override Future<Page<Review>> history({String? problemId,int page=1})=>remote.history(problemId:problemId,page:page); }
class RemoteDashboardRepository implements DashboardRepository { RemoteDashboardRepository(this.remote); final DashboardRemote remote; @override Future<Dashboard> get()=>remote.getDashboard(); }
