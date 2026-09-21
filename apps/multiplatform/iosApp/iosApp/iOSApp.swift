import ComposeApp
import SwiftUI

@main
struct IOSApp: App {
    var body: some Scene {
        WindowGroup {
            ComposeView()
                .ignoresSafeArea(.keyboard)
        }
    }
}

private struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(defaultApiBaseUrl: configuredApiBaseUrl())
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}

    private func configuredApiBaseUrl() -> String {
        (Bundle.main.object(forInfoDictionaryKey: "API_BASE_URL") as? String)
            ?? "https://algorithm-learning-api-qvepavg7qa-de.run.app"
    }
}
