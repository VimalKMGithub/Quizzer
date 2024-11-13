package com.example.quizpr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quizpr.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {
    private var hbinding: FragmentHomeBinding? = null
    private val binding get() = hbinding!!
    private lateinit var database: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        hbinding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        database = FirebaseFirestore.getInstance()
        val categories = ArrayList<CategoryModel>()
        val adapter = CategoryAdapter(requireContext(), categories)
        database.collection("categories").addSnapshotListener { value, _ ->
                categories.clear()
                value?.let {
                    for (snapshot in it.documents) {
                        val model = snapshot.toObject(CategoryModel::class.java)
                        model?.categoryId = snapshot.id
                        model?.let { categoryModel -> categories.add(categoryModel) }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        binding.categoryList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.categoryList.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hbinding = null
    }
}