package com.example.aplikasicabai.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aplikasicabai.R
import com.example.aplikasicabai.ViewPagerAdapter
import com.example.aplikasicabai.databinding.FragmentSettingBinding
import com.google.android.material.tabs.TabLayoutMediator

class SettingFragment : Fragment() {
    private var _binding:FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: ViewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPagerAdapter = ViewPagerAdapter(this)
        with(binding){
            viewPager.adapter = viewPagerAdapter

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when(position){
                    0 -> tab.text = "Details"
                    1 -> tab.text = "Setting"
                }
            }.attach()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}