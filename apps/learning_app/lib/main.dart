import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:learning_app/features/problems/presentation/problems_page.dart';

void main() => runApp(const ProviderScope(child: LearningApp()));

final _router = GoRouter(routes: [
  GoRoute(path: '/', builder: (_, _) => const _PlaceholderPage()),
  GoRoute(path: '/login', builder: (_, _) => const _PlaceholderPage()),
  GoRoute(path: '/register', builder: (_, _) => const _PlaceholderPage()),
  GoRoute(path: '/problems', builder: (_, _) => const ProblemsPage()),
]);

class LearningApp extends StatelessWidget {
  const LearningApp({super.key});
  @override
  Widget build(BuildContext context) => MaterialApp.router(
    title: 'Algorithm Learning', routerConfig: _router,
    localizationsDelegates: GlobalMaterialLocalizations.delegates,
    supportedLocales: const [Locale('en'), Locale('zh', 'TW')],
    theme: ThemeData(colorSchemeSeed: Colors.indigo, useMaterial3: true),
    darkTheme: ThemeData(colorSchemeSeed: Colors.indigo, brightness: Brightness.dark, useMaterial3: true),
  );
}

class _PlaceholderPage extends StatelessWidget {
  const _PlaceholderPage();
  @override
  Widget build(BuildContext context) => const Scaffold(body: Center(child: Text('Algorithm Learning')));
}
