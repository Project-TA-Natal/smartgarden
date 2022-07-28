package com.example.aplikasicabai

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.aplikasicabai.fragments.DetailFragment
import com.example.aplikasicabai.fragments.PengaturanFragment

class ViewPagerAdapter(fragment: Fragment)
    :FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2
    override fun createFragment(position: Int): Fragment {
       var fragment = Fragment()
        when(position){
            0 -> fragment = DetailFragment()
            1 -> fragment = PengaturanFragment()
        }
        return fragment
    }
}