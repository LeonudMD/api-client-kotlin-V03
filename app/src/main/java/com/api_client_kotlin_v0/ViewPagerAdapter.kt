package com.api_client_kotlin_v0

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.api_client_kotlin_v0.ui.fragments.TicketCreateFragment
import com.api_client_kotlin_v0.ui.fragments.TicketDeleteFragment
import com.api_client_kotlin_v0.ui.fragments.TicketUpdateFragment
import com.api_client_kotlin_v0.ui.fragments.TicketsFragment

class ViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TicketsFragment()
            1 -> TicketCreateFragment()
            2 -> TicketUpdateFragment()
            3 -> TicketDeleteFragment()
            else -> TicketsFragment()
        }
    }
}
