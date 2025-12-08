package org.example.inzynierka.transaction.application

import org.example.inzynierka.commission.application.CommissionApplicationService
import org.example.inzynierka.transaction.domain.*
import org.example.inzynierka.mail.domain.TransactionMailSender
import org.example.inzynierka.payment.application.PaymentService
import org.example.inzynierka.shared.domain.FiatCurrency
import org.example.inzynierka.shared.domain.FiatCurrencyCode
import org.example.inzynierka.transaction.application.api.CalculateRequest
import org.example.inzynierka.transaction.application.api.CalculateResponse
import org.example.inzynierka.transaction.infrastructure.CreateWidgetTransactionRequest
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.time.temporal.TemporalAmount
import java.util.UUID

@Service
class TransactionService(
    private val repository: TransactionRepository,
    private val verificationService: TransactionVerificationService,
    private val mailSender: TransactionMailSender,
    private val commissionApplicationService: CommissionApplicationService,
    private val webhookSender: WebhookSender,
    private val paymentService: PaymentService,
    private val crossRateService: CrossRateService
) {

    fun createWidgetTransaction(
        request: CreateWidgetTransactionRequest
    ): Transaction {
        val now = LocalDateTime.now()

        val calc = calculateWidgetAmounts(
            partnerId = request.partnerId,
            currencyFrom = request.currencyFrom,
            currencyTo = request.currencyTo,
            amountFiatRaw = request.amountFiat,
        )

        val tx = Transaction(
            id = null,
            partnerId = request.partnerId,
            currencyFrom = request.currencyFrom,
            currencyTo = request.currencyTo,
            amountFiat = calc.amountFiat,
            amountToPay = calc.amountToPay,
            partnerFee = calc.partnerFee,
            platformFee = calc.platformFee,
            amountAfterFee = calc.amountAfterFee,
            totalCommission = calc.totalFee,
            nbpRate = calc.nbpRate,
            convertedAmount = calc.convertedAmount,
            status = TransactionStatus.CREATED,
            createdAt = now,
            updatedAt = now,
        )

        return repository.save(tx)
    }

    fun calculateWidgetTransaction(
        request: CreateWidgetTransactionRequest
    ): WidgetCalculationResult =
        calculateWidgetAmounts(
            partnerId = request.partnerId,
            currencyFrom = request.currencyFrom,
            currencyTo = request.currencyTo,
            amountFiatRaw = request.amountFiat,
        )

    fun getWidgetTransaction(id: Long): Transaction {
        return repository.findById(id)
            ?: throw IllegalArgumentException("Transaction not found")
    }

    fun sendVerificationCode(transactionId: Long, email: String) {
        val tx = repository.findById(transactionId)
            ?: throw IllegalArgumentException("Transaction not found")

        if (tx.resendCount >= 3) {
            throw IllegalStateException("Verification code resend limit reached")
        }

        val code = verificationService.generateCode()
        tx.verificationCode = code
        tx.resendCount += 1
        tx.status = TransactionStatus.VERIFICATION_SENT

        repository.save(tx)

        mailSender.sendVerificationCode(email, code)
    }

    fun verifyTransactionCode(dto: TransactionVerifyDto, transactionId: Long): Transaction {
        val tx = repository.findById(transactionId)
            ?: throw IllegalArgumentException("Transaction not found")

        if (!verificationService.validateCode(tx, dto.code)) {
            throw IllegalArgumentException("Invalid verification code")
        }

        tx.status = TransactionStatus.VERIFIED
        return repository.save(tx)
    }

    fun submitTransaction(dto: TransactionSubmitDto): Transaction {
        val tx = repository.findById(dto.transactionId)
            ?: throw IllegalArgumentException("Transaction not found")

        if (tx.status != TransactionStatus.VERIFIED) {
            throw IllegalStateException("Transaction not verified yet")
        }

        tx.paymentMethod = dto.paymentMethod
        tx.status = TransactionStatus.SUBMITTED
        repository.save(tx)

        val paymentInit = paymentService.startPayment(tx)
        tx.externalPaymentId = paymentInit.externalPaymentId
        tx.status = TransactionStatus.WAITING_FOR_PAYMENT

        val saved = repository.save(tx)
        webhookSender.send(WebhookEvent.PAYMENT_START, saved)

        return saved
    }

    fun markPaymentCompleted(externalPaymentId: String): Transaction {
        val tx = repository.findByExternalPaymentId(externalPaymentId)
            ?: throw IllegalArgumentException("Transaction not found")

        tx.status = TransactionStatus.COMPLETED
        val saved = repository.save(tx)

        webhookSender.send(WebhookEvent.COMPLETED, saved)

        return saved
    }

    fun cancelTransaction(transactionId: Long): Transaction {
        val tx = repository.findById(transactionId)
            ?: throw IllegalArgumentException("Transaction not found")

        tx.status = TransactionStatus.CANCELED
        val saved = repository.save(tx)

        webhookSender.send(WebhookEvent.CANCELLED, saved)

        return saved
    }

    fun calculateFromTargetAmount(request: CalculateRequest): CalculateResponse {
        val fromCode = FiatCurrencyCode.valueOf(request.sourceCurrency)
        val toCode = FiatCurrencyCode.valueOf(request.targetCurrency)

        val rate = crossRateService.getRate(fromCode, toCode)
            ?: throw IllegalStateException("Brak kursu dla pary ${request.sourceCurrency}/${request.targetCurrency}")

        val rateValue = rate.rate

        val targetAmount = request.targetAmount

        var sourceAmount = targetAmount
            .divide(rateValue, 8, RoundingMode.HALF_UP)

        val commission = BigDecimal.ZERO

        return CalculateResponse(
            sourceAmount = sourceAmount,
            targetAmount = targetAmount,
            rate = rateValue,
            commission = commission,
        )
    }

    fun createPayment(id: Long, paymentMethodId: String): String {
        val tx = repository.findById(id)
            ?: throw IllegalArgumentException("Transaction not found")

        tx.paymentMethod = paymentMethodId
        tx.externalPaymentId = "MOCK-${UUID.randomUUID()}"
        repository.save(tx)

        // mockowy URL providera
        return "https://payments-sandbox.example.com/pay?paymentId=${tx.externalPaymentId}"
    }

    private fun calculateWidgetAmounts(
        partnerId: String,
        currencyFrom: String,
        currencyTo: String,
        amountFiatRaw: BigDecimal,
    ): WidgetCalculationResult {
        val amountFiat = amountFiatRaw.setScale(6, RoundingMode.HALF_UP)

        val nbpRate = crossRateService.getRate(FiatCurrencyCode.of(currencyFrom), FiatCurrencyCode.of(currencyTo))

        val commission = commissionApplicationService.calculateForPartner(partnerId, FiatCurrency.of(amountFiat, FiatCurrencyCode.of(currencyFrom)) )

        val partnerFee = BigDecimal.ZERO
        val platformFee = BigDecimal.ZERO

        val amountAfterFee = commission.net.amount

        val totalFee = commission.total.amount
        val grossAmount = commission.gross.amount
        val buyAmount = (amountAfterFee * (nbpRate?.rate ?: 1.0.toBigDecimal())).setScale(2, RoundingMode.HALF_UP)
        val calculatedRate = amountFiat.divide(buyAmount, RoundingMode.HALF_UP)
        return WidgetCalculationResult(
            amountFiat = amountFiat,
            amountToPay = grossAmount,
            partnerFee = partnerFee,
            platformFee = platformFee,
            amountAfterFee = amountAfterFee,
            nbpRate = calculatedRate,
            totalFee = totalFee,
            convertedAmount = buyAmount,
        )
    }
}

data class WidgetCalculationResult(
    val amountFiat: BigDecimal,
    val amountToPay: BigDecimal,
    val partnerFee: BigDecimal,
    val platformFee: BigDecimal,
    val amountAfterFee: BigDecimal,
    val nbpRate: BigDecimal,
    val convertedAmount: BigDecimal,
    val totalFee: BigDecimal,
)
