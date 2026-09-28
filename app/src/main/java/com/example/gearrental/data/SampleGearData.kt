package com.example.gearrental.data

import com.example.gearrental.model.GearItem

/** Fixed, in-memory catalog. No database or network call — this is the entire data source. */
object SampleGearData {
    fun initial(): List<GearItem> = listOf(
        GearItem(
            id = "g1",
            name = "4-Season Tent",
            category = "Camping",
            year = 2023,
            rating = 4.5f,
            availableUnits = 3,
            costPerNight = 45,
            description = "Insulated tent rated for alpine conditions, sleeps 2.",
            location = "Shed 2",
            condition = "Good"
        ),
        GearItem(
            id = "g2",
            name = "Dynamic Climbing Rope 60m",
            category = "Climbing",
            year = 2024,
            rating = 4.8f,
            availableUnits = 5,
            costPerNight = 25,
            description = "60m single rope, low impact force, dry-treated.",
            location = "Rack A3",
            condition = "New"
        ),
        GearItem(
            id = "g3",
            name = "Full-Body Harness",
            category = "Climbing",
            year = 2022,
            rating = 4.2f,
            availableUnits = 4,
            costPerNight = 10,
            description = "Adjustable harness suitable for lead and top-rope climbing.",
            location = "Rack A1",
            condition = "Good"
        ),
        GearItem(
            id = "g4",
            name = "Camp Stove",
            category = "Camping",
            year = 2021,
            rating = 3.9f,
            availableUnits = 2,
            costPerNight = 15,
            description = "Compact canister stove, boils 1L of water in under 3 minutes.",
            location = "Shed 1",
            condition = "Worn"
        ),
        GearItem(
            id = "g5",
            name = "Steel Crampons",
            category = "Mountaineering",
            year = 2023,
            rating = 4.6f,
            availableUnits = 3,
            costPerNight = 20,
            description = "12-point steel crampons for snow and ice routes.",
            location = "Rack B2",
            condition = "Good"
        ),
        GearItem(
            id = "g6",
            name = "Approach Shoes",
            category = "Footwear",
            year = 2024,
            rating = 4.3f,
            availableUnits = 6,
            costPerNight = 12,
            description = "Sticky-rubber soled shoes built for technical approaches.",
            location = "Rack C1",
            condition = "New"
        ),
        GearItem(
            id = "g7",
            name = "Avalanche Beacon",
            category = "Safety",
            year = 2022,
            rating = 4.9f,
            availableUnits = 4,
            costPerNight = 18,
            description = "Digital 3-antenna transceiver for backcountry safety.",
            location = "Shed 3",
            condition = "Good"
        )
    )
}
