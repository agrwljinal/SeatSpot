package com.spot.app.data.model

data class Booking(
    val bookingId: String = "",
    val userId: String = "",
    val userName: String = "",
    val seatId: String = "",
    val timeSlot: String = "",
    val date: String = "",
    val status: String = "active",
    val createdAt: Long = 0L,
    val expiresAt: Long = 0L
)
