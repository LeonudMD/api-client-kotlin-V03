package com.api_client_kotlin_v0.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.api_client_kotlin_v0.ApiClient
import com.api_client_kotlin_v0.R
import com.api_client_kotlin_v0.models.Ticket
import com.api_client_kotlin_v0.ui.adapters.TicketAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class TicketsFragment : Fragment() {

    private lateinit var rvTickets: RecyclerView
    private lateinit var adapter: TicketAdapter

    private lateinit var tvTicketCount: TextView
    private lateinit var tvLastUpdated: TextView
    private lateinit var btnRefresh: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tickets, container, false)

        rvTickets = view.findViewById(R.id.rvTickets)
        tvTicketCount = view.findViewById(R.id.tvTicketCount)
        tvLastUpdated = view.findViewById(R.id.tvLastUpdated)
        btnRefresh = view.findViewById(R.id.btnRefresh)

        rvTickets.layoutManager = LinearLayoutManager(context)
        adapter = TicketAdapter(emptyList())
        rvTickets.adapter = adapter

        loadTickets()

        // Кнопка «Обновить»
        btnRefresh.setOnClickListener {
            loadTickets()
        }

        return view
    }

    private fun loadTickets() {
        CoroutineScope(Dispatchers.IO).launch {
            val tickets = ApiClient.getTickets(requireContext()) // запрос к API
            withContext(Dispatchers.Main) {
                tickets?.let {
                    adapter.updateData(it)
                    tvTicketCount.text = "Количество билетов: ${it.size}"
                    tvLastUpdated.text = "Последнее обновление: ${getCurrentTime()}"
                }
            }
        }
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}
