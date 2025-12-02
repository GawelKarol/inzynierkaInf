package org.example.inzynierka.commission

import org.example.inzynierka.commission.application.CommissionApplicationService
import org.example.inzynierka.commission.domain.*
import org.example.inzynierka.commission.infrastructure.CommissionRuleEntity
import org.example.inzynierka.commission.infrastructure.CommissionRuleMapper
import org.example.inzynierka.commission.infrastructure.CommissionRuleRepository
import org.example.inzynierka.shared.domain.FiatCurrency
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
class CommissionApplicationServiceTest {

    private val repository = mock<CommissionRuleRepository>()
    private val mapper = mock<CommissionRuleMapper>()
    private val factory = mock<CommissionCalculatorFactory>()

    private val service = CommissionApplicationService(repository, factory, mapper)

    @Test
    fun `should calculate commission result`() {

        val amount = FiatCurrency(100.00.toBigDecimal(), FiatCurrencyCode.PLN)

        val entity = CommissionRuleEntity(
            id = 1,
            partnerId = "Gawel",
            kind = CommissionKind.SERVICE_PROVIDER.name,
            addedOnTop = false,
            rate = 0.02.toBigDecimal(),
            fixedValue = null,
            currency = null
        )

        val domainRule = RateCommissionRule(
            kind = CommissionKind.SERVICE_PROVIDER,
            addedOnTop = false,
            rate = 0.02.toBigDecimal()
        )

        val calculator = mock<CommissionCalculator>()

        whenever(repository.findByPartnerId("Gawel")).thenReturn(listOf(entity))
        whenever(mapper.toDomainList(listOf(entity))).thenReturn(listOf(domainRule))
        whenever(factory.create(listOf(domainRule), amount)).thenReturn(calculator)

        whenever(calculator.deducted(amount)).thenReturn(FiatCurrency(2.00.toBigDecimal(), FiatCurrencyCode.PLN))
        whenever(calculator.onTop(amount)).thenReturn(FiatCurrency(BigDecimal.ZERO, FiatCurrencyCode.PLN))
        whenever(calculator.total(amount)).thenReturn(FiatCurrency(2.00.toBigDecimal(), FiatCurrencyCode.PLN))
        whenever(calculator.net(amount)).thenReturn(FiatCurrency(98.00.toBigDecimal(), FiatCurrencyCode.PLN))
        whenever(calculator.gross(amount)).thenReturn(amount)

        val result = service.calculateForPartner("Gawel", amount)

        assertEquals(2.00.toBigDecimal(), result.deducted.amount)
        assertEquals(BigDecimal.ZERO, result.onTop.amount)
        assertEquals(2.00.toBigDecimal(), result.total.amount)
        assertEquals(98.00.toBigDecimal(), result.net.amount)
        assertEquals(100.00.toBigDecimal(), result.gross.amount)
    }
}
