package com.example.aplikasicabai

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasicabai.databinding.ActivityNotificationBinding
import com.google.firebase.database.*

class Notification : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var binding: ActivityNotificationBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance().reference
    }
}









