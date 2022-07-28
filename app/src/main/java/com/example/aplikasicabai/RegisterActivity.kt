package com.example.aplikasicabai

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.aplikasicabai.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding:ActivityRegisterBinding
    private lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }
        binding.btnRegistrasi.setOnClickListener {
            val email = binding.edtRegistrasiEmail.editText?.text.toString()
            val password = binding.edtRegistrasiPassword.editText?.text.toString()

            //validasi email
            if (email.isEmpty()){
                binding.edtRegistrasiEmail.error = getString(R.string.val_email_regis)
                binding.edtRegistrasiEmail.requestFocus()
                return@setOnClickListener
            }
            //validasi email tidak sesuai
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtRegistrasiEmail.error = getString(R.string.val_regis)
                binding.edtRegistrasiEmail.requestFocus()
                return@setOnClickListener
            }
            //validasi password
            if (password.isEmpty()){
                binding.edtRegistrasiPassword.error = getString(R.string.val_pass_regis)
                binding.edtRegistrasiPassword.requestFocus()
                return@setOnClickListener
            }
            //validasi panjang password
            if (password.length < 6){
                binding.edtRegistrasiPassword.error = getString(R.string.val_pass)
                binding.edtRegistrasiPassword.requestFocus()
                return@setOnClickListener
            }
            registerFirebase(email,password)
        }
    }

    private fun registerFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this){
                if (it.isSuccessful){
                    Toast.makeText(this, getString(R.string.succes_regis), Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,LoginActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(this, "$.{it.exception?message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}