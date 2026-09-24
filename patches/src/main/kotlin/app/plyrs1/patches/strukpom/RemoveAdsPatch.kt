package app.plyrs1.patches.strukpom

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.plyrs1.patches.shared.Constants.COMPATIBILITY_STRUKPOM

@Suppress("unused")
val disableAdsPatch = bytecodePatch(
    name = "Disable Ad Preloader",
    description = "Disables background AdMob ad preloading to save memory and network data.",
    default = true
) {
    compatibleWith(COMPATIBILITY_STRUKPOM)

    execute {
        // Short-circuit preloadAd() by returning immediately at index 0
        AdPreloadFingerprint.method.addInstructions(
            0,
            """
                return-void
            """
        )
    }
}
