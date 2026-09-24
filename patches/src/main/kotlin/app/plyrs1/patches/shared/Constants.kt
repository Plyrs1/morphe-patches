package app.plyrs1.patches.shared

import app.morphe.patcher.patch.ApkFileType
import app.morphe.patcher.patch.AppTarget
import app.morphe.patcher.patch.Compatibility

object Constants {
    /**
     * Compatibility definition for Struk POM (com.garnesapps.strukpom).
     */
    val COMPATIBILITY_STRUKPOM = Compatibility(
        name = "Struk POM",
        packageName = "com.garnesapps.strukpom",
        apkFileType = ApkFileType.APK,
        appIconColor = 0x1E88E5,
        targets = listOf(
            AppTarget(version = "1.17.057"),
            AppTarget(version = null, isExperimental = true)
        )
    )

    /**
     * Generic example compatibility target.
     */
    val COMPATIBILITY_EXAMPLE = Compatibility(
        name = "Example App",
        packageName = "com.example.app",
        apkFileType = ApkFileType.APK,
        targets = listOf(
            AppTarget(version = "1.0.0"),
            AppTarget(version = null, isExperimental = true)
        )
    )
}
