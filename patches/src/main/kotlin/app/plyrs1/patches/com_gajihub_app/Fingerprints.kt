package app.plyrs1.patches.com_gajihub_app

import app.morphe.patcher.Fingerprint

/**
 * Matches LocationSerializationHelper.toWritableMap(Location) [i3/i;->i(Location)]
 * Serializes Android Location into React Native WritableMap with coordinates and "mocked" flag.
 */
object LocationSerializationFingerprint : Fingerprint(
    definingClass = "Li3/i;",
    name = "i",
    returnType = "Lcom/facebook/react/bridge/WritableMap;",
    parameters = listOf("Landroid/location/Location;"),
    strings = listOf("latitude", "longitude", "coords", "provider", "timestamp", "mocked")
)

/**
 * Matches MapView location change callback [com/rnmaps/maps/z$k;->a(Location)]
 * Serializes location for react-native-maps with "isFromMockProvider" flag.
 */
object MapViewLocationChangeFingerprint : Fingerprint(
    definingClass = "Lcom/rnmaps/maps/z\$k;",
    name = "a",
    returnType = "V",
    parameters = listOf("Landroid/location/Location;"),
    strings = listOf("coordinate", "isFromMockProvider")
)

/**
 * Matches Google Play Services / Android client check for User build type [C6/h;->b()]
 * Checks if Build.TYPE equals "user".
 */
object BuildTypeCheckFingerprint : Fingerprint(
    definingClass = "LC6/h;",
    name = "b",
    returnType = "Z",
    strings = listOf("user")
)

/**
 * Matches RNDeviceModule.isEmulatorSync()
 * Detects emulator environment based on Build fingerprints, models, and hardware strings.
 */
object RNDeviceModuleIsEmulatorSyncFingerprint : Fingerprint(
    definingClass = "Lcom/learnium/RNDeviceInfo/RNDeviceModule;",
    name = "isEmulatorSync",
    returnType = "Z",
    strings = listOf("generic", "google_sdk", "Emulator")
)

/**
 * Matches RNDeviceModule.isEmulator(Promise)
 * Asynchronous React Native bridge method for emulator check.
 */
object RNDeviceModuleIsEmulatorFingerprint : Fingerprint(
    definingClass = "Lcom/learnium/RNDeviceInfo/RNDeviceModule;",
    name = "isEmulator",
    returnType = "V",
    parameters = listOf("Lcom/facebook/react/bridge/Promise;")
)

/**
 * Matches OneSignal root detection helper [com/onesignal/common/i;->isRooted()]
 * Checks for presence of "su" binary across standard paths.
 */
object OneSignalIsRootedFingerprint : Fingerprint(
    definingClass = "Lcom/onesignal/common/i;",
    name = "isRooted",
    returnType = "Z",
    strings = listOf("su", "/sbin/", "/system/bin/")
)

/**
 * Matches SoLoader SysUtil.j() returning supported ABIs [com/facebook/soloader/SysUtil;->j()[Ljava/lang/String;]
 * Overridden to return ["arm64-v8a"] so DirectApkSoSource finds native libraries on x86_64 emulators.
 */
object SoLoaderGetSupportedAbisFingerprint : Fingerprint(
    definingClass = "Lcom/facebook/soloader/SysUtil\$MarshmallowSysdeps;",
    name = "getSupportedAbis",
    returnType = "[Ljava/lang/String;"
)

/**
 * Matches RNSentryModule.initNativeSdk(ReadableMap, Promise)
 * Neutralized to prevent Sentry React Native bridge from initializing native crash/event reporting.
 */
object RNSentryInitNativeSdkFingerprint : Fingerprint(
    definingClass = "Lio/sentry/react/RNSentryModule;",
    name = "initNativeSdk",
    returnType = "V",
    parameters = listOf("Lcom/facebook/react/bridge/ReadableMap;", "Lcom/facebook/react/bridge/Promise;")
)

/**
 * Matches RNSentryModule.captureEnvelope(String, ReadableMap, Promise)
 * Suppresses JS crash and error envelope submission to Sentry.
 */
object RNSentryCaptureEnvelopeFingerprint : Fingerprint(
    definingClass = "Lio/sentry/react/RNSentryModule;",
    name = "captureEnvelope",
    returnType = "V",
    parameters = listOf("Ljava/lang/String;", "Lcom/facebook/react/bridge/ReadableMap;", "Lcom/facebook/react/bridge/Promise;")
)

