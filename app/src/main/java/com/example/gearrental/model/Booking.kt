package com.example.gearrental.model

enum class BookingStatus { ACTIVE, CANCELLED }

/** A committed booking, kept for the history screen. Lives only in MainActivity's ViewModel. */
data class Booking(
    val id: String,
    val renterName: String,
    val items: List<BookingLineItem>,
    val totalCost: Int,
    val status: BookingStatus
)
