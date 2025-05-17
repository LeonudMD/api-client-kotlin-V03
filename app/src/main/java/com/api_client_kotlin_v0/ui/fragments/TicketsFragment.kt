package com.api_client_kotlin_v0.ui.fragments

import android.app.AlertDialog
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var bottomNav: BottomNavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tickets, container, false)

        apiClient = ApiClientImpl(SessionManager(requireContext()))
        bottomNav = requireActivity().findViewById(R.id.bottomNavigation)

        rvTickets = view.findViewById(R.id.rvTickets)
        tvTicketCount = view.findViewById(R.id.tvTicketCount)
        tvLastUpdated = view.findViewById(R.id.tvLastUpdated)
        btnRefresh = view.findViewById(R.id.btnRefresh)
        fabAdd = view.findViewById(R.id.fabAddTicket)

        rvTickets.layoutManager = LinearLayoutManager(context)
        adapter = TicketAdapter(
            onClick = { ticket ->
                // короткий клик — перейти на редактирование
                bottomNav.selectedItemId = R.id.nav_put
            },
            onLongClick = { ticket ->
                // длинный клик — подтверждение удаления
                AlertDialog.Builder(requireContext())
                    .setTitle("Удалить билет #${ticket.id}?")
                    .setMessage("${ticket.name}, ${ticket.date}")
                    .setNegativeButton("Отмена", null)
                    .setPositiveButton("Удалить") { _, _ ->
                        viewLifecycleOwner.lifecycleScope.launch {
                            when (val r = apiClient.deleteTicket(ticket.id)) {
                                is ApiResult.Success -> {
                                    Toast.makeText(context, "Удалено", Toast.LENGTH_SHORT).show()
                                    loadTickets()
                                }
                                is ApiResult.Error -> {
                                    val msg = r.exception?.message ?: "Код ${r.code}"
                                    Toast.makeText(context, "Ошибка удаления: $msg", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                    .show()
            }
        )
        rvTickets.adapter = adapter

        // Первичная загрузка и обновление по кнопке
        loadTickets()
        btnRefresh.setOnClickListener { loadTickets() }

        fabAdd.setOnClickListener {
            bottomNav.selectedItemId = R.id.nav_post
        }

        return view
    }

    private fun loadTickets() {
        viewLifecycleOwner.lifecycleScope.launch {
            when (val r = apiClient.getTickets()) {
                is ApiResult.Success -> {
                    adapter.submitList(r.data)
                    tvTicketCount.text = getString(
                             R.string.ticket_count,
                             r.data.size
                    )
                     tvLastUpdated.text = getString(
                             R.string.last_updated,
                             getCurrentTime()
                     )
                }
                is ApiResult.Error -> {
                    val msg = r.exception?.message ?: "Код ${r.code}"
                    Toast.makeText(context, "Ошибка получения: $msg", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getCurrentTime(): String =
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())
}
