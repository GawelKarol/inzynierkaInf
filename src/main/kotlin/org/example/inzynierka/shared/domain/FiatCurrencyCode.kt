package org.example.inzynierka.shared.domain

enum class FiatCurrencyCode(val code: String) {
    EUR("EUR"),
    USD("USD"),
    PLN("PLN");

    companion object {
        val supportedCurrencies = listOf(EUR, USD, PLN)

        fun of(code: String): FiatCurrencyCode =
            entries.firstOrNull { it.code.equals(code, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unsupported currency: $code")
    }
}
