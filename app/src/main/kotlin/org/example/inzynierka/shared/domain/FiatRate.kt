package org.example.inzynierka.shared.domain

import java.math.BigDecimal
import java.time.LocalDateTime

data class ExchangeRate(
    val date: LocalDateTime,
    val rate: BigDecimal,
    val from: FiatCurrencyCode,
    val to: FiatCurrencyCode
) {
    init {
        require(rate >= BigDecimal.ZERO) { "Rate cannot be negative" }
    }

    operator fun times(multiplier: BigDecimal): ExchangeRate =
        ExchangeRate(date, rate * multiplier, from, to)
}
