package com.api_client_kotlin_v0.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.api_client_kotlin_v0.R
import com.api_client_kotlin_v0.models.Ticket

class TicketAdapter
    : ListAdapter<Ticket, TicketAdapter.TicketViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Ticket>() {
            override fun areItemsTheSame(old: Ticket, new: Ticket) = old.id == new.id
            override fun areContentsTheSame(old: Ticket, new: Ticket) = old == new
        }
    }

    inner class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView      = view.findViewById(R.id.tvName)
        val tvLocation: TextView  = view.findViewById(R.id.tvLocation)
        val tvDate: TextView      = view.findViewById(R.id.tvDate)
        val tvFreeSeats: TextView = view.findViewById(R.id.tvFreeSeats)
        val tvPrice: TextView     = view.findViewById(R.id.tvPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = getItem(position)
        holder.tvName.text      = ticket.name
        holder.tvLocation.text  = "Место: ${ticket.location}"
        holder.tvDate.text      = "Дата: ${ticket.date}"
        holder.tvFreeSeats.text = "Свободные места: ${ticket.freeSeats}"
        holder.tvPrice.text     = "Цена: ${ticket.price}"
    }
}