/**
 * Matches RNSentryModule.captureScreenshot(Promise)
 * Suppresses Sentry automatic screenshot capture on error.
 */
object RNSentryCaptureScreenshotFingerprint : Fingerprint(
    definingClass = "Lio/sentry/react/RNSentryModule;",
    name = "captureScreenshot",
    returnType = "V",
    parameters = listOf("Lcom/facebook/react/bridge/Promise;")
)

/**
 * Matches RNSentryModule.fetchViewHierarchy(Promise)
 * Suppresses Sentry UI view tree scraping on error.
 */
object RNSentryFetchViewHierarchyFingerprint : Fingerprint(
    definingClass = "Lio/sentry/react/RNSentryModule;",
    name = "fetchViewHierarchy",
    returnType = "V",
    parameters = listOf("Lcom/facebook/react/bridge/Promise;")
)

/**
 * Matches OneSignal location sharing flag [com/onesignal/location/internal/a;->isShared()Z]
 * Forced to false so OneSignal never collects or beacons GPS coordinates.
 */
object OneSignalLocationIsSharedFingerprint : Fingerprint(
    definingClass = "Lcom/onesignal/location/internal/a;",
    name = "isShared",
    returnType = "Z"
)

/**
 * Matches OneSignal location tracker startup [com/onesignal/location/internal/a;->start()V]
 * Neutralized so OneSignal does not subscribe to location permissions.
 */
object OneSignalLocationStartFingerprint : Fingerprint(
    definingClass = "Lcom/onesignal/location/internal/a;",
    name = "start",
    returnType = "V"
)

/**
 * Matches OneSignal location sharing setter [com/onesignal/location/internal/a;->setShared(Z)V]
 * Neutralized so location sharing cannot be enabled.
 */
object OneSignalLocationSetSharedFingerprint : Fingerprint(
    definingClass = "Lcom/onesignal/location/internal/a;",
    name = "setShared",
    returnType = "V",
    parameters = listOf("Z")
)

/**
 * Matches RNOneSignal.addOutcome(String)
 * Suppresses user engagement outcome telemetry to OneSignal.
 */
object RNOneSignalAddOutcomeFingerprint : Fingerprint(
    definingClass = "Lcom/onesignal/rnonesignalandroid/RNOneSignal;",
    name = "addOutcome",
    returnType = "V",
    parameters = listOf("Ljava/lang/String;")
)

/**
 * Matches RNOneSignal.addOutcomeWithValue(String, float)
 * Suppresses user conversion value telemetry to OneSignal.
 */
object RNOneSignalAddOutcomeWithValueFingerprint : Fingerprint(
    definingClass = "Lcom/onesignal/rnonesignalandroid/RNOneSignal;",
    name = "addOutcomeWithValue",
    returnType = "V",
    parameters = listOf("Ljava/lang/String;", "F")
)

/**
 * Matches RNOneSignal.addUniqueOutcome(String)
 * Suppresses unique outcome telemetry to OneSignal.
 */
object RNOneSignalAddUniqueOutcomeFingerprint : Fingerprint(
    definingClass = "Lcom/onesignal/rnonesignalandroid/RNOneSignal;",
    name = "addUniqueOutcome",
    returnType = "V",
    parameters = listOf("Ljava/lang/String;")
)

/**
 * Matches CodePushNativeModule.recordStatusReported(ReadableMap)
 * Suppresses OTA deployment status reporting to Microsoft AppCenter.
 */
object CodePushRecordStatusReportedFingerprint : Fingerprint(
    definingClass = "Lcom/microsoft/codepush/react/CodePushNativeModule;",
    name = "recordStatusReported",
    returnType = "V",
    parameters = listOf("Lcom/facebook/react/bridge/ReadableMap;")
)

/**
 * Matches CodePushNativeModule.saveStatusReportForRetry(ReadableMap)
 * Suppresses OTA deployment retry telemetry to Microsoft AppCenter.
 */
object CodePushSaveStatusReportForRetryFingerprint : Fingerprint(
    definingClass = "Lcom/microsoft/codepush/react/CodePushNativeModule;",
    name = "saveStatusReportForRetry",
    returnType = "V",
    parameters = listOf("Lcom/facebook/react/bridge/ReadableMap;")
)
