package com.example.gearrental.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gearrental.data.SampleGearData
import com.example.gearrental.model.BasketItem
import com.example.gearrental.model.Booking
import com.example.gearrental.model.BookingLineItem
import com.example.gearrental.model.BookingRequest
import com.example.gearrental.model.BookingResult
import com.example.gearrental.model.BookingStatus
import com.example.gearrental.model.GearItem
import com.example.gearrental.model.SortOption
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.UUID

/**
 * Single source of truth for the whole app, scoped to MainActivity so the
 * browse/basket/history Navigation Compose destinations all read and write
 * the same instance.
 */
class BookingViewModel : ViewModel() {

    companion object {
        private const val TAG = "BookingViewModel"
        const val MAX_CREDITS = 1000
        const val STARTING_CREDITS = 500
        const val MAX_BOOKING_COST = 400
        const val MIN_NIGHTS = 1
        const val MAX_NIGHTS = 7
    }

    // The catalog itself is never mutated by search/sort - only by a confirmed
    // or cancelled booking changing a resource's availableUnits.
    private val _resources = mutableStateListOf<GearItem>().apply { addAll(SampleGearData.initial()) }

    private val _basket = mutableStateListOf<BasketItem>()
    val basket: List<BasketItem> get() = _basket

    private val _bookings = mutableStateListOf<Booking>()
    val bookings: List<Booking> get() = _bookings

    var searchQuery by mutableStateOf("")
        private set

    var sortOption by mutableStateOf(SortOption.RATING_DESC)
        private set

    var creditBalance by mutableStateOf(STARTING_CREDITS)
        private set

    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 4)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    /**
     * Derived, filtered + sorted view of the catalog. Always builds a new
     * list; [_resources] itself is never reordered or filtered in place.
     */
    fun visibleResources(): List<GearItem> {
        val filtered = _resources.filter { resource ->
            resource.availableUnits > 0 &&
                (searchQuery.isBlank() ||
                    resource.name.contains(searchQuery, ignoreCase = true) ||
                    resource.category.contains(searchQuery, ignoreCase = true))
        }
        return when (sortOption) {
            SortOption.RATING_DESC -> filtered.sortedByDescending { it.rating }
            SortOption.YEAR_DESC -> filtered.sortedByDescending { it.year }
            SortOption.PRICE_ASC -> filtered.sortedBy { it.costPerNight }
        }
    }

    fun onSearchQueryChange(query: String) {
        Log.d(TAG, "Search query changed to '$query'")
        searchQuery = query
    }

    fun onSortOptionChange(option: SortOption) {
        Log.d(TAG, "Sort option changed to $option")
        sortOption = option
    }

    fun findResource(id: String): GearItem? = _resources.find { it.id == id }

    fun isInBasket(id: String): Boolean = _basket.any { it.resourceId == id }

    fun addToBasket(resourceId: String) {
        if (isInBasket(resourceId)) {
            Log.w(TAG, "Add to basket ignored, already present: $resourceId")
            return
        }
        Log.d(TAG, "Added to basket: $resourceId")
        _basket.add(BasketItem(resourceId, MIN_NIGHTS))
    }

    fun removeFromBasket(resourceId: String) {
        Log.d(TAG, "Removed from basket: $resourceId")
        _basket.removeAll { it.resourceId == resourceId }
    }

    fun updateNights(resourceId: String, nights: Int) {
        val clamped = nights.coerceIn(MIN_NIGHTS, MAX_NIGHTS)
        val index = _basket.indexOfFirst { it.resourceId == resourceId }
        if (index >= 0) {
            _basket[index] = _basket[index].copy(nights = clamped)
            Log.d(TAG, "Updated nights for $resourceId to $clamped")
        }
    }

    fun basketLineItems(): List<BookingLineItem> = _basket.mapNotNull { item ->
        val resource = findResource(item.resourceId) ?: return@mapNotNull null
        BookingLineItem(
            resourceId = resource.id,
            resourceName = resource.name,
            category = resource.category,
            nights = item.nights,
            costPerNight = resource.costPerNight,
            lineTotal = resource.costPerNight * item.nights
        )
    }

    fun basketTotal(): Int = basketLineItems().sumOf { it.lineTotal }

    /** Validation messages per the booking constraints; an empty list means booking can proceed. */
    fun basketValidationErrors(): List<String> {
        val errors = mutableListOf<String>()

        if (_basket.isEmpty()) {
            errors.add("Add at least one item to your basket before booking.")
            return errors
        }

        val total = basketTotal()
        if (total > MAX_BOOKING_COST) {
            errors.add("A single booking cannot exceed $MAX_BOOKING_COST credits (currently $total).")
        }
        if (total > creditBalance) {
            errors.add("You don't have enough credits for this booking (balance: $creditBalance).")
        }
        _basket.forEach { item ->
            if (item.nights !in MIN_NIGHTS..MAX_NIGHTS) {
                errors.add("Nights must be between $MIN_NIGHTS and $MAX_NIGHTS for every item.")
            }
            val resource = findResource(item.resourceId)
            if (resource == null || resource.availableUnits <= 0) {
                errors.add("One of the selected items is no longer available.")
            }
        }
        return errors
    }

    fun canBook(): Boolean = basketValidationErrors().isEmpty()

    fun buildBookingRequest(): BookingRequest = BookingRequest(
        items = basketLineItems(),
        totalCost = basketTotal(),
        creditBalanceBefore = creditBalance
    )

    /** Applied only after BookingActivity returns a confirmed result via the Activity Result API. */
    fun confirmBooking(result: BookingResult) {
        val request = result.request
        Log.d(TAG, "Confirming booking of ${request.items.size} item(s) for ${result.renterName}, total=${request.totalCost}")

        request.items.forEach { line ->
            val index = _resources.indexOfFirst { it.id == line.resourceId }
            if (index >= 0) {
                val current = _resources[index]
                _resources[index] = current.copy(availableUnits = (current.availableUnits - 1).coerceAtLeast(0))
            }
        }

        creditBalance = (creditBalance - request.totalCost).coerceIn(0, MAX_CREDITS)

        _bookings.add(
            Booking(
                id = UUID.randomUUID().toString(),
                renterName = result.renterName,
                items = request.items,
                totalCost = request.totalCost,
                status = BookingStatus.ACTIVE
            )
        )

        _basket.clear()
        _messages.tryEmit("Booking confirmed for ${result.renterName}!")
    }

    /** Called when BookingActivity is cancelled; no application state changes at all. */
    fun onBookingCancelled() {
        Log.d(TAG, "Booking cancelled, no state changes applied")
        _messages.tryEmit("Booking cancelled - no changes were made.")
    }

    fun cancelBooking(bookingId: String) {
        val index = _bookings.indexOfFirst { it.id == bookingId }
        if (index < 0) return
        val booking = _bookings[index]
        if (booking.status == BookingStatus.CANCELLED) return

        Log.d(TAG, "Cancelling booking $bookingId, refunding ${booking.totalCost} credits")

        booking.items.forEach { line ->
            val resIndex = _resources.indexOfFirst { it.id == line.resourceId }
            if (resIndex >= 0) {
                val current = _resources[resIndex]
                _resources[resIndex] = current.copy(availableUnits = current.availableUnits + 1)
            }
        }

        creditBalance = (creditBalance + booking.totalCost).coerceIn(0, MAX_CREDITS)
        _bookings[index] = booking.copy(status = BookingStatus.CANCELLED)
        _messages.tryEmit("Booking cancelled and ${booking.totalCost} credits refunded.")
    }
}
