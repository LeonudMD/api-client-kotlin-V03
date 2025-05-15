package com.api_client_kotlin_v0

import android.content.Context
import android.widget.Toast
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
    private val context: Context,
    private val baseUrl: String = "http://10.0.2.2:5000"
) : ApiClient {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun register(request: RegisterRequest): Boolean = withContext(Dispatchers.IO) {
        val body = json.encodeToString(request)
            .toRequestBody("application/json".toMediaType())
        val resp = client.newCall(Request.Builder()
            .url("$baseUrl/auth/register")
            .post(body)
            .build()
        ).execute()
        if (!resp.isSuccessful) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка регистрации: ${resp.code}", Toast.LENGTH_LONG).show()
            }
        }
        resp.isSuccessful
    }

    override suspend fun login(request: LoginRequest): String = withContext(Dispatchers.IO) {
        val body = json.encodeToString(request)
            .toRequestBody("application/json".toMediaType())
        val resp = client.newCall(Request.Builder()
            .url("$baseUrl/auth/login")
            .post(body)
            .build()
        ).execute()
        val text = resp.body?.string().orEmpty()
        if (resp.isSuccessful) {
            // сохраняем токены сразу после успешного логина
            val obj = json.decodeFromString<TokenResponse>(text)
            sessionManager.saveAuthTokens(obj.accessToken, obj.refreshToken)
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка входа: ${resp.code}", Toast.LENGTH_LONG).show()
            }
        }
        text
    }

    override suspend fun getTickets(): List<Ticket>? = withContext(Dispatchers.IO) {
        sessionManager.getAccessToken()?.let { token ->
            val resp = client.newCall(Request.Builder()
                .url("$baseUrl/eventure")
                .addHeader("Authorization", "Bearer $token")
                .build()
            ).execute()
            if (!resp.isSuccessful) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка получения билетов: ${resp.code}", Toast.LENGTH_LONG).show()
                }
                return@withContext null
            }
            json.decodeFromString(resp.body?.string().orEmpty())
        }
    }

    override suspend fun createTicket(request: TicketRequest): String = withContext(Dispatchers.IO) {
        val token = sessionManager.getAccessToken().orEmpty()
        val body = json.encodeToString(request)
            .toRequestBody("application/json".toMediaType())
        val builder = Request.Builder().url("$baseUrl/eventure").post(body)
        if (token.isNotEmpty()) builder.addHeader("Authorization", "Bearer $token")
        val resp = client.newCall(builder.build()).execute()
        val text = resp.body?.string().orEmpty()
        if (!resp.isSuccessful) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка создания: ${resp.code}\n$text", Toast.LENGTH_LONG).show()
            }
        }
        text
    }

    override suspend fun updateTicket(id: Int, request: TicketRequest): String = withContext(Dispatchers.IO) {
        val token = sessionManager.getAccessToken().orEmpty()
        val body = json.encodeToString(request)
            .toRequestBody("application/json".toMediaType())
        val resp = client.newCall(Request.Builder()
            .url("$baseUrl/eventure/$id")
            .addHeader("Authorization", "Bearer $token")
            .put(body)
            .build()
        ).execute()
        if (!resp.isSuccessful) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка обновления: ${resp.code}", Toast.LENGTH_LONG).show()
            }
        }
        resp.body?.string().orEmpty()
    }

    override suspend fun deleteTicket(id: Int): String = withContext(Dispatchers.IO) {
        val token = sessionManager.getAccessToken().orEmpty()
        val resp = client.newCall(Request.Builder()
            .url("$baseUrl/eventure/$id")
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()
        ).execute()
        val msg = if (resp.isSuccessful) "Удалено: ${resp.code}" else "Ошибка удаления: ${resp.code}"
        withContext(Dispatchers.Main) {
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
        msg
    }

    override suspend fun logout(): String = withContext(Dispatchers.IO) {
        val resp = client.newCall(Request.Builder()
            .url("$baseUrl/api/Users/logout")
            .post("".toRequestBody("application/json".toMediaType()))
            .build()
        ).execute()
        "Выход: ${resp.code}"
    }

    // Модель для разбора токенов после логина
    @kotlinx.serialization.Serializable
    private data class TokenResponse(val accessToken: String, val refreshToken: String)
}
