package com.api_client_kotlin_v0.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.api_client_kotlin_v0.ApiClient
import com.api_client_kotlin_v0.MainActivity
import com.api_client_kotlin_v0.R
import com.api_client_kotlin_v0.SessionManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginFragment : Fragment() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnGoToRegister: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        editEmail = view.findViewById(R.id.editEmail)
        editPassword = view.findViewById(R.id.editPassword)
        btnLogin = view.findViewById(R.id.btnLoginSubmit)
        btnGoToRegister = view.findViewById(R.id.btnGoToRegister)

        // Кнопка "Войти"
        btnLogin.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val response = ApiClient.login(requireContext(), email, password)
                    Log.d("LOGIN_DEBUG", "Сервер вернул: $response")
                    withContext(Dispatchers.Main) {
                        try {
                            val jsonObject = JSONObject(response)
                            val accessToken = jsonObject.getString("accessToken")
                            val refreshToken = jsonObject.getString("refreshToken")

                            // Сохраняем токены
                            val sessionManager = SessionManager(requireContext())
                            sessionManager.saveAuthTokens(accessToken, refreshToken)
                            Log.d("LoginFragment", "accessToken: ${accessToken}")
                            Log.d("LoginFragment", "refreshToken: ${refreshToken}")

                            Toast.makeText(context, "Авторизация успешна", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }


        // Кнопка "Зарегистрируйтесь"
        btnGoToRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, RegisterFragment())
                .commit()
        }

        return view
    }
}
