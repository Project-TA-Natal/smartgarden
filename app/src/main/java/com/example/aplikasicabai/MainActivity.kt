package com.example.aplikasicabai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.aplikasicabai.databinding.ActivityMainBinding
import com.example.aplikasicabai.fragments.HistoriFragment
import com.example.aplikasicabai.fragments.HomeFragment
import com.example.aplikasicabai.fragments.NotifikasiFragment
import com.example.aplikasicabai.fragments.SettingFragment

class MainActivity : AppCompatActivity() {

    private val homeFragment = HomeFragment()
    private val notificationFragment = NotifikasiFragment()
    private val settingFragment = SettingFragment()
    private val historiFragment = HistoriFragment()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(homeFragment)
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.itemHome -> replaceFragment(homeFragment)
                R.id.itemNotification -> replaceFragment(notificationFragment)
                R.id.itemSetting -> replaceFragment(settingFragment)
                R.id.itemHistori -> replaceFragment(historiFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        if(fragment !=null){
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commit()
        }
    }
}