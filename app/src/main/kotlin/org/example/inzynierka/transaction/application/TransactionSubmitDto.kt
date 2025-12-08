package org.example.inzynierka.transaction.application

data class TransactionSubmitDto(
    val transactionId: Long,
    val paymentMethod: String
)
