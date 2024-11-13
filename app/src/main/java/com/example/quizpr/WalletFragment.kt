package com.example.quizpr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quizpr.databinding.FragmentWalletBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class WalletFragment : Fragment() {
    private lateinit var binding: FragmentWalletBinding
    private lateinit var database: FirebaseFirestore
    private lateinit var user: User
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalletBinding.inflate(inflater, container, false)
        database = FirebaseFirestore.getInstance()
        database.collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener { documentSnapshot ->
                user = documentSnapshot.toObject(User::class.java)!!
                binding.currentCoins.text = user.coins.toString()
            }
        binding.sendRequest.setOnClickListener {
            if (user.coins > 50000) {
                val uid = FirebaseAuth.getInstance().uid
                val payPal = binding.emailBox.text.toString()
                val request = WithdrawRequest(payPal, user.name)
                if (uid != null) {
                    database.collection("withdraws").document(uid).set(request)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context, "Request sent successfully...", Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            } else {
                Toast.makeText(context, "You need more coins to withdraw...", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return binding.root
    }
}