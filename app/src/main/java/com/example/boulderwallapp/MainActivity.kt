package com.example.boulderwallapp

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import com.example.boulderwallapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: BoulderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "onCreate: orientation=${resources.configuration.orientation}")

        binding.btnGrip.setOnClickListener {
            Log.d(TAG, "Grip button clicked")
            viewModel.onGrip()
        }

        binding.btnFall.setOnClickListener {
            Log.d(TAG, "Fall button clicked")
            viewModel.onFall()
        }

        binding.btnReset.setOnClickListener {
            Log.d(TAG, "Reset button clicked")
            viewModel.onReset()
        }

        binding.btnChalk.setOnClickListener {
            Log.d(TAG, "Chalk Up button clicked")
            if (viewModel.onChalkUp()) {
                Toast.makeText(this, R.string.msg_chalk_used, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLanguage.setOnClickListener {
            toggleLanguage()
        }

        viewModel.state.observe(this) { state -> render(state) }
    }

    private fun render(state: GameState) {
        binding.tvScore.text = state.score.toString()
        binding.tvHold.text = getString(R.string.label_hold, state.hold, BoulderRules.TOTAL_HOLDS)
        binding.tvStreak.text = state.streak.toString()
        binding.tvBonus.text = state.bonusScore.toString()

        val zoneColor = ContextCompat.getColor(this, BoulderRules.zoneColorRes(state.hold))
        binding.tvScore.setTextColor(zoneColor)
        binding.btnGrip.backgroundTintList = ColorStateList.valueOf(zoneColor)

        binding.tvStatus.text = when {
            state.fallen -> getString(R.string.msg_fallen)
            state.routeComplete -> getString(R.string.msg_route_complete)
            else -> ""
        }

        binding.btnGrip.isEnabled = state.canGrip
        binding.btnFall.isEnabled = state.canFall
        binding.btnChalk.isEnabled = state.canChalk

        Log.d(
            TAG,
            "Render: score=${state.score} hold=${state.hold} streak=${state.streak} " +
                "bonus=${state.bonusScore} fallen=${state.fallen} chalkUsed=${state.chalkUsed}"
        )
    }

    private fun toggleLanguage() {
        val current = AppCompatDelegate.getApplicationLocales()
        val currentLanguage = if (!current.isEmpty) current.get(0)?.language else null
        val newLanguage = if (currentLanguage == "es") "en" else "es"

        Log.d(TAG, "Language button clicked: switching from $currentLanguage to $newLanguage")
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(newLanguage))
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
