import 'package:flutter/material.dart' hide Page;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:learning_app/features/library/contracts.dart';
import 'package:learning_app/features/reviews/presentation/review_page.dart';

void main() {
  testWidgets('enforces disclosure order before confidence submission', (
    tester,
  ) async {
    final repo = FakeReviewRepository();
    await tester.pumpWidget(
      ProviderScope(
        overrides: [reviewRepositoryProvider.overrideWithValue(repo)],
        child: const MaterialApp(home: ReviewPage(problemId: 'p')),
      ),
    );
    await tester.pumpAndSettle();
    expect(find.text('Reveal hint'), findsNothing);
    expect(find.text('Rate confidence'), findsNothing);
    await tester.tap(find.text('Start thinking'));
    await tester.pump();
    await tester.tap(find.text('Reveal hint'));
    await tester.pump();
    await tester.tap(find.text('Reveal my approach'));
    await tester.pump();
    await tester.tap(find.text('Reveal solution'));
    await tester.pump();
    expect(find.text('Rate confidence'), findsOneWidget);
    await tester.tap(find.text('Rate confidence'));
    await tester.pump();
    await tester.tap(find.widgetWithText(ChoiceChip, '3'));
    await tester.pump();
    await tester.tap(find.widgetWithText(FilledButton, 'Submit review'));
    await tester.pumpAndSettle();
    expect(repo.submittedConfidence, 3);
  });
}

class FakeReviewRepository implements ReviewRepository {
  int? submittedConfidence;
  @override
  Future<ProblemDetail> problem(String id) async => const ProblemDetail(
    id: 'p',
    title: 'Two Sum',
    platform: ProblemPlatform.leetcode,
    difficulty: ProblemDifficulty.easy,
    tags: [],
    reviewStatus: ReviewStatus.neverReviewed,
    description: 'Summary',
    keyInsight: 'Hint',
    notes: 'Approach',
    solutions: [
      Solution(id: 's', problemId: 'p', language: 'dart', code: 'code'),
    ],
  );
  @override
  Future<Review> submit(String problemId, int confidence, String? notes) async {
    submittedConfidence = confidence;
    return Review('r', problemId, confidence, DateTime(2026));
  }

  @override
  Future<Page<Review>> history({String? problemId, int page = 1}) async =>
      const Page(items: [], page: 1, pageSize: 20, totalItems: 0);
}
