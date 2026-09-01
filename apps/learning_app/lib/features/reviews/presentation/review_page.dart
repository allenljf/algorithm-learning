import 'package:flutter/material.dart' hide Page;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:learning_app/features/library/contracts.dart';

final reviewRepositoryProvider = Provider<ReviewRepository>(
  (ref) => const _UnavailableReviews(),
);

class ReviewPage extends ConsumerStatefulWidget {
  const ReviewPage({super.key, required this.problemId});
  final String problemId;
  @override
  ConsumerState<ReviewPage> createState() => _ReviewPageState();
}

class _ReviewPageState extends ConsumerState<ReviewPage> {
  int stage = 0;
  int? confidence;
  AsyncValue<ProblemDetail> problem = const AsyncLoading();
  bool submitting = false;
  @override
  void initState() {
    super.initState();
    load();
  }

  Future<void> load() async {
    try {
      final p = await ref
          .read(reviewRepositoryProvider)
          .problem(widget.problemId);
      if (mounted) {
        setState(() => problem = AsyncData(p));
      }
    } catch (e, s) {
      if (mounted) {
        setState(() => problem = AsyncError(e, s));
      }
    }
  }

  Future<void> submit() async {
    if (confidence == null) return;
    setState(() => submitting = true);
    await ref
        .read(reviewRepositoryProvider)
        .submit(widget.problemId, confidence!, null);
    if (mounted) {
      setState(() => submitting = false);
    }
  }

  @override
  Widget build(BuildContext c) => Scaffold(
    appBar: AppBar(title: const Text('Review')),
    body: problem.when(
      loading: () => const Center(child: CircularProgressIndicator()),
      error: (error, stackTrace) => Center(
        child: TextButton(onPressed: load, child: const Text('Retry')),
      ),
      data: view,
    ),
  );
  Widget view(ProblemDetail p) {
    final actions = <Widget>[
      if (stage == 0) action('Start thinking'),
      if (stage == 1) action('Reveal hint'),
      if (stage == 2) action('Reveal my approach'),
      if (stage == 3) action('Reveal solution'),
      if (stage == 4) action('Rate confidence'),
      if (stage == 5) ...[
        Wrap(
          children: List.generate(
            5,
            (i) => ChoiceChip(
              label: Text('$i'),
              selected: confidence == i,
              onSelected: (_) => setState(() => confidence = i),
            ),
          ),
        ),
        FilledButton(
          onPressed: confidence == null || submitting ? null : submit,
          child: const Text('Submit review'),
        ),
      ],
    ];
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Text(p.title),
        if (p.description != null) Text(p.description!),
        if (stage >= 1) const Text('Think without looking at your notes.'),
        if (stage >= 2) Text(p.keyInsight ?? 'No hint saved'),
        if (stage >= 3) Text(p.notes ?? 'No approach saved'),
        if (stage >= 4) ...p.solutions.map((s) => Text(s.code)),
        ...actions,
      ],
    );
  }

  Widget action(String label) => FilledButton(
    onPressed: () => setState(() => stage++),
    child: Text(label),
  );
}

class _UnavailableReviews implements ReviewRepository {
  const _UnavailableReviews();
  Never fail() => throw const ApiFailure('Review service is not configured.');
  @override
  Future<ProblemDetail> problem(String id) async => fail();
  @override
  Future<Review> submit(String p, int c, String? n) async => fail();
  @override
  Future<Page<Review>> history({String? problemId, int page = 1}) async =>
      fail();
}
