package org.example.inzynierka.mail.infrastructure

import jakarta.annotation.PostConstruct
import org.example.inzynierka.mail.application.TransactionEmailData
import org.example.inzynierka.mail.domain.MailData
import org.example.inzynierka.mail.domain.TransactionMailSender
import org.example.inzynierka.mail.domain.MailSenderAdapter
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

open class DefaultTransactionMailSender(
    private val mailSenderAdapter: MailSenderAdapter,
    private val templateEngine: SpringTemplateEngine,
    private val mailFrom: String
) : TransactionMailSender {

    override fun sendVerificationCode(email: String, code: String) {
        val body = templateEngine.process(
            "transaction_verification",
            Context().apply {
                setVariable("code", code)
            }
        )

        mailSenderAdapter.send(
            MailData(
                from = mailFrom,
                to = setOf(email),
                subject = "Twój kod weryfikacyjny",
                htmlBody = body
            )
        )
    }

    override fun sendPaymentStarted(data: TransactionEmailData) {
        val body = templateEngine.process(
            "transaction_payment_started",
            Context().apply {
                setVariable("transactionId", data.transactionId)
                setVariable("amount", data.amount)
                setVariable("currency", data.currency)
            }
        )

        mailSenderAdapter.send(
            MailData(
                from = mailFrom,
                to = setOf(data.email),
                subject = "Przejdź do płatności — transakcja ${data.transactionId}",
                htmlBody = body
            )
        )
    }

    override fun sendPaymentCompleted(data: TransactionEmailData) {
        val body = templateEngine.process(
            "transaction_completed",
            Context().apply {
                setVariable("transactionId", data.transactionId)
                setVariable("amount", data.amount)
                setVariable("currency", data.currency)
            }
        )

        mailSenderAdapter.send(
            MailData(
                from = mailFrom,
                to = setOf(data.email),
                subject = "Transakcja ${data.transactionId} została zakończona",
                htmlBody = body
            )
        )
    }
}
