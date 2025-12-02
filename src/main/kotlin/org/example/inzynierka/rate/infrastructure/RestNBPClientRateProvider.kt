package org.example.inzynierka.rate.infrastructure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.example.inzynierka.shared.domain.ExchangeRate
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Locale.getDefault

@Component
class RestNBPClientRateProvider(
    private val restTemplate: RestTemplate,
) {


    fun getCurrentRates(from: FiatCurrencyCode, to: FiatCurrencyCode): List<ExchangeRate> {
        require(to == FiatCurrencyCode.PLN) { "NBP only supports rates to PLN" }

        val url = "https://api.nbp.pl/api/exchangerates/rates/a/${from.code.lowercase(getDefault())}/?format=json"

        return try {
            val response =
                restTemplate.getForEntity(
                    url,
                    NbpResponseExchangeRates::class.java
                ).body
                ?: return emptyList()

            response.rates.map {
                ExchangeRate(
                    date = LocalDate.parse(it.effectiveDate).atStartOfDay(),
                    rate = BigDecimal.valueOf(it.mid),
                    from = from,
                    to = to
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class NbpResponseExchangeRates(
    val code: String,
    val rates: List<NbpRate>
)

data class NbpRate(
    val effectiveDate: String,
    val mid: Double
)
