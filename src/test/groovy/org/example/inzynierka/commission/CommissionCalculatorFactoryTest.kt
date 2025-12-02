package org.example.inzynierka.commission

import org.example.inzynierka.commission.domain.CommissionCalculatorFactory
import org.example.inzynierka.commission.domain.CommissionKind
import org.example.inzynierka.commission.domain.FixedCommissionRule
import org.example.inzynierka.commission.domain.RateCommissionRule
import org.example.inzynierka.shared.domain.FiatCurrency
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CommissionCalculatorFactoryTest {

    private val factory = CommissionCalculatorFactory()
    private val amount = FiatCurrency(BigDecimal(100), FiatCurrencyCode.PLN)

    @Test
    fun `should create calculator from rules`() {

        val rules = listOf(
            RateCommissionRule(CommissionKind.SERVICE_PROVIDER, addedOnTop = false, rate = BigDecimal("0.02")),
            FixedCommissionRule(
                CommissionKind.BASIC_FIXED,
                addedOnTop = false,
                fixed = FiatCurrency(BigDecimal(5), FiatCurrencyCode.PLN)
            )
        )

        val calculator = factory.create(rules, amount)

        val deducted = calculator.deducted(amount)

        assertEquals(BigDecimal("7.00"), deducted.amount)
    }
}
