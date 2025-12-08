package org.example.inzynierka.rate.application

import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.example.inzynierka.shared.domain.ExchangeRate
import org.example.inzynierka.rate.domain.ExchangeRateRepository
import org.example.inzynierka.rate.infrastructure.RestNBPClientRateProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime

@Service
open class RateRefreshService(
    private val restNBPClientRateProvider: RestNBPClientRateProvider,
    private val exchangeRateRepository: ExchangeRateRepository
) {

    @Transactional
    open fun refreshNbpRates() {
        FiatCurrencyCode.supportedCurrencies
            .filter { it != FiatCurrencyCode.PLN }
            .forEach { from ->

                if (existsRateForYesterday(from)) {
                    return@forEach
                }

                val latest = getLatestNbpRate(from)
                    ?: return@forEach

                exchangeRateRepository.storeRate(latest)
            }
    }

    private fun existsRateForYesterday(from: FiatCurrencyCode): Boolean {
        val yesterday = LocalDate.now().minusDays(1)
        return exchangeRateRepository.findRateForDate(from, FiatCurrencyCode.PLN, yesterday) != null
    }

    open fun getLatestNbpRate(from: FiatCurrencyCode): ExchangeRate? {
        val rates = restNBPClientRateProvider.getCurrentRates(from, FiatCurrencyCode.PLN)
        val yesterday = LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay()

        return rates
            .sortedByDescending { it.date }
            .firstOrNull { it.date.isBefore(yesterday) || it.date.isEqual(yesterday) }
    }
    open fun getLatestRate(from: FiatCurrencyCode, to: FiatCurrencyCode): ExchangeRate? =
        exchangeRateRepository.findLatestRate(from, to)

}
