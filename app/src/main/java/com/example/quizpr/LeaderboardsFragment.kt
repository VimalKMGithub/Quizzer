package com.example.quizpr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quizpr.databinding.FragmentLeaderboardsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class LeaderboardsFragment : Fragment() {
    private var lbinding: FragmentLeaderboardsBinding? = null
    private val binding get() = lbinding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        lbinding = FragmentLeaderboardsBinding.inflate(inflater, container, false)
        val database = FirebaseFirestore.getInstance()
        val users = ArrayList<User>()
        val adapter = LeaderboardsAdapter(requireContext(), users)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        database.collection("users").orderBy("coins", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                for (snapshot in queryDocumentSnapshots) {
                    val user = snapshot.toObject(User::class.java)
                    users.add(user)
                }
                adapter.notifyDataSetChanged()
            }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        lbinding = null
    }
}