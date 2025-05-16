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

class TicketAdapter(
    private val onClick: (Ticket) -> Unit,
    private val onLongClick: (Ticket) -> Unit
) : ListAdapter<Ticket, TicketAdapter.TicketViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Ticket>() {
            override fun areItemsTheSame(old: Ticket, new: Ticket) = old.id == new.id
            override fun areContentsTheSame(old: Ticket, new: Ticket) = old == new
        }
    }

    inner class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView      = view.findViewById(R.id.tvName)
        private val tvLocation: TextView  = view.findViewById(R.id.tvLocation)
        private val tvDate: TextView      = view.findViewById(R.id.tvDate)
        private val tvFreeSeats: TextView = view.findViewById(R.id.tvFreeSeats)
        private val tvPrice: TextView     = view.findViewById(R.id.tvPrice)

        fun bind(ticket: Ticket) {
            tvName.text = ticket.name
            val ctx = itemView.context
            tvLocation.text  = ctx.getString(R.string.location_format, ticket.location)
            tvDate.text      = ctx.getString(R.string.date_format, ticket.date)
            tvFreeSeats.text = ctx.getString(R.string.free_seats_format, ticket.freeSeats)
            tvPrice.text     = ctx.getString(R.string.price_format, ticket.price)


            itemView.setOnClickListener    { onClick(ticket) }
            itemView.setOnLongClickListener {
                onLongClick(ticket)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
