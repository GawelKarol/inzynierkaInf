package org.example.inzynierka.commission.domain

import org.example.inzynierka.shared.domain.FiatCurrency
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import java.math.BigDecimal
import java.math.BigDecimal.ZERO

abstract class AppliedCommissionsCalculator(
    open val appliedCommissions: List<AppliedCommission>
) {

    val totalCommissionRate: BigDecimal
        get() = appliedCommissions
            .filterIsInstance<AppliedRateCommission>()
            .map { it.rate }
            .fold(ZERO, BigDecimal::add)

    val totalFixedCommission: FiatCurrency
        get() = appliedCommissions
            .filterIsInstance<AppliedFixedCommission>()
            .map { it.fixed }
            .reduceOrNull(FiatCurrency::plus)
            ?: FiatCurrency.zero(FiatCurrencyCode.PLN)

    fun deductedCommission(amount: FiatCurrency): FiatCurrency =
        appliedCommissions
            .filter { !it.addedOnTop }
            .map { it.calculate(amount) }
            .reduceOrNull(FiatCurrency::plus)
            ?: FiatCurrency.zero(amount.code)

    fun onTopCommission(amount: FiatCurrency): FiatCurrency =
        appliedCommissions
            .filter { it.addedOnTop }
            .map { it.calculate(amount) }
            .reduceOrNull(FiatCurrency::plus)
            ?: FiatCurrency.zero(amount.code)

    fun totalCommission(amount: FiatCurrency): FiatCurrency =
        deductedCommission(amount) + onTopCommission(amount)

    fun net(amount: FiatCurrency): FiatCurrency =
        amount - deductedCommission(amount)

    fun gross(amount: FiatCurrency): FiatCurrency =
        amount + onTopCommission(amount)

    protected fun fixed(kind: CommissionKind, currency: String): FiatCurrency =
        appliedCommissions
            .filterIsInstance<AppliedFixedCommission>()
            .find { it.kind == kind }
            ?.fixed
            ?: FiatCurrency.zero(FiatCurrencyCode.valueOf(currency))

    protected fun rate(kind: CommissionKind): BigDecimal =
        appliedCommissions
            .filterIsInstance<AppliedRateCommission>()
            .find { it.kind == kind }
            ?.rate
            ?: ZERO
}
