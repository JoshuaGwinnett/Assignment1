package com.example.boulderwallapp

/**
 * Immutable snapshot of a climbing attempt. Held by [BoulderViewModel] and survives
 * configuration changes (e.g. rotation) since the ViewModel is retained.
 */
data class GameState(
    val score: Int = 0,
    val hold: Int = 0,
    val streak: Int = 0,
    val bonusScore: Int = 0,
    val fallen: Boolean = false,
    val chalkUsed: Boolean = false
) {
    val routeComplete: Boolean
        get() = hold >= BoulderRules.TOTAL_HOLDS

    val canGrip: Boolean
        get() = !fallen && !routeComplete

    val canFall: Boolean
        get() = !fallen && hold in 1 until BoulderRules.TOTAL_HOLDS

    val canChalk: Boolean
        get() = !fallen && !routeComplete && !chalkUsed
}
