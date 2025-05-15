package com.api_client_kotlin_v0

import com.api_client_kotlin_v0.models.LoginRequest
import com.api_client_kotlin_v0.models.RegisterRequest
import com.api_client_kotlin_v0.models.Ticket
import com.api_client_kotlin_v0.models.TicketRequest

interface ApiClient {
        suspend fun register(request: RegisterRequest): ApiResult<Unit>
        suspend fun login(request: LoginRequest): ApiResult<TokenResponse>
        suspend fun getTickets(): ApiResult<List<Ticket>>
        suspend fun createTicket(request: TicketRequest): ApiResult<String>
        suspend fun updateTicket(id: Int, request: TicketRequest): ApiResult<String>
       suspend fun deleteTicket(id: Int): ApiResult<String>
        suspend fun logout(): ApiResult<Unit>
}

@kotlinx.serialization.Serializable
data class TokenResponse(val accessToken: String, val refreshToken: String)