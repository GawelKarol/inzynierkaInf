package org.example.inzynierka.transaction.domain

import java.math.BigDecimal
import java.time.LocalDateTime

class Transaction(
    val id: Long? = null,

    // z DDL
    val partnerId: String,
    val currencyFrom: String,
    val currencyTo: String,

    val amountFiat: BigDecimal,
    val amountToPay: BigDecimal,
    val partnerFee: BigDecimal,
    val platformFee: BigDecimal,
    val amountAfterFee: BigDecimal,
    val totalCommission: BigDecimal,
    val nbpRate: BigDecimal,
    val convertedAmount: BigDecimal,

    var status: TransactionStatus = TransactionStatus.CREATED,

    var userEmail: String? = null,
    var verificationCode: String? = null,
    var verificationExpires: LocalDateTime? = null,

    var paymentMethod: String? = null,
    var webhookUrl: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    var resendCount: Int = 0,
    var externalPaymentId: String? = null,
)
