package com.example.quizpr

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quizpr.databinding.RowLeaderboardsBinding

class LeaderboardsAdapter(private val context: Context, private val users: ArrayList<User>) :
    RecyclerView.Adapter<LeaderboardsAdapter.LeaderboardViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_leaderboards, parent, false)
        return LeaderboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        val user = users[position]
        holder.binding.name.text = user.name
        holder.binding.coins.text = user.coins.toString()
        "#${position + 1}".also { holder.binding.index.text = it }
        Glide.with(context).load(user.profile).into(holder.binding.imageView7)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    inner class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: RowLeaderboardsBinding = RowLeaderboardsBinding.bind(itemView)
    }
}