package org.example.inzynierka.commission

import org.example.inzynierka.commission.domain.AppliedFixedCommission
import org.example.inzynierka.commission.domain.AppliedRateCommission
import org.example.inzynierka.commission.domain.CommissionCalculator
import org.example.inzynierka.commission.domain.CommissionKind
import org.example.inzynierka.shared.domain.FiatCurrency
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CommissionCalculatorTest {

    private val amount = FiatCurrency(BigDecimal(100), FiatCurrencyCode.PLN)

    @Test
    fun `should calculate deducted commissions correctly`() {
        val applied = listOf(
            AppliedRateCommission(CommissionKind.SERVICE_PROVIDER, addedOnTop = false, rate = BigDecimal("0.02")),
            AppliedFixedCommission(
                CommissionKind.BASIC_FIXED,
                addedOnTop = false,
                fixed = FiatCurrency(BigDecimal(5), FiatCurrencyCode.PLN)
            )
        )

        val calculator = CommissionCalculator(applied)

        val result = calculator.deducted(amount)

        assertEquals(BigDecimal("7.00"), result.amount)   // 2% = 2 + fixed 5 = 7
    }

    @Test
    fun `should calculate on-top commissions correctly`() {
        val applied = listOf(
            AppliedRateCommission(CommissionKind.PARTNER, addedOnTop = true, rate = BigDecimal("0.03"))
        )

        val calculator = CommissionCalculator(applied)

        val result = calculator.onTop(amount)

        assertEquals(BigDecimal("3.00"), result.amount)   // 3% = 3
    }

    @Test
    fun `should calculate net amount correctly`() {
        val applied = listOf(
            AppliedRateCommission(CommissionKind.SERVICE_PROVIDER, addedOnTop = false, rate = BigDecimal("0.02"))
        )

        val calculator = CommissionCalculator(applied)
        val result = calculator.net(amount)

        assertEquals(BigDecimal("98.00"), result.amount)
    }

    @Test
    fun `should calculate gross amount correctly`() {
        val applied = listOf(
            AppliedRateCommission(CommissionKind.PARTNER, addedOnTop = true, rate = BigDecimal("0.01"))
        )

        val calculator = CommissionCalculator(applied)
        val result = calculator.gross(amount)

        assertEquals(BigDecimal("101.00"), result.amount)
    }
}
