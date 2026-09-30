package com.example.gearrental.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.collect
import com.example.gearrental.ui.screens.BasketScreen
import com.example.gearrental.ui.screens.BrowseScreen
import com.example.gearrental.ui.screens.HistoryScreen
import com.example.gearrental.viewmodel.BookingViewModel

private const val ROUTE_BROWSE = "browse"
private const val ROUTE_BASKET = "basket"
private const val ROUTE_HISTORY = "history"

/**
 * Root composable for MainActivity. Navigation Compose provides the three
 * destinations (browse/basket/history); [viewModel] is created once in
 * MainActivity and shared by all of them, so a change on one screen (e.g.
 * cancelling a booking in History) is reflected immediately everywhere else.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GearRentalApp(viewModel: BookingViewModel) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect { message -> snackbarHostState.showSnackbar(message) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gear Rental") },
                actions = {
                    Text(
                        text = "${viewModel.creditBalance}/${BookingViewModel.MAX_CREDITS} cr",
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
            )
        },
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route

            NavigationBar {
                NavigationBarItem(
                    selected = currentRoute == ROUTE_BROWSE,
                    onClick = { navigateSingleTop(navController, ROUTE_BROWSE) },
                    icon = { Icon(Icons.Default.Explore, contentDescription = null) },
                    label = { Text("Browse") }
                )
                NavigationBarItem(
                    selected = currentRoute == ROUTE_BASKET,
                    onClick = { navigateSingleTop(navController, ROUTE_BASKET) },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text("Basket") }
                )
                NavigationBarItem(
                    selected = currentRoute == ROUTE_HISTORY,
                    onClick = { navigateSingleTop(navController, ROUTE_HISTORY) },
                    icon = { Icon(Icons.Default.History, contentDescription = null) },
                    label = { Text("History") }
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ROUTE_BROWSE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(ROUTE_BROWSE) { BrowseScreen(viewModel) }
            composable(ROUTE_BASKET) { BasketScreen(viewModel) }
            composable(ROUTE_HISTORY) { HistoryScreen(viewModel) }
        }
    }
}

private fun navigateSingleTop(navController: androidx.navigation.NavHostController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
