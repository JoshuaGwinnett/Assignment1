package com.example.gearrental.model

/** One entry in the user's basket before booking: a resource plus a rental length. */
data class BasketItem(
    val resourceId: String,
    val nights: Int
)
