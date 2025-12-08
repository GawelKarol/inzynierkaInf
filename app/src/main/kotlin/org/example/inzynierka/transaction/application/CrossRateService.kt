package org.example.inzynierka.transaction.application

import org.example.inzynierka.rate.application.RateRefreshService
import org.example.inzynierka.rate.infrastructure.RateScheduler
import org.example.inzynierka.shared.domain.ExchangeRate
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

@Service
class CrossRateService(
    private val rateRefreshService: RateRefreshService,
) {

    fun getRate(from: FiatCurrencyCode, to: FiatCurrencyCode): ExchangeRate? {
        if (from == to) {
            return ExchangeRate(
                date = LocalDateTime.now(),
                rate = BigDecimal.ONE,
                from = from,
                to = to,
            )
        }

        if (to == FiatCurrencyCode.PLN) {
            return rateRefreshService.getLatestRate(from, to)
        }

        if (from == FiatCurrencyCode.PLN) {
            val toPln = rateRefreshService.getLatestRate(from, to) ?: return null
            val value = BigDecimal.ONE.divide(toPln.rate, 8, RoundingMode.HALF_UP)
            return ExchangeRate(
                date = toPln.date,
                rate = value,
                from = FiatCurrencyCode.PLN,
                to = to,
            )
        }

        val toToPln = rateRefreshService.getLatestRate(FiatCurrencyCode.PLN, from) ?: return null
        val fromToPln = rateRefreshService.getLatestRate(FiatCurrencyCode.PLN, to ) ?: return null

        val cross = fromToPln.rate.divide(toToPln.rate, 8, RoundingMode.HALF_UP)

        return ExchangeRate(
            date = minOf(fromToPln.date, toToPln.date),
            rate = cross,
            from = from,
            to = to,
        )
    }
}
