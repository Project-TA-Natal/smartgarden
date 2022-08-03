package com.example.aplikasicabai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.aplikasicabai.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var registerBinding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(registerBinding.root)
        auth = Firebase.auth

        registerBinding.apply {
            btnRegistrasi.setOnClickListener {
                registerUser()
            }
            tvBtnLogin.setOnClickListener {
                goToLogin()
            }
        }
    }

    private  fun goToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        startActivity(loginIntent)
    }

    private  fun registerUser() {
        registerBinding.apply {
            val username = etUsernameRegister.text.toString()
            val password = etPasswordRegister.text.toString()
            val confirmPassword = etConfirmPasswordRegister.text.toString()
            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                if (username.isEmpty()) {
                    edtRegistrasiEmail.error = getString(R.string.val_email_regis)
                }
                if (password.isEmpty()) {
                    edtRegistrasiPassword.error = getString(R.string.val_pass_regis)
                }
                if (confirmPassword.isEmpty()) {
                    edtRegistrasiRepass.error = getString(R.string.val_repass_regis)
                }
            }
            if (username.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                edtRegistrasiEmail.error = getString(R.string.val_email)
            }
            if (username.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                edtRegistrasiEmail.error = null
            }
            if (password.isNotEmpty() && password.length > 6) {
                edtRegistrasiPassword.error = null
            }
            if (password.isNotEmpty() && password.length < 6) {
                edtRegistrasiPassword.error = getString(R.string.val_password)
            }
            if (confirmPassword.isNotEmpty() && confirmPassword.length > 6) {
                edtRegistrasiRepass.error = null
            }
            if (confirmPassword.isNotEmpty() && confirmPassword.length < 6) {
                edtRegistrasiRepass.error = getString(R.string.val_password)
            }

            if (username.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password != confirmPassword) {
                    Toast.makeText(this@RegisterActivity, "Password masih salah", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(username, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this@RegisterActivity, "Berhasil Register", Toast.LENGTH_SHORT).show()
                            val mainIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(mainIntent)
                            finish()
                        }
                    }.addOnFailureListener { exception ->
                        Toast.makeText(this@RegisterActivity, exception.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}