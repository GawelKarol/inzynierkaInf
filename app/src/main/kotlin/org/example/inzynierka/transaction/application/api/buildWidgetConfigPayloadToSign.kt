package org.example.inzynierka.transaction.application.api

fun buildWidgetConfigPayloadToSign(config: WidgetConfig): String =
    buildString {
        append("widgetSessionId=").append(config.widgetSessionId)
        append("&partnerId=").append(config.partnerId)
        append("&sourceCurrency=").append(config.sourceCurrency)
        append("&targetCurrency=").append(config.targetCurrency)
        append("&editableSide=").append(config.editableSide.name)
        append("&initialTargetAmount=").append(config.initialTargetAmount?.toPlainString() ?: "")
        append("&rate=").append(config.rate.toPlainString())
        append("&minTargetAmount=").append(config.minTargetAmount.toPlainString())
        append("&maxTargetAmount=").append(config.maxTargetAmount.toPlainString())
        append("&expiresAt=").append(config.expiresAt)
        append("&nonce=").append(config.nonce)
    }
