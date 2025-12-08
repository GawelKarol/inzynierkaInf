package org.example.inzynierka.payment.application

import org.example.inzynierka.payment.domain.PaymentGateway
import org.example.inzynierka.payment.domain.PaymentInitDto
import org.example.inzynierka.payment.domain.PaymentInitResultDto
import org.example.inzynierka.transaction.domain.Transaction
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
open class PaymentService(
    private val paymentGateway: PaymentGateway,
    @Value("\${payment.return-url}") private val returnUrlTemplate: String,
    @Value("\${payment.notify-url}") private val notifyUrlTemplate: String
) {

    open fun startPayment(transaction: Transaction): PaymentInitResultDto {
        val returnUrl = returnUrlTemplate.replace("{transactionId}", transaction.id.toString())
        val notifyUrl = notifyUrlTemplate.replace("{transactionId}", transaction.id.toString())

        val request = PaymentInitDto(
            transactionId = transaction.id!!,
            amount = transaction.amountFiat,
            currency = transaction.currencyTo,
            paymentMethod = transaction.paymentMethod
                ?: throw IllegalStateException("Payment method not set for transaction ${transaction.id}"),
            returnUrl = returnUrl,
            notifyUrl = notifyUrl
        )

        return paymentGateway.initPayment(request)
    }
}
