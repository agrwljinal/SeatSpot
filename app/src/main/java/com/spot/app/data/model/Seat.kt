package com.spot.app.data.model

data class Seat(
    val seatId: String = "",
    val row: String = "",
    val col: Int = 0,
    val status: String = "available",
    val occupantId: String = "",
    val occupantName: String = ""
)
