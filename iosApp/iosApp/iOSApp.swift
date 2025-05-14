import SwiftUI
import ComposeApp

@main
struct iOSApp: App {

   init() {
       NapierProxyKt.iosInitLog()
    }

    var body: some Scene {
         WindowGroup {
            ContentView().edgesIgnoringSafeArea(.all)
         }
     }
}