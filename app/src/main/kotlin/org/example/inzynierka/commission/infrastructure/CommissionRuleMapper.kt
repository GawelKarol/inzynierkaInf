package org.example.inzynierka.commission.infrastructure

import org.example.inzynierka.commission.domain.CommissionKind
import org.example.inzynierka.commission.domain.CommissionRule
import org.example.inzynierka.commission.domain.FixedCommissionRule
import org.example.inzynierka.commission.domain.RateCommissionRule
import org.example.inzynierka.shared.domain.FiatCurrency
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.springframework.stereotype.Component

@Component
open class CommissionRuleMapper {


    fun toDomainList(entities: List<CommissionRuleEntity>): List<CommissionRule> =
        entities.map { toDomain(it) }

    fun toDomain(entity: CommissionRuleEntity): CommissionRule =
        if (entity.rate != null) {
            RateCommissionRule(
                kind = CommissionKind.valueOf(entity.kind),
                addedOnTop = entity.addedOnTop,
                rate = entity.rate!!
            )
        } else {
            FixedCommissionRule(
                kind = CommissionKind.valueOf(entity.kind),
                addedOnTop = entity.addedOnTop,
                fixed = FiatCurrency(
                    entity.fixedValue!!,
                    FiatCurrencyCode.valueOf(entity.currency!!)
                )
            )
        }
}
