import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:learning_app/features/library/contracts.dart';

final dashboardRepositoryProvider = Provider<DashboardRepository>(
  (ref) => const _UnavailableDashboard(),
);

class DashboardPage extends ConsumerWidget {
  const DashboardPage({super.key});
  @override
  Widget build(BuildContext c, WidgetRef ref) => FutureBuilder<Dashboard>(
    future: ref.read(dashboardRepositoryProvider).get(),
    builder: (c, s) {
      if (s.connectionState != ConnectionState.done) {
        return const Scaffold(body: Center(child: CircularProgressIndicator()));
      }
      if (s.hasError) {
        return Scaffold(
          body: Center(
            child: TextButton(
              onPressed: () => ref.invalidate(dashboardRepositoryProvider),
              child: const Text('Retry'),
            ),
          ),
        );
      }
      final d = s.data!;
      if (d.totalProblems == 0) {
        return const Scaffold(
          body: Center(child: Text('Add your first problem')),
        );
      }
      return Scaffold(
        appBar: AppBar(title: const Text('Home')),
        body: ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Text('Total problems: ${d.totalProblems}'),
            Text('Easy: ${d.easy}'),
            Text('Medium: ${d.medium}'),
            Text('Hard: ${d.hard}'),
            Text('Due reviews: ${d.dueReviewCount}'),
          ],
        ),
      );
    },
  );
}

class _UnavailableDashboard implements DashboardRepository {
  const _UnavailableDashboard();
  @override
  Future<Dashboard> get() async =>
      throw const ApiFailure('Dashboard service is not configured.');
}
