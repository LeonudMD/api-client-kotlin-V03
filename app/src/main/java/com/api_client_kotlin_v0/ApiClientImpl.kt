package com.api_client_kotlin_v0

import com.api_client_kotlin_v0.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request

class ApiClientImpl(
    private val sessionManager: SessionManager,
    private val baseUrl: String = "http://10.0.2.2:5000"
) : ApiClient {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun register(request: RegisterRequest): ApiResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val body = json.encodeToString(request)
                    .toRequestBody("application/json".toMediaType())
                val resp = client.newCall(
                    Request.Builder()
                        .url("$baseUrl/auth/register")
                        .post(body)
                        .build()
                ).execute()
                if (resp.isSuccessful) ApiResult.Success(Unit)
                else ApiResult.Error(code = resp.code)
            } catch (e: Throwable) {
                ApiResult.Error(exception = e)
            }
        }

    override suspend fun login(request: LoginRequest): ApiResult<TokenResponse> =
        withContext(Dispatchers.IO) {
            try {
                val body = json.encodeToString(request)
                    .toRequestBody("application/json".toMediaType())
                val resp = client.newCall(
                    Request.Builder()
                        .url("$baseUrl/auth/login")
                        .post(body)
                        .build()
                ).execute()
                val text = resp.body?.string().orEmpty()
                if (!resp.isSuccessful) {
                    ApiResult.Error(code = resp.code)
                } else {
                    val tr = json.decodeFromString<TokenResponse>(text)
                    sessionManager.saveAuthTokens(tr.accessToken, tr.refreshToken)
                    ApiResult.Success(tr)
                }
            } catch (e: Throwable) {
                ApiResult.Error(exception = e)
            }
        }

    override suspend fun getTickets(): ApiResult<List<Ticket>> =
        withContext(Dispatchers.IO) {
            val token = sessionManager.getAccessToken()
                ?: return@withContext ApiResult.Error(exception = IllegalStateException("No token"))
            try {
                val resp = client.newCall(
                    Request.Builder()
                        .url("$baseUrl/eventure")
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                ).execute()
                if (!resp.isSuccessful) {
                    ApiResult.Error(code = resp.code)
                } else {
                    val list = json.decodeFromString<List<Ticket>>(resp.body?.string().orEmpty())
                    ApiResult.Success(list)
                }
            } catch (e: Throwable) {
                ApiResult.Error(exception = e)
            }
        }

    override suspend fun createTicket(request: TicketRequest): ApiResult<String> =
        withContext(Dispatchers.IO) {
            try {
                val token = sessionManager.getAccessToken().orEmpty()
                val body = json.encodeToString(request)
                    .toRequestBody("application/json".toMediaType())
                val builder = Request.Builder()
                    .url("$baseUrl/eventure")
                    .post(body)
                if (token.isNotEmpty()) builder.addHeader("Authorization", "Bearer $token")
                val resp = client.newCall(builder.build()).execute()
                val text = resp.body?.string().orEmpty()
                if (resp.isSuccessful) ApiResult.Success(text)
                else ApiResult.Error(code = resp.code)
            } catch (e: Throwable) {
                ApiResult.Error(exception = e)
            }
        }

    override suspend fun updateTicket(id: Int, request: TicketRequest): ApiResult<String> =
        withContext(Dispatchers.IO) {
            try {
                val token = sessionManager.getAccessToken().orEmpty()
                val body = json.encodeToString(request)
                    .toRequestBody("application/json".toMediaType())
                val resp = client.newCall(
                    Request.Builder()
                        .url("$baseUrl/eventure/$id")
                        .addHeader("Authorization", "Bearer $token")
                        .put(body)
                        .build()
                ).execute()
                val text = resp.body?.string().orEmpty()
                if (resp.isSuccessful) ApiResult.Success(text)
                else ApiResult.Error(code = resp.code)
            } catch (e: Throwable) {
                ApiResult.Error(exception = e)
            }
        }

    override suspend fun deleteTicket(id: Int): ApiResult<String> =
        withContext(Dispatchers.IO) {
            try {
                val token = sessionManager.getAccessToken().orEmpty()
                val resp = client.newCall(
                    Request.Builder()
                        .url("$baseUrl/eventure/$id")
                        .addHeader("Authorization", "Bearer $token")
                        .delete()
                        .build()
                ).execute()
                val text = "Код: ${resp.code}"
                if (resp.isSuccessful) ApiResult.Success(text)
                else ApiResult.Error(code = resp.code)
            } catch (e: Throwable) {
                ApiResult.Error(exception = e)
            }
        }

    override suspend fun logout(): ApiResult<Unit> =
        withContext(Dispatchers.IO) {
            try {
                client.newCall(
                    Request.Builder()
                        .url("$baseUrl/api/Users/logout")
                        .post("".toRequestBody("application/json".toMediaType()))
                        .build()
                ).execute()
                ApiResult.Success(Unit)
            } catch (e: Throwable) {
                ApiResult.Error(exception = e)
            }
        }
}
