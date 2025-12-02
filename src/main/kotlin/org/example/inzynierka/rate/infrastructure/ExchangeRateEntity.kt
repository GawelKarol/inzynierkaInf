package org.example.inzynierka.rate.infrastructure

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "fiat_rate_history")
class ExchangeRateEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val currency: String = "",

    @Column(nullable = false)
    val rate: Double = 0.0,

    @Column(name = "effective_at", nullable = false)
    val effectiveAt: LocalDate = LocalDate.now(),

    @Column(name = "fetched_at", nullable = false)
    val fetchedAt: LocalDateTime = LocalDateTime.now()
)
