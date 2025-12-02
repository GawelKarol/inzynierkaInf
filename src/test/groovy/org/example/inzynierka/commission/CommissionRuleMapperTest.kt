package org.example.inzynierka.commission

import org.example.inzynierka.commission.domain.CommissionKind
import org.example.inzynierka.commission.domain.FixedCommissionRule
import org.example.inzynierka.commission.domain.RateCommissionRule
import org.example.inzynierka.commission.infrastructure.CommissionRuleEntity
import org.example.inzynierka.commission.infrastructure.CommissionRuleMapper
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class CommissionRuleMapperTest {

    private val mapper = CommissionRuleMapper()

    @Test
    fun `maps rate commission entity to domain`() {
        val entity = CommissionRuleEntity(
            id = 1,
            partnerId = "Gawel",
            kind = CommissionKind.SERVICE_PROVIDER.name,
            addedOnTop = false,
            rate = BigDecimal("0.03"),
            fixedValue = null,
            currency = null
        )

        val domain = mapper.toDomain(entity)

        assertTrue(domain is RateCommissionRule)
        assertEquals(BigDecimal("0.03"), (domain as RateCommissionRule).rate)
    }

    @Test
    fun `maps fixed commission entity to domain`() {
        val entity = CommissionRuleEntity(
            id = 1,
            partnerId = "Gawel",
            kind = CommissionKind.BASIC_FIXED.name,
            addedOnTop = false,
            rate = null,
            fixedValue = BigDecimal("5.00"),
            currency = "PLN"
        )

        val domain = mapper.toDomain(entity)

        assertTrue(domain is FixedCommissionRule)
        assertEquals(BigDecimal("5.00"), (domain as FixedCommissionRule).fixed.amount)
        assertEquals(FiatCurrencyCode.PLN, domain.fixed.code)
    }
}
