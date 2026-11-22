package app.plyrs1.patches.com_gajihub_app

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.resourcePatch
import app.plyrs1.patches.shared.Constants.COMPATIBILITY_GAJIHUB
import org.w3c.dom.Element

/**
 * Manifest modifications for Disable Telemetry:
 * - Injects meta-data tags to disable auto-initialization of Sentry, Firebase, and MLKit
 * - Disables SentryInitProvider and SentryPerformanceProvider
 * - Disables FirebaseInitProvider and ComponentDiscoveryService
 * - Disables OneSignal broadcast receivers, services, and sync tasks
 */
private val disableTelemetryManifestPatch = resourcePatch(
    name = null,
    description = "Internal: Telemetry suppression metadata, providers, and service disabling in AndroidManifest.xml",
    default = true
) {
    compatibleWith(COMPATIBILITY_GAJIHUB)

    execute {
        document("AndroidManifest.xml").use { doc ->
            val manifest = doc.documentElement
            val application = manifest.getElementsByTagName("application").item(0) as Element

            // 1. Meta-data flags for Sentry & Firebase auto-init suppression
            val metaDataFlags = mapOf(
                "io.sentry.auto-init" to "false",
                "firebase_crashlytics_collection_enabled" to "false",
                "firebase_analytics_collection_enabled" to "false",
                "firebase_performance_collection_enabled" to "false",
                "firebase_sessions_enabled" to "false"
            )

            val existingMetaData = application.getElementsByTagName("meta-data")
            val existingMetaMap = mutableMapOf<String, Element>()
            for (i in 0 until existingMetaData.length) {
                val elem = existingMetaData.item(i) as? Element ?: continue
                val name = elem.getAttribute("android:name")
                if (name.isNotEmpty()) {
                    existingMetaMap[name] = elem
                }
            }

            for ((name, value) in metaDataFlags) {
                val elem = existingMetaMap[name]
                if (elem != null) {
                    elem.setAttribute("android:value", value)
                } else {
                    val newElem = doc.createElement("meta-data")
                    newElem.setAttribute("android:name", name)
                    newElem.setAttribute("android:value", value)
                    application.appendChild(newElem)
                }
            }

            // 2. Disable Telemetry ContentProviders (Sentry & Firebase)
            val providersToDisable = setOf(
                "io.sentry.android.core.SentryInitProvider",
                "io.sentry.android.core.SentryPerformanceProvider",
                "com.google.firebase.provider.FirebaseInitProvider"
            )
            val providers = application.getElementsByTagName("provider")
            for (i in 0 until providers.length) {
                val p = providers.item(i) as? Element ?: continue
                if (p.getAttribute("android:name") in providersToDisable) {
                    p.setAttribute("android:enabled", "false")
                }
            }

            // 3. Disable OneSignal and Firebase background services
            val servicesToDisable = setOf(
                "com.onesignal.core.services.SyncJobService",
                "com.onesignal.notifications.services.HmsMessageServiceOneSignal",
                "com.google.firebase.components.ComponentDiscoveryService",
                "com.google.android.datatransport.runtime.backends.TransportBackendDiscovery",
                "com.google.android.datatransport.runtime.scheduling.jobscheduling.JobInfoSchedulerService"
            )
            val services = application.getElementsByTagName("service")
            for (i in 0 until services.length) {
                val s = services.item(i) as? Element ?: continue
                if (s.getAttribute("android:name") in servicesToDisable) {
                    s.setAttribute("android:enabled", "false")
                }
            }

            // 4. Disable OneSignal broadcast receivers
            val receiversToDisable = setOf(
                "com.onesignal.notifications.receivers.FCMBroadcastReceiver",
                "com.onesignal.notifications.receivers.NotificationDismissReceiver",
                "com.onesignal.notifications.receivers.BootUpReceiver",
                "com.onesignal.notifications.receivers.UpgradeReceiver",
                "com.google.firebase.iid.FirebaseInstanceIdReceiver"
            )
            val receivers = application.getElementsByTagName("receiver")
            for (i in 0 until receivers.length) {
                val r = receivers.item(i) as? Element ?: continue
                if (r.getAttribute("android:name") in receiversToDisable) {
                    r.setAttribute("android:enabled", "false")
                }
            }
        }
    }
}

