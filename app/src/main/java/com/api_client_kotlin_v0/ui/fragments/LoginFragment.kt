package com.api_client_kotlin_v0.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.api_client_kotlin_v0.ApiClientImpl
import com.api_client_kotlin_v0.ApiResult
import com.api_client_kotlin_v0.MainActivity
import com.api_client_kotlin_v0.R
import com.api_client_kotlin_v0.SessionManager
import com.api_client_kotlin_v0.models.LoginRequest
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private lateinit var apiClient: ApiClientImpl
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnGoToRegister: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Инициализируем ApiClientImpl
        apiClient = ApiClientImpl(
            SessionManager(requireContext())
        )

        // Находим View
        editEmail = view.findViewById(R.id.editEmail)
        editPassword = view.findViewById(R.id.editPassword)
        btnLogin = view.findViewById(R.id.btnLoginSubmit)
        btnGoToRegister = view.findViewById(R.id.btnGoToRegister)

        // Кнопка "Войти"
        btnLogin.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Корутинный вызов в UI-слое
            viewLifecycleOwner.lifecycleScope.launch {
                when (val result = apiClient.login(LoginRequest(email, password))) {
                    is ApiResult.Success -> {
                        Toast.makeText(context, "Авторизация успешна", Toast.LENGTH_SHORT).show()
                        // Переход в MainActivity
                        Intent(activity, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }.also { startActivity(it) }
                    }
                    is ApiResult.Error -> {
                        val msg = result.exception?.message ?: "Код ${result.code}"
                        Toast.makeText(context, "Ошибка входа: $msg", Toast.LENGTH_LONG).show()
                    }
                }
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
