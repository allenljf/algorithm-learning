import 'package:flutter/material.dart' hide Page;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:learning_app/features/library/contracts.dart';
import 'package:learning_app/features/problems/presentation/problems_page.dart';

void main() {
  testWidgets('renders loading, error, empty, and paged data states', (
    tester,
  ) async {
    final repository = FakeProblems();
    await tester.pumpWidget(_app(repository));
    expect(find.byKey(const Key('problems-loading')), findsOneWidget);
    await tester.pumpAndSettle();
    expect(find.text('No problems yet'), findsOneWidget);
    repository.failure = const ApiFailure('Network unavailable');
    await tester.tap(find.byTooltip('Refresh problems'));
    await tester.pumpAndSettle();
    expect(find.text('Network unavailable'), findsOneWidget);
    repository.failure = null;
    repository.items = [_summary('p1', 'Two Sum'), _summary('p2', 'Three Sum')];
    await tester.tap(find.text('Retry'));
    await tester.pumpAndSettle();
    expect(find.text('Two Sum'), findsOneWidget);
    expect(find.text('Next page'), findsOneWidget);
  });

  testWidgets('keeps draft and displays server field errors', (tester) async {
    final repository = FakeProblems()
      ..createFailure = const ApiFailure(
        'Invalid request',
        fieldErrors: {
          'title': ['must be unique'],
        },
      );
    await tester.pumpWidget(_app(repository));
    await tester.pumpAndSettle();
    await tester.tap(find.byType(FloatingActionButton));
    await tester.pump();
    await tester.enterText(find.byKey(const Key('problem-title')), 'Two Sum');
    await tester.tap(find.text('Save problem'));
    await tester.pumpAndSettle();
    expect(find.text('must be unique'), findsOneWidget);
    expect(find.text('Two Sum'), findsOneWidget);
  });

  testWidgets('saves independent solutions and confirms deletion', (
    tester,
  ) async {
    final repository = FakeProblems()
      ..items = [_summary('p1', 'Two Sum')]
      ..details['p1'] = ProblemDetail(
        id: 'p1',
        title: 'Two Sum',
        platform: ProblemPlatform.leetcode,
        difficulty: ProblemDifficulty.easy,
        tags: const [],
        reviewStatus: ReviewStatus.neverReviewed,
        notes: 'Use a hash map',
      );
    await tester.pumpWidget(_app(repository));
    await tester.pumpAndSettle();
    await tester.tap(find.text('Two Sum'));
    await tester.pumpAndSettle();
    expect(find.text('My Approach'), findsOneWidget);
    await tester.enterText(
      find.byKey(const Key('solution-code')),
      'return map[target - n];',
    );
    await tester.tap(find.text('Save solution'));
    await tester.pumpAndSettle();
    expect(find.text('return map[target - n];'), findsOneWidget);
    await tester.tap(find.byTooltip('Delete problem'));
    await tester.pumpAndSettle();
    expect(find.text('Delete problem?'), findsOneWidget);
    await tester.tap(find.text('Delete'));
    await tester.pumpAndSettle();
    expect(repository.deleted, ['p1']);
  });
}

Widget _app(FakeProblems repository) => ProviderScope(
  overrides: [problemRepositoryProvider.overrideWithValue(repository)],
  child: const MaterialApp(home: ProblemsPage()),
);

ProblemSummary _summary(String id, String title) => ProblemSummary(
  id: id,
  title: title,
  platform: ProblemPlatform.leetcode,
  difficulty: ProblemDifficulty.easy,
  tags: const [],
  reviewStatus: ReviewStatus.neverReviewed,
);

class FakeProblems implements ProblemRepository {
  List<ProblemSummary> items = [];
  ApiFailure? failure;
  ApiFailure? createFailure;
  final List<String> deleted = [];
  final Map<String, ProblemDetail> details = {};
  final Map<String, List<Solution>> solutions = {};
  @override
  Future<Page<ProblemSummary>> list(ProblemQuery query) async {
    if (failure != null) throw failure!;
    return Page(
      items: items,
      page: query.page,
      pageSize: 20,
      totalItems: items.length,
    );
  }

  @override
  Future<ProblemDetail> get(String id) async => details.putIfAbsent(
    id,
    () => ProblemDetail.fromSummary(items.firstWhere((item) => item.id == id)),
  );
  @override
  Future<ProblemSummary> create(ProblemWrite write) async {
    if (createFailure != null) throw createFailure!;
    final item = _summary('created', write.title);
    items = [...items, item];
    return item;
  }

  @override
  Future<ProblemSummary> replace(String id, ProblemWrite write) async =>
      _summary(id, write.title);
  @override
  Future<void> delete(String id) async {
    deleted.add(id);
    items = items.where((item) => item.id != id).toList();
  }

  @override
  Future<List<Tag>> listTags() async => const [Tag('tag-1', 'array')];
  @override
  Future<Tag> createTag(String name) async => Tag('new-tag', name);
  @override
  Future<List<Solution>> listSolutions(String problemId) async =>
      solutions[problemId] ?? const [];
  @override
  Future<Solution> createSolution(String problemId, SolutionWrite write) async {
    final value = Solution(
      id: 'solution-${(solutions[problemId] ?? []).length}',
      problemId: problemId,
      language: write.language,
      code: write.code,
      explanation: write.explanation,
    );
    solutions[problemId] = [...(solutions[problemId] ?? []), value];
    return value;
  }

  @override
  Future<Solution> replaceSolution(String id, SolutionWrite write) async =>
      throw UnimplementedError();
  @override
  Future<void> deleteSolution(String id) async {}
}
