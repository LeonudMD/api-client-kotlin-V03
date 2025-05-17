package com.api_client_kotlin_v0.models
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(val username: String, val email: String, val password: String)

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class TicketRequest(
    val name: String,
    val location: String,
    val date: String,
    val freeSeats: Int,
    val price: Double
)
