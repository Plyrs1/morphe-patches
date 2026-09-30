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
 * Matches OneSignal SDK initialization [com/onesignal/internal/a;->initWithContext(Context, String)]
 * Neutralized to prevent OneSignal push/analytics SDK initialization and device tracking.
 */
object OneSignalInitWithContextFingerprint : Fingerprint(
    definingClass = "Lcom/onesignal/internal/a;",
    name = "initWithContext",
    returnType = "Z",
    parameters = listOf("Landroid/content/Context;", "Ljava/lang/String;")
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
