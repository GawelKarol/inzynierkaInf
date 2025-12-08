package org.example.inzynierka.transaction.application

import org.example.inzynierka.partner.application.PartnerKeyService
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.example.inzynierka.transaction.application.api.WidgetConfig
import org.example.inzynierka.transaction.application.api.WidgetEditableSide
import org.example.inzynierka.transaction.application.api.WidgetInitRequest
import org.example.inzynierka.transaction.application.api.WidgetInitResponse
import org.example.inzynierka.transaction.application.api.buildWidgetConfigPayloadToSign
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class WidgetInitService(
    private val partnerKeyService: PartnerKeyService,
    private val crossRateService: CrossRateService,
) {

    fun initWidget(request: WidgetInitRequest): WidgetInitResponse {
        val fromCode = FiatCurrencyCode.valueOf(request.sourceCurrency)
        val toCode = FiatCurrencyCode.valueOf(request.targetCurrency)

        val rate = crossRateService.getRate(fromCode, toCode)
            ?: throw IllegalStateException("Brak kursu dla pary ${request.sourceCurrency}/${request.targetCurrency}")

        val now = Instant.now()
        val config = WidgetConfig(
            widgetSessionId = UUID.randomUUID().toString(),
            partnerId = request.partnerId,
            sourceCurrency = request.sourceCurrency,
            targetCurrency = request.targetCurrency,
            editableSide = WidgetEditableSide.TARGET_ONLY,
            initialTargetAmount = request.initialTargetAmount,
            rate = rate.rate,
            minTargetAmount = BigDecimal("10.00"),
            maxTargetAmount = BigDecimal("100000.00"),
            expiresAt = now.plus(5, ChronoUnit.MINUTES).epochSecond,
            nonce = UUID.randomUUID().toString(),
        )

        val payload = buildWidgetConfigPayloadToSign(config)
        val signature = partnerKeyService.signForPartner(request.partnerId, payload)

        return WidgetInitResponse(
            config = config,
            signature = signature,
        )
    }
}
