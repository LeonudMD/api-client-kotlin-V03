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

class TicketCreateFragment : Fragment() {

    private lateinit var editName: EditText
    private lateinit var editLocation: EditText
    private lateinit var editDate: EditText
    private lateinit var editFreeSeats: EditText
    private lateinit var editPrice: EditText
    private lateinit var btnCreate: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_ticket_create, container, false)
        editName = view.findViewById(R.id.editName)
        editLocation = view.findViewById(R.id.editLocation)
        editDate = view.findViewById(R.id.editDate)
        editFreeSeats = view.findViewById(R.id.editFreeSeats)
        editPrice = view.findViewById(R.id.editPrice)
        btnCreate = view.findViewById(R.id.btnCreate)

        btnCreate.setOnClickListener {
            val name = editName.text.toString().trim()
            val location = editLocation.text.toString().trim()
            val date = editDate.text.toString().trim()
            val freeSeatsText = editFreeSeats.text.toString().trim()
            val priceText = editPrice.text.toString().trim()

            if (name.isNotEmpty() && location.isNotEmpty() && date.isNotEmpty() &&
                freeSeatsText.isNotEmpty() && priceText.isNotEmpty()) {
                val freeSeats = freeSeatsText.toIntOrNull()
                val price = priceText.toDoubleOrNull()

                if (freeSeats != null && price != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = ApiClient.createTicket(requireContext(), name, location, date, freeSeats, price)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, response, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Проверьте корректность числовых значений", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
