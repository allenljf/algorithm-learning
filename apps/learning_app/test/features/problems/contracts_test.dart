import 'package:flutter_test/flutter_test.dart';
import 'package:learning_app/features/library/contracts.dart';

void main() {
  test('repository delegates paginated list contract', () async {
    final page = await RemoteProblemRepository(_Fake())
        .list(const ProblemQuery(query: 'sum'));
    expect(page.items.single.title, 'Two Sum');
  });
}

class _Fake implements ProblemRemote {
  const _Fake();
  static const value = ProblemSummary(
    id: 'p',
    title: 'Two Sum',
    platform: ProblemPlatform.leetcode,
    difficulty: ProblemDifficulty.easy,
    tags: [],
    reviewStatus: ReviewStatus.neverReviewed,
  );
  @override
  Future<Page<ProblemSummary>> listProblems(ProblemQuery query) async =>
      const Page(items: [value], page: 1, pageSize: 20, totalItems: 1);
  @override
  Future<ProblemDetail> getProblem(String id) async =>
      ProblemDetail.fromSummary(value);
  @override
  Future<ProblemSummary> createProblem(ProblemWrite write) async => value;
  @override
  Future<ProblemSummary> replaceProblem(String id, ProblemWrite write) async =>
      value;
  @override
  Future<void> deleteProblem(String id) async {}
  @override
  Future<List<Tag>> listTags() async => const [];
  @override
  Future<Tag> createTag(String name) async => Tag('tag', name);
  @override
  Future<List<Solution>> listSolutions(String problemId) async => const [];
  @override
  Future<Solution> createSolution(
    String problemId,
    SolutionWrite write,
  ) async => Solution(
    id: 's',
    problemId: problemId,
    language: write.language,
    code: write.code,
  );
  @override
  Future<Solution> replaceSolution(String id, SolutionWrite write) async =>
      Solution(
        id: id,
        problemId: 'p',
        language: write.language,
        code: write.code,
      );
  @override
  Future<void> deleteSolution(String id) async {}
}
