package org.example.inzynierka.partner.domain

interface PartnerRepository {
    fun findById(id: PartnerId): Partner?
}
