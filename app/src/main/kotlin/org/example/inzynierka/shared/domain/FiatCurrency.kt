package org.example.inzynierka.shared.domain

import java.math.BigDecimal

data class FiatCurrency(
    val amount: BigDecimal,
    val code: FiatCurrencyCode
) {
    operator fun plus(other: FiatCurrency): FiatCurrency {
        require(other.code == code) { "Currency mismatch" }
        return FiatCurrency(amount.add(other.amount).setScale(2), code)
    }

    operator fun minus(other: FiatCurrency): FiatCurrency {
        require(other.code == code) { "Currency mismatch" }
        return FiatCurrency(amount.subtract(other.amount).setScale(2), code)
    }

    operator fun times(rate: BigDecimal): FiatCurrency =
        FiatCurrency(amount.multiply(rate).setScale(2), code)

    companion object {
        fun zero(code: FiatCurrencyCode) = FiatCurrency(BigDecimal.ZERO.setScale(2), code)
        fun of(value: BigDecimal, code: FiatCurrencyCode) = FiatCurrency(value, code)
    }
}
