package org.example.inzynierka.rate.infrastructure

import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.example.inzynierka.shared.domain.ExchangeRate
import org.example.inzynierka.rate.domain.ExchangeRateRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
open class ExchangeRateRepositoryImpl(
    private val jpa: ExchangeRateJpaRepository
) : ExchangeRateRepository {

    override fun storeRate(rate: ExchangeRate) {
        jpa.save(
            ExchangeRateEntity(
                currency = rate.from.code,
                rate = rate.rate.toDouble(),
                effectiveAt = rate.date.toLocalDate(),
                fetchedAt = LocalDateTime.now()
            )
        )
    }

    override fun findRateForDate(from: FiatCurrencyCode, to: FiatCurrencyCode, date: LocalDate): ExchangeRate? {
        val entity = jpa.findForDate(from.code, date) ?: return null
        return ExchangeRate(
            date = entity.effectiveAt.atStartOfDay(),
            rate = entity.rate.toBigDecimal(),
            from = from,
            to = to
        )
    }

    override fun findLatestRate(from: FiatCurrencyCode, to: FiatCurrencyCode): ExchangeRate? {
        val entity = jpa.findLatest(to.code) ?: return null
        return ExchangeRate(
            date = entity.effectiveAt.atStartOfDay(),
            rate = entity.rate.toBigDecimal(),
            from = from,
            to = to
        )
    }
}
