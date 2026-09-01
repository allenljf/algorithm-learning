import 'package:flutter/material.dart' hide Page;
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:learning_app/features/library/contracts.dart';

final problemRepositoryProvider = Provider<ProblemRepository>(
  (ref) => const _UnavailableProblemRepository(),
);

class ProblemsPage extends ConsumerStatefulWidget {
  const ProblemsPage({super.key});
  @override
  ConsumerState<ProblemsPage> createState() => _ProblemsPageState();
}

class _ProblemsPageState extends ConsumerState<ProblemsPage> {
  final _search = TextEditingController();
  final _title = TextEditingController();
  final _notes = TextEditingController();
  final _solutionCode = TextEditingController();
  final _tagName = TextEditingController();
  ProblemQuery _query = const ProblemQuery();
  AsyncValue<Page<ProblemSummary>> _list = const AsyncLoading();
  ProblemDetail? _detail;
  List<Tag> _tags = const [];
  List<Solution> _solutions = const [];
  Set<String> _tagIds = {};
  String? _editingId;
  Map<String, List<String>> _fieldErrors = const {};
  bool _saving = false;
  @override
  void initState() {
    super.initState();
    _load();
    _loadTags();
  }

  @override
  void dispose() {
    _search.dispose();
    _title.dispose();
    _notes.dispose();
    _solutionCode.dispose();
    _tagName.dispose();
    super.dispose();
  }

  ProblemRepository get _repository => ref.read(problemRepositoryProvider);
  Future<void> _load() async {
    setState(() => _list = const AsyncLoading());
    try {
      final page = await _repository.list(_query);
      if (mounted) setState(() => _list = AsyncData(page));
    } catch (error, stack) {
      if (mounted) setState(() => _list = AsyncError(error, stack));
    }
  }

  Future<void> _loadTags() async {
    try {
      final tags = await _repository.listTags();
      if (mounted) setState(() => _tags = tags);
    } catch (_) {}
  }

  Future<void> _open(String id) async {
    try {
      final detail = await _repository.get(id);
      final solutions = await _repository.listSolutions(id);
      if (mounted) {
        setState(() {
          _detail = detail;
          _solutions = solutions;
          _tagIds = detail.tags.map((tag) => tag.id).toSet();
          _editingId = null;
          _fieldErrors = const {};
        });
      }
    } on ApiFailure catch (failure) {
      _show(failure.message);
    }
  }

