package org.example.inzynierka.commission.domain


import org.example.inzynierka.shared.domain.FiatCurrency

class CommissionCalculator(
    val applied: List<AppliedCommission>
) {

    fun deducted(amount: FiatCurrency): FiatCurrency =
        applied
            .filter { !it.addedOnTop }
            .map { it.calculate(amount) }
            .reduceOrNull { a, b -> a + b }
            ?: FiatCurrency.zero(amount.code)

    fun onTop(amount: FiatCurrency): FiatCurrency =
        applied.filter { it.addedOnTop }
            .map { it.calculate(amount) }
            .reduceOrNull { a, b -> (a + b) }
            ?: FiatCurrency.zero(amount.code)

    fun total(amount: FiatCurrency) =
        deducted(amount) + onTop(amount)

    fun net(amount: FiatCurrency) =
        amount - deducted(amount)

    fun gross(amount: FiatCurrency): FiatCurrency {
        println(onTop(amount))
        println(amount)
        return amount + onTop(amount)
    }
}

