package org.example.inzynierka.partner.application

import org.example.inzynierka.partner.domain.*
import org.springframework.stereotype.Service

@Service
class PartnerService(
    private val repository: PartnerRepository
) {

    fun getPartner(id: String): Partner? =
        repository.findById(PartnerId(id))
}
