package org.example.inzynierka.commission.infrastructure


import org.springframework.data.jpa.repository.JpaRepository

interface CommissionRuleRepository : JpaRepository<CommissionRuleEntity, Long> {
    fun findByPartnerId(partnerId: String): List<CommissionRuleEntity>
}
