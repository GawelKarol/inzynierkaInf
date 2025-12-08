package org.example.inzynierka.payment.domain

data class PaymentInitResultDto(
    val externalPaymentId: String,
    val redirectUrl: String
)
