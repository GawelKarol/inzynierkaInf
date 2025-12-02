package org.example.inzynierka.partner.infrastructure

import jakarta.persistence.*

@Entity
@Table(name = "partner_configuration")
class PartnerConfigurationEntity(
    @Id
    val partnerId: String,

    val publicKey: String,

    val privateKey: String,

    @Column(name = "webhook_paid_url")
    val webhookPaidUrl: String,

    @Column(name = "webhook_canceled_url")
    val webhookCanceledUrl: String,

    @Column(name = "webhook_complete_url")
    val webhookCompleteUrl: String
)
