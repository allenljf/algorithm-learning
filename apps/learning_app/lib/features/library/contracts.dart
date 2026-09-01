enum ProblemPlatform { leetcode, hackerRank, other }

enum ProblemDifficulty { easy, medium, hard }

enum ReviewStatus { neverReviewed, due, scheduled }

class Tag {
  const Tag(this.id, this.name);
  final String id;
  final String name;
}

class ProblemQuery {
  const ProblemQuery({
    this.query,
    this.difficulty,
    this.platform,
    this.tagIds = const [],
    this.reviewStatus,
    this.page = 1,
  });
  final String? query;
  final ProblemDifficulty? difficulty;
  final ProblemPlatform? platform;
  final List<String> tagIds;
  final ReviewStatus? reviewStatus;
  final int page;
  ProblemQuery copyWith({
    String? query,
    ProblemDifficulty? difficulty,
    ProblemPlatform? platform,
    List<String>? tagIds,
    ReviewStatus? reviewStatus,
    int? page,
  }) => ProblemQuery(
    query: query ?? this.query,
    difficulty: difficulty ?? this.difficulty,
    platform: platform ?? this.platform,
    tagIds: tagIds ?? this.tagIds,
    reviewStatus: reviewStatus ?? this.reviewStatus,
    page: page ?? this.page,
  );
}

class ProblemSummary {
  const ProblemSummary({
    required this.id,
    required this.title,
    required this.platform,
    required this.difficulty,
    required this.tags,
    required this.reviewStatus,
  });
  final String id, title;
  final ProblemPlatform platform;
  final ProblemDifficulty difficulty;
  final List<Tag> tags;
  final ReviewStatus reviewStatus;
}

class ProblemDetail extends ProblemSummary {
  const ProblemDetail({
    required super.id,
    required super.title,
    required super.platform,
    required super.difficulty,
    required super.tags,
    required super.reviewStatus,
    this.description,
    this.notes,
    this.keyInsight,
    this.timeComplexity,
    this.spaceComplexity,
    this.mistakes,
    this.interviewNotes,
    this.externalUrl,
    this.solutions = const [],
  });
  final String? description,
      notes,
      keyInsight,
      timeComplexity,
      spaceComplexity,
      mistakes,
      interviewNotes,
      externalUrl;
  final List<Solution> solutions;
  factory ProblemDetail.fromSummary(ProblemSummary value) => ProblemDetail(
    id: value.id,
    title: value.title,
    platform: value.platform,
    difficulty: value.difficulty,
    tags: value.tags,
    reviewStatus: value.reviewStatus,
  );
}

class ProblemWrite {
  const ProblemWrite({
    required this.title,
    required this.platform,
    required this.difficulty,
    this.description,
    this.notes,
    this.keyInsight,
    this.timeComplexity,
    this.spaceComplexity,
    this.mistakes,
    this.interviewNotes,
    this.tagIds = const [],
  });
  final String title;
  final ProblemPlatform platform;
  final ProblemDifficulty difficulty;
  final String? description,
      notes,
      keyInsight,
      timeComplexity,
      spaceComplexity,
      mistakes,
      interviewNotes;
  final List<String> tagIds;
}

class Solution {
  const Solution({
    required this.id,
    required this.problemId,
    required this.language,
    required this.code,
    this.explanation,
  });
  final String id, problemId, language, code;
  final String? explanation;
}

class SolutionWrite {
  const SolutionWrite({
    required this.language,
    required this.code,
    this.explanation,
  });
  final String language, code;
  final String? explanation;
}

class ApiFailure implements Exception {
  const ApiFailure(this.message, {this.fieldErrors = const {}});
  final String message;
  final Map<String, List<String>> fieldErrors;
}

class Page<T> {
  const Page({
    required this.items,
    required this.page,
    required this.pageSize,
    required this.totalItems,
  });
  final List<T> items;
  final int page, pageSize, totalItems;
  int get totalPages => totalItems == 0 ? 1 : (totalItems / pageSize).ceil();
}

abstract interface class ProblemRemote {
  Future<Page<ProblemSummary>> listProblems(ProblemQuery query);
  Future<ProblemDetail> getProblem(String id);
  Future<ProblemSummary> createProblem(ProblemWrite write);
  Future<ProblemSummary> replaceProblem(String id, ProblemWrite write);
  Future<void> deleteProblem(String id);
  Future<List<Tag>> listTags();
  Future<Tag> createTag(String name);
  Future<List<Solution>> listSolutions(String problemId);
  Future<Solution> createSolution(String problemId, SolutionWrite write);
  Future<Solution> replaceSolution(String id, SolutionWrite write);
  Future<void> deleteSolution(String id);
}

