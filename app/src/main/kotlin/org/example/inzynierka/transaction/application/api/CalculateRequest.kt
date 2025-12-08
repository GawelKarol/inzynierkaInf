package org.example.inzynierka.transaction.application.api

import java.math.BigDecimal

data class CalculateRequest(
    val partnerId: String,
    val widgetSessionId: String,
    val sourceCurrency: String,
    val targetCurrency: String,
    val targetAmount: BigDecimal,   // kwota „za”
)

data class CalculateResponse(
    val sourceAmount: BigDecimal,   // ile „kupujesz”
    val targetAmount: BigDecimal,
    val rate: BigDecimal,
    val commission: BigDecimal,
)
