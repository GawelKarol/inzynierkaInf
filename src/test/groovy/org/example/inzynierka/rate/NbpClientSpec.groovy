package org.example.inzynierka.rate

import org.example.inzynierka.rate.infrastructure.NbpRate
import org.example.inzynierka.rate.infrastructure.NbpResponseExchangeRates
import org.example.inzynierka.rate.infrastructure.RestNBPClientRateProvider
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@SpringBootTest
class NbpClientSpec extends Specification {

    def "should parse NBP response correctly"() {
        given:
        def restTemplate = Mock(RestTemplate)
        def client = new RestNBPClientRateProvider(restTemplate)

        def jsonResponse = new NbpResponseExchangeRates(
                "USD",
                [ new NbpRate("2024-01-02", 3.7654) ]
        )

        restTemplate.getForEntity(_ as String, NbpResponseExchangeRates.class) >> ResponseEntity.ok(jsonResponse)

        when:
        def result = client.getCurrentRates(FiatCurrencyCode.USD, FiatCurrencyCode.PLN)

        then:
        result.size() == 1

        with(result.first()) {
            date.toLocalDate().toString() == "2024-01-02"
            rate == new BigDecimal("3.7654")
            from == FiatCurrencyCode.USD
            to == FiatCurrencyCode.PLN
        }
    }


    def "should return empty list on exception"() {
        given:
        def restTemplate = Mock(RestTemplate)
        def client = new RestNBPClientRateProvider(restTemplate)

        restTemplate.getForEntity(_ as URI, NbpResponseExchangeRates.class) >> { throw new RuntimeException("boom") }

        when:
        def result = client.getCurrentRates(FiatCurrencyCode.EUR, FiatCurrencyCode.PLN)

        then:
        result == []
    }

    def "should throw error when target currency is not PLN"() {
        given:
        def client = new RestNBPClientRateProvider(new RestTemplate())

        when:
        client.getCurrentRates(FiatCurrencyCode.USD, FiatCurrencyCode.EUR)

        then:
        thrown(IllegalArgumentException)
    }
}
