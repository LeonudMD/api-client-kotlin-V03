package com.api_client_kotlin_v0

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.api_client_kotlin_v0.ApiClient.logout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_get -> viewPager.currentItem = 0
                R.id.nav_post -> viewPager.currentItem = 1
                R.id.nav_put -> viewPager.currentItem = 2
                R.id.nav_delete -> viewPager.currentItem = 3
                R.id.nav_logout -> {
                    CoroutineScope(Dispatchers.IO).launch {
                        logout()
                        val sessionManager = SessionManager(this@MainActivity)
                        sessionManager.clearTokens()
                        runOnUiThread {
                            val intent = Intent(this@MainActivity, AuthActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                    }
                }
            }
            true
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position in 0..3) {
                    bottomNavigation.menu.getItem(position).isChecked = true
                }
            }
        })
    }
}
