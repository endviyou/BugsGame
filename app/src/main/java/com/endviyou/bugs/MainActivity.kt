package com.endviyou.bugs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.endviyou.bugs.fragments.*
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)

        val adapter = TabsAdapter(this)
        viewPager.adapter = adapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab?.position ?: 0
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        // Добавляем вкладки в новом порядке
        tabLayout.addTab(tabLayout.newTab().setText("Регистрация"))
        tabLayout.addTab(tabLayout.newTab().setText("Правила"))
        tabLayout.addTab(tabLayout.newTab().setText("Настройки"))
        tabLayout.addTab(tabLayout.newTab().setText("Игра"))
        tabLayout.addTab(tabLayout.newTab().setText("Авторы"))
    }
}

class TabsAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 5

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RegistrationFragment()   // Регистрация (открывается первой)
            1 -> RulesFragment()          // Правила
            2 -> SettingsFragment()       // Настройки
            3 -> GameFragment()           // Игра
            4 -> AuthorsFragment()        // Авторы
            else -> RegistrationFragment()
        }
    }
}