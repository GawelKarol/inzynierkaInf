package org.example.inzynierka.transaction.application

data class TransactionCreateDto(
    val email: String,
    val amount: String,
    val currency: String,
    val webhookUrl: String?
)
