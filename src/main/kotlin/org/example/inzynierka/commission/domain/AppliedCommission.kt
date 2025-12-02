package org.example.inzynierka.commission.domain

import org.example.inzynierka.shared.domain.FiatCurrency
import java.math.BigDecimal

sealed class AppliedCommission {
    abstract val kind: CommissionKind
    abstract val addedOnTop: Boolean
    abstract fun calculate(amount: FiatCurrency): FiatCurrency
}

data class AppliedRateCommission(
    override val kind: CommissionKind,
    override val addedOnTop: Boolean,
    val rate: BigDecimal
) : AppliedCommission() {

    override fun calculate(amount: FiatCurrency): FiatCurrency =
        amount * rate
}

data class AppliedFixedCommission(
    override val kind: CommissionKind,
    override val addedOnTop: Boolean,
    val fixed: FiatCurrency
) : AppliedCommission() {

    override fun calculate(amount: FiatCurrency): FiatCurrency =
        fixed
}
