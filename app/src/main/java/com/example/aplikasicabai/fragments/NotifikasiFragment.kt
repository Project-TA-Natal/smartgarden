package com.example.aplikasicabai.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasicabai.RecyclerAdapter
import com.example.aplikasicabai.databinding.FragmentNotifikasiBinding


class NotifikasiFragment : Fragment() {

        private lateinit var binding: FragmentNotifikasiBinding

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            binding = FragmentNotifikasiBinding.inflate(inflater, container, false)
            return binding.root
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RecyclerAdapter()
        }
        }

    }