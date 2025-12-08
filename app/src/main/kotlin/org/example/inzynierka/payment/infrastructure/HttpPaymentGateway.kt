package org.example.inzynierka.payment.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.inzynierka.payment.domain.PaymentGateway
import org.example.inzynierka.payment.domain.PaymentInitDto
import org.example.inzynierka.payment.domain.PaymentInitResultDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class HttpPaymentGateway(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    @Value("\${payment.provider-url}") private val providerUrl: String
) : PaymentGateway {

    private val log = LoggerFactory.getLogger(HttpPaymentGateway::class.java)

    override fun initPayment(request: PaymentInitDto): PaymentInitResultDto {
        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        // tutaj możesz dostosować payload do konkretnego operatora
        val payload = mapOf(
            "transactionId" to request.transactionId,
            "amount" to request.amount,
            "currency" to request.currency,
            "method" to request.paymentMethod,
            "returnUrl" to request.returnUrl,
            "notifyUrl" to request.notifyUrl
        )

        val body = objectMapper.writeValueAsString(payload)
        val entity = HttpEntity(body, headers)

        log.info("Sending payment init request to $providerUrl for transaction ${request.transactionId}")

        // Tu dla prostoty zakładam, że provider odsyła JSON:
        // { "externalPaymentId": "...", "redirectUrl": "https://..." }
        val response = restTemplate.postForEntity(providerUrl, entity, String::class.java)

        val json = response.body ?: throw IllegalStateException("Empty response from payment provider")

        val node = objectMapper.readTree(json)
        val externalId = node.get("externalPaymentId").asText()
        val redirectUrl = node.get("redirectUrl").asText()

        return PaymentInitResultDto(
            externalPaymentId = externalId,
            redirectUrl = redirectUrl
        )
    }
}
