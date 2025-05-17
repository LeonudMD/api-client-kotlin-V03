// app/src/main/java/com/api_client_kotlin_v0/ui/fragments/TicketUpdateFragment.kt
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
import com.api_client_kotlin_v0.models.TicketRequest
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class TicketUpdateFragment : Fragment() {

    private lateinit var apiClient: ApiClientImpl
    private lateinit var editId: EditText
    private lateinit var editName: EditText
    private lateinit var editLocation: EditText
    private lateinit var editDate: EditText
    private lateinit var editFreeSeats: EditText
    private lateinit var editPrice: EditText
    private lateinit var btnUpdate: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_ticket_update, container, false)

        apiClient = ApiClientImpl(SessionManager(requireContext()))

        editId = view.findViewById(R.id.editId)
        editName = view.findViewById(R.id.editName)
        editLocation = view.findViewById(R.id.editLocation)
        editDate = view.findViewById(R.id.editDate)
        editFreeSeats = view.findViewById(R.id.editFreeSeats)
        editPrice = view.findViewById(R.id.editPrice)
        btnUpdate = view.findViewById(R.id.btnUpdate)

        btnUpdate.setOnClickListener {
            val idText = editId.text.toString().trim()
            val name = editName.text.toString().trim()
            val location = editLocation.text.toString().trim()
            val date = editDate.text.toString().trim()
            val freeSeatsText = editFreeSeats.text.toString().trim()
            val priceText = editPrice.text.toString().trim()

            if (idText.isEmpty() || name.isEmpty() || location.isEmpty() ||
                date.isEmpty() || freeSeatsText.isEmpty() || priceText.isEmpty()
            ) {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = idText.toIntOrNull()
            val freeSeats = freeSeatsText.toIntOrNull()
            val price = priceText.toDoubleOrNull()
            if (id == null || freeSeats == null || price == null) {
                Toast.makeText(context, "Проверьте корректность числовых значений", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                when (val result = apiClient.updateTicket(
                    id,
                    TicketRequest(name, location, date, freeSeats, price)
                )) {
                    is ApiResult.Success -> {
                        Toast.makeText(context, result.data, Toast.LENGTH_LONG).show()
                    }
                    is ApiResult.Error -> {
                        val msg = result.exception?.message ?: "Код ${result.code}"
                        Toast.makeText(context, "Ошибка обновления: $msg", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        return view
    }
}
