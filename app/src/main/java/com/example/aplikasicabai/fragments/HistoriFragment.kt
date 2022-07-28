package com.example.aplikasicabai.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.aplikasicabai.MainActivity
import com.example.aplikasicabai.R
import com.example.aplikasicabai.SplashscreenActivity
import com.example.aplikasicabai.databinding.FragmentHistoriBinding
import com.example.aplikasicabai.databinding.FragmentHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth

class HistoriFragment : Fragment() {
    private lateinit var binding: FragmentHistoriBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(("Log Out"))
                .setMessage("Apakah Anda yakin keluar dari aplikasi?")
                .setNegativeButton("No") { dialog, which ->
                    // Respond to negative button press
                }
                .setPositiveButton("Ya") { dialog, which ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(context, SplashscreenActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                .show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoriBinding.inflate(inflater, container,false)
        return binding.root

    }
}



