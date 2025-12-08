package org.example.inzynierka.transaction

import org.example.inzynierka.mail.domain.TransactionMailSender
import org.example.inzynierka.payment.application.PaymentService
import org.example.inzynierka.transaction.application.TransactionCreateDto
import org.example.inzynierka.transaction.application.TransactionService
import org.example.inzynierka.transaction.application.TransactionSubmitDto
import org.example.inzynierka.transaction.application.TransactionVerifyDto
import org.example.inzynierka.transaction.domain.*
import spock.lang.Specification

import java.time.LocalDateTime

class TransactionServiceSpec extends Specification {

    // MOCKI
    TransactionRepository repository = Mock()
    TransactionVerificationService verificationService = new TransactionVerificationService() // prawdziwy serwis
    TransactionMailSender mailSender = Mock()
    WebhookSender webhookSender = Mock()
    PaymentService paymentService = Mock()

    // SUT
    TransactionService service = new TransactionService(
            repository,
            verificationService,
            mailSender,
            webhookSender,
            paymentService
    )

    // Pomocniczy builder domenowej transakcji
    private static Transaction tx(
            Long id,
            String email,
            String amount,
            String currency,
            TransactionStatus status = TransactionStatus.CREATED,
            String verificationCode = null,
            int resendCount = 0,
            String paymentMethod = null,
            String externalPaymentId = null,
            String webhookUrl = null
    ) {
        return new Transaction(
                id,
                email,
                amount,
                currency,
                status,
                verificationCode,
                resendCount,
                paymentMethod,
                externalPaymentId,
                webhookUrl,
                LocalDateTime.now()
        )
    }

    // ========== createTransaction ==========

    def "should create transaction and save it via repository"() {
        given:
        def dto = new TransactionCreateDto(
                "user@example.com",
                "100.00",
                "PLN",
                "https://example.com/webhook"
        )

        when:
        def result = service.createTransaction(dto)

        then:
        1 * repository.save(_ as Transaction) >> { Transaction t ->
            // tutaj asercje na to, co zostało przekazane do repo
            assert t.id == null                // dopiero baza nadaje ID
            assert t.email == dto.email
            assert t.amount == dto.amount
            assert t.currency == dto.currency
            assert t.webhookUrl == dto.webhookUrl
            assert t.status == TransactionStatus.CREATED
            assert t.verificationCode == null
            assert t.resendCount == 0

            // symulujemy to, co normalnie zwróciłaby baza
            return tx(1L, t.email, t.amount, t.currency,
                    t.status, t.verificationCode, t.resendCount,
                    t.paymentMethod, t.externalPaymentId, t.webhookUrl)
        }

        and:
        result != null
        result.id == 1L
        result.email == dto.email
        result.amount == dto.amount
        result.currency == dto.currency
        result.webhookUrl == dto.webhookUrl
        result.status == TransactionStatus.CREATED
    }

    // ========== sendVerificationCode ==========

    def "should generate and send verification code and update transaction"() {
        given:
        def id = 10L
        def transaction = tx(id, "user@example.com", "100.00", "PLN")

        repository.findById(_ as Long) >> transaction

        when:
        service.sendVerificationCode(id)

        then: "wysłany mail z tym samym kodem, który zapisano w transakcji"
        1 * mailSender.sendVerificationCode("user@example.com", _ as String) >> { String email, String code ->
            assert email == "user@example.com"
            assert code == transaction.verificationCode
        }

        and: "transakcja jest zaktualizowana"
        transaction.verificationCode != null
        transaction.verificationCode.size() == 6
        transaction.resendCount == 1
        transaction.status == TransactionStatus.VERIFICATION_SENT

        and: "żaden webhook nie jest wysyłany na tym etapie"
        0 * webhookSender._
    }


    def "should throw when transaction not found on sendVerificationCode"() {
        given:
        repository.findById(999L) >> null

        when:
        service.sendVerificationCode(999L)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Transaction not found"

        0 * mailSender.sendVerificationCode(_, _)
        0 * repository.save(_)
        0 * webhookSender._
    }

    def "should fail when resend limit exceeded"() {
        given:
        def id = 11L
        def transaction = tx(id, "user@example.com", "50.00", "EUR",
                TransactionStatus.CREATED, null, 3)

        repository.findById(id) >> transaction

        when:
        service.sendVerificationCode(id)

        then:
        def ex = thrown(IllegalStateException)
        ex.message == "Verification code resend limit reached"

        0 * mailSender.sendVerificationCode(_, _)
        0 * repository.save(_)
        0 * webhookSender._
    }

    // ========== verifyTransactionCode ==========

