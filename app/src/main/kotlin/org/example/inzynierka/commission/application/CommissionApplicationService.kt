package org.example.inzynierka.commission.application

import org.example.inzynierka.commission.domain.CommissionCalculatorFactory
import org.example.inzynierka.commission.infrastructure.CommissionRuleMapper
import org.example.inzynierka.commission.infrastructure.CommissionRuleRepository
import org.example.inzynierka.shared.domain.FiatCurrency
import org.springframework.stereotype.Service

@Service
open class CommissionApplicationService(
    private val repository: CommissionRuleRepository,
    private val factory: CommissionCalculatorFactory,
    private val mapper: CommissionRuleMapper
) {

    open fun calculateForPartner(partnerId: String, amount: FiatCurrency): CommissionResultDto {
        val rules = repository.findByPartnerId(partnerId)
            .let { mapper.toDomainList(it) }
        val calculator = factory.create(rules, amount)

        return CommissionResultDto(
            deducted = calculator.deducted(amount),
            onTop = calculator.onTop(amount),
            total = calculator.total(amount),
            net = calculator.net(amount),
            gross = calculator.gross(amount)
        )
    }
}
