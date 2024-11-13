package com.example.quizpr

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CategoryAdapter(
    private val context: Context, private val categoryModels: ArrayList<CategoryModel>
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        if (categoryModels.isEmpty()) return
        val model = categoryModels[position]
        holder.textView.text = model.categoryName
        Glide.with(context).load(model.categoryImage).into(holder.imageView)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, QuizActivity::class.java)
            intent.putExtra("catId", model.categoryId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return categoryModels.size
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.image)
        val textView: TextView = itemView.findViewById(R.id.category)
    }
}