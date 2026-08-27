package com.example.boulderwallapp

object BoulderRules {
    const val TOTAL_HOLDS = 10
    const val FALL_PENALTY = 3
    const val MIN_SCORE = 0
    const val MAX_SCORE = 30
    const val STREAK_BONUS_THRESHOLD = 3

    fun pointsForHold(hold: Int): Int = when (hold) {
        in 1..3 -> 2
        in 4..7 -> 3
        in 8..10 -> 4
        else -> 0
    }

    fun zoneColorRes(hold: Int): Int = when (hold) {
        in 1..3 -> R.color.zone_warmup
        in 4..7 -> R.color.zone_technical
        in 8..10 -> R.color.zone_crux
        else -> R.color.zone_none
    }
}
