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
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var homeBinding: FragmentHomeBinding? = null
    private var smartGardenDB: SmartGardenDatabase? = null
    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "smart garden channel"
    }
    // private val notificationRef = FirebaseDatabase.getInstance().getReference("notifications_kesel")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeBinding = FragmentHomeBinding.inflate(inflater, container,false)
        smartGardenDB = SmartGardenDatabase.getInstance(context?.applicationContext!!)
        getDataFirebase()
        return homeBinding?.root
    }

    private fun getDataFirebase() {
        val database = FirebaseDatabase.getInstance().reference
        // database.child("notifications").removeValue()
        val notificationReference = FirebaseDatabase.getInstance().getReference("notifications_kesel")
        val databaseListener = object : ValueEventListener {
            @SuppressLint("SimpleDateFormat")
            override fun onDataChange(snapshot: DataSnapshot) {
                val monitoring = snapshot.child("Monitoring")
                val suhu = monitoring.child("Suhu_Ruang").getValue()
                val kelembapan = monitoring.child("Kelembapan_Tanah").getValue()
                val phTanah = monitoring.child("pH_Tanah").getValue()
                val kipas = monitoring.child("Kipas").getValue()
                val penetralPh = monitoring.child("Penetral_pH").getValue()
                val pompa = monitoring.child("Pompa_Air").getValue()

                val kelembapanValue = kelembapan.toString()
                val suhuValue = suhu.toString()
                val phTanahValue = phTanah.toString()
                val kipasValue = kipas.toString()
                val penetralPhValue = penetralPh.toString()
                val pompaValue = pompa.toString()

                val configurationValue = snapshot.child("konfigurasi")
                var temperatureUpperLimit = configurationValue.child("ba_suhu").getValue().toString()
                var temperatureLowerLimit = configurationValue.child("bb_suhu").getValue().toString()
                var soilMoistureUpperLimit = configurationValue.child("ba_kelembapan").getValue().toString()
                var soilMoistureLowerLimit = configurationValue.child("bb_kelembapan").getValue().toString()
                var soilPhUpperLimit = configurationValue.child("ba_ph").getValue().toString()
                var soilPhLowerLimit = configurationValue.child("bb_ph").getValue().toString()

                val sdf = SimpleDateFormat("dd MMMM yyyy hh:mm")
                val currentDate = sdf.format(Date())

                val objectMonitoring = Monitoring(1, kelembapanValue, suhuValue, phTanahValue, pompaValue, kipasValue, penetralPhValue)
                println("INI objectMonitoring $objectMonitoring")

                GlobalScope.async {
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
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)

                            }
                            if (kelembapanValue.toFloat() < soilMoistureLowerLimit.toFloat()) {
                                println("NOTIFIKASI 2")
                                val message = "Kelembapan Tanah ($kelembapanValue) berada dibawah ambang batas"
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                        }
                        if (suhuDB.toFloat() != suhuValue.toFloat()) {
                            if (suhuValue.toFloat() > temperatureUpperLimit.toFloat()) {
                                println("NOTIFIKASI 3")
                                val message = "Suhu berada diatas ambang batas"
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                            if (suhuValue.toFloat() < temperatureLowerLimit.toFloat()) {
                                println("NOTIFIKASI 4")
                                val message = "Suhu berada dibawah ambang batas"
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                        }
                        if (phTanahDB.toFloat() != phTanahValue.toFloat()) {
                            if (phTanahValue.toFloat() > soilPhUpperLimit.toFloat()) {
                                println("NOTIFIKASI 5")
                                val message = "pH Tanah berada diatas ambang batas"
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                            if (phTanahValue.toFloat() < soilPhLowerLimit.toFloat()) {
                                println("NOTIFIKASI 6")
                                val message = "pH Tanah berada dibawah ambang batas"
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                        }
                        if (pompaAirDB != pompaValue) {
                            if (pompaValue == "ON") {
                                println("NOTIFIKASI 7")
                                val message = "Pompa air sedang menyala pada tanggal ${currentDate}"
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                            if (pompaValue == "OFF") {
                                println("NOTIFIKASI 8")
                                val message = "Pompa air sedang mati pada tanggal ${currentDate}"
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                        }
                        if (kipasAnginDB != kipasValue) {
                            if (kipasValue == "ON") {
                                println("NOTIFIKASI 9")
                                val message = "Kipas angin sedang menyala pada tanggal ${currentDate}"
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                            if (kipasValue == "OFF") {
                                println("NOTIFIKASI 10")
                                val message = "Kipas angin sedang mati pada tanggal ${currentDate}"
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                        }
                        if (penetralPhDB != penetralPhValue) {
                            if (penetralPhValue == "ON") {
                                println("NOTIFIKASI 11")
                                val message = "Penetral pH sedang menyala pada tanggal ${currentDate}"
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                            if (penetralPhValue == "OFF") {
                                println("NOTIFIKASI 12")
                                val message = "Penetral pH sedang mati pada tanggal ${currentDate}"
                                val notificationID = notificationReference.push().key!!
                                val updateObjectMonitoring = Monitoring(id = 1, kelembapan = kelembapanValue, suhu = suhuValue, phTanah = phTanahValue, pompaAir =  pompaValue, kipasAngin = kipasValue, penetralPh = penetralPhValue)
                                createNotificationToFirebase(notificationID, message, currentDate, false, updateObjectMonitoring)
                                sendNotification(message)
                            }
                        }
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
        println("INI updateObjectMonitoring $updateObjectMonitoring")
        val notification = Notification(notificationID, message, currentDate, isRead)
        println("NOTIF ID $notificationID")
         val notificationRef = FirebaseDatabase.getInstance().getReference("notifications_kesel")


        GlobalScope.launch {
//            notificationRef.child(notificationID).setValue(notification)
            val resultUpdateMonitoring = smartGardenDB?.monitoringDao()?.updateMitoring(updateObjectMonitoring)
            if (resultUpdateMonitoring != 0) {
                println("SUCCESSFULLY UPDATE MONITORING DB")
                notificationRef.child(notificationID).setValue(notification)
            } else {
                println("FAILED TO UPDATE MONITORING DB")
            }
        }
    }
}


