package com.example.aplikasicabai.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasicabai.R
import com.example.aplikasicabai.databinding.ItemNotificationBinding
import com.example.aplikasicabai.model.Notification
import com.google.firebase.database.FirebaseDatabase

class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val notificationReference = FirebaseDatabase.getInstance().getReference("notifications")
    fun bind(notification: Notification) = ItemNotificationBinding.bind(itemView).run {
        tvNotificationMessage.text = notification.message
        tvNotificationDate.text = notification.date
        when (notification.isRead) {
            true -> tvNotificationStatus.text = ""
            else -> {
                tvNotificationStatus.text = "New"
                cardItemNotification.setBackgroundColor(itemView.resources.getColor(R.color.bg_color_app))
            }
        }
        itemView.setOnClickListener {
            notificationReference.child("${notification.id}").child("read").setValue(true)
            Toast.makeText(itemView.context, "${notification.message}", Toast.LENGTH_SHORT).show()
        }
    }

}