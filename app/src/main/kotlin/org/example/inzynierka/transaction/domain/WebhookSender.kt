package org.example.inzynierka.transaction.domain

interface WebhookSender {
    fun send(event: WebhookEvent, transaction: Transaction)
}
