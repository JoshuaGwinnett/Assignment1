package com.example.boulderwallapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Holds the current [GameState] and enforces the route's scoring rules. Being a
 * ViewModel, this state is retained across configuration changes such as rotation.
 */
class BoulderViewModel : ViewModel() {

    private val _state = MutableLiveData(GameState())
    val state: LiveData<GameState> get() = _state

    fun onGrip() {
        val s = _state.value ?: GameState()
        if (!s.canGrip) {
            Log.w(TAG, "Grip ignored: fallen=${s.fallen}, hold=${s.hold}")
            return
        }

        val nextHold = s.hold + 1
        val basePoints = BoulderRules.pointsForHold(nextHold)
        // A streak of 3+ grants the next grip a +1 bonus, then the streak resets
        // and starts counting again from the following grip.
        val bonus = if (s.streak >= BoulderRules.STREAK_BONUS_THRESHOLD) 1 else 0
        val newStreak = if (bonus > 0) 0 else s.streak + 1
        val newScore = (s.score + basePoints + bonus)
            .coerceIn(BoulderRules.MIN_SCORE, BoulderRules.MAX_SCORE)

        Log.d(
            TAG,
            "Grip: hold=$nextHold base=$basePoints bonus=$bonus score=$newScore streak=$newStreak"
        )

        _state.value = s.copy(
            score = newScore,
            hold = nextHold,
            streak = newStreak,
            bonusScore = s.bonusScore + bonus
        )
    }

    fun onFall() {
        val s = _state.value ?: GameState()
        if (!s.canFall) {
            Log.w(TAG, "Fall ignored: fallen=${s.fallen}, hold=${s.hold}")
            return
        }

        val newScore = (s.score - BoulderRules.FALL_PENALTY).coerceAtLeast(BoulderRules.MIN_SCORE)
        Log.d(TAG, "Fall: score ${s.score} -> $newScore, streak reset")

        _state.value = s.copy(score = newScore, fallen = true, streak = 0)
    }

    fun onReset() {
        Log.d(TAG, "Reset pressed")
        _state.value = GameState()
    }

    /** Returns true if chalk was actually applied (i.e. it was allowed). */
    fun onChalkUp(): Boolean {
        val s = _state.value ?: GameState()
        if (!s.canChalk) {
            Log.w(
                TAG,
                "Chalk ignored: fallen=${s.fallen}, hold=${s.hold}, chalkUsed=${s.chalkUsed}"
            )
            return false
        }

        Log.d(TAG, "Chalk used at hold=${s.hold}")
        _state.value = s.copy(chalkUsed = true)
        return true
    }

    companion object {
        private const val TAG = "BoulderViewModel"
    }
}
