package org.example.inzynierka.rate.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface ExchangeRateJpaRepository : JpaRepository<ExchangeRateEntity, Long> {

    @Query("""
        SELECT e FROM ExchangeRateEntity e
        WHERE e.currency = :currency
        ORDER BY e.effectiveAt DESC, e.fetchedAt DESC
        LIMIT 1
    """)
    fun findLatest(currency: String): ExchangeRateEntity?

    @Query("""
        SELECT e FROM ExchangeRateEntity e
        WHERE e.currency = :currency AND e.effectiveAt = :date
    """)
    fun findForDate(currency: String, date: LocalDate): ExchangeRateEntity?
}
