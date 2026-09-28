package com.example.gearrental

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.gearrental.ui.GearRentalApp
import com.example.gearrental.ui.theme.GearRentalTheme
import com.example.gearrental.viewmodel.BookingViewModel

/**
 * Activity 1. Hosts Navigation Compose for the browse/basket/history
 * destinations, all sharing one activity-scoped [BookingViewModel].
 */
class MainActivity : ComponentActivity() {

    private val viewModel: BookingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GearRentalTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    GearRentalApp(viewModel)
                }
            }
        }
    }
}
