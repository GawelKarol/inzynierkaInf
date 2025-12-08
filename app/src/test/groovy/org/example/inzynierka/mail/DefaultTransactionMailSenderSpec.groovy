package org.example.inzynierka.mail

import org.example.inzynierka.mail.application.TransactionEmailData
import org.example.inzynierka.mail.domain.MailData
import org.example.inzynierka.mail.domain.MailSenderAdapter
import org.example.inzynierka.mail.infrastructure.DefaultTransactionMailSender
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.StringTemplateResolver
import spock.lang.Specification

class DefaultTransactionMailSenderSpec extends Specification {

    MailSenderAdapter mailSenderAdapter = Mock()

    // używamy prawdziwego SpringTemplateEngine z prostym resolverem stringowym
    SpringTemplateEngine templateEngine = {
        def resolver = new StringTemplateResolver()
        resolver.setTemplateMode(TemplateMode.HTML)
        resolver.setCacheable(false)

        def engine = new SpringTemplateEngine()
        engine.setTemplateResolver(resolver)
        return engine
    }()

    String mailFrom = "no-reply@kantor.pl"

    DefaultTransactionMailSender transactionMailSender = new DefaultTransactionMailSender(
            mailSenderAdapter,
            templateEngine,
            mailFrom
    )

    def "should send verification email with correct data"() {
        given:
        def email = "user@example.com"
        def code = "123456"

        when:
        transactionMailSender.sendVerificationCode(email, code)

        then: "MailSenderAdapter dostaje poprawne MailData"
        1 * mailSenderAdapter.send(_ as MailData) >> { MailData mail ->
            assert mail.from == mailFrom
            assert mail.to == [email] as Set
            assert mail.subject == "Twój kod weryfikacyjny"
            assert mail.htmlBody != null
            assert !mail.htmlBody.isBlank()
        }
    }

    def "should send payment started email with correct subject and body"() {
        given:
        def data = new TransactionEmailData(
                "user@example.com",
                "TX-123",
                "100.00",
                "PLN"
        )

        when:
        transactionMailSender.sendPaymentStarted(data)

        then:
        1 * mailSenderAdapter.send(_ as MailData) >> { MailData mail ->
            assert mail.from == mailFrom
            assert mail.to == [data.email] as Set
            assert mail.subject == "Przejdź do płatności — transakcja ${data.transactionId}"
            assert mail.htmlBody != null
            assert !mail.htmlBody.isBlank()
        }
    }

    def "should send payment completed email with correct subject and body"() {
        given:
        def data = new TransactionEmailData(
                "user@example.com",
                "TX-999",
                "250.00",
                "EUR"
        )

        when:
        transactionMailSender.sendPaymentCompleted(data)

        then:
        1 * mailSenderAdapter.send(_ as MailData) >> { MailData mail ->
            assert mail.from == mailFrom
            assert mail.to == [data.email] as Set
            assert mail.subject == "Transakcja ${data.transactionId} została zakończona"
            assert mail.htmlBody != null
            assert !mail.htmlBody.isBlank()
        }
    }
}
