package com.example.aplikasicabai.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.aplikasicabai.R
import com.example.aplikasicabai.databinding.FragmentDetailBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetailFragment : Fragment() {

    private var detailBinding: FragmentDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        detailBinding = FragmentDetailBinding.inflate(inflater, container, false)
        getDataFirebase()
        return detailBinding?.root
    }

    private fun getDataFirebase() {
        val database = FirebaseDatabase.getInstance().reference
        val databaseListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val configurationValue = snapshot.child("konfigurasi")
                var temperatureUpperLimit = configurationValue.child("ba_suhu").getValue()
                temperatureUpperLimit = temperatureUpperLimit ?: "-"
                var temperatureLowerLimit = configurationValue.child("bb_suhu").getValue()
                temperatureLowerLimit = temperatureLowerLimit ?: "-"

                var soilMoistureUpperLimit = configurationValue.child("ba_kelembapan").getValue()
                soilMoistureUpperLimit = soilMoistureUpperLimit ?: "-"
                var soilMoistureLowerLimit = configurationValue.child("bb_kelembapan").getValue()
                soilMoistureLowerLimit = soilMoistureLowerLimit ?: "-"

                var soilPhUpperLimit = configurationValue.child("ba_ph").getValue()
                soilPhUpperLimit = soilPhUpperLimit ?: "-"
                var soilPhLowerLimit = configurationValue.child("bb_ph").getValue()
                soilPhLowerLimit = soilPhLowerLimit ?: "-"
                var soilPhMaximumLimit = configurationValue.child("bmax_ph").getValue()
                soilPhMaximumLimit = soilPhMaximumLimit ?: "-"
                var soilPhMinimumLimit = configurationValue.child("bmin_ph").getValue()
                soilPhMinimumLimit = soilPhMinimumLimit ?: "-"

                detailBinding?.apply {
                    tvTemperatureUpperLimit.text = temperatureUpperLimit.toString()
                    tvTemperatureLowerLimit.text = temperatureLowerLimit.toString()
                    tvSoilMoistureUpperLimit.text = soilMoistureUpperLimit.toString()
                    tvSoilMoistureLowerLimit.text = soilMoistureLowerLimit.toString()
                    tvSoilPhUpperLimit.text = soilPhUpperLimit.toString()
                    tvSoilPhMaximumLimit.text = soilPhMaximumLimit.toString()
                    tvSoilPhLowerLimit.text = soilPhLowerLimit.toString()
                    tvSoilPhMinimumLimit.text = soilPhMinimumLimit.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        database.addValueEventListener(databaseListener)
    }
}