package com.example.quizpr

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quizpr.databinding.ActivityResultBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private val points = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val correctAnswers = intent.getIntExtra("correct", 0)
        val totalQuestions = intent.getIntExtra("total", 0)
        val points = correctAnswers * points.toLong()
        binding.score.text = String.format("%d/%d", correctAnswers, totalQuestions)
        binding.earnedCoins.text = points.toString()
        val database = FirebaseFirestore.getInstance()
        database.collection("users").document(FirebaseAuth.getInstance().uid!!)
            .update("coins", FieldValue.increment(points))
        binding.restartBtn.setOnClickListener {
            startActivity(Intent(this@ResultActivity, MainActivity::class.java))
            finishAffinity()
        }
    }
}