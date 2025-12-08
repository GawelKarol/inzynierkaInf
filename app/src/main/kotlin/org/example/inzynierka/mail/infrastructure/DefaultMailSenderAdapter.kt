package org.example.inzynierka.mail.infrastructure

import org.example.inzynierka.mail.domain.MailData
import org.example.inzynierka.mail.domain.MailSenderAdapter
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.mail.javamail.MimeMessagePreparator
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture

class DefaultMailSenderAdapter(
    private val javaMailSender: JavaMailSender
) : MailSenderAdapter {

    override fun send(mailData: MailData): CompletableFuture<Void> =
        CompletableFuture.runAsync {
            val preparator = MimeMessagePreparator { message ->
                MimeMessageHelper(message, false, "UTF-8").apply {
                    setFrom(mailData.from)
                    setTo(mailData.to.toTypedArray())
                    setSubject(mailData.subject)
                    setText(mailData.htmlBody, true)
                    mailData.bcc?.forEach { addBcc(it) }
                }
            }

            try {
                javaMailSender.send(preparator)
            } catch (ex: MailException) {
                log.error("Mail error", ex)
            }
        }

    companion object {
        private val log = LoggerFactory.getLogger(DefaultMailSenderAdapter::class.java)
    }
}
