package com.api_client_kotlin_v0.models

data class Ticket(
    val id: Int,
    val name: String,
    val location: String,
    val date: String,
    val freeSeats: Int,
    val price: Double
)
