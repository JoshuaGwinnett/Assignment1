package com.example.gearrental.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * A single resource line within a booking, sized down to just what
 * BookingActivity needs to render a summary. Parcelable so it can travel
 * inside [BookingRequest]/[BookingResult] across the Activity boundary.
 */
@Parcelize
data class BookingLineItem(
    val resourceId: String,
    val resourceName: String,
    val category: String,
    val nights: Int,
    val costPerNight: Int,
    val lineTotal: Int
) : Parcelable

/**
 * The outbound payload: MainActivity -> BookingActivity, describing the basket
 * at the moment "Book Now" was pressed.
 */
@Parcelize
data class BookingRequest(
    val items: List<BookingLineItem>,
    val totalCost: Int,
    val creditBalanceBefore: Int
) : Parcelable

/**
 * The inbound payload: BookingActivity -> MainActivity, returned via the
 * Activity Result API. [confirmed] distinguishes a genuine confirmation from
 * a cancel (explicit button or system back) so MainActivity's ViewModel knows
 * whether to commit state changes at all.
 */
@Parcelize
data class BookingResult(
    val confirmed: Boolean,
    val request: BookingRequest,
    val renterName: String
) : Parcelable
