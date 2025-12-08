package org.example.inzynierka.commission.domain

import org.example.inzynierka.shared.domain.FiatCurrency
import java.math.BigDecimal

sealed interface CommissionRule {
    val kind: CommissionKind
    val addedOnTop: Boolean

    fun apply(amount: FiatCurrency): AppliedCommission
}

data class RateCommissionRule(
    override val kind: CommissionKind,
    override val addedOnTop: Boolean,
    val rate: BigDecimal
) : CommissionRule {
    override fun apply(amount: FiatCurrency) = AppliedRateCommission(kind, addedOnTop, rate)
}

data class FixedCommissionRule(
    override val kind: CommissionKind,
    override val addedOnTop: Boolean,
    val fixed: FiatCurrency
) : CommissionRule {
    override fun apply(amount: FiatCurrency) = AppliedFixedCommission(kind, addedOnTop, fixed)
}
