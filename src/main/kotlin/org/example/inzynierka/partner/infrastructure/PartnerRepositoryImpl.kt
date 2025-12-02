package org.example.inzynierka.partner.infrastructure

import org.example.inzynierka.partner.domain.*
import org.springframework.stereotype.Repository

@Repository
open class PartnerRepositoryImpl(
    private val jpa: PartnerJpaRepository
) : PartnerRepository {

    override fun findById(id: PartnerId): Partner? {
        val entity = jpa.findById(id.value).orElse(null) ?: return null

        return Partner(
            id = id,
            publicKey = entity.publicKey,
            privateKey = entity.privateKey,
            webhookPaidUrl = entity.webhookPaidUrl,
            webhookCanceledUrl = entity.webhookCanceledUrl,
            webhookCompleteUrl = entity.webhookCompleteUrl
        )
    }
}
