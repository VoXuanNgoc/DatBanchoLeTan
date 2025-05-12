package com.example.datban.model

data class Reservation(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val pax: Int = 0,
    val date: String = "",
    val timeArrival: String = "",
    val timeDeparture: String = "",
    val table: String = "",
    val status: String = "Chưa đến"
)

