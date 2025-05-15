// app/src/main/java/com/api_client_kotlin_v0/ui/fragments/TicketsFragment.kt
package com.api_client_kotlin_v0.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.api_client_kotlin_v0.ApiClientImpl
import com.api_client_kotlin_v0.ApiResult
import com.api_client_kotlin_v0.R
import com.api_client_kotlin_v0.SessionManager
import com.api_client_kotlin_v0.ui.adapters.TicketAdapter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TicketsFragment : Fragment() {

    private lateinit var apiClient: ApiClientImpl
    private lateinit var rvTickets: RecyclerView
    private lateinit var adapter: TicketAdapter
    private lateinit var tvTicketCount: TextView
    private lateinit var tvLastUpdated: TextView
    private lateinit var btnRefresh: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tickets, container, false)

        apiClient = ApiClientImpl(SessionManager(requireContext()))

        rvTickets = view.findViewById(R.id.rvTickets)
        tvTicketCount = view.findViewById(R.id.tvTicketCount)
        tvLastUpdated = view.findViewById(R.id.tvLastUpdated)
        btnRefresh = view.findViewById(R.id.btnRefresh)

        rvTickets.layoutManager = LinearLayoutManager(context)
        adapter = TicketAdapter()
        rvTickets.adapter = adapter

        // Загрузка сразу
        loadTickets()

        // Кнопка «Обновить»
        btnRefresh.setOnClickListener { loadTickets() }

        return view
    }

    private fun loadTickets() {
        viewLifecycleOwner.lifecycleScope.launch {
            when (val result = apiClient.getTickets()) {
                is ApiResult.Success -> {
                    val list = result.data
                    adapter.submitList(list)
                    tvTicketCount.text = "Количество билетов: ${list.size}"
                    tvLastUpdated.text = "Последнее обновление: ${getCurrentTime()}"
                }
                is ApiResult.Error -> {
                    val msg = result.exception?.message ?: "Код ${result.code}"
                    Toast.makeText(context, "Ошибка получения билетов: $msg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return sdf.format(Date())
    }
}
