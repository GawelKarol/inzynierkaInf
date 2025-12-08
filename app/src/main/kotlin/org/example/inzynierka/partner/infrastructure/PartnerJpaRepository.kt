package org.example.inzynierka.partner.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface PartnerJpaRepository : JpaRepository<PartnerConfigurationEntity, String>
