package org.example.inzynierka.commission.application

import org.example.inzynierka.shared.domain.FiatCurrency

data class CommissionResultDto(
    val deducted: FiatCurrency,
    val onTop: FiatCurrency,
    val total: FiatCurrency,
    val net: FiatCurrency,
    val gross: FiatCurrency
)
