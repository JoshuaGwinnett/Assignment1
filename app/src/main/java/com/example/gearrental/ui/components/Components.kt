package com.example.gearrental.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Reusable section title, used at the top of the Browse, Basket and History
 * screens so every screen shares the same heading treatment.
 */
@Composable
fun SectionHeading(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 8.dp)
    )
}

/**
 * Reusable price display. Used on the resource card, basket rows, history
 * rows, and the booking confirmation summary - always the same "N cr" pill
 * styled from the current color scheme, so it adapts to light/dark for free.
 */
@Composable
fun PriceTag(credits: Int, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = "$credits cr",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

enum class BadgeTone { POSITIVE, NEGATIVE, NEUTRAL }

/**
 * Reusable status/category pill. Used for a resource's category and condition
 * on the browse card, and for a booking's Active/Cancelled status in history.
 */
@Composable
fun StatusBadge(text: String, tone: BadgeTone = BadgeTone.NEUTRAL, modifier: Modifier = Modifier) {
    val containerColor = when (tone) {
        BadgeTone.POSITIVE -> MaterialTheme.colorScheme.tertiaryContainer
        BadgeTone.NEGATIVE -> MaterialTheme.colorScheme.errorContainer
        BadgeTone.NEUTRAL -> MaterialTheme.colorScheme.secondaryContainer
    }
    val contentColor = when (tone) {
        BadgeTone.POSITIVE -> MaterialTheme.colorScheme.onTertiaryContainer
        BadgeTone.NEGATIVE -> MaterialTheme.colorScheme.onErrorContainer
        BadgeTone.NEUTRAL -> MaterialTheme.colorScheme.onSecondaryContainer
    }
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/** Reusable +/- stepper for a bounded integer, used to pick rental nights in the basket. */
@Composable
fun QuantityStepper(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        IconButton(
            onClick = { if (value - 1 in range) onValueChange(value - 1) },
            enabled = value - 1 in range
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrease")
        }
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
        IconButton(
            onClick = { if (value + 1 in range) onValueChange(value + 1) },
            enabled = value + 1 in range
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increase")
        }
    }
}
