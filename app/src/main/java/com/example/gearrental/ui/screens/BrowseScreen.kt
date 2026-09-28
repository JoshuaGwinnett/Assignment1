package com.example.gearrental.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.Hiking
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.gearrental.model.GearItem
import com.example.gearrental.model.SortOption
import com.example.gearrental.ui.components.BadgeTone
import com.example.gearrental.ui.components.PriceTag
import com.example.gearrental.ui.components.SectionHeading
import com.example.gearrental.ui.components.StatusBadge
import com.example.gearrental.viewmodel.BookingViewModel
import kotlin.math.roundToInt

/**
 * The primary screen: browse resources one at a time via [HorizontalPager],
 * with a Material3 [SearchBar] and a sort [DropdownMenu].
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BrowseScreen(viewModel: BookingViewModel, modifier: Modifier = Modifier) {
    val items = viewModel.visibleResources()
    var sortMenuExpanded by remember { mutableStateOf(false) }
    var searchActive by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        SectionHeading("Available Gear")

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar(
                query = viewModel.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = { searchActive = false },
                active = searchActive,
                onActiveChange = { searchActive = it },
                placeholder = { Text("Search by name or category") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.weight(1f)
            ) {}

            Box {
                IconButton(onClick = { sortMenuExpanded = true }) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort")
                }
                DropdownMenu(
                    expanded = sortMenuExpanded,
                    onDismissRequest = { sortMenuExpanded = false }
                ) {
                    SortOption.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = {
                                viewModel.onSortOptionChange(option)
                                sortMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No gear matches your search.")
            }
        } else {
            val pagerState = rememberPagerState(pageCount = { items.size })
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) { page ->
                val resource = items[page]
                GearCard(
                    resource = resource,
                    inBasket = viewModel.isInBasket(resource.id),
                    onAddToBasket = { viewModel.addToBasket(resource.id) }
                )
            }
            Text(
                text = "${pagerState.currentPage + 1} / ${items.size}",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(vertical = 8.dp).align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun GearCard(resource: GearItem, inBasket: Boolean, onAddToBasket: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = categoryIcon(resource.category),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(resource.name, style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.height(8.dp))
            Row {
                StatusBadge(resource.category, tone = BadgeTone.NEUTRAL)
                Spacer(Modifier.width(8.dp))
                StatusBadge(resource.condition, tone = BadgeTone.POSITIVE)
            }

            Spacer(Modifier.height(8.dp))
            RatingRow(resource.rating)

            Spacer(Modifier.height(8.dp))
            Text(resource.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(4.dp))
            Text(
                "Location: ${resource.location}  •  ${resource.availableUnits} available  •  ${resource.year}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriceTag(credits = resource.costPerNight)
                Button(onClick = onAddToBasket, enabled = !inBasket) {
                    Text(if (inBasket) "In Basket" else "Add to Basket")
                }
            }
        }
    }
}

@Composable
private fun RatingRow(rating: Float) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(5) { index ->
            val filled = index < rating.roundToInt()
            Icon(
                imageVector = if (filled) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(4.dp))
        Text("($rating)", style = MaterialTheme.typography.bodySmall)
    }
}

private fun categoryIcon(category: String): ImageVector = when (category) {
    "Climbing" -> Icons.Default.Terrain
    "Camping" -> Icons.Default.Cabin
    "Mountaineering" -> Icons.Default.Landscape
    "Footwear" -> Icons.Default.Hiking
    "Safety" -> Icons.Default.Shield
    else -> Icons.Default.Inventory2
}
