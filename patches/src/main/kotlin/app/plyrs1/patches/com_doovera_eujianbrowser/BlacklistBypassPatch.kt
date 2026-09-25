package app.plyrs1.patches.com_doovera_eujianbrowser

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.plyrs1.patches.shared.Constants.COMPATIBILITY_EUJIANBROWSER

/**
 * Bypasses the pre-exam installed app blacklist scanner.
 *
 * Before ExamActivity starts, MainActivity's exam button triggers a coroutine
 * (q0.v.invokeSuspend) that:
 *   1. Fetches dynamic blacklist from GET api/seb/blacklisted-apps
 *   2. Merges with the hardcoded list (8 apps: AnyDesk, TeamViewer,
 *      floating apps, screen recorders, split-screen tools)
 *   3. Calls PackageManager.getInstalledApplications()
 *   4. Blocks exam start if any installed app matches the combined blacklist
 *
 * Hardcoded blacklist (u0/e.java):
 *   com.lwi.android.flapps, com.floatingapps.floatingapps,
 *   com.lwi.android.flappspro, com.teamviewer.quicksupport.market,
 *   com.anydesk.anydeskandroid, com.splitscreen.multiwindow,
 *   com.duapps.recorder, com.hecorat.screenrecorder.free
 *
 * Returning early from invokeSuspend with the COROUTINE_SUSPENDED sentinel
 * makes the coroutine appear suspended to its parent — it never completes,
 * so the blacklist block and the subsequent connectivity check are skipped.
 *
 * However, this would stall the entire exam launch. A cleaner approach is
 * to return the Result.success(Unit) sentinel so the coroutine completes
 * successfully with no action taken.
 *
 * The Kotlin coroutine machinery uses a specific singleton for success:
 * kotlin.Unit / V0.j.f1006a (as seen in other methods returning j.f1006a).
 * We return that sentinel so the coroutine framework treats this as completed
 * successfully and the calling code proceeds to launch ExamActivity normally.
 *
 * Smali: .locals 14 — v0..v13 available. The method's return type is
 * Ljava/lang/Object; so we sget the Unit singleton and return-object it.
 */
@Suppress("unused")
val bypassBlacklistScanPatch = bytecodePatch(
    name = "Bypass App Blacklist Scan",
    description = "Skips the pre-exam scan that blocks exam start if blacklisted apps " +
            "(AnyDesk, TeamViewer, screen recorders, floating apps) are installed.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        // V0.j.f1006a is the Kotlin Unit / COROUTINE_COMPLETED sentinel used
        // throughout the app (visible in other coroutine return sites).
        // Returning it tells the coroutine framework this invokeSuspend
        // completed successfully, allowing the outer launch block to proceed
        // to startActivity(ExamActivity).
        BlacklistScanFingerprint.method.addInstructions(
            0,
            """
                sget-object v0, LV0/j;->f1006a:Ljava/lang/Object;
                return-object v0
            """
        )
    }
}
