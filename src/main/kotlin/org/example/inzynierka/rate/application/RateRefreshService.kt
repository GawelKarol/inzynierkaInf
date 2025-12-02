package org.example.inzynierka.rate.application

import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.example.inzynierka.shared.domain.ExchangeRate
import org.example.inzynierka.rate.domain.ExchangeRateRepository
import org.example.inzynierka.rate.infrastructure.RestNBPClientRateProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
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

                val latest = getLatestNbpRate(from, FiatCurrencyCode.PLN)
                    ?: return@forEach

                exchangeRateRepository.storeRate(latest)
            }
    }

    private fun existsRateForYesterday(from: FiatCurrencyCode): Boolean {
        val yesterday = LocalDate.now().minusDays(1)
        return exchangeRateRepository.findRateForDate(from, FiatCurrencyCode.PLN, yesterday) != null
    }

    private fun getLatestNbpRate(from: FiatCurrencyCode, to: FiatCurrencyCode): ExchangeRate? {
        val rates = restNBPClientRateProvider.getCurrentRates(from, to)
        val yesterday = LocalDateTime.now().minusDays(1).toLocalDate().atStartOfDay()

        return rates
            .sortedByDescending { it.date }
            .firstOrNull { it.date.isBefore(yesterday) || it.date.isEqual(yesterday) }
    }
}