    def "should verify transaction code and set status VERIFIED"() {
        given:
        def id = 20L
        def transaction = tx(id, "user@example.com", "100.00", "PLN",
                TransactionStatus.CREATED, "123456", 1)

        repository.findById(id) >> transaction
        repository.save(_ as Transaction) >> { Transaction t -> t }

        when:
        def result = service.verifyTransactionCode(
                new TransactionVerifyDto(id, "123456")
        )

        then:
        result.status == TransactionStatus.VERIFIED
    }

    def "should throw when verification code is invalid"() {
        given:
        def id = 21L
        def transaction = tx(id, "user@example.com", "100.00", "PLN",
                TransactionStatus.CREATED, "111111", 1)

        repository.findById(id) >> transaction

        when:
        service.verifyTransactionCode(
                new TransactionVerifyDto(id, "999999")
        )

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Invalid verification code"

        0 * repository.save(_)
    }

    // ========== submitTransaction (happy path) ==========

/*    def "should submit verified transaction, start payment and send PAYMENT_START webhook"() {
        given:
        def id = 30L
        def transaction = tx(id, "user@example.com", "200.00", "PLN",
                TransactionStatus.VERIFIED, "123456", 1, null, null, "https://example.com/webhook")

        and: "repozytorium zwraca transakcję"
        repository.findById(id) >> transaction

        and: "payment service zwraca identyfikator płatności"
        def paymentResult = new PaymentInitResult("EXT-999")
        paymentService.startPayment(_ as Transaction) >> paymentResult

        and: "zapis transakcji zwraca przekazany obiekt"
        repository.save(_ as Transaction) >> { Transaction t -> t }

        when:
        def result = service.submitTransaction(new TransactionSubmitDto(id, "BLIK"))

        then:
        result.status == TransactionStatus.WAITING_FOR_PAYMENT
        result.paymentMethod == "BLIK"
        result.externalPaymentId == "EXT-999"

        and: "webhook PAYMENT_START wysłany"
        1 * webhookSender.send(WebhookEvent.PAYMENT_START, _ as Transaction)
    }*/

    def "should throw when submitting not verified transaction"() {
        given:
        def id = 31L
        def transaction = tx(id, "user@example.com", "200.00", "PLN",
                TransactionStatus.CREATED)

        repository.findById(id) >> transaction

        when:
        service.submitTransaction(new TransactionSubmitDto(id, "BLIK"))

        then:
        def ex = thrown(IllegalStateException)
        ex.message == "Transaction not verified yet"

        0 * paymentService.startPayment(_)
        0 * webhookSender.send(_, _)
        0 * repository.save(_)
    }

    // ========== markPaymentCompleted ==========

    def "should mark payment completed and send COMPLETED webhook"() {
        given:
        def externalId = "EXT-123"
        def transaction = tx(40L, "user@example.com", "300.00", "PLN",
                TransactionStatus.WAITING_FOR_PAYMENT, "123456", 1, "BLIK",
                externalId, "https://example.com/webhook")

        repository.findByExternalPaymentId(externalId) >> transaction
        repository.save(_ as Transaction) >> { Transaction t -> t }

        when:
        def result = service.markPaymentCompleted(externalId)

        then:
        result.status == TransactionStatus.COMPLETED
        1 * webhookSender.send(WebhookEvent.COMPLETED, _ as Transaction)
    }

    def "should throw when marking completed for unknown externalPaymentId"() {
        given:
        repository.findByExternalPaymentId("UNKNOWN") >> null

        when:
        service.markPaymentCompleted("UNKNOWN")

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Transaction not found"

        0 * repository.save(_)
        0 * webhookSender.send(_, _)
    }

    // ========== cancelTransaction ==========

    def "should cancel transaction and send CANCELLED webhook"() {
        given:
        def id = 50L
        def transaction = tx(id, "user@example.com", "80.00", "PLN",
                TransactionStatus.WAITING_FOR_PAYMENT, null, 0,
                "BLIK", "EXT-456", "https://example.com/webhook")

        repository.findById(id) >> transaction
        repository.save(_ as Transaction) >> { Transaction t -> t }

        when:
        def result = service.cancelTransaction(id)

        then:
        result.status == TransactionStatus.CANCELED
        1 * webhookSender.send(WebhookEvent.CANCELLED, _ as Transaction)
    }

    def "should throw when cancelling unknown transaction"() {
        given:
        repository.findById(999L) >> null

        when:
        service.cancelTransaction(999L)

        then:
        def ex = thrown(IllegalArgumentException)
        ex.message == "Transaction not found"

        0 * repository.save(_)
        0 * webhookSender.send(_, _)
    }
}
