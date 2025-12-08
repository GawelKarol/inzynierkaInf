package org.example.inzynierka.payment.domain

interface PaymentGateway {
    fun initPayment(request: PaymentInitDto): PaymentInitResultDto
}
