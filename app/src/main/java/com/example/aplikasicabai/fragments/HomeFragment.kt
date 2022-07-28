package com.example.aplikasicabai.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasicabai.R
import com.example.aplikasicabai.RecyclerAdapter
import com.example.aplikasicabai.databinding.FragmentHomeBinding
import com.google.firebase.database.*


class HomeFragment : Fragment() {
    private lateinit var dref : DatabaseReference
    private lateinit var binding: FragmentHomeBinding

    var nilaikelembaban = 0f
    var nilaisuhu = 0f
    var nilaiph =0f
    var statuspompa = "Off"
    var statuskipas ="Off"
    var statuspenetralph ="Off"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dref = FirebaseDatabase.getInstance().reference
        dref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                nilaikelembaban = snapshot.child("Monitoring/Kelembapan").value.toString().toFloat()
                nilaisuhu = snapshot.child("Monitoring/Suhu").value.toString().toFloat()
                nilaiph = snapshot.child("Monitoring/Ph Tanah").value.toString().toFloat()
                statuspompa = snapshot.child("Monitoring/Pompa").value.toString()
                statuskipas = snapshot.child("Monitoring/kipas").value.toString()
                statuspenetralph = snapshot.child("Monitoring/Penetral ph").value.toString()
                with(binding) {
                    kelembapan.text = "${nilaikelembaban}"
                    suhu.text = "${nilaisuhu}"
                    ph.text = "${nilaiph}"
                    pompaair.text = "${statuspompa}"
                    kipas.text = "${statuskipas}"
                    penetralph.text = "${statuspenetralph}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container,false)
        return binding.root

    }
}

