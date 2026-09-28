package com.example.gearrental.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gearrental.model.Booking
import com.example.gearrental.model.BookingStatus
import com.example.gearrental.ui.components.BadgeTone
import com.example.gearrental.ui.components.PriceTag
import com.example.gearrental.ui.components.SectionHeading
import com.example.gearrental.ui.components.StatusBadge
import com.example.gearrental.viewmodel.BookingViewModel

@Composable
fun HistoryScreen(viewModel: BookingViewModel, modifier: Modifier = Modifier) {
    val bookings = viewModel.bookings

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        SectionHeading("Booking History")

        if (bookings.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No bookings yet.")
            }
        } else {
            LazyColumn {
                items(bookings, key = { it.id }) { booking ->
                    BookingRow(booking = booking, onCancel = { viewModel.cancelBooking(booking.id) })
                }
            }
        }
    }
}

@Composable
private fun BookingRow(booking: Booking, onCancel: () -> Unit) {
    val isActive = booking.status == BookingStatus.ACTIVE
    val totalNights = booking.items.sumOf { it.nights }

    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(booking.renterName, style = MaterialTheme.typography.titleMedium)
                StatusBadge(
                    text = if (isActive) "Active" else "Cancelled",
                    tone = if (isActive) BadgeTone.POSITIVE else BadgeTone.NEGATIVE
                )
            }

            Spacer(Modifier.height(4.dp))
            booking.items.forEach { line ->
                Text(
                    "${line.resourceName} - ${line.nights} night(s)",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$totalNights night(s) total", style = MaterialTheme.typography.bodySmall)
                PriceTag(credits = booking.totalCost)
            }

            if (isActive) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancel Booking")
                }
            }
        }
    }
}
