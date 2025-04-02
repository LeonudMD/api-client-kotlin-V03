package com.api_client_kotlin_v0

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.api_client_kotlin_v0.models.Ticket
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ApiClient {
    private val client = OkHttpClient()
    private val gson = Gson()
    private const val BASE_URL = "http://10.0.2.2:5000"

    // Регистрация
    suspend fun register(
        context: Context,
        username: String,
        email: String,
        password: String
    ): Boolean {
        val json = gson.toJson(
            mapOf(
                "username" to username,
                "email" to email,
                "password" to password
            )
        )
        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL/auth/register")
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    val errorMsg = "Ошибка регистрации: ${response.code}"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                    false
                } else {
                    true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMsg = "Ошибка: ${e.message}"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
                false
            }
        }
    }

    // Вход
    suspend fun login(context: Context, email: String, password: String): String {
        val json = gson.toJson(mapOf("email" to email, "password" to password))
        val requestBody = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$BASE_URL/auth/login")
            .post(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    val errorMsg = "Ошибка входа: ${response.code}"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                    return@withContext errorMsg
                }

                response.body?.string() ?: "Ошибка входа (пустой ответ)"
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMsg = "Ошибка: ${e.message}"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
                errorMsg
            }
        }
    }


    // Получение билетов с проверкой статуса и уведомлением об ошибке
    suspend fun getTickets(context: Context): List<Ticket>? {
        val sessionManager = SessionManager(context)
        val token = sessionManager.getAccessToken() ?: return null

        val request = Request.Builder()
            .url("$BASE_URL/eventure")
            .addHeader("Authorization", "Bearer $token")
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    val errorMsg = "Ошибка получения билетов: ${response.code}"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                    return@withContext null
                }
                response.body?.string()?.let {
                    return@withContext gson.fromJson(it, Array<Ticket>::class.java).toList()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                }
                null
            }
        }
    }

    // Создание билета с проверкой статуса и уведомлением
    suspend fun createTicket(
        context: Context,
        name: String,
        location: String,
        date: String,
        freeSeats: Int,
        price: Double
    ): String {

        val sessionManager = SessionManager(context)
        val token = sessionManager.getAccessToken() ?: ""

        val ticketRequest = mapOf(
            "name" to name,
            "location" to location,
            "date" to date,
            "freeSeats" to freeSeats,
            "price" to price
        )
        val json = gson.toJson(ticketRequest)
        val requestBody = json.toRequestBody("application/json".toMediaType())

        // Собираем запрос
        val requestBuilder = Request.Builder()
            .url("$BASE_URL/eventure")
            .post(requestBody)

        if (token.isNotEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d("CREATE_TICKET", "code=${response.code}, body=$responseBody")

                if (!response.isSuccessful) {
                    val errorMsg = "Ошибка создания билета: ${response.code}"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "$errorMsg\n$responseBody", Toast.LENGTH_LONG).show()
                    }
                    return@withContext errorMsg
                }

                return@withContext responseBody ?: "Билет создан (пустой ответ)"
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMsg = "Ошибка: ${e.message}"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
                errorMsg
            }
        }
    }

    // Обновление билета с проверкой статуса и уведомлением
    suspend fun updateTicket(
        context: Context,
        id: Int,
        name: String,
        location: String,
        date: String,
        freeSeats: Int,
        price: Double
    ): String {
        val token = SessionManager(context).getAccessToken() ?: return "Токен не найден"

        val ticketRequest = mapOf(
            "name" to name,
            "location" to location,
            "date" to date,
            "freeSeats" to freeSeats,
            "price" to price
        )
        val json = gson.toJson(ticketRequest)
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/eventure/$id")
            .addHeader("Authorization", "Bearer $token")
            .put(requestBody)
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    val errorMsg = "Ошибка обновления билета: ${response.code}"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                    return@withContext errorMsg
                }
                response.body?.string() ?: "Ошибка обновления билета"
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMsg = "Ошибка: ${e.message}"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
                errorMsg
            }
        }
    }

    // Удаление билета с проверкой статуса и уведомлением
    suspend fun deleteTicket(
        context: Context,
        id: Int
    ): String {
        val token = SessionManager(context).getAccessToken() ?: return "Токен не найден"

        val request = Request.Builder()
            .url("$BASE_URL/eventure/$id")
            .addHeader("Authorization", "Bearer $token")
            .delete()
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    val errorMsg = "Ошибка удаления билета: ${response.code}"
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                    }
                    return@withContext errorMsg
                }
                val successMsg = "Удалено: ${response.code}"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, successMsg, Toast.LENGTH_LONG).show()
                }
                successMsg
            } catch (e: Exception) {
                e.printStackTrace()
                val errorMsg = "Ошибка: ${e.message}"
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
                errorMsg
            }
        }
    }

    // Выход
    suspend fun logout(): String {
        val request = Request.Builder()
            .url("$BASE_URL/api/Users/logout")
            .post("".toRequestBody("application/json".toMediaType()))
            .build()
        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                "Выход: ${response.code}"
            } catch (e: Exception) {
                e.printStackTrace()
                "Ошибка: ${e.message}"
            }
        }
    }
}
