package org.example.inzynierka.commission

import org.example.inzynierka.commission.domain.CommissionCalculatorFactory
import org.example.inzynierka.commission.domain.FixedCommissionRule
import org.example.inzynierka.commission.domain.RateCommissionRule
import org.example.inzynierka.shared.domain.FiatCurrency
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import spock.lang.Specification

import static org.example.inzynierka.commission.domain.CommissionKind.*

class CommissionCalculatorFactorySpec extends Specification {

    def factory = new CommissionCalculatorFactory()
    def amount = new FiatCurrency(new BigDecimal("100.00"), FiatCurrencyCode.PLN)

    def "should create calculator from rules"() {
        given: "zestaw reguł prowizji"
        def rules = [
                new RateCommissionRule(
                        SERVICE_PROVIDER,
                        false,
                        new BigDecimal("0.02")
                ),
                new FixedCommissionRule(
                        BASIC_FIXED,
                        false,
                        new FiatCurrency(new BigDecimal("5.00"), FiatCurrencyCode.PLN)
                )
        ]

        when: "tworzymy kalkulator"
        def calculator = factory.create(rules, amount)
        def deducted = calculator.deducted(amount)

        then: "łączna prowizja potrącana wynosi 7.00 (2% z 100 + 5)"
        deducted.amount == new BigDecimal("7.00")
    }
}
