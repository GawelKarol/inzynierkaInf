package org.example.inzynierka.commission;

import org.example.inzynierka.commission.domain.CommissionKind
import org.example.inzynierka.commission.domain.FixedCommissionRule
import org.example.inzynierka.commission.domain.RateCommissionRule
import org.example.inzynierka.commission.infrastructure.CommissionRuleEntity
import org.example.inzynierka.commission.infrastructure.CommissionRuleMapper
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import spock.lang.Specification

class CommissionRuleMapperSpec extends Specification {

    def mapper = new CommissionRuleMapper()

    def "should map entity with rate to RateCommissionRule"() {
        given: "entity z procentową prowizją (rate != null)"
        def entity = new CommissionRuleEntity(
                1,
                "Gawel",
                "SERVICE_PROVIDER",   // kind
                false,                // addedOnTop
                new BigDecimal("0.02"), // rate
                null,                 // fixedValue
                null                  // currency
        )

        when: "mapujemy entity na domenę"
        def result = mapper.toDomain(entity)

        then: "powstaje RateCommissionRule z poprawnymi danymi"
        result instanceof RateCommissionRule
        result.kind == CommissionKind.SERVICE_PROVIDER
        !result.addedOnTop
        result.rate == new BigDecimal("0.02")
    }

    def "should map entity with fixed value to FixedCommissionRule"() {
        given: "entity ze stałą prowizją (fixedValue != null, rate == null)"
        def entity = new CommissionRuleEntity(
                1,
                "Gawel",
                "BASIC_FIXED",            // kind
                true,                     // addedOnTop
                null,                     // rate
                new BigDecimal("5.00"),   // fixedValue
                "PLN"                     // currency
        )

        when: "mapujemy entity na domenę"
        def result = mapper.toDomain(entity)

        then: "powstaje FixedCommissionRule z poprawnymi danymi"
        result instanceof FixedCommissionRule
        result.kind == CommissionKind.BASIC_FIXED
        result.addedOnTop
        result.fixed.amount == new BigDecimal("5.00")
        result.fixed.code == FiatCurrencyCode.PLN
    }

    def "should map list of entities to list of domain rules"() {
        given: "lista dwóch encji – jedna procentowa, druga stała"
        def entities = [
                new CommissionRuleEntity(
                        1,
                        "",
                        "SERVICE_PROVIDER",
                        false,
                        new BigDecimal("0.01"),
                        null,
                        null
                ),
                new CommissionRuleEntity(
                        1,
                        "",
                        "BASIC_FIXED",
                        false,
                        null,
                        new BigDecimal("3.00"),
                        "EUR"
                )
        ]

        when: "mapujemy listę encji na listę reguł domenowych"
        def result = mapper.toDomainList(entities)

        then: "powstają dwie reguły w tej samej kolejności"
        result.size() == 2

        and: "pierwsza to RateCommissionRule"
        result[0] instanceof RateCommissionRule
        result[0].kind == CommissionKind.SERVICE_PROVIDER
        !result[0].addedOnTop
        result[0].rate == new BigDecimal("0.01")

        and: "druga to FixedCommissionRule"
        result[1] instanceof FixedCommissionRule
        result[1].kind == CommissionKind.BASIC_FIXED
        !result[1].addedOnTop
        result[1].fixed.amount == new BigDecimal("3.00")
        result[1].fixed.code == FiatCurrencyCode.EUR
    }
}
