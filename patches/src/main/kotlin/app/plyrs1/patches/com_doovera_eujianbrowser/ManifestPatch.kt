package app.plyrs1.patches.com_doovera_eujianbrowser

import app.morphe.patcher.patch.resourcePatch
import app.plyrs1.patches.shared.Constants.COMPATIBILITY_EUJIANBROWSER
import org.w3c.dom.Element

/**
 * Adds the permissions and Service declaration required by
 * [backgroundPersistencePatch] to AndroidManifest.xml.
 *
 * Permissions added:
 *   android.permission.WAKE_LOCK
 *     Required to acquire PowerManager.WakeLock inside the service.
 *
 *   android.permission.FOREGROUND_SERVICE
 *     Required to call Service.startForeground() on API 28+.
 *
 *   android.permission.FOREGROUND_SERVICE_SPECIAL_USE
 *     Required on API 34+ for services that don't fit a specific type.
 *     Must be accompanied by <property> metadata — added below.
 *
 * Service declaration:
 *   com.doovera.eujianbrowser.ExamKeepAliveService
 *   foregroundServiceType="specialUse"
 *   exported="false"
 *   stopWithTask="false" — keeps running even if user swipes app from recents
 */
@Suppress("unused")
val manifestPatch = resourcePatch(
    name = "Manifest: Background Persistence Permissions",
    description = "Adds WAKE_LOCK, FOREGROUND_SERVICE, and FOREGROUND_SERVICE_SPECIAL_USE " +
            "permissions, and registers ExamKeepAliveService in AndroidManifest.xml.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)
    dependsOn(backgroundPersistencePatch)

    execute {
        document("AndroidManifest.xml").use { doc ->
            val manifest = doc.documentElement

            // ── Permissions ───────────────────────────────────────────────────
            val permissionsToAdd = listOf(
                "android.permission.WAKE_LOCK",
                "android.permission.FOREGROUND_SERVICE",
                "android.permission.FOREGROUND_SERVICE_SPECIAL_USE"
            )

            // Find the last existing <uses-permission> to insert after it.
            val existingPermissions = manifest.getElementsByTagName("uses-permission")
            val lastPermission = existingPermissions.item(existingPermissions.length - 1)

            for (permName in permissionsToAdd) {
                val elem = doc.createElement("uses-permission")
                elem.setAttribute("android:name", permName)
                manifest.insertBefore(elem, lastPermission.nextSibling)
            }

            // ── Service declaration ───────────────────────────────────────────
            val application = manifest.getElementsByTagName("application").item(0) as Element

            val service = doc.createElement("service")
            service.setAttribute("android:name", "com.doovera.eujianbrowser.ExamKeepAliveService")
            service.setAttribute("android:exported", "false")
            service.setAttribute("android:stopWithTask", "false")
            service.setAttribute("android:foregroundServiceType", "specialUse")

            // <property> required for FOREGROUND_SERVICE_SPECIAL_USE on API 34+
            val property = doc.createElement("property")
            property.setAttribute("android:name", "android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE")
            property.setAttribute("android:value", "keepAlive")
            service.appendChild(property)

            application.appendChild(service)
        }
    }
}
