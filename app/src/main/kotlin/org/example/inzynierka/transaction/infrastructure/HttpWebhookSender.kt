package org.example.inzynierka.transaction.infrastructure

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.inzynierka.transaction.domain.Transaction
import org.example.inzynierka.transaction.domain.WebhookEvent
import org.example.inzynierka.transaction.domain.WebhookSender
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class HttpWebhookSender(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) : WebhookSender {

    private val log = LoggerFactory.getLogger(HttpWebhookSender::class.java)

    override fun send(event: WebhookEvent, transaction: Transaction) {
        val url = transaction.webhookUrl ?: run {
            log.debug("No webhookUrl for transaction ${transaction.id}, skipping webhook $event")
            return
        }

        val payload = mapOf(
            "event" to event.name.lowercase(),        // payment_start / completed / cancelled
            "transactionId" to transaction.id,
            "email" to transaction.userEmail,
            "amount" to transaction.amountFiat,
            "currency" to transaction.currencyTo,
            "status" to transaction.status.name.lowercase()
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
        }

        val body = objectMapper.writeValueAsString(payload)
        val request = HttpEntity(body, headers)

        try {
            restTemplate.postForEntity(url, request, String::class.java)
            log.info("Webhook $event sent for transaction ${transaction.id} → $url")
        } catch (ex: Exception) {
            log.error("Failed to send webhook $event for transaction ${transaction.id} → $url", ex)
        }
    }
}
