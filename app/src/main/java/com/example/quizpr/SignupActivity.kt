package com.example.quizpr

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quizpr.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseFirestore
    private lateinit var dialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseFirestore.getInstance()
        dialog = ProgressDialog(this)
        dialog.setMessage("We're creating new account...")
        dialog.setCancelable(false)
        binding.createNewBtn.setOnClickListener {
            val email = binding.emailBox.text.toString().trim()
            val pass = binding.passwordBox.text.toString()
            val name = binding.nameBox.text.toString()
            val referCode = binding.referBox.text.toString()
            if (name.isEmpty()) {
                binding.nameBox.error = "Enter name..."
            } else if (email.isEmpty()) {
                binding.emailBox.error = "Enter email..."
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailBox.error = "Enter a valid email..."
            } else if (pass.isEmpty()) {
                binding.passwordBox.error = "Enter password..."
            } else {
                val user = User(
                    name,
                    email,
                    "https://firebasestorage.googleapis.com/v0/b/quiz-5c7fb.appspot.com/o/avatar.png?alt=media&token=c4c1d3ed-541a-4d39-8e8a-9f005fae9ce2",
                    referCode
                )
                dialog.show()
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = task.result?.user?.uid
                        uid?.let {
                            database.collection("users").document(it).set(user)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        dialog.dismiss()
                                        startActivity(
                                            Intent(
                                                this@SignupActivity, MainActivity::class.java
                                            )
                                        )
                                        finish()
                                    } else {
                                        dialog.dismiss()
                                        auth.currentUser?.delete()
                                        Toast.makeText(
                                            this@SignupActivity,
                                            "Failed try again...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }.addOnFailureListener {
                                    dialog.dismiss()
                                    auth.currentUser?.delete()
                                    Toast.makeText(
                                        this@SignupActivity,
                                        "Failed try again...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        dialog.dismiss()
                        Toast.makeText(
                            this@SignupActivity, "Failed try again...", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
        binding.loginBtn.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
            finish()
        }
        binding.policy.setOnClickListener {
            Toast.makeText(
                this@SignupActivity, "Opening browser...", Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://vimalkmgithub.github.io/QuizzerAppPrivacyPolicy/")
            )
            startActivity(intent)
        }
    }
}