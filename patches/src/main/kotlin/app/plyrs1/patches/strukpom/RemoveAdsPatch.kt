package app.plyrs1.patches.strukpom

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.plyrs1.patches.shared.Constants.COMPATIBILITY_STRUKPOM

@Suppress("unused")
val removeAllAdsPatch = bytecodePatch(
    name = "Remove All Ads",
    description = "Disables background AdMob preloading and bypasses all interstitial ads when navigating between screens.",
    default = true
) {
    compatibleWith(COMPATIBILITY_STRUKPOM)

    execute {
        // 1. Short-circuit background ad preloading in MyApplication
        AdPreloadFingerprint.method.addInstructions(
            0,
            """
                return-void
            """
        )

        // 2. Bypass interstitial ad in PomActivity.showInter(i, str, str2) -> call goAs directly
        PomActivityShowInterFingerprint.method.addInstructions(
            0,
            """
                const-string v0, "sudah"
                invoke-virtual {p0, p1, p2, p3, v0}, Lcom/garnesapps/strukpom/PomActivity;->goAs(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V
                return-void
            """
        )

        // 3. Bypass interstitial ad in StrukActivity.showInter(i) -> call goAs directly
        StrukActivityShowInterFingerprint.method.addInstructions(
            0,
            """
                const-string v0, "sudah"
                invoke-virtual {p0, p1, v0}, Lcom/garnesapps/strukpom/StrukActivity;->goAs(ILjava/lang/String;)V
                return-void
            """
        )

        // 4. Bypass interstitial ad in HistoriActivity.showInter(i, i2) -> call goAs directly
        HistoriActivityShowInterFingerprint.method.addInstructions(
            0,
            """
                const-string v0, "sudah"
                invoke-virtual {p0, p1, p2, v0}, Lcom/garnesapps/strukpom/HistoriActivity;->goAs(IILjava/lang/String;)V
                return-void
            """
        )
    }
}
