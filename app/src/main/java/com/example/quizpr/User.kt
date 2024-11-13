package com.example.quizpr

data class User(
    var name: String = "",
    var email: String = "",
    var profile: String = "",
    var referCode: String = "",
    var coins: Long = 25
)