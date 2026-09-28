package com.example.gearrental

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.IntentCompat
import com.example.gearrental.model.BookingRequest
import com.example.gearrental.model.BookingResult
import com.example.gearrental.ui.screens.BookingConfirmationScreen
import com.example.gearrental.ui.theme.GearRentalTheme

/**
 * Activity 2: the booking confirmation workflow. Reached from MainActivity via
 * an Intent carrying a Parcelable [BookingRequest]; its outcome is returned to
 * MainActivity as a Parcelable [BookingResult] through the Activity Result API
 * (never by mutating any shared state directly - MainActivity's ViewModel only
 * acts once the result comes back).
 */
class BookingActivity : ComponentActivity() {

    companion object {
        const val EXTRA_REQUEST = "com.example.gearrental.EXTRA_BOOKING_REQUEST"
        const val EXTRA_RESULT = "com.example.gearrental.EXTRA_BOOKING_RESULT"
        private const val TAG = "BookingActivity"
    }

    private val request: BookingRequest by lazy {
        IntentCompat.getParcelableExtra(intent, EXTRA_REQUEST, BookingRequest::class.java)
            ?: BookingRequest(items = emptyList(), totalCost = 0, creditBalanceBefore = 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: received ${request.items.size} item(s), total=${request.totalCost}")

        setContent {
            GearRentalTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    BookingConfirmationScreen(
                        request = request,
                        onConfirm = { renterName -> confirmAndFinish(renterName) },
                        onCancel = { cancelAndFinish() }
                    )
                }
            }
        }
    }

    private fun confirmAndFinish(renterName: String) {
        Log.d(TAG, "Booking confirmed by '$renterName'")
        val result = BookingResult(confirmed = true, request = request, renterName = renterName)
        setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_RESULT, result))
        finish()
    }

    private fun cancelAndFinish() {
        Log.d(TAG, "Booking cancelled")
        val result = BookingResult(confirmed = false, request = request, renterName = "")
        setResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_RESULT, result))
        finish()
    }
}
