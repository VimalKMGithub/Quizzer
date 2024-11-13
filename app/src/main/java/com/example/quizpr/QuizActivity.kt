package com.example.quizpr

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.quizpr.databinding.ActivityQuizBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private var questions = ArrayList<Question>()
    private lateinit var question: Question
    private lateinit var timer: CountDownTimer
    private lateinit var database: FirebaseFirestore
    private var correctAnswers = 0
    private var index = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseFirestore.getInstance()
        val catId = intent.getStringExtra("catId")
        val random = Random()
        val rand = random.nextInt(5)
        database.collection("categories").document(catId!!).collection("questions")
            .whereGreaterThanOrEqualTo("index", rand).orderBy("index").limit(5).get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                if (queryDocumentSnapshots.documents.size < 5) {
                    database.collection("categories").document(catId).collection("questions")
                        .whereLessThanOrEqualTo("index", rand).orderBy("index").limit(5).get()
                        .addOnSuccessListener { querySnapshot ->
                            for (snapshot in querySnapshot) {
                                val question = snapshot.toObject(Question::class.java)
                                questions.add(question)
                            }
                            setNextQuestion()
                        }
                } else {
                    for (snapshot in queryDocumentSnapshots) {
                        val question = snapshot.toObject(Question::class.java)
                        questions.add(question)
                    }
                    setNextQuestion()
                }
            }
        resetTimer()
    }

    private fun resetTimer() {
        timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                index++
                if (index < questions.size) {
                    setNextQuestion()
                } else {
                    timer.cancel()
                    val intent = Intent(this@QuizActivity, ResultActivity::class.java)
                    intent.putExtra("correct", correctAnswers)
                    intent.putExtra("total", questions.size)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun showAnswer() {
        when (question.answer) {
            binding.option1.text -> binding.option1.setBackgroundResource(R.drawable.option_right)
            binding.option2.text -> binding.option2.setBackgroundResource(R.drawable.option_right)
            binding.option3.text -> binding.option3.setBackgroundResource(R.drawable.option_right)
            binding.option4.text -> binding.option4.setBackgroundResource(R.drawable.option_right)
        }
    }

    private fun setNextQuestion() {
        if (::timer.isInitialized) timer.cancel()
        timer.start()
        if (index < questions.size) {
            binding.questionCounter.text = String.format("%d/%d", index + 1, questions.size)
            question = questions[index]
            binding.question.text = question.question
            binding.option1.text = question.option1
            binding.option2.text = question.option2
            binding.option3.text = question.option3
            binding.option4.text = question.option4
        } else {
            timer.cancel()
            val intent = Intent(this@QuizActivity, ResultActivity::class.java)
            intent.putExtra("correct", correctAnswers)
            intent.putExtra("total", questions.size)
            startActivity(intent)
            finish()
        }
    }

    private fun checkAnswer(textView: TextView) {
        val selectedAnswer = textView.text.toString()
        if (selectedAnswer == question.answer) {
            correctAnswers++
            textView.setBackgroundResource(R.drawable.option_right)
        } else {
            showAnswer()
            textView.setBackgroundResource(R.drawable.option_wrong)
        }
    }

    private fun reset() {
        binding.option1.setBackgroundResource(R.drawable.option_unselected)
        binding.option2.setBackgroundResource(R.drawable.option_unselected)
        binding.option3.setBackgroundResource(R.drawable.option_unselected)
        binding.option4.setBackgroundResource(R.drawable.option_unselected)
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.option_1, R.id.option_2, R.id.option_3, R.id.option_4 -> {
                val selected = view as TextView
                checkAnswer(selected)
            }

            R.id.nextBtn -> {
                reset()
                if (index < questions.size - 1) {
                    index++
                    setNextQuestion()
                } else {
                    timer.cancel()
                    val intent = Intent(this@QuizActivity, ResultActivity::class.java)
                    intent.putExtra("correct", correctAnswers)
                    intent.putExtra("total", questions.size)
                    startActivity(intent)
                    finish()
                }
            }

            R.id.quizBtn -> {
                finish()
            }
        }
    }
}