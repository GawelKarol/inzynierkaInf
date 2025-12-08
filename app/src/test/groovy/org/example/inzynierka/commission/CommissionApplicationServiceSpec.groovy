package org.example.inzynierka.commission.application

import org.example.inzynierka.commission.infrastructure.CommissionRuleEntity
import org.example.inzynierka.commission.infrastructure.CommissionRuleMapper
import org.example.inzynierka.commission.infrastructure.CommissionRuleRepository
import org.example.inzynierka.commission.domain.CommissionCalculatorFactory
import org.example.inzynierka.shared.domain.FiatCurrency
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import spock.lang.Specification

class CommissionApplicationServiceSpec extends Specification {

    CommissionRuleRepository repository = Stub()
    CommissionRuleMapper mapper = new CommissionRuleMapper()
    CommissionCalculatorFactory factory = new CommissionCalculatorFactory()

    CommissionApplicationService service =
            new CommissionApplicationService(repository, factory, mapper)

    def "should calculate commission result for partner with mixed rules"() {
        given:
        def partnerId = "partner-1"
        def amount = new FiatCurrency(new BigDecimal("100.00"), FiatCurrencyCode.PLN)

        def entities = [
                new CommissionRuleEntity(
                        1L,
                        partnerId,
                        "SERVICE_PROVIDER",
                        false,                       // addedOnTop
                        new BigDecimal("0.02"),      // rate
                        null,                        // fixedValue
                        null                         // currency
                ),
                new CommissionRuleEntity(
                        2L,
                        partnerId,
                        "BASIC_FIXED",
                        true,                        // addedOnTop
                        null,                        // rate
                        new BigDecimal("5.00"),      // fixedValue
                        "PLN"                        // currency
                )
        ]

        repository.findByPartnerId(partnerId) >> entities

        when:
        def result = service.calculateForPartner(partnerId, amount)

        then: "commission values are calculated correctly"
        result.deducted.amount == new BigDecimal("2.00")
        result.deducted.code == FiatCurrencyCode.PLN

        result.onTop.amount == new BigDecimal("5.00")
        result.onTop.code == FiatCurrencyCode.PLN

        result.total.amount == new BigDecimal("7.00")
        result.net.amount == new BigDecimal("98.00")
        result.gross.amount == new BigDecimal("105.00")
    }

    def "should return zero commissions when partner has no rules"() {
        given:
        def partnerId = "partner-2"
        def amount = new FiatCurrency(new BigDecimal("100.00"), FiatCurrencyCode.PLN)

        repository.findByPartnerId(partnerId) >> []

        when:
        def result = service.calculateForPartner(partnerId, amount)

        then: "everything is zero except net/gross=amount"
        result.deducted.amount == BigDecimal.ZERO
        result.onTop.amount == BigDecimal.ZERO
        result.total.amount == BigDecimal.ZERO

        result.net.amount == new BigDecimal("100.00")
        result.gross.amount == new BigDecimal("100.00")

        and: "waluta jest zgodna z wejściem"
        result.deducted.code == FiatCurrencyCode.PLN
        result.onTop.code == FiatCurrencyCode.PLN
        result.total.code == FiatCurrencyCode.PLN
        result.net.code == FiatCurrencyCode.PLN
        result.gross.code == FiatCurrencyCode.PLN
    }

    def "should sum multiple deducted and on-top commissions correctly"() {
        given:
        def partnerId = "partner-3"
        def amount = new FiatCurrency(new BigDecimal("200.00"), FiatCurrencyCode.PLN)

        def entities = [
                new CommissionRuleEntity(
                        1L,
                        partnerId,
                        "SERVICE_PROVIDER",
                        false,
                        new BigDecimal("0.01"),  // 1% deducted
                        null,
                        null
                ),
                new CommissionRuleEntity(
                        2L,
                        partnerId,
                        "BASIC_FIXED",
                        false,
                        null,
                        new BigDecimal("4.00"), // 4 PLN deducted
                        "PLN"
                ),
                new CommissionRuleEntity(
                        3L,
                        partnerId,
                        "PARTNER",
                        true,
                        new BigDecimal("0.03"),  // 3% on top
                        null,
                        null
                ),
                new CommissionRuleEntity(
                        4L,
                        partnerId,
                        "BASIC_FIXED",
                        true,
                        null,
                        new BigDecimal("6.00"), // 6 PLN on top
                        "PLN"
                )
        ]

        repository.findByPartnerId(partnerId) >> entities

        when:
        def result = service.calculateForPartner(partnerId, amount)

        then:
        result.deducted.amount == new BigDecimal("6.00")
        result.onTop.amount == new BigDecimal("12.00")
        result.total.amount == new BigDecimal("18.00")
        result.net.amount == new BigDecimal("194.00")
        result.gross.amount == new BigDecimal("212.00")
    }
}
