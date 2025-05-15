import CoreHaptics
import ComposeApp

enum HapticCategory {
    case a
    case b

    var hapticEvents: [CHHapticEvent] {
        switch self {
        case .a:
            let eventType = CHHapticEvent.EventType.hapticContinuous
            let parameters: [CHHapticEvent.Parameter] = []
            return [
                CHHapticEvent(eventType: eventType, parameters: parameters, relativeTime: 0.3, duration: 0.5),
                CHHapticEvent(eventType: eventType, parameters: parameters, relativeTime: 0.9, duration: 0.5),
                CHHapticEvent(eventType: eventType, parameters: parameters, relativeTime: 1.5, duration: 0.5),
            ]
        case .b:
            return [CHHapticEvent(eventType: .hapticContinuous, parameters: [], relativeTime: 0.1, duration: 3)]
        }
    }
}

class CustomHaptic  {
    private var engine: CHHapticEngine?
}

private extension CustomHaptic {

    // Play haptic feedback based on the specified category
    func playHaptic(category: HapticCategory) {
        // Will be implemented below
        resetEngine()

        // Return if the device does not support haptics
        guard CHHapticEngine.capabilitiesForHardware().supportsHaptics else { return }
        do {
            let pattern = try CHHapticPattern(events: category.hapticEvents, parameters: [])
            let player = try engine?.makePlayer(with: pattern)
            try player?.start(atTime: 0)
        } catch {
            // Error occurred due to an abnormal CHHapticEvent structure
            print("An error occurred due to an abnormal CHHapticEvent structure \(error.localizedDescription).")
        }
    }

    // Reset the haptic engine
    func resetEngine() {
        engine = try? CHHapticEngine()
        try? engine?.start()

        setStopHandler()
        setResetHandler()
    }

    // Set the stop handler for the haptic engine
    func setStopHandler() {
        engine?.stoppedHandler = { reason in
            // The engine has stopped
            print("The engine has stopped \(reason)")
        }
    }

    func setResetHandler() {
        engine?.resetHandler = { [weak self] in
            do {
                try self?.engine?.start()
            } catch {
                // Failed to restart
                print("Failed to restart \(error)")
            }
        }
    }
}