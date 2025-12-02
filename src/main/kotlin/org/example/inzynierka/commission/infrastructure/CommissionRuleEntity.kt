package org.example.inzynierka.commission.infrastructure

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "partner_commission_rule")
class CommissionRuleEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val partnerId: String,

    val kind: String,

    val addedOnTop: Boolean,

    val rate: BigDecimal?,
    val fixedValue: BigDecimal?,

    val currency: String?
)
