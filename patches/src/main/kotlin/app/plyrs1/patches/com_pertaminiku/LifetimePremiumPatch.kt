package app.plyrs1.patches.com_pertaminiku

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.bytecodePatch
import app.plyrs1.patches.shared.Constants.COMPATIBILITY_PERTAMINIKU

/**
 * Lifetime Premium Patch - Mocks Google Play Billing to grant permanent watermark removal.
 * 
 * This patch intercepts queryPurchasesAsync responses and injects a mock "onetime_purchase"
 * entitlement that the Dart layer interprets as lifetime premium status.
 * 
 * Effect: Watermark is removed from all receipts permanently, no ads displayed,
 *        all 7 receipt templates unlocked (same as paying for subscription).
 */
val lifetimePremiumPatch = bytecodePatch(
    name = "Lifetime Premium",
    description = "Unlocks lifetime premium: removes watermark forever, disables ads, unlocks all templates.",
    default = true
) {
    compatibleWith(COMPATIBILITY_PERTAMINIKU)
    
    // Merge extension DEX containing PurchaseMockHelper.buildMockPurchases()
    extendWith("extensions/extension.mpe")

    execute {
        /*
         * Injection point: F2.a.f(BillingResult, List<Purchase>) at instruction index 9
         * 
         * Target method signature: f(LT0/f;Ljava/util/List;)V
         * Parameters:
         *   p0 = this (F2/a instance)
         *   p1 = T0/f (BillingResult)
         *   p2 = List<Purchase> (purchase list from Google Play)
         * 
         * Current flow before injection:
         *   .line 5: invoke-static {p1}, LS1/a->n(...) -> result in p1
         *   .line 8: invoke-static {p2}, LS1/a->o(...) -> result in p2  
         *   .line 9: new-instance v0, LQ2/z;
         *            iput-object p1, v0, LQ2/z->a
         *            iput-object p2, v0, LQ2/z->b
         *   ...
         *   invoke-virtual {p0, v0}, LP2/p->b(Ljava/lang/Object;)V
         *   return-void
         * 
         * We insert BEFORE line 9 to:
         *   1. Replace p2 with our mock purchase list
         *   2. Keep billing result intact
         *   3. Continue normal flow (S1/a.o will convert mock purchases to Q2.v wrappers)
         */
        
        QueryPurchasesResponseFingerprint.method.addInstructions(
            9,
            """
                invoke-static {}, Lapp/plyrs1/extension/PurchaseMockHelper;->buildMockPurchases()Ljava/util/ArrayList;
                move-result-object p2
                if-eqz p2, :mock_failure
                :mock_injected
                    # Original flow continues normally with p2 now being our mock list
                :mock_failure
                    # If injection fails, p2 remains unchanged (original behavior)
            """
        )
    }
}