@Suppress("unused")
val disableTelemetryPatch = bytecodePatch(
    name = "Disable Telemetry",
    description = "Disables third-party telemetry, crash reporting, and tracking services in GajiHub. " +
            "Suppresses Sentry native SDK initialization, OneSignal analytics and device tracking, " +
            "neutralizes root detection telemetry, and disables Sentry/Firebase background providers.",
    default = true
) {
    compatibleWith(COMPATIBILITY_GAJIHUB)
    dependsOn(disableTelemetryManifestPatch)

    execute {
        // 1. RNSentryModule.initNativeSdk(ReadableMap, Promise) -> immediately resolve true and return
        RNSentryInitNativeSdkFingerprint.method.addInstructions(
            0,
            """
                sget-object v0, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
                invoke-interface {p2, v0}, Lcom/facebook/react/bridge/Promise;->resolve(Ljava/lang/Object;)V
                return-void
            """
        )

        // 2. OneSignal.isRooted() -> return false (0x0)
        OneSignalIsRootedFingerprint.method.addInstructions(
            0,
            """
                const/4 v0, 0x0
                return v0
            """
        )

        // 3. RNSentryModule.captureEnvelope(String, ReadableMap, Promise) -> immediately resolve true and return
        RNSentryCaptureEnvelopeFingerprint.method.addInstructions(
            0,
            """
                sget-object v0, Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;
                invoke-interface {p3, v0}, Lcom/facebook/react/bridge/Promise;->resolve(Ljava/lang/Object;)V
                return-void
            """
        )

        // 4. RNSentryModule.captureScreenshot(Promise) -> immediately resolve null and return
        RNSentryCaptureScreenshotFingerprint.method.addInstructions(
            0,
            """
                const/4 v0, 0x0
                invoke-interface {p1, v0}, Lcom/facebook/react/bridge/Promise;->resolve(Ljava/lang/Object;)V
                return-void
            """
        )

        // 5. RNSentryModule.fetchViewHierarchy(Promise) -> immediately resolve null and return
        RNSentryFetchViewHierarchyFingerprint.method.addInstructions(
            0,
            """
                const/4 v0, 0x0
                invoke-interface {p1, v0}, Lcom/facebook/react/bridge/Promise;->resolve(Ljava/lang/Object;)V
                return-void
            """
        )

        // 6. OneSignal location sharing -> force isShared() to false
        OneSignalLocationIsSharedFingerprint.method.addInstructions(
            0,
            """
                const/4 v0, 0x0
                return v0
            """
        )

        // 7. OneSignal location tracker -> neutralize start() and setShared()
        OneSignalLocationStartFingerprint.method.addInstructions(
            0,
            "return-void"
        )
        OneSignalLocationSetSharedFingerprint.method.addInstructions(
            0,
            "return-void"
        )

        // 8. RNOneSignal outcomes -> neutralize addOutcome, addOutcomeWithValue, addUniqueOutcome
        RNOneSignalAddOutcomeFingerprint.method.addInstructions(
            0,
            "return-void"
        )
        RNOneSignalAddOutcomeWithValueFingerprint.method.addInstructions(
            0,
            "return-void"
        )
        RNOneSignalAddUniqueOutcomeFingerprint.method.addInstructions(
            0,
            "return-void"
        )

        // 9. CodePush deployment & rollback telemetry -> neutralize recordStatusReported, saveStatusReportForRetry
        CodePushRecordStatusReportedFingerprint.method.addInstructions(
            0,
            "return-void"
        )
        CodePushSaveStatusReportForRetryFingerprint.method.addInstructions(
            0,
            "return-void"
        )
    }
}
