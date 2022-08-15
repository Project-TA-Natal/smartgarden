package com.example.aplikasicabai.fragments

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.example.aplikasicabai.MainActivity
import com.example.aplikasicabai.R
import com.example.aplikasicabai.RecyclerAdapter
import com.example.aplikasicabai.database.SmartGardenDatabase
import com.example.aplikasicabai.databinding.FragmentHomeBinding
import com.example.aplikasicabai.model.Monitoring
import com.example.aplikasicabai.model.MonitoringConfig
import com.example.aplikasicabai.model.Notification
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class HomeFragment : Fragment() {

    private var _homeBinding: FragmentHomeBinding? = null
    private val homeBinding get() = _homeBinding!!
    private var smartGardenDB: SmartGardenDatabase? = null
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "smart garden channel"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _homeBinding = FragmentHomeBinding.inflate(inflater, container,false)
        smartGardenDB = SmartGardenDatabase.getInstance(context?.applicationContext!!)
        getDataFirebase()
        return homeBinding.root
    }

    private fun getDataFirebase() {
        val database = FirebaseDatabase.getInstance().reference
        // database.child("notifications").removeValue()
        val notificationReference = FirebaseDatabase.getInstance().getReference("notifications_kesel")
        val notificationID = notificationReference.push().key!!
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

                val objectMonitoring = Monitoring(1, kelembapanValue, suhuValue, phTanahValue, pompa as Boolean, kipas as Boolean, penetralPh as Boolean)
                println("INI objectMonitoring $objectMonitoring")

                GlobalScope.launch {
                    val monitoringRoomDB = smartGardenDB?.monitoringDao()?.getDetailMonitoring(1)
                    if (monitoringRoomDB == null) {
                        println("INI sampleData is NULL")
                        val resultMonitoring = smartGardenDB?.monitoringDao()?.createMonitoring(objectMonitoring)
                        if (resultMonitoring != 0.toLong()) {
                            println("SUKSES CREATE DATABASE")
                        } else {
                            println("GAGAL CREATE DATABASE")
                        }
                    }
                    if (monitoringRoomDB != null) {
                        println("INI sampleData $monitoringRoomDB")
                        val kelembapanDB = monitoringRoomDB.kelembapan
                        val suhuDB = monitoringRoomDB.suhu
                        val phTanahDB = monitoringRoomDB.phTanah
                        val pompaAirDB = monitoringRoomDB.pompaAir
                        val kipasAnginDB = monitoringRoomDB.kipasAngin
                        val penetralPhDB = monitoringRoomDB.penetralPh

                        if (kelembapanDB.toFloat() != kelembapanValue.toFloat()) {
                            if (kelembapanValue.toFloat() > soilMoistureUpperLimit.toFloat()) {
                                println("NOTIFIKASI 1")
                                val message = "Kelembapan Tanah ($kelembapanValue) berada diatas ambang batas"
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)

                            }
                            if (kelembapanValue.toFloat() < soilMoistureLowerLimit.toFloat()) {
                                println("NOTIFIKASI 2")
                                val message = "Kelembapan Tanah ($kelembapanValue) berada dibawah ambang batas"
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                        }
                        if (suhuDB.toFloat() != suhuValue.toFloat()) {
                            if (suhuValue.toFloat() > temperatureUpperLimit.toFloat()) {
                                println("NOTIFIKASI 3")
                                val message = "Suhu berada diatas ambang batas"
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                            if (suhuValue.toFloat() < temperatureLowerLimit.toFloat()) {
                                println("NOTIFIKASI 4")
                                val message = "Suhu berada dibawah ambang batas"
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                        }
                        if (phTanahDB.toFloat() != phTanahValue.toFloat()) {
                            if (phTanahValue.toFloat() > soilPhUpperLimit.toFloat()) {
                                println("NOTIFIKASI 5")
                                val message = "pH Tanah berada diatas ambang batas"
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                            if (phTanahValue.toFloat() < soilPhLowerLimit.toFloat()) {
                                println("NOTIFIKASI 6")
                                val message = "pH Tanah berada dibawah ambang batas"
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                        }
                        if (pompaAirDB != pompa) {
                            when (pompa) {
                                true -> {
                                    println("NOTIFIKASI 7")
                                    val message = "Pompa air sedang menyala pada tanggal ${currentDate}"
                                    val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                    createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                    sendNotification(message)
                                }
                                false -> {
                                    println("NOTIFIKASI 8")
                                    val message = "Pompa air sedang mati pada tanggal ${currentDate}"
                                    val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                    createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                    sendNotification(message)
                                }
                            }
                        }
                        if (kipasAnginDB != kipas) {
                            when (kipas) {
                                true -> {
                                    println("NOTIFIKASI 9")
                                    val message = "Kipas angin sedang menyala pada tanggal ${currentDate}"
                                    val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                    createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                    sendNotification(message)
                                }
                                false -> {
                                    println("NOTIFIKASI 10")
                                    val message = "Kipas angin sedang mati pada tanggal ${currentDate}"
                                    val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                    createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                    sendNotification(message)
                                }
                            }
                        }
                        if (penetralPhDB != penetralPh) {
                            when (penetralPh) {
                                true -> {
                                    println("NOTIFIKASI 11")
                                    val message = "Penetral pH sedang menyala pada tanggal ${currentDate}"
                                    val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                    createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                    sendNotification(message)
                                }
                                false -> {
                                    println("NOTIFIKASI 12")
                                    val message = "Penetral pH sedang mati pada tanggal ${currentDate}"
                                    val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompa, kipasAngin = kipas, penetralPh = penetralPh)
                                    createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                    sendNotification(message)
                                }
                            }
                        }
                    }
                }

                homeBinding.apply {
                    tvKelembapan.text = kelembapanValue
                    tvSuhu.text = suhuValue
                    tvPh.text = phTanahValue
                    tvPompaAir.text = pompaValue
                    tvKipasAngin.text = kipasValue
                    tvPenetralPh.text = penetralPhValue
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("INI ERROR $error")
            }

        }
        database.addValueEventListener(databaseListener)
    }

    private fun sendNotification(message: String) {
        val intent = Intent(activity, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            activity,
            0,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(resources.getString(R.string.title_notif))
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = CHANNEL_NAME
            builder.setChannelId(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationToFirebase(notificationID: String, message: String, currentDate: String, isRead: Boolean, updateObjectMonitoring: Monitoring) {
        val notification = Notification(notificationID, message, currentDate, isRead)
        println("NOTIF ID $notificationID")
        val notificationRef = FirebaseDatabase.getInstance().getReference("notifications_kesel")
        notificationRef.child(notificationID).setValue(notification)

        GlobalScope.launch {
            val resultUpdateMonitoring = smartGardenDB?.monitoringDao()?.updateMitoring(updateObjectMonitoring)
            if (resultUpdateMonitoring != 0) {
                println("SUCCESSFULLY UPDATE MONITORING DB")
            } else {
                println("FAILED TO UPDATE MONITORING DB")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _homeBinding = null
    }
}


