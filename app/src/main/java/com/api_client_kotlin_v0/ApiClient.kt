package com.api_client_kotlin_v0

import com.api_client_kotlin_v0.models.LoginRequest
import com.api_client_kotlin_v0.models.RegisterRequest
import com.api_client_kotlin_v0.models.Ticket
import com.api_client_kotlin_v0.models.TicketRequest

interface ApiClient {
    suspend fun register(request: RegisterRequest): Boolean
    suspend fun login(request: LoginRequest): String
    suspend fun getTickets(): List<Ticket>?
    suspend fun createTicket(request: TicketRequest): String
    suspend fun updateTicket(id: Int, request: TicketRequest): String
    suspend fun deleteTicket(id: Int): String
    suspend fun logout(): String
}
