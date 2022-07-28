package com.example.aplikasicabai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

class SplashscreenActivity : AppCompatActivity() {
    private val waktu_loading = 4000
    //4000=4 detik

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        Handler().postDelayed({//Setelah loading maka akan langsung berpindah ke halaman login// }
            val Login= Intent(this@SplashscreenActivity, LoginActivity::class.java)
            startActivity(Login)
            finish()
        }, waktu_loading.toLong())
    }
    }