package com.api_client_kotlin_v0.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.api_client_kotlin_v0.ApiClient
import com.api_client_kotlin_v0.R
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TicketDeleteFragment : Fragment() {

    private lateinit var editId: EditText
    private lateinit var btnDelete: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ticket_delete, container, false)
        editId = view.findViewById(R.id.editId)
        btnDelete = view.findViewById(R.id.btnDelete)

        btnDelete.setOnClickListener {
            val idText = editId.text.toString().trim()
            if (idText.isNotEmpty()) {
                val id = idText.toIntOrNull()
                if (id != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = ApiClient.deleteTicket(requireContext(), id)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, response, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Введите корректный ID", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Введите ID билета для удаления", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
