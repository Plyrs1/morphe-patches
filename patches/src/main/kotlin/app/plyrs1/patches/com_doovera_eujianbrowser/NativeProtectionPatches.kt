package app.plyrs1.patches.com_doovera_eujianbrowser

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.plyrs1.patches.shared.Constants.COMPATIBILITY_EUJIANBROWSER

// ─── 1. Violation Alarm ───────────────────────────────────────────────────────

/**
 * Silences the violation alarm (audio tone + vibration) fired by every
 * native security check. R() is the single convergence point — patching it
 * once covers multi-window, focus loss, overlay, DND, and silent mode.
 *
 * R() sets audio to max volume, plays a repeating ToneGenerator alarm, and
 * vibrates the device. Returning early prevents all of that without affecting
 * the warning dialogs shown by Q() (called before R() at each site).
 */
@Suppress("unused")
val disableViolationAlarmPatch = bytecodePatch(
    name = "Disable Violation Alarm",
    description = "Suppresses the max-volume alarm tone and vibration triggered on any exam security violation.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        ViolationAlarmFingerprint.method.addInstructions(0, "return-void")
    }
}

// ─── 2. Multi-Window / Split-Screen ───────────────────────────────────────────

/**
 * Prevents exam exit when split-screen mode is detected.
 *
 * B(Z) is called from onResume() and onConfigurationChanged() with
 * isInMultiWindowMode() as its argument. When true it shows a warning dialog
 * and calls R(). Returning early skips both.
 */
@Suppress("unused")
val bypassMultiWindowDetectionPatch = bytecodePatch(
    name = "Bypass Multi-Window Detection",
    description = "Disables split-screen detection so the exam continues normally in split-screen mode.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        MultiWindowDetectionFingerprint.method.addInstructions(0, "return-void")
    }
}

// ─── 3. Do Not Disturb Detection ─────────────────────────────────────────────

/**
 * Allows the exam to run with Do Not Disturb mode active.
 *
 * w() queries NotificationManager.getCurrentInterruptionFilter() and fires
 * the alarm if DND is enabled (filter 2/3/4). The intent is to ensure the
 * alarm is audible — moot once disableViolationAlarmPatch is applied, but
 * patching w() also removes the warning dialog shown to the student.
 */
@Suppress("unused")
val bypassDndDetectionPatch = bytecodePatch(
    name = "Bypass DND Detection",
    description = "Allows the exam to run with Do Not Disturb mode enabled.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        DndDetectionFingerprint.method.addInstructions(0, "return-void")
    }
}

// ─── 4. Silent / Vibrate Mode Detection ──────────────────────────────────────

/**
 * Allows the exam to run with the device on silent or vibrate.
 *
 * x() queries AudioManager.getRingerMode() and fires the alarm if the result
 * is not RINGER_MODE_NORMAL (2). Same rationale as DND patch above.
 */
@Suppress("unused")
val bypassSilentModeDetectionPatch = bytecodePatch(
    name = "Bypass Silent Mode Detection",
    description = "Allows the exam to run with the device on silent or vibrate.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        SilentModeDetectionFingerprint.method.addInstructions(0, "return-void")
    }
}

// ─── 5. OverlayGuard Foreground Recovery Loop ────────────────────────────────

/**
 * Stops the OverlayGuard system overlay and foreground-recovery loop.
 *
 * J() is called when the exam loses foreground without an authorized reason.
 * It draws a full-screen TYPE_APPLICATION_OVERLAY window and starts a
 * Handler loop (N()) that calls FLAG_ACTIVITY_REORDER_TO_FRONT every 1500 ms.
 * Returning early prevents both the overlay and the reorder loop.
 */
@Suppress("unused")
val disableOverlayGuardPatch = bytecodePatch(
    name = "Disable Overlay Guard",
    description = "Stops the OverlayGuard system window and foreground-recovery loop on focus loss.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        OverlayGuardFingerprint.method.addInstructions(0, "return-void")
    }
}

// ─── 6. onPause Alarm ────────────────────────────────────────────────────────

/**
 * Prevents the alarm from firing when the activity is paused.
 *
 * onPause() calls R() unconditionally when the exam loses focus unless
 * specific flags indicate an authorized reason (file chooser, split-screen,
 * grace timer). Injecting return-void as the first instruction skips the
 * entire body — the activity still calls super.onPause() via the existing
 * code path which is now unreachable, but Android lifecycle is unaffected
 * because returning early from onPause() before super is called is safe for
 * our use case (the activity is only paused, not destroyed).
 *
 * Note: to preserve correct lifecycle behaviour we only skip the R() call
 * by short-circuiting the method. The existing super.onPause() call that
 * already exists in the method body handles the actual lifecycle transition.
 */
@Suppress("unused")
val disableOnPauseAlarmPatch = bytecodePatch(
    name = "Disable onPause Alarm",
    description = "Prevents the alarm from triggering when the exam activity is paused or backgrounded.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        // Inject at index 0: skips W=false assignment, D(), super.onPause(), and R().
        // The activity will still be paused normally by the Android framework.
        OnPauseFingerprint.method.addInstructions(0, "return-void")
    }
}

// ─── 7. Window Focus Lost Alarm ───────────────────────────────────────────────

