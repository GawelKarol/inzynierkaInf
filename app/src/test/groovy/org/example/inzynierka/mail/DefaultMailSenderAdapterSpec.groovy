package org.example.inzynierka.mail

import org.example.inzynierka.mail.domain.MailData
import org.example.inzynierka.mail.domain.MailSenderAdapter
import org.example.inzynierka.mail.infrastructure.DefaultMailSenderAdapter
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessagePreparator
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class DefaultMailSenderAdapterSpec extends Specification {

    JavaMailSender javaMailSender = Mock()
    MailSenderAdapter adapter = new DefaultMailSenderAdapter(javaMailSender)

    def "should delegate sending mail to JavaMailSender"() {
        given: "MailData do wysłania"
        def mailData = new MailData(
                "no-reply@kantor.pl",
                ["user@example.com"] as Set,
                "Test subject",
                "<html>Test body</html>",
                null
        )

        when: "wysyłamy maila"
        adapter.send(mailData).get(3, TimeUnit.SECONDS)

        then: "JavaMailSender zostaje wywołany"
        1 * javaMailSender.send(_ as MimeMessagePreparator)
    }

    def "should catch MailException and not rethrow"() {
        given: "MailData do wysłania"
        def mailData = new MailData(
                "no-reply@kantor.pl",
                ["user@example.com"] as Set,
                "Test subject",
                "<html>Test body</html>",
                null
        )

        when: "wysyłamy maila, a JavaMailSender rzuca MailSendException"
        adapter.send(mailData).get(3, TimeUnit.SECONDS)

        then: "JavaMailSender został wywołany i rzucił wyjątek, który został złapany"
        1 * javaMailSender.send(_ as MimeMessagePreparator) >> { throw new MailSendException("boom") }
        noExceptionThrown()
    }
}
