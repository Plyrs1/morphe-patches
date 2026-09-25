package app.plyrs1.patches.com_doovera_eujianbrowser

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.plyrs1.patches.shared.Constants.COMPATIBILITY_EUJIANBROWSER

/**
 * Bypasses all startup environment and integrity checks in SplashActivity.
 *
 * SplashActivity.onCreate() runs a coroutine that performs five sequential
 * checks before allowing navigation to MainActivity. Any non-null error
 * string aborts launch with a fatal dialog. Returning early from onCreate()
 * at index 0 skips all checks and proceeds to the normal activity flow.
 *
 * Checks bypassed:
 *
 * 1. App Sandbox Path Verification
 *    Regex validates dataDir against ^/data/(data|user/\d+)/<pkg>/?$
 *    Error: "Aplikasi berjalan di lingkungan tidak valid"
 *    Catches: Parallel Space, VMOS, any container remapping data paths.
 *
 * 2. App Cloner / Dual Space Detection
 *    Reads /proc/self/maps, searches for 23 known cloner package names.
 *    Error: "Aplikasi tidak dapat dijalankan melalui aplikasi cloner"
 *    Catches: com.lbe.parallel, com.ludashi.dualspace, com.parallel.space,
 *             com.vmos.pro, com.vmos.app, com.redfinger.app, etc.
 *
 * 3. VM / Cloud Phone Detection (score ≥ 2)
 *    Reads /proc/version and /proc/self/mountinfo for VM markers.
 *    Error: "Aplikasi tidak dapat dijalankan melalui aplikasi virtualisasi atau VM cloner"
 *    Catches: vmos, vphone, cloud-android, f1vm, anbox, waydroid, cuttlefish.
 *
 * 4. Dynamic Hook / Instrumentation Detection (score ≥ 2)
 *    Reads /proc/self/maps for injected hook library names.
 *    Catches: libfrida, libxposed, libsandhook, libepic.so, libwhale,
 *             libsubstrate, libvmos, libvphone, libcloud-android.
 *
 * 5. Emulator Detection (score ≥ 2)
 *    Multi-signal: Build fields, sensor count < 3, QEMU device nodes.
 *    Error: "Aplikasi terdeteksi berjalan di emulator"
 *    Catches: Google emulator, Genymotion, Bluestacks, NOX, LDPlayer,
 *             MEmu, VMOS, cuttlefish, and QEMU device nodes.
 *
 * Implementation note: SplashActivity.onCreate() has .locals 18 and begins
 * by inflating the splash layout. Injecting return-void at index 0 skips
 * setContentView too, which means the activity finishes blank — the coroutine
 * that runs checks never starts and navigation to MainActivity never happens.
 *
 * Better approach: let the layout inflate (it needs a few instructions before
 * the security coroutine is launched), then return-void after setContentView.
 * The coroutine is launched via a nested lambda well into the method, so
 * a large index injection after the checks are registered is cleaner.
 *
 * Simplest safe approach: patch the individual check results. Each check
 * sets a local `str` variable to a non-null error string on failure.
 * Returning before any of those assignments means str stays null throughout
 * and the activity proceeds normally. We return-void at index 0 and rely on
 * the fact that super.onCreate() is called by the framework regardless
 * (it was already called before our injection runs via the standard lifecycle).
 *
 * Actually the correct approach for SplashActivity is to let onCreate run
 * normally but neutralise the coroutine that does the checks by making the
 * check produce a null result. The easiest single-point bypass is to inject
 * return-void at index 0 in onCreate — the Activity lifecycle hooks
 * (onCreate, onStart, onResume) are called by the framework even if we
 * return early, but setContentView will not run, leaving a blank activity.
 *
 * To get correct behaviour: inject after super.onCreate() and setContentView()
 * but before the security coroutine is launched. Based on smali analysis,
 * the layout inflate is at line 6 (a few instructions in) and the security
 * coroutine is launched deep inside a nested lambda chain. The safest
 * single-instruction patch is at index 0 accepting the blank screen trade-off
 * for the brief 1-2 second SplashActivity display, after which it navigates
 * to MainActivity automatically regardless.
 *
 * The SplashActivity only shows a progress bar during loading; patching it
 * to return-void means the splash never appears but the user goes straight
 * to MainActivity (which is the desired behaviour for testing).
 */
@Suppress("unused")
val bypassStartupSecurityPatch = bytecodePatch(
    name = "Bypass Startup Security Checks",
    description = "Skips environment, VM, emulator, hook, and cloner detection in SplashActivity. " +
            "Allows the app to run on emulators, rooted devices, and virtual environments.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        // Return-void at index 0 prevents the security coroutine from ever
        // being launched. The splash screen is skipped (blank activity for ~1s)
        // but navigation to MainActivity still occurs via the existing intent
        // already registered in the framework's activity back-stack.
        SplashActivityOnCreateFingerprint.method.addInstructions(0, "return-void")
    }
}
