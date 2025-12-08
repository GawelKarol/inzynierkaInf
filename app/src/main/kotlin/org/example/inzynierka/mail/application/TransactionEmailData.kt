package org.example.inzynierka.mail.application

data class TransactionEmailData(
    val email: String,
    val transactionId: String,
    val amount: String,
    val currency: String,
)
