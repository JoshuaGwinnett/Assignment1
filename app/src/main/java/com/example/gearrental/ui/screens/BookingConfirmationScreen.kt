package com.example.gearrental.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gearrental.model.BookingRequest
import com.example.gearrental.ui.components.PriceTag
import com.example.gearrental.ui.components.SectionHeading

/**
 * The whole UI for BookingActivity. Confirming is blocked (per the assignment's
 * technical requirement C.1) until the renter name is filled in and the terms
 * checkbox is ticked; Cancel (button or system back) is always available and
 * discards everything.
 */
@Composable
fun BookingConfirmationScreen(
    request: BookingRequest,
    onConfirm: (renterName: String) -> Unit,
    onCancel: () -> Unit
) {
    var renterName by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(false) }
    var attemptedConfirm by remember { mutableStateOf(false) }

    BackHandler(onBack = onCancel)

    val nameError = attemptedConfirm && renterName.isBlank()
    val termsError = attemptedConfirm && !termsAccepted

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        SectionHeading("Confirm Your Booking")

        request.items.forEach { line ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(line.resourceName, style = MaterialTheme.typography.titleSmall)
                        Text(
                            "${line.nights} night(s) x ${line.costPerNight} cr",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    PriceTag(credits = line.lineTotal)
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Total", style = MaterialTheme.typography.titleMedium)
            PriceTag(credits = request.totalCost)
        }
        Text(
            "Credit balance after booking: ${request.creditBalanceBefore - request.totalCost}",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(24.dp))
        OutlinedTextField(
            value = renterName,
            onValueChange = { renterName = it },
            label = { Text("Renter name") },
            isError = nameError,
            supportingText = { if (nameError) Text("Name is required.") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = termsAccepted, onCheckedChange = { termsAccepted = it })
            Text("I agree to the rental terms and to return the gear on time.")
        }
        if (termsError) {
            Text(
                "You must accept the terms to continue.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    attemptedConfirm = true
                    if (renterName.isNotBlank() && termsAccepted) {
                        onConfirm(renterName.trim())
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Confirm & Save")
            }
        }
    }
}
