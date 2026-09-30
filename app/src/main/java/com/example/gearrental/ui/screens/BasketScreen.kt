package com.example.gearrental.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.IntentCompat
import com.example.gearrental.BookingActivity
import com.example.gearrental.model.BookingResult
import com.example.gearrental.ui.components.PriceTag
import com.example.gearrental.ui.components.QuantityStepper
import com.example.gearrental.ui.components.SectionHeading
import com.example.gearrental.viewmodel.BookingViewModel

@Composable
fun BasketScreen(viewModel: BookingViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lineItems = viewModel.basketLineItems()
    val errors = viewModel.basketValidationErrors()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val data = activityResult.data
            val bookingResult = data?.let {
                IntentCompat.getParcelableExtra(it, BookingActivity.EXTRA_RESULT, BookingResult::class.java)
            }
            if (bookingResult != null) {
                if (bookingResult.confirmed) {
                    viewModel.confirmBooking(bookingResult)
                } else {
                    viewModel.onBookingCancelled()
                }
            }
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        SectionHeading("Your Basket")

        if (lineItems.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your basket is empty. Add gear from the Browse tab.")
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(lineItems, key = { it.resourceId }) { line ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(line.resourceName, style = MaterialTheme.typography.titleMedium)
                                IconButton(onClick = { viewModel.removeFromBasket(line.resourceId) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Nights", style = MaterialTheme.typography.bodySmall)
                                    QuantityStepper(
                                        value = line.nights,
                                        range = BookingViewModel.MIN_NIGHTS..BookingViewModel.MAX_NIGHTS,
                                        onValueChange = { viewModel.updateNights(line.resourceId, it) }
                                    )
                                }
                                PriceTag(credits = line.lineTotal)
                            }
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total", style = MaterialTheme.typography.titleLarge)
                PriceTag(credits = viewModel.basketTotal())
            }
        }

        if (errors.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Column {
                errors.forEach { error ->
                    Text(
                        text = "• $error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                val request = viewModel.buildBookingRequest()
                val intent = Intent(context, BookingActivity::class.java)
                    .putExtra(BookingActivity.EXTRA_REQUEST, request)
                launcher.launch(intent)
            },
            enabled = viewModel.canBook(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Book Now")
        }
    }
}
