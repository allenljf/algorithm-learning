import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:learning_app/features/dashboard/presentation/dashboard_page.dart';
import 'package:learning_app/features/library/contracts.dart';

void main() {
  testWidgets('shows no-data and populated dashboard states', (t) async {
    await t.pumpWidget(
      ProviderScope(
        overrides: [
          dashboardRepositoryProvider.overrideWithValue(FakeDashboard(0)),
        ],
        child: const MaterialApp(home: DashboardPage()),
      ),
    );
    await t.pumpAndSettle();
    expect(find.text('Add your first problem'), findsOneWidget);
    await t.pumpWidget(
      ProviderScope(
        overrides: [
          dashboardRepositoryProvider.overrideWithValue(FakeDashboard(2)),
        ],
        child: const MaterialApp(home: DashboardPage()),
      ),
    );
    await t.pumpAndSettle();
    expect(find.text('Total problems: 2'), findsOneWidget);
  });
}

class FakeDashboard implements DashboardRepository {
  FakeDashboard(this.total);
  final int total;
  @override
  Future<Dashboard> get() async => Dashboard(
    totalProblems: total,
    easy: 1,
    medium: 1,
    hard: 0,
    dueReviewCount: 1,
  );
}