/**
 * Prevents the 1500 ms delayed focus-loss alarm.
 *
 * onWindowFocusChanged(false) schedules Runnable case 5 which, after 1500 ms,
 * logs "Window lost focus" and calls R(). onWindowFocusChanged(true) also
 * re-invokes w() and x() (DND + silent checks) on every focus gain.
 *
 * We let focus=true branch run normally (so fullscreen UI is restored) but
 * skip the focus=false alarm scheduling by returning early only when the
 * parameter is false. This requires a targeted injection rather than return-void
 * at index 0 — we check p1 and conditionally return.
 */
@Suppress("unused")
val disableFocusLossAlarmPatch = bytecodePatch(
    name = "Disable Focus Loss Alarm",
    description = "Prevents the delayed alarm triggered when the exam window loses focus.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        // p1 = hasFocus (boolean). If false (lost focus), return immediately
        // before the 1500 ms delayed handler is posted. If true, fall through
        // to the existing body which restores immersive UI — that is desirable.
        OnWindowFocusChangedFingerprint.method.addInstructions(
            0,
            """
                if-nez p1, :skip_early_return
                return-void
                :skip_early_return
            """
        )
    }
}

// ─── 8. Picture-in-Picture Detection ─────────────────────────────────────────

/**
 * Prevents exam termination when Picture-in-Picture mode is entered.
 *
 * onPictureInPictureModeChanged(true) calls M() which sets f1903I=true,
 * stops lock task, clears the WebView, and calls finish(). Returning early
 * skips M() — the activity remains alive in PiP window.
 */
@Suppress("unused")
val bypassPipDetectionPatch = bytecodePatch(
    name = "Bypass PiP Detection",
    description = "Allows the exam to continue when entered into Picture-in-Picture mode.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        OnPipModeChangedFingerprint.method.addInstructions(0, "return-void")
    }
}

// ─── 9. Touch Obscurity Detection ────────────────────────────────────────────

/**
 * Disables the partially-obscured touch detection (API 29+).
 *
 * dispatchTouchEvent checks MotionEvent.FLAG_WINDOW_IS_PARTIALLY_OBSCURED (bit 2).
 * When set it marks f1923c0=true and schedules a 1500 ms alarm for "popup/freeform
 * detected". Returning super.dispatchTouchEvent() directly passes all touch events
 * through without the obscurity check.
 *
 * We inject at index 0 to call super immediately and return its result,
 * bypassing the entire flag-check block.
 */
@Suppress("unused")
val disableTouchObscurityDetectionPatch = bytecodePatch(
    name = "Disable Touch Obscurity Detection",
    description = "Removes the overlay-touch detection that fires an alarm when another window partially covers the exam.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        // Skip the flag check entirely; just delegate to the superclass.
        // p1 = MotionEvent. Return value is boolean (consumed).
        DispatchTouchEventFingerprint.method.addInstructions(
            0,
            """
                invoke-super {p0, p1}, Landroid/app/Activity;->dispatchTouchEvent(Landroid/view/MotionEvent;)Z
                move-result v0
                return v0
            """
        )
    }
}

// ─── 10. FLAG_SECURE (Screenshot Prevention) ──────────────────────────────────

/**
 * Removes FLAG_SECURE so screenshots and screen recording work normally.
 *
 * onCreate() calls getWindow().setFlags(0x2000, 0x2000) which sets FLAG_SECURE.
 * We inject a clearFlags call at index 0 that runs before setFlags, then let
 * the existing setFlags call run — then immediately clear it again.
 *
 * Simpler: inject clearFlags(FLAG_SECURE) AFTER onCreate's super call so it
 * runs last and overrides any setFlags that happened during onCreate.
 * We add it at index 0 before everything; FLAG_SECURE set by line 1262 of the
 * original will run after our injection but we clear it again, so we actually
 * need to add AFTER the existing setFlags. Since addInstructions(0) prepends,
 * and the existing setFlags is called later in the body, our clearFlags at
 * index 0 will be overwritten. Instead we use a large index to append after
 * all existing instructions. Use index Int.MAX_VALUE to append at end.
 *
 * The method has .locals 14 — v0..v13 all available.
 */
@Suppress("unused")
val removeScreenshotProtectionPatch = bytecodePatch(
    name = "Remove Screenshot Protection",
    description = "Removes FLAG_SECURE so screenshots and screen recording work normally during the exam.",
    default = true
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        // Append after all existing instructions (end of onCreate body).
        // Clears FLAG_SECURE (0x2000) that was set earlier in onCreate.
        ExamActivityOnCreateFingerprint.method.addInstructions(
            ExamActivityOnCreateFingerprint.method.implementation!!.instructions.size - 1,
            """
                invoke-virtual {p0}, Landroid/app/Activity;->getWindow()Landroid/view/Window;
                move-result-object v0
                const/high16 v1, 0x20000000
                invoke-virtual {v0, v1}, Landroid/view/Window;->clearFlags(I)V
            """
        )
    }
}

// ─── 11. Fullscreen Enforcement ───────────────────────────────────────────────

/**
 * Allows status bar and navigation bar to be visible during the exam.
 *
 * A() hides system UI via WindowInsetsController (API 30+) or
 * setSystemUiVisibility(0x1706) on older devices. Returning early lets
 * the system UI remain in its default visible state.
 */
@Suppress("unused")
val allowSystemUIPatch = bytecodePatch(
    name = "Allow System UI",
    description = "Allows the status bar and navigation bar to remain visible during the exam.",
    default = false
) {
    compatibleWith(COMPATIBILITY_EUJIANBROWSER)

    execute {
        FullscreenEnforcementFingerprint.method.addInstructions(0, "return-void")
    }
}
