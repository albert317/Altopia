import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    let helper = KoinHelper()
                    helper.deepLinkHandler.handleDeepLink(urlString: url.absoluteString) { error in
                        if let error = error {
                            print("DeepLink error: \(error)")
                        }
                    }
                }
        }
    }
}