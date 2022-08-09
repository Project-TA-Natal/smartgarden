package com.example.aplikasicabai.fragments

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
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
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "smart garden channel"
    }

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
        // database.child("notifications").removeValue()
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
                println("INI kelembapanValue $kelembapanValue")
                println("INI soilMoistureUpperLimit $soilMoistureUpperLimit")
                if (kelembapanValue.toFloat() > soilMoistureUpperLimit.toFloat()) {
                    println("NOTIFIKASI 1")
                    val message = "Kelembapan Tanah berada diatas ambang batas"
                    val notification = Notification(notificationID, message, currentDate, false)
                    // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                    notificationReference.child(notificationID).setValue(notification)
                    sendNotification(message)

                }
                if (kelembapanValue.toFloat() < soilMoistureLowerLimit.toFloat()) {
                    println("NOTIFIKASI 2")
                    val message = "Kelembapan Tanah berada dibawah ambang batas"
                    val notification = Notification(notificationID, message, currentDate, false)
                    // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                    notificationReference.child(notificationID).setValue(notification)
                    sendNotification(message)
                }
                if (suhuValue.toFloat() > temperatureUpperLimit.toFloat()) {
                    println("NOTIFIKASI 3")
                    val message = "Suhu berada diatas ambang batas"
                    val notification = Notification(notificationID, message, currentDate, false)
                    // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                    notificationReference.child(notificationID).setValue(notification)
                    sendNotification(message)
                }
                if (suhuValue.toFloat() < temperatureLowerLimit.toFloat()) {
                    println("NOTIFIKASI 4")
                    val message = "Suhu berada dibawah ambang batas"
                    val notification = Notification(notificationID, message, currentDate, false)
                    // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                    notificationReference.child(notificationID).setValue(notification)
                    sendNotification(message)
                }
                if (phTanahValue.toFloat() > soilPhUpperLimit.toFloat()) {
                    println("NOTIFIKASI 5")
                    val message = "pH Tanah berada diatas ambang batas"
                    val notification = Notification(notificationID, message, currentDate, false)
                    // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                    notificationReference.child(notificationID).setValue(notification)
                    sendNotification(message)
                }
                if (phTanahValue.toFloat() < soilPhLowerLimit.toFloat()) {
                    println("NOTIFIKASI 6")
                    val message = "pH Tanah berada dibawah ambang batas"
                    val notification = Notification(notificationID, message, currentDate, false)
                    // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                    notificationReference.child(notificationID).setValue(notification)
                    sendNotification(message)
                }
                when (pompa) {
                    true -> {
                        println("NOTIFIKASI 7")
                        val message = "Pompa air sedang menyala pada tanggal ${currentDate}"
                        val notification = Notification(notificationID, message, currentDate, false)
                        // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                        notificationReference.child(notificationID).setValue(notification)
                        sendNotification(message)
                    }
                    false -> {
                        println("NOTIFIKASI 8")
                        val message = "Pompa air sedang mati pada tanggal ${currentDate}"
                        val notification = Notification(notificationID, message, currentDate, false)
                        // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                        notificationReference.child(notificationID).setValue(notification)
                        sendNotification(message)
                    }
                }
                when (kipas) {
                    true -> {
                        println("NOTIFIKASI 9")
                        val message = "Kipas angin sedang menyala pada tanggal ${currentDate}"
                        val notification = Notification(notificationID, message, currentDate, false)
                        // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                        notificationReference.child(notificationID).setValue(notification)
                        sendNotification(message)
                    }
                    false -> {
                        println("NOTIFIKASI 10")
                        val message = "Kipas angin sedang mati pada tanggal ${currentDate}"
                        val notification = Notification(notificationID, message, currentDate, false)
                        // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                        notificationReference.child(notificationID).setValue(notification)
                        sendNotification(message)
                    }
                }
                when (penetralPh) {
                    true -> {
                        println("NOTIFIKASI 11")
                        val message = "Penetral pH sedang menyala pada tanggal ${currentDate}"
                        val notification = Notification(notificationID, message, currentDate, false)
                        // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                        notificationReference.child(notificationID).setValue(notification)
                        sendNotification(message)
                    }
                    false -> {
                        println("NOTIFIKASI 12")
                        val message = "Penetral pH sedang mati pada tanggal ${currentDate}"
                        val notification = Notification(notificationID, message, currentDate, false)
                        // DI KOMEN SOALNYA DI CREATE TEREUS NOTIFNYA
                        notificationReference.child(notificationID).setValue(notification)
                        sendNotification(message)
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

    private fun sendNotification(message: String) {
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(resources.getString(R.string.title_notif))
            .setContentText(message)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = CHANNEL_NAME
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}


