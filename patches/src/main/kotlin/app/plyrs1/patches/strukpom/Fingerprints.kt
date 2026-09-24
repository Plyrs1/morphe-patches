package app.plyrs1.patches.strukpom

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.opcode
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.Opcode

/**
 * Fingerprint matching the AdMob ad preload routine in Struk POM.
 *
 * In com.garnesapps.strukpom.MyApplication:
 *   private final void preloadAd(String adId, AdFormat format)
 */
object AdPreloadFingerprint : Fingerprint(
    definingClass = "Lcom/garnesapps/strukpom/MyApplication;",
    name = "preloadAd",
    returnType = "V",
    parameters = listOf("Ljava/lang/String;", "Lcom/google/android/libraries/ads/mobile/sdk/common/AdFormat;")
)
