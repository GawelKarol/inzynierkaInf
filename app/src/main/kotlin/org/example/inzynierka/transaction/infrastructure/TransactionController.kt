package org.example.inzynierka.transaction.infrastructure

import org.example.inzynierka.transaction.application.TransactionService
import org.example.inzynierka.transaction.application.TransactionCreateDto
import org.example.inzynierka.transaction.application.TransactionSubmitDto
import org.example.inzynierka.transaction.application.TransactionVerifyDto
import org.example.inzynierka.transaction.application.api.CalculateRequest
import org.example.inzynierka.transaction.application.api.CalculateResponse
import org.example.inzynierka.transaction.domain.Transaction
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/widget/transactions")
@CrossOrigin(origins = ["http://localhost:5173"])
class TransactionController(
    private val service: TransactionService,
) {

    @PostMapping
    fun create(@RequestBody request: CreateWidgetTransactionRequest): Map<String, Long> {
        val tx = service.createWidgetTransaction(request)
        return mapOf("id" to (tx.id ?: error("Transaction id is null after save")))
    }
    @PostMapping("/{id}/verification-code")
    fun sendVerificationCode(
        @PathVariable id: Long,
        @RequestBody request: VerificationEmailRequest
    ) {
        service.sendVerificationCode(id, request.email)
    }

    @GetMapping("/{id}")
    fun getWidgetTransaction(@PathVariable id: Long): WidgetTransactionResponse {
        val tx = service.getWidgetTransaction(id)
        return tx.toWidgetResponse()
    }

    @PostMapping("/{id}/verify")
    fun verify(
        @PathVariable id: Long,
        @RequestBody request: VerificationCodeRequest
    ) =
        service.verifyTransactionCode(request.toDto(), id)

    @PostMapping("/submit")
    fun submit(@RequestBody request: TransactionSubmitRequest) =
        service.submitTransaction(request.toDto())

    @PostMapping("/payment/callback")
    fun paymentCallback(@RequestParam externalPaymentId: String) =
        service.markPaymentCompleted(externalPaymentId)

    @PostMapping("/{id}/cancel")
    fun cancel(@PathVariable id: Long) =
        service.cancelTransaction(id)

    @PostMapping("/calculate")
    fun calculate(
        @RequestBody request: CreateWidgetTransactionRequest
    ): CalculateWidgetResponse {
        val calc = service.calculateWidgetTransaction(request)

        return CalculateWidgetResponse(
            amount = calc.amountAfterFee,
            sourceCurrency = request.currencyFrom,
            targetCurrency = request.currencyTo,
            rate = calc.nbpRate,
            commission = calc.totalFee,
            totalToPay = calc.amountAfterFee,
            amountToReceive = calc.convertedAmount,
        )
    }

    @GetMapping("/{id}/payment-methods")
    fun getPaymentMethods(@PathVariable id: Long): List<PaymentMethodResponse> {
        // upewniamy się, że transakcja istnieje (rzuci wyjątek jeśli nie)
        service.getWidgetTransaction(id)

        return listOf(
            PaymentMethodResponse(
                id = "CARD",
                name = "Karta płatnicza",
                description = "Visa, Mastercard"
            ),
            PaymentMethodResponse(
                id = "BANK_TRANSFER",
                name = "Szybki przelew",
                description = "Przelew online"
            ),
            PaymentMethodResponse(
                id = "BLIK",
                name = "BLIK",
                description = "Płatność BLIK (mock)"
            )
        )
    }

    @PostMapping("/{id}/payment")
    fun createPayment(
        @PathVariable id: Long,
        @RequestBody request: CreatePaymentRequest,
    ): PaymentRedirectResponse {
        val redirectUrl = service.createPayment(id, request.paymentMethodId)
        return PaymentRedirectResponse(redirectUrl = redirectUrl)
    }
}

data class TransactionCreateRequest(
    val email: String,
    val amount: String,
    val currency: String,
    val webhookUrl: String? = null
)

private fun TransactionCreateRequest.toDto() = TransactionCreateDto(
    email = email,
    amount = amount,
    currency = currency,
    webhookUrl = webhookUrl
)

data class VerificationCodeRequest(
    val code: String = ""
)

private fun VerificationCodeRequest.toDto() = TransactionVerifyDto(
    code = code
)

data class TransactionSubmitRequest(
    val transactionId: Long,
    val paymentMethod: String
)

private fun TransactionSubmitRequest.toDto() = TransactionSubmitDto(
    transactionId = transactionId,
    paymentMethod = paymentMethod
)

data class VerificationEmailRequest(
    val email: String = ""
)

data class CreateWidgetTransactionRequest(
    val partnerId: String,
    val currencyFrom: String,
    val currencyTo: String,
    val amountFiat: BigDecimal
)

data class CreateWidgetTransactionResponse(
    val id: Long,
)

data class WidgetTransactionResponse(
    val id: Long,
    val partnerName: String,
    val amount: BigDecimal,
    val sourceCurrency: String,
    val targetCurrency: String,
    val rate: BigDecimal,
    val commission: BigDecimal,
    val totalToPay: BigDecimal,
)

fun Transaction.toWidgetResponse(): WidgetTransactionResponse {
    return WidgetTransactionResponse(
        id = this.id ?: error("Transaction id is null"),
        partnerName = this.partnerId,
        amount = this.amountAfterFee,
        sourceCurrency = this.currencyFrom,
        targetCurrency = this.currencyTo,
        rate = this.nbpRate,
        commission = this.totalCommission,
        totalToPay = this.amountToPay,
    )
}

data class PaymentMethodResponse(
    val id: String,
    val name: String,
    val description: String,
)

data class CreatePaymentRequest(
    val paymentMethodId: String,
)

data class PaymentRedirectResponse(
    val redirectUrl: String,
)

data class CalculateWidgetResponse(
    val amount: BigDecimal,          // kwota wejściowa (amountFiat)
    val sourceCurrency: String,
    val targetCurrency: String,
    val rate: BigDecimal,
    val commission: BigDecimal,      // partnerFee + platformFee
    val totalToPay: BigDecimal,      // amountAfterFee
    val amountToReceive: BigDecimal, // convertedAmount – ile waluty docelowej dostanie user
)
