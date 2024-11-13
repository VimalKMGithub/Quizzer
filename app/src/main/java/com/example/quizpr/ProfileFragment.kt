package com.example.quizpr

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quizpr.databinding.FragmentProfileBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    private var pbinding: FragmentProfileBinding? = null
    private val binding get() = pbinding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        pbinding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.emailBox.isEnabled = false
        binding.passBox.isEnabled = false
        binding.nameBox.isEnabled = false

        super.onViewCreated(view, savedInstanceState)
        val userUid = FirebaseAuth.getInstance().currentUser?.uid
        val users = FirebaseFirestore.getInstance().collection("users")
        userUid?.let { uid ->
            users.document(uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        binding.emailBox.setText(documentSnapshot["email"].toString())
                        binding.nameBox.setText(documentSnapshot["name"].toString())
                    }
                }
        }
        binding.signOutBtn.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pbinding = null
    }
}