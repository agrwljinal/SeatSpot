package com.spot.app.data.model

data class TimeSlot(
    val id: String = "",
    val label: String = "",
    val startHour: Int = 0,
    val endHour: Int = 0
)

val LAB_TIME_SLOTS = listOf(
    TimeSlot("slot1", "9:00 AM – 11:00 AM",  9,  11),
    TimeSlot("slot2", "11:00 AM – 1:00 PM",  11, 13),
    TimeSlot("slot3", "1:00 PM – 3:00 PM",   13, 15),
    TimeSlot("slot4", "3:00 PM – 5:00 PM",   15, 17),
    TimeSlot("slot5", "5:00 PM – 7:00 PM",   17, 19)
)
