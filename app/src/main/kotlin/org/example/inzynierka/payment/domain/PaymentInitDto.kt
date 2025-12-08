package org.example.inzynierka.payment.domain

import java.math.BigDecimal

data class PaymentInitDto(
    val transactionId: Long,
    val amount: BigDecimal,
    val currency: String,
    val paymentMethod: String,
    val returnUrl: String,
    val notifyUrl: String
)
