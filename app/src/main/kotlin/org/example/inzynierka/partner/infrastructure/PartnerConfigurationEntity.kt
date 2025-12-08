package org.example.inzynierka.partner.infrastructure

import jakarta.persistence.*

@Entity
@Table(name = "partner_configuration")
class PartnerConfigurationEntity(

    @Id
    @Column(name = "partner_id")
    val partnerId: String,

    @Column(name = "public_key", nullable = false)
    val publicKey: String,

    @Column(name = "private_key", nullable = false)
    val privateKey: String,

    @Column(name = "webhook_paid_url")
    val webhookPaidUrl: String = "",

    @Column(name = "webhook_cancel_url")
    val webhookCanceledUrl: String = "",

    @Column(name = "webhook_complete_url")
    val webhookCompleteUrl: String = "",
)
