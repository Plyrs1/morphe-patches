package app.plyrs1.patches.strukpom

import app.morphe.patcher.Fingerprint

/**
 * Fingerprint matching PomActivity.goAs(int, String, String, String)
 */
object PomActivityGoAsFingerprint : Fingerprint(
    definingClass = "Lcom/garnesapps/strukpom/PomActivity;",
    name = "goAs",
    returnType = "V",
    parameters = listOf("I", "Ljava/lang/String;", "Ljava/lang/String;", "Ljava/lang/String;")
)

/**
 * Fingerprint matching StrukActivity.goAs(int, String)
 */
object StrukActivityGoAsFingerprint : Fingerprint(
    definingClass = "Lcom/garnesapps/strukpom/StrukActivity;",
    name = "goAs",
    returnType = "V",
    parameters = listOf("I", "Ljava/lang/String;")
)

/**
 * Fingerprint matching HistoriActivity.goAs(int, int, String)
 */
object HistoriActivityGoAsFingerprint : Fingerprint(
    definingClass = "Lcom/garnesapps/strukpom/HistoriActivity;",
    name = "goAs",
    returnType = "V",
    parameters = listOf("I", "I", "Ljava/lang/String;")
)
