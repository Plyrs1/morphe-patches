package app.plyrs1.patches.strukpom

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.plyrs1.patches.shared.Constants.COMPATIBILITY_STRUKPOM

@Suppress("unused")
val bypassWatermarkRewardPatch = bytecodePatch(
    name = "Bypass Watermark Reward",
    description = "Forces the app to always pass 'hadiah = sudah' when opening receipt activities, enabling instant watermark removal.",
    default = true
) {
    compatibleWith(COMPATIBILITY_STRUKPOM)

    execute {
        // In PomActivity.goAs(i, str, str2, str3):
        // Overwrite parameter p4 (str3 / hadiah) with "sudah" at index 0
        PomActivityGoAsFingerprint.method.addInstructions(
            0,
            """
                const-string p4, "sudah"
            """
        )

        // In StrukActivity.goAs(i, str):
        // Overwrite parameter p2 (str / hadiah) with "sudah" at index 0
        StrukActivityGoAsFingerprint.method.addInstructions(
            0,
            """
                const-string p2, "sudah"
            """
        )

        // In HistoriActivity.goAs(i, i2, str):
        // Overwrite parameter p3 (str / hadiah) with "sudah" at index 0
        HistoriActivityGoAsFingerprint.method.addInstructions(
            0,
            """
                const-string p3, "sudah"
            """
        )
    }
}
