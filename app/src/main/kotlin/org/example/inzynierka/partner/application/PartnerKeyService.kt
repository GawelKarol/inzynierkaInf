package org.example.inzynierka.partner.application

import org.example.inzynierka.partner.infrastructure.PartnerJpaRepository
import org.example.inzynierka.shared.domain.RsaSignatureUtil
import org.springframework.stereotype.Service
import java.security.PrivateKey
import java.security.PublicKey

@Service
class PartnerKeyService(
    private val partnerConfigurationRepository: PartnerJpaRepository,
) {

    private fun getConfig(partnerId: String) =
        partnerConfigurationRepository.findById(partnerId)
            .orElseThrow { IllegalArgumentException("Unknown partner: $partnerId") }

    fun getPrivateKey(partnerId: String): PrivateKey =
        RsaSignatureUtil.loadPrivateKeyFromPem(getConfig(partnerId).privateKey)

    fun getPublicKey(partnerId: String): PublicKey =
        RsaSignatureUtil.loadPublicKeyFromPem(getConfig(partnerId).publicKey)

    fun signForPartner(partnerId: String, payloadToSign: String): String {
        val privateKey = getPrivateKey(partnerId)
        return RsaSignatureUtil.signSha256WithRsa(payloadToSign, privateKey)
    }
}
