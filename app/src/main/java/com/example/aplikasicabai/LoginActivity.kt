package com.example.aplikasicabai

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.aplikasicabai.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("user", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)
        auth = FirebaseAuth.getInstance()

        loginBinding.apply {
            tvNewUser.setOnClickListener {
                goToRegister()
            }

            btnLogin.setOnClickListener {
                login()
            }
        }
    }

    private  fun goToRegister() {
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
    }

    private fun login() {
        loginBinding.apply {
            val username = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (username.isEmpty()) {
                edtLoginEmail.error = "Please input username"
            }
            if (username.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                edtLoginEmail.error = null
            }
            if (username.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                edtLoginEmail.error = getString(R.string.val_email)
            }
            if (password.isEmpty()) {
                edtLoginPass.error = "Please input password"
            }
            if (password.isNotEmpty() && password.length > 6) {
                edtLoginPass.error = null
            }
            if (password.isNotEmpty() && password.length < 6) {
                edtLoginPass.error = getString(R.string.val_password)
            }
            if (username.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(username, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        saveSharePrefs(username, password)
                        val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(mainIntent)
                        finish()
                    }
                }.addOnFailureListener { exception ->
                    Toast.makeText(this@LoginActivity, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveSharePrefs(email: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email_key", email)
        editor.putString("password_key", password)
        editor.apply()
        Toast.makeText(this@LoginActivity, "Successfully login: $email", Toast.LENGTH_SHORT).show()
    }
}