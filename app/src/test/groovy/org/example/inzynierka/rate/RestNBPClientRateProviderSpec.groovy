package org.example.inzynierka.rate

import org.example.inzynierka.rate.infrastructure.NbpRate
import org.example.inzynierka.rate.infrastructure.NbpResponseExchangeRates
import org.example.inzynierka.rate.infrastructure.RestNBPClientRateProvider;
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.example.inzynierka.shared.domain.ExchangeRate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import java.math.BigDecimal
import java.time.LocalDate

class RestNBPClientRateProviderSpec extends Specification {

    RestTemplate restTemplate = Mock()
    RestNBPClientRateProvider provider = new RestNBPClientRateProvider(restTemplate)

    def "should fetch and map current rates from NBP when target is PLN"() {
        given: "waluty oraz odpowiedź z NBP"
            def from = FiatCurrencyCode.USD
            def to = FiatCurrencyCode.PLN

            def jsonResponse = new NbpResponseExchangeRates(
                "USD",
                [ new NbpRate("2024-01-02", 3.7654d) ]
            )

            restTemplate.getForEntity(_ as String, NbpResponseExchangeRates) >>
                new ResponseEntity<NbpResponseExchangeRates>(jsonResponse, HttpStatus.OK)

        when: "pobieramy kursy"
            List<ExchangeRate> result = provider.getCurrentRates(from, to)

        then: "zostaje zwrócona lista z jednym kursem poprawnie zmapowanym"
            result.size() == 1
            def rate = result[0]
            rate.from == from
            rate.to == to
            rate.rate == BigDecimal.valueOf(3.7654d)
            rate.date.toLocalDate() == LocalDate.parse("2024-01-02")
    }

    def "should throw when target currency is not PLN"() {
        when: "wołamy z walutą docelową inną niż PLN"
            provider.getCurrentRates(FiatCurrencyCode.USD, FiatCurrencyCode.EUR)

        then: "rzucany jest IllegalArgumentException"
            def e = thrown(IllegalArgumentException)
            e.message == "NBP only supports rates to PLN"
    }

    def "should return empty list when response body is null"() {
        given: "NBP zwraca odpowiedź bez body"
            restTemplate.getForEntity(_ as String, NbpResponseExchangeRates) >>
                new ResponseEntity<NbpResponseExchangeRates>(null, HttpStatus.OK)

        when:
            def result = provider.getCurrentRates(FiatCurrencyCode.USD, FiatCurrencyCode.PLN)

        then:
            result.isEmpty()
    }

    def "should return empty list when RestTemplate throws exception"() {
        given: "RestTemplate rzuca wyjątek podczas wywołania"
            restTemplate.getForEntity(_ as String, NbpResponseExchangeRates) >> { throw new RuntimeException("boom") }

        when:
            def result = provider.getCurrentRates(FiatCurrencyCode.USD, FiatCurrencyCode.PLN)

        then:
            result.isEmpty()
    }
}