abstract interface class ProblemRepository {
  Future<Page<ProblemSummary>> list(ProblemQuery query);
  Future<ProblemDetail> get(String id);
  Future<ProblemSummary> create(ProblemWrite write);
  Future<ProblemSummary> replace(String id, ProblemWrite write);
  Future<void> delete(String id);
  Future<List<Tag>> listTags();
  Future<Tag> createTag(String name);
  Future<List<Solution>> listSolutions(String problemId);
  Future<Solution> createSolution(String problemId, SolutionWrite write);
  Future<Solution> replaceSolution(String id, SolutionWrite write);
  Future<void> deleteSolution(String id);
}

class RemoteProblemRepository implements ProblemRepository {
  RemoteProblemRepository(this._remote);
  final ProblemRemote _remote;
  @override
  Future<Page<ProblemSummary>> list(ProblemQuery query) =>
      _remote.listProblems(query);
  @override
  Future<ProblemDetail> get(String id) => _remote.getProblem(id);
  @override
  Future<ProblemSummary> create(ProblemWrite write) =>
      _remote.createProblem(write);
  @override
  Future<ProblemSummary> replace(String id, ProblemWrite write) =>
      _remote.replaceProblem(id, write);
  @override
  Future<void> delete(String id) => _remote.deleteProblem(id);
  @override
  Future<List<Tag>> listTags() => _remote.listTags();
  @override
  Future<Tag> createTag(String name) => _remote.createTag(name);
  @override
  Future<List<Solution>> listSolutions(String id) => _remote.listSolutions(id);
  @override
  Future<Solution> createSolution(String id, SolutionWrite write) =>
      _remote.createSolution(id, write);
  @override
  Future<Solution> replaceSolution(String id, SolutionWrite write) =>
      _remote.replaceSolution(id, write);
  @override
  Future<void> deleteSolution(String id) => _remote.deleteSolution(id);
}

class Review {
  const Review(this.id, this.problemId, this.confidence, this.nextReviewAt);
  final String id, problemId;
  final int confidence;
  final DateTime nextReviewAt;
}

class Dashboard {
  const Dashboard({
    required this.totalProblems,
    required this.easy,
    required this.medium,
    required this.hard,
    required this.dueReviewCount,
  });
  final int totalProblems, easy, medium, hard, dueReviewCount;
}

abstract interface class TagRemote {
  Future<List<Tag>> listTags();
}

abstract interface class SolutionRemote {
  Future<List<Solution>> listSolutions(String problemId);
}

abstract interface class ReviewRemote {
  Future<Page<Review>> history({String? problemId, int page = 1});
}

abstract interface class DashboardRemote {
  Future<Dashboard> getDashboard();
}

abstract interface class TagRepository {
  Future<List<Tag>> list();
}

abstract interface class SolutionRepository {
  Future<List<Solution>> list(String problemId);
}

abstract interface class ReviewRepository {
  Future<ProblemDetail> problem(String id);
  Future<Review> submit(String problemId, int confidence, String? notes);
  Future<Page<Review>> history({String? problemId, int page = 1});
}

abstract interface class DashboardRepository {
  Future<Dashboard> get();
}

class RemoteTagRepository implements TagRepository {
  RemoteTagRepository(this.remote);
  final TagRemote remote;
  @override
  Future<List<Tag>> list() => remote.listTags();
}

class RemoteSolutionRepository implements SolutionRepository {
  RemoteSolutionRepository(this.remote);
  final SolutionRemote remote;
  @override
  Future<List<Solution>> list(String id) => remote.listSolutions(id);
}

class RemoteReviewRepository implements ReviewRepository {
  RemoteReviewRepository(this.remote);
  final ReviewRemote remote;
  @override
  Future<ProblemDetail> problem(String id) => throw UnimplementedError();
  @override
  Future<Review> submit(String problemId, int confidence, String? notes) =>
      throw UnimplementedError();
  @override
  Future<Page<Review>> history({String? problemId, int page = 1}) =>
      remote.history(problemId: problemId, page: page);
}

class RemoteDashboardRepository implements DashboardRepository {
  RemoteDashboardRepository(this.remote);
  final DashboardRemote remote;
  @override
  Future<Dashboard> get() => remote.getDashboard();
}
