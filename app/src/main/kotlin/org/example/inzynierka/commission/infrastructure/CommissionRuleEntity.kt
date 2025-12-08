package org.example.inzynierka.commission.infrastructure

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "partner_commission_rule")
class CommissionRuleEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "partner_id", nullable = false)
    var partnerId: String = "",

    @Column(name = "kind", nullable = false)
    var kind: String = "",

    @Column(name = "added_on_top", nullable = false)
    var addedOnTop: Boolean = false,

    @Column(name = "rate")
    var rate: BigDecimal? = null,

    @Column(name = "fixed_value")
    var fixedValue: BigDecimal? = null,

    @Column(name = "currency")
    var currency: String? = null,
)
