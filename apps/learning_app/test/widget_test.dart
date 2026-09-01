import 'package:flutter_test/flutter_test.dart';
import 'package:learning_app/main.dart';

void main() {
  testWidgets('renders the application shell', (tester) async {
    await tester.pumpWidget(const LearningApp());
    expect(find.text('Algorithm Learning'), findsOneWidget);
  });
}
