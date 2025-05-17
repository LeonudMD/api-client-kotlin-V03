package com.api_client_kotlin_v0.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.api_client_kotlin_v0.ApiClientImpl
import com.api_client_kotlin_v0.ApiResult
import com.api_client_kotlin_v0.R
import com.api_client_kotlin_v0.SessionManager
import com.api_client_kotlin_v0.models.RegisterRequest
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private lateinit var apiClient: ApiClientImpl
    private lateinit var editUsername: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnRegisterSubmit: MaterialButton
    private lateinit var btnBackToLogin: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Инициализируем ApiClientImpl
        apiClient = ApiClientImpl(
            SessionManager(requireContext())
        )

        // Находим View
        editUsername = view.findViewById(R.id.editUsername)
        editEmail = view.findViewById(R.id.editEmail)
        editPassword = view.findViewById(R.id.editPassword)
        btnRegisterSubmit = view.findViewById(R.id.btnRegisterSubmit)
        btnBackToLogin = view.findViewById(R.id.btnBackToLogin)

        // Кнопка "Зарегистрироваться"
        btnRegisterSubmit.setOnClickListener {
            val username = editUsername.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val password = editPassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Запускаем корутину в Main-потоке через lifecycleScope
            viewLifecycleOwner.lifecycleScope.launch {
                when (val result = apiClient.register(RegisterRequest(username, email, password))) {
                    is ApiResult.Success -> {
                        Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.authFragmentContainer, LoginFragment())
                            .commit()
                    }
                    is ApiResult.Error -> {
                        val msg = result.exception?.message ?: "Код ${result.code}"
                        Toast.makeText(context, "Ошибка регистрации: $msg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        // Кнопка "Вернуться к авторизации"
        btnBackToLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, LoginFragment())
                .commit()
        }

        return view
    }
}
