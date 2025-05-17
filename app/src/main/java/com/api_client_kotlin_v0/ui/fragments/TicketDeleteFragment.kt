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
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class TicketDeleteFragment : Fragment() {

    private lateinit var apiClient: ApiClientImpl
    private lateinit var editId: EditText
    private lateinit var btnDelete: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_ticket_delete, container, false)

        apiClient = ApiClientImpl(SessionManager(requireContext()))

        editId = view.findViewById(R.id.editId)
        btnDelete = view.findViewById(R.id.btnDelete)

        btnDelete.setOnClickListener {
            val idText = editId.text.toString().trim()
            if (idText.isEmpty()) {
                Toast.makeText(context, "Введите ID билета для удаления", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = idText.toIntOrNull()
            if (id == null) {
                Toast.makeText(context, "Введите корректный ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                when (val result = apiClient.deleteTicket(id)) {
                    is ApiResult.Success -> {
                        Toast.makeText(context, result.data, Toast.LENGTH_LONG).show()
                    }
                    is ApiResult.Error -> {
                        val msg = result.exception?.message ?: "Код ${result.code}"
                        Toast.makeText(context, "Ошибка удаления: $msg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        return view
    }
}
