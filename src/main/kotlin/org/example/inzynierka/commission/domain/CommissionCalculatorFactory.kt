package org.example.inzynierka.commission.domain

import org.example.inzynierka.shared.domain.FiatCurrency

open class CommissionCalculatorFactory {

    fun create(rules: List<CommissionRule>, amount: FiatCurrency): CommissionCalculator {
        val applied = rules.map { it.apply(amount) }
        return CommissionCalculator(applied)
    }
}
