package com.example.gearrental.model

/**
 * A single piece of rentable outdoor/climbing gear. This is the app's fixed,
 * in-memory catalog entry — never Parcelable, since only a derived summary
 * ([BookingLineItem]) ever needs to cross the Activity boundary.
 */
data class GearItem(
    val id: String,
    val name: String,
    val category: String,
    val year: Int,
    val rating: Float,
    val availableUnits: Int,
    val costPerNight: Int,
    val description: String,
    val location: String,
    val condition: String
)
