package com.example.aplikasicabai.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasicabai.R
import com.example.aplikasicabai.RecyclerAdapter
import com.example.aplikasicabai.databinding.FragmentHomeBinding
import com.example.aplikasicabai.model.Monitoring
import com.example.aplikasicabai.model.MonitoringConfig
import com.example.aplikasicabai.model.Notification
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class HomeFragment : Fragment() {

    private var homeBinding: FragmentHomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = FragmentHomeBinding.inflate(inflater, container,false)
        getDataFirebase()
        return homeBinding?.root
    }

    private fun getDataFirebase() {
        val database = FirebaseDatabase.getInstance().reference
//        database.child("notifications").removeValue()
        val notificationReference = FirebaseDatabase.getInstance().getReference("notifications")
        val databaseListener = object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(snapshot: DataSnapshot) {
                val monitoring = snapshot.child("monitoring")
                val suhu = monitoring.child("suhu").getValue()
                val kelembapan = monitoring.child("kelembapan").getValue()
                val phTanah = monitoring.child("ph_tanah").getValue()
                val kipas = monitoring.child("kipas").getValue()
                val penetralPh = monitoring.child("penetral_ph").getValue()
                val pompa = monitoring.child("pompa").getValue()

                val kelembapanValue = kelembapan.toString()
                val suhuValue = suhu.toString()
                val phTanahValue = phTanah.toString()
                var kipasValue = "OFF"
                var penetralPhValue = "OFF"
                var pompaValue = "OFF"
                if (kipas == true) {
                    kipasValue = "ON"
                }
                if (pompa == true) {
                    pompaValue = "ON"
                }
                if (penetralPh == true) {
                    penetralPhValue = "ON"
                }

                val configurationValue = snapshot.child("konfigurasi")
                var temperatureUpperLimit = configurationValue.child("ba_suhu").getValue().toString()
                var temperatureLowerLimit = configurationValue.child("bb_suhu").getValue().toString()
                var soilMoistureUpperLimit = configurationValue.child("ba_kelembapan").getValue().toString()
                var soilMoistureLowerLimit = configurationValue.child("bb_kelembapan").getValue().toString()
                var soilPhUpperLimit = configurationValue.child("ba_ph").getValue().toString()
                var soilPhLowerLimit = configurationValue.child("bb_ph").getValue().toString()


                val sdf = SimpleDateFormat("dd MMMM yyyy hh:mm")
                val currentDate = sdf.format(Date())
                val notificationID = notificationReference.push().key!!

                //createNotification(
                //    Monitoring(kelembapanValue.toFloat(), suhuValue.toFloat(), phTanahValue.toFloat(), pompa as Boolean?, kipas as Boolean?, penetralPh as Boolean?),
                //    MonitoringConfig(soilMoistureLowerLimit, soilMoistureUpperLimit, temperatureLowerLimit, temperatureUpperLimit, soilPhLowerLimit, soilPhUpperLimit, soilPhMinimumLimit, soilPhMaximumLimit),
                //    notificationId
                //)

                if (kelembapanValue > soilMoistureUpperLimit) {
                    println("NOTIFIKASI 1")
                    val notification = Notification(notificationID, "Kelembapan Tanah berada diatas ambang batas", currentDate, false)
                    // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                    // notificationReference.child(notificationID).setValue(notification)
                }
                if (kelembapanValue < soilMoistureLowerLimit) {
                    println("NOTIFIKASI 2")
                    val notification = Notification(notificationID, "Kelembapan Tanah berada dibawah ambang batas", currentDate, false)
                }
                if (suhuValue > temperatureUpperLimit) {
                    println("NOTIFIKASI 3")
                    val notification = Notification(notificationID, "Suhu berada diatas ambang batas", currentDate, false)
                }
                if (suhuValue < temperatureLowerLimit) {
                    println("NOTIFIKASI 4")
                    val notification = Notification(notificationID, "Suhu berada dibawah ambang batas", currentDate, false)
                }
                if (phTanahValue > soilPhUpperLimit) {
                    println("NOTIFIKASI 5")
                    val notification = Notification(notificationID, "pH Tanah berada diatas ambang batas", currentDate, false)
                }
                if (phTanahValue < soilPhLowerLimit) {
                    println("NOTIFIKASI 6")
                    val notification = Notification(notificationID, "pH Tanah berada dibawah ambang batas", currentDate, false)
                }
                when (pompa) {
                    true -> {
                        println("NOTIFIKASI 7")
                        val notification = Notification(notificationID, "Pompa air sedang menyala pada tanggal ${currentDate}", currentDate, false)
                    }
                    false -> {
                        println("NOTIFIKASI 8")
                        val notification = Notification(notificationID, "Pompa air sedang mati pada tanggal ${currentDate}", currentDate, false)
                    }
                }
                when (kipas) {
                    true -> {
                        println("NOTIFIKASI 9")
                        val notification = Notification(notificationID, "Kipas angin sedang menyala pada tanggal ${currentDate}", currentDate, false)
                    }
                    false -> {
                        println("NOTIFIKASI 10")
                        val notification = Notification(notificationID, "Kipas angin sedang mati pada tanggal ${currentDate}", currentDate, false)
                    }
                }
                when (penetralPh) {
                    true -> {
                        println("NOTIFIKASI 11")
                        val notification = Notification(notificationID, "Penetral pH sedang menyala pada tanggal ${currentDate}", currentDate, false)
                    }
                    false -> {
                        println("NOTIFIKASI 12")
                        val notification = Notification(notificationID, "Penetral pH sedang mati pada tanggal ${currentDate}", currentDate, false)
                    }
                }

                homeBinding?.apply {
                    tvKelembapan.text = kelembapanValue
                    tvSuhu.text = suhuValue
                    tvPh.text = phTanahValue
                    tvPompaAir.text = pompaValue
                    tvKipasAngin.text = kipasValue
                    tvPenetralPh.text = penetralPhValue
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        database.addValueEventListener(databaseListener)
    }

    private fun createNotification(monitoring: Monitoring, monitoringConfig: MonitoringConfig, notificationID: String) {
        val kelembapanValue = monitoring.kelembapan
        val suhuValue = monitoring.suhu
        val phTanahValue = monitoring.phTanah
        val pompaAirValue = monitoring.pompaAir
        val kipasAnginValue = monitoring.kipasAngin
        val penetralPhValue = monitoring.penetralPh

        val batasBawahKelembapan = monitoringConfig.batasBawahKelembapan
        val batasAtasKelembapan = monitoringConfig.batasAtasKelembapan
        val batasBawahSuhu = monitoringConfig.batasBawahSuhu
        val batasAtasSuhu = monitoringConfig.batasAtasSuhu
        val batasBawahPh = monitoringConfig.batasBawahPh
        val batasAtasPh = monitoringConfig.batasAtasPh
        val batasMinimalPh = monitoringConfig.batasMinimalPh
        val batasMaximalPh = monitoringConfig.batasMaximalPh
    }
}


