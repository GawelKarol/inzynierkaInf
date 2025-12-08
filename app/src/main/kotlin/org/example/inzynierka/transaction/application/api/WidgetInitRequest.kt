package org.example.inzynierka.transaction.application.api

import java.math.BigDecimal

// Request od partnera – co chce na starcie
data class WidgetInitRequest(
    val partnerId: String,
    val targetCurrency: String,     // waluta w polu "za" (EDYTOWALNA)
    val sourceCurrency: String,     // waluta w polu "kupujesz" (AUTO)
    val initialTargetAmount: BigDecimal?, // np. 100; może być null
)

// która strona jest edytowalna
enum class WidgetEditableSide {
    TARGET_ONLY  // na razie tylko ten wariant
}

data class WidgetConfig(
    val widgetSessionId: String,
    val partnerId: String,

    val sourceCurrency: String,       // po lewej: KUPUJESZ (auto)
    val targetCurrency: String,       // po prawej: ZA (edytowalne)

    val editableSide: WidgetEditableSide, // zawsze TARGET_ONLY

    val initialTargetAmount: BigDecimal?, // kwota w polu "za"
    val rate: BigDecimal,                 // kurs 1 source -> target
    val minTargetAmount: BigDecimal,
    val maxTargetAmount: BigDecimal,

    val expiresAt: Long,                  // timestamp do kiedy ważna konfiguracja
    val nonce: String,                    // żeby podpis był jednorazowy
)

data class WidgetInitResponse(
    val config: WidgetConfig,
    val signature: String,
)
