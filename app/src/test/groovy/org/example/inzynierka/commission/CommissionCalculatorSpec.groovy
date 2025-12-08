package org.example.inzynierka.commission

import org.example.inzynierka.commission.domain.AppliedFixedCommission
import org.example.inzynierka.commission.domain.AppliedRateCommission
import org.example.inzynierka.commission.domain.CommissionCalculator
import org.example.inzynierka.commission.domain.CommissionKind;
import org.example.inzynierka.shared.domain.FiatCurrency
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import spock.lang.Specification

import static org.example.inzynierka.commission.domain.CommissionKind.*

class CommissionCalculatorSpec extends Specification {

    def amount = new FiatCurrency(new BigDecimal("100.00"), FiatCurrencyCode.PLN)

    def "should calculate deducted commissions correctly"() {
        given: "zastosowane prowizje potrącane z kwoty"
            def applied = [
                new AppliedRateCommission(
                        SERVICE_PROVIDER,
                    false,
                    new BigDecimal("0.02")
                ),
                new AppliedFixedCommission(
                        BASIC_FIXED,
                    false,
                    new FiatCurrency(new BigDecimal("5.00"), FiatCurrencyCode.PLN)
                )
            ]

            def calculator = new CommissionCalculator(applied)

        when:
            def result = calculator.deducted(amount)

        then: "2% z 100 = 2 + 5 = 7"
            result.amount == new BigDecimal("7.00")
    }

    def "should calculate on-top commissions correctly"() {
        given: "zastosowane prowizje doliczane na wierzch"
            def applied = [
                new AppliedRateCommission(
                        PARTNER,
                    true,
                    new BigDecimal("0.03")
                )
            ]

            def calculator = new CommissionCalculator(applied)

        when:
            def result = calculator.onTop(amount)

        then: "3% z 100 = 3"
            result.amount == new BigDecimal("3.00")
    }

    def "should calculate net amount correctly"() {
        given: "zastosowana tylko prowizja potrącana"
            def applied = [
                new AppliedRateCommission(
                        SERVICE_PROVIDER,
                    false,
                    new BigDecimal("0.02")
                )
            ]

            def calculator = new CommissionCalculator(applied)

        when:
            def result = calculator.net(amount)

        then: "netto = 100 - 2 = 98"
            result.amount == new BigDecimal("98.00")
    }

    def "should calculate gross amount correctly"() {
        given: "zastosowana tylko prowizja doliczana na wierzch"
            def applied = [
                new AppliedRateCommission(
                        PARTNER,
                    true,
                    new BigDecimal("0.01")
                )
            ]

            def calculator = new CommissionCalculator(applied)

        when:
            def result = calculator.gross(amount)

        then: "brutto = 100 + 1 = 101"
            result.amount == new BigDecimal("101.00")
    }
}
