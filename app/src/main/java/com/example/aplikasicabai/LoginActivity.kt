package com.example.aplikasicabai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.aplikasicabai.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.edtLoginEmail.editText?.text.toString()
            val password = binding.edtLoginPass.editText?.text.toString()

            //validasi email
            if (email.isEmpty()){
                binding.edtLoginEmail.error = getString(R.string.harus_diisi)
                binding.edtLoginEmail.requestFocus()
                return@setOnClickListener
            }
            //validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtLoginEmail.error = getString(R.string.val_email)
                binding.edtLoginEmail.requestFocus()
                return@setOnClickListener
            }

            //validasi panjang password
            if (password.length < 6){
                binding.edtLoginPass.error = getString(R.string.val_password)
                binding.edtLoginPass.requestFocus()
                return@setOnClickListener
            }
            loginFirebase(email,password)
        }
    }

    private fun loginFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(this, "Selamat Datang", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, "$.{it.exception?message}", Toast.LENGTH_SHORT).show()
                }
            }
    }


}