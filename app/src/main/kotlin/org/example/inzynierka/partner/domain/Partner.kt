package org.example.inzynierka.partner.domain

data class Partner(
    val id: PartnerId,
    val publicKey: String,
    val privateKey: String,
    val webhookPaidUrl: String,
    val webhookCanceledUrl: String,
    val webhookCompleteUrl: String
)
