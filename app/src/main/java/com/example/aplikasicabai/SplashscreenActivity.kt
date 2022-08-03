package com.example.aplikasicabai

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

@SuppressLint("CustomSplashScreen")
class SplashscreenActivity : AppCompatActivity() {
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("user", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        Handler().postDelayed({
            val sharedEmailValue = sharedPreferences.getString("email_key", "default_email")
            println("ini sharedEmailValue $sharedEmailValue")
            if (sharedEmailValue.equals("default_email")) {
                val loginIntent= Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            } else {
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }
            finish()
        }, 4000)
    }
}