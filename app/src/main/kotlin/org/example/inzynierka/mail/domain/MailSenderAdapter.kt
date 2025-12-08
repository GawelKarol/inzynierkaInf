package org.example.inzynierka.mail.domain

import java.util.concurrent.CompletableFuture

interface MailSenderAdapter {
    fun send(mailData: MailData): CompletableFuture<Void>
}

data class MailData(
    val from: String,
    val to: Set<String>,
    val subject: String,
    val htmlBody: String,
    val bcc: Set<String>? = null
)