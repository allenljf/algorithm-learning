import 'package:flutter/material.dart' hide Page;
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:learning_app/features/library/contracts.dart';
import 'package:learning_app/features/problems/presentation/problems_page.dart';
import 'package:learning_app/features/reviews/presentation/review_page.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();

  testWidgets(
    'runs the controlled MVP create, edit, browse, and review journey',
    (tester) async {
      final api = ControlledMvpApi();

      await tester.pumpWidget(
        ProviderScope(
          overrides: [problemRepositoryProvider.overrideWithValue(api)],
          child: const MaterialApp(home: ProblemsPage()),
        ),
      );
      await tester.pumpAndSettle();

      await tester.tap(find.byType(FloatingActionButton));
      await tester.pump();
      await tester.enterText(find.byKey(const Key('problem-title')), 'Two Sum');
      await tester.tap(find.text('Save problem'));
      await tester.pumpAndSettle();
      expect(find.text('Two Sum'), findsWidgets);

      await tester.tap(find.byTooltip('Edit problem'));
      await tester.pump();
      await tester.enterText(
        find.byKey(const Key('problem-title')),
        'Two Sum updated',
      );
      await tester.tap(find.text('Save problem'));
      await tester.pumpAndSettle();
      expect(find.text('Two Sum updated'), findsWidgets);

      await tester.pumpWidget(const SizedBox.shrink());
      await tester.pump();
      await tester.pumpWidget(
        ProviderScope(
          overrides: [reviewRepositoryProvider.overrideWithValue(api)],
          child: const MaterialApp(home: ReviewPage(problemId: 'problem-1')),
        ),
      );
      await tester.pumpAndSettle();
      await tester.tap(find.text('Start thinking'));
      await tester.pump();
      await tester.tap(find.text('Reveal hint'));
      await tester.pump();
      await tester.tap(find.text('Reveal my approach'));
      await tester.pump();
      await tester.tap(find.text('Reveal solution'));
      await tester.pump();
      await tester.tap(find.text('Rate confidence'));
      await tester.pump();
      await tester.tap(find.widgetWithText(ChoiceChip, '3'));
      await tester.pump();
      await tester.tap(find.widgetWithText(FilledButton, 'Submit review'));
      await tester.pumpAndSettle();

      expect(api.submittedConfidence, 3);
    },
  );
}

class ControlledMvpApi implements ProblemRepository, ReviewRepository {
  ProblemDetail? _problem;
  int? submittedConfidence;

  ProblemDetail _requireProblem(String id) {
    final problem = _problem;
    if (problem == null || problem.id != id) {
      throw const ApiFailure('Problem not found');
    }
    return problem;
  }

  @override
  Future<ProblemSummary> create(ProblemWrite write) async {
    _problem = _detail('problem-1', write);
    return _problem!;
  }

  @override
  Future<ProblemSummary> replace(String id, ProblemWrite write) async {
    _requireProblem(id);
    _problem = _detail(id, write);
    return _problem!;
  }

  ProblemDetail _detail(String id, ProblemWrite write) => ProblemDetail(
    id: id,
    title: write.title,
    platform: write.platform,
    difficulty: write.difficulty,
    tags: const [],
    reviewStatus: ReviewStatus.neverReviewed,
    description: 'Personal summary',
    keyInsight: 'Use a hash map',
    notes: write.notes ?? 'Track complements',
    solutions: const [
      Solution(
        id: 'solution-1',
        problemId: 'problem-1',
        language: 'dart',
        code: 'return map[target - value];',
      ),
    ],
  );

  @override
  Future<ProblemDetail> get(String id) async => _requireProblem(id);

  @override
  Future<Page<ProblemSummary>> list(ProblemQuery query) async {
    final problem = _problem;
    final items = problem == null ? const <ProblemSummary>[] : [problem];
    return Page(
      items: items,
      page: query.page,
      pageSize: 20,
      totalItems: items.length,
    );
  }

  @override
  Future<ProblemDetail> problem(String id) async => _requireProblem(id);

  @override
  Future<Review> submit(String problemId, int confidence, String? notes) async {
    _requireProblem(problemId);
    submittedConfidence = confidence;
    return Review('review-1', problemId, confidence, DateTime.utc(2026, 9, 11));
  }

  @override
  Future<void> delete(String id) async => _problem = null;
  @override
  Future<List<Tag>> listTags() async => const [];
  @override
  Future<Tag> createTag(String name) async => Tag('tag-1', name);
  @override
  Future<List<Solution>> listSolutions(String problemId) async =>
      _requireProblem(problemId).solutions;
  @override
  Future<Solution> createSolution(
    String problemId,
    SolutionWrite write,
  ) async => throw UnimplementedError();
  @override
  Future<Solution> replaceSolution(String id, SolutionWrite write) async =>
      throw UnimplementedError();
  @override
  Future<void> deleteSolution(String id) async {}
  @override
  Future<Page<Review>> history({String? problemId, int page = 1}) async =>
      const Page(items: [], page: 1, pageSize: 20, totalItems: 0);
}
