package com.example.aplikasicabai.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasicabai.model.Notification
import com.example.aplikasicabai.RecyclerAdapter
import com.example.aplikasicabai.adapter.NotificationAdapter
import com.example.aplikasicabai.databinding.FragmentNotificationBinding
import com.google.firebase.database.*

class NotificationFragment : Fragment() {

    private var notificationBinding: FragmentNotificationBinding? = null
    private lateinit var dbref : DatabaseReference
    private lateinit var notificationList : ArrayList<Notification>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationBinding = FragmentNotificationBinding.inflate(inflater, container, false)
        notificationList = arrayListOf()
        getNotificationList()
        return notificationBinding?.root
    }

    private fun getNotificationList() {
        dbref = FirebaseDatabase.getInstance().getReference("notifications_kesel")
        val dbListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (notificationSnapshot in snapshot.children) {
                        val notification = notificationSnapshot.getValue(Notification::class.java)
                        notificationList.add(notification!!)
                    }
                    val linearLayoutManager = LinearLayoutManager(activity)
                    linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                    val notificationAdapter = NotificationAdapter(notificationList)
                    notificationBinding?.rvNotifications?.setHasFixedSize(true)
                    notificationBinding?.rvNotifications?.layoutManager = linearLayoutManager
                    notificationBinding?.rvNotifications?.adapter = notificationAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        dbref.addValueEventListener(dbListener)
    }
}