  void _newProblem() => setState(() {
    _editingId = '';
    _detail = null;
    _tagIds = {};
    _title.clear();
    _notes.clear();
    _fieldErrors = const {};
  });
  void _edit() => setState(() {
    _editingId = _detail!.id;
    _title.text = _detail!.title;
    _notes.text = _detail!.notes ?? '';
    _tagIds = _detail!.tags.map((tag) => tag.id).toSet();
    _fieldErrors = const {};
  });
  ProblemWrite _write() => ProblemWrite(
    title: _title.text.trim(),
    platform: _detail?.platform ?? ProblemPlatform.leetcode,
    difficulty: _detail?.difficulty ?? ProblemDifficulty.easy,
    notes: _nullable(_notes.text),
    tagIds: _tagIds.toList(),
  );
  Future<void> _save() async {
    if (_title.text.trim().isEmpty) {
      setState(
        () => _fieldErrors = const {
          'title': ['Title is required'],
        },
      );
      return;
    }
    setState(() => _saving = true);
    try {
      final saved = _editingId!.isEmpty
          ? await _repository.create(_write())
          : await _repository.replace(_editingId!, _write());
      if (!mounted) {
        return;
      }
      await _open(saved.id);
      await _load();
    } on ApiFailure catch (failure) {
      if (mounted) {
        setState(
          () => _fieldErrors = failure.fieldErrors.isEmpty
              ? {
                  'form': [failure.message],
                }
              : failure.fieldErrors,
        );
      }
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  Future<void> _deleteProblem() async {
    final detail = _detail;
    if (detail == null) return;
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Delete problem?'),
        content: const Text(
          'This also deletes its solutions and review records.',
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          FilledButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('Delete'),
          ),
        ],
      ),
    );
    if (confirmed != true) return;
    try {
      await _repository.delete(detail.id);
      if (!mounted) return;
      setState(() => _detail = null);
      await _load();
      _show('Problem deleted');
    } on ApiFailure catch (failure) {
      _show(failure.message);
    }
  }

  Future<void> _saveSolution() async {
    final detail = _detail;
    if (detail == null || _solutionCode.text.trim().isEmpty) {
      return;
    }
    setState(() => _saving = true);
    try {
      final saved = await _repository.createSolution(
        detail.id,
        SolutionWrite(language: 'dart', code: _solutionCode.text),
      );
      if (mounted) {
        setState(() {
          _solutions = [..._solutions, saved];
          _solutionCode.clear();
        });
      }
    } on ApiFailure catch (failure) {
      _show(failure.message);
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  Future<void> _deleteSolution(Solution solution) async {
    try {
      await _repository.deleteSolution(solution.id);
      if (mounted) {
        setState(
          () => _solutions = _solutions
              .where((item) => item.id != solution.id)
              .toList(),
        );
      }
    } on ApiFailure catch (failure) {
      _show(failure.message);
    }
  }

  Future<void> _createTag() async {
    final name = _tagName.text.trim();
    if (name.isEmpty) {
      return;
    }
    try {
      final tag = await _repository.createTag(name);
      if (mounted) {
        setState(() {
          _tags = [..._tags, tag];
          _tagName.clear();
        });
      }
    } on ApiFailure catch (failure) {
      _show(failure.message);
    }
  }

  void _show(String value) {
    if (mounted) {
      ScaffoldMessenger.of(context)
          .showSnackBar(SnackBar(content: Text(value)));
    }
  }

  @override
  Widget build(BuildContext context) => Scaffold(
    appBar: AppBar(
      title: Text(_detail == null ? 'Problems' : _detail!.title),
      actions: _detail == null
          ? [
              IconButton(
                tooltip: 'Refresh problems',
                icon: const Icon(Icons.refresh),
                onPressed: _load,
              ),
            ]
          : [
              IconButton(
                tooltip: 'Delete problem',
                icon: const Icon(Icons.delete),
                onPressed: _deleteProblem,
              ),
              IconButton(
                tooltip: 'Edit problem',
                icon: const Icon(Icons.edit),
                onPressed: _edit,
              ),
            ],
    ),
    floatingActionButton: _detail == null && _editingId == null
        ? FloatingActionButton.extended(
            onPressed: _newProblem,
            icon: const Icon(Icons.add),
            label: const Text('Add problem'),
          )
        : null,
    body: _editingId != null
        ? _editor()
        : _detail != null
        ? _detailView()
        : _library(),
  );
  Widget _library() => Column(
    children: [
      Padding(
        padding: const EdgeInsets.all(16),
        child: Wrap(
          spacing: 12,
          runSpacing: 8,
          children: [
            SizedBox(
              width: 280,
              child: TextField(
                key: const Key('search'),
                controller: _search,
                onSubmitted: (value) {
                  _query = _query.copyWith(query: value.trim(), page: 1);
                  _load();
                },
                decoration: const InputDecoration(
                  labelText: 'Search',
                  suffixIcon: Icon(Icons.search),
                ),
              ),
            ),
            DropdownButton<ProblemDifficulty>(
              hint: const Text('Difficulty'),
              value: _query.difficulty,
              items: ProblemDifficulty.values
                  .map(
                    (item) =>
                        DropdownMenuItem(value: item, child: Text(item.name)),
                  )
                  .toList(),
              onChanged: (value) {
                _query = _query.copyWith(difficulty: value, page: 1);
                _load();
              },
            ),
            DropdownButton<ReviewStatus>(
              hint: const Text('Review status'),
              value: _query.reviewStatus,
              items: ReviewStatus.values
                  .map(
                    (item) =>
                        DropdownMenuItem(value: item, child: Text(item.name)),
                  )
                  .toList(),
              onChanged: (value) {
                _query = _query.copyWith(reviewStatus: value, page: 1);
                _load();
              },
            ),
            DropdownButton<ProblemPlatform>(
              hint: const Text('Platform'),
              value: _query.platform,
              items: ProblemPlatform.values
                  .map(
                    (item) =>
                        DropdownMenuItem(value: item, child: Text(item.name)),
                  )
                  .toList(),
              onChanged: (value) {
                _query = _query.copyWith(platform: value, page: 1);
                _load();
              },
            ),
            ..._tags.map(
              (tag) => FilterChip(
                label: Text(tag.name),
                selected: _query.tagIds.contains(tag.id),
                onSelected: (selected) {
                  final tagIds = [..._query.tagIds];
                  selected ? tagIds.add(tag.id) : tagIds.remove(tag.id);
                  _query = _query.copyWith(tagIds: tagIds, page: 1);
                  _load();
                },
              ),
            ),
          ],
        ),
      ),
      Expanded(
        child: _list.when(
          loading: () => const Center(
            key: Key('problems-loading'),
            child: CircularProgressIndicator(),
          ),
          error: (error, _) => Center(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(
                  error is ApiFailure
                      ? error.message
                      : 'Unable to load problems',
                ),
                FilledButton(onPressed: _load, child: const Text('Retry')),
              ],
            ),
          ),
          data: (page) {
            if (page.items.isEmpty) {
              return Center(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Text('No problems yet'),
                    FilledButton(
                      onPressed: _newProblem,
                      child: const Text('Add problem'),
                    ),
                  ],
                ),
              );
            }
            return ListView.builder(
              itemCount: page.items.length + 1,
              itemBuilder: (context, index) {
                if (index == page.items.length) {
                  return Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      TextButton(
                        onPressed: _query.page > 1
                            ? () {
                                _query = _query.copyWith(page: _query.page - 1);
                                _load();
                              }
                            : null,
                        child: const Text('Previous page'),
                      ),
                      TextButton(
                        onPressed: _query.page < page.totalPages
                            ? () {
                                _query = _query.copyWith(page: _query.page + 1);
                                _load();
                              }
                            : null,
                        child: const Text('Next page'),
                      ),
                    ],
                  );
                }
                final item = page.items[index];
                return ListTile(
                  key: ValueKey(item.id),
                  title: Text(item.title),
                  subtitle: Text(
                    '${item.difficulty.name} · ${item.tags.map((tag) => tag.name).join(', ')}',
                  ),
                  onTap: () => _open(item.id),
                );
              },
            );
          },
        ),
      ),
    ],
  );
  Widget _editor() => ListView(
    padding: const EdgeInsets.all(16),
    children: [
      Text(
        _editingId!.isEmpty ? 'New problem' : 'Edit problem',
        style: Theme.of(context).textTheme.headlineSmall,
      ),
      TextField(
        key: const Key('problem-title'),
        controller: _title,
        decoration: InputDecoration(
          labelText: 'Title',
          errorText: _fieldErrors['title']?.join(', '),
        ),
      ),
      TextField(
        controller: _notes,
        minLines: 3,
        maxLines: 6,
        decoration: InputDecoration(
          labelText: 'My Approach',
          errorText: _fieldErrors['notes']?.join(', '),
        ),
      ),
      if (_fieldErrors['form'] != null)
        Text(
          _fieldErrors['form']!.join(', '),
          style: TextStyle(color: Theme.of(context).colorScheme.error),
        ),
      Wrap(
        children: _tags
            .map(
              (tag) => FilterChip(
                label: Text(tag.name),
                selected: _tagIds.contains(tag.id),
                onSelected: (selected) => setState(
                  () => selected ? _tagIds.add(tag.id) : _tagIds.remove(tag.id),
                ),
              ),
            )
            .toList(),
      ),
      Row(
        children: [
          Expanded(
            child: TextField(
              controller: _tagName,
              decoration: const InputDecoration(labelText: 'New tag'),
            ),
          ),
          IconButton(
            tooltip: 'Create tag',
            icon: const Icon(Icons.add),
            onPressed: _createTag,
          ),
        ],
      ),
      const SizedBox(height: 16),
      Wrap(
        spacing: 8,
        children: [
          FilledButton(
            onPressed: _saving ? null : _save,
            child: const Text('Save problem'),
          ),
          TextButton(
            onPressed: () => setState(() => _editingId = null),
            child: const Text('Cancel'),
          ),
        ],
      ),
    ],
  );
  Widget _detailView() {
    final detail = _detail!;
    return ListView(
      padding: const EdgeInsets.all(16),
      children: [
        Text(detail.title, style: Theme.of(context).textTheme.headlineMedium),
        Text(detail.difficulty.name),
        Wrap(
          spacing: 8,
          children: detail.tags
              .map((tag) => Chip(label: Text(tag.name)))
              .toList(),
        ),
        _section('Problem summary', detail.description),
        _section('My Approach', detail.notes),
        _section('Key Insight', detail.keyInsight),
        _section('Time Complexity', detail.timeComplexity),
        _section('Space Complexity', detail.spaceComplexity),
        _section('Common Mistakes', detail.mistakes),
        _section('Interview Notes', detail.interviewNotes),
        const Divider(),
        Text('Solutions', style: Theme.of(context).textTheme.titleLarge),
        ..._solutions.map(
          (solution) => Card(
            child: ListTile(
              title: Text(solution.language),
              subtitle: Text(
                solution.explanation == null
                    ? solution.code
                    : '${solution.explanation}\n${solution.code}',
              ),
              trailing: IconButton(
                tooltip: 'Delete solution',
                icon: const Icon(Icons.delete_outline),
                onPressed: () => _deleteSolution(solution),
              ),
            ),
          ),
        ),
        TextField(
          key: const Key('solution-code'),
          controller: _solutionCode,
          minLines: 3,
          maxLines: 8,
          decoration: const InputDecoration(labelText: 'Solution code'),
        ),
        FilledButton(
          onPressed: _saving ? null : _saveSolution,
          child: const Text('Save solution'),
        ),
      ],
    );
  }

  Widget _section(String title, String? value) => value == null || value.isEmpty
      ? const SizedBox.shrink()
      : Padding(
          padding: const EdgeInsets.only(top: 16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(title, style: Theme.of(context).textTheme.titleMedium),
              Text(value),
            ],
          ),
        );
}

String? _nullable(String value) => value.trim().isEmpty ? null : value;

class _UnavailableProblemRepository implements ProblemRepository {
  const _UnavailableProblemRepository();
  Never _fail() => throw const ApiFailure('Problem service is not configured.');
  @override
  Future<Page<ProblemSummary>> list(ProblemQuery query) async => _fail();
  @override
  Future<ProblemDetail> get(String id) async => _fail();
  @override
  Future<ProblemSummary> create(ProblemWrite write) async => _fail();
  @override
  Future<ProblemSummary> replace(String id, ProblemWrite write) async =>
      _fail();
  @override
  Future<void> delete(String id) async => _fail();
  @override
  Future<List<Tag>> listTags() async => _fail();
  @override
  Future<Tag> createTag(String name) async => _fail();
  @override
  Future<List<Solution>> listSolutions(String problemId) async => _fail();
  @override
  Future<Solution> createSolution(
    String problemId,
    SolutionWrite write,
  ) async => _fail();
  @override
  Future<Solution> replaceSolution(String id, SolutionWrite write) async =>
      _fail();
  @override
  Future<void> deleteSolution(String id) async => _fail();
}
