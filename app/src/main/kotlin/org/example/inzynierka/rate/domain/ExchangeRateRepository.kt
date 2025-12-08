package org.example.inzynierka.rate.domain

import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.example.inzynierka.shared.domain.ExchangeRate
import java.time.LocalDate

interface ExchangeRateRepository {

    fun storeRate(rate: ExchangeRate)

    fun findRateForDate(
        from: FiatCurrencyCode,
        to: FiatCurrencyCode,
        date: LocalDate
    ): ExchangeRate?

    fun findLatestRate(
        from: FiatCurrencyCode,
        to: FiatCurrencyCode
    ): ExchangeRate?
}
