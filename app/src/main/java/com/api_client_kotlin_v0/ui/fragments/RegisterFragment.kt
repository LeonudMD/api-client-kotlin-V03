package com.api_client_kotlin_v0.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.api_client_kotlin_v0.ApiClientImpl
import com.api_client_kotlin_v0.MainActivity
import com.api_client_kotlin_v0.R
import com.api_client_kotlin_v0.SessionManager
import com.api_client_kotlin_v0.models.RegisterRequest
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {
    private lateinit var apiClient: ApiClientImpl
    private lateinit var editUsername: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var btnRegisterSubmit: MaterialButton
    private lateinit var btnBackToLogin: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        apiClient = ApiClientImpl(SessionManager(requireContext()), requireContext())
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

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val success = apiClient.register(RegisterRequest(username, email, password))
                    withContext(Dispatchers.Main) {
                        if (success) {
                            Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.beginTransaction()
                                .replace(R.id.authFragmentContainer, LoginFragment())
                                .commit()
                        } else {
                            Toast.makeText(context, "Ошибка регистрации!", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
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
