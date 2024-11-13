package com.example.quizpr

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class WithdrawRequest(
    var emailAddress: String = "", var requestedBy: String = ""
) {
    @ServerTimestamp
    var createdAt: Date? = null
}
