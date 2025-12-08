package org.example.inzynierka.transaction.infrastructure

import org.example.inzynierka.transaction.domain.Transaction
import org.example.inzynierka.transaction.domain.TransactionRepository
import org.example.inzynierka.transaction.domain.TransactionStatus
import org.springframework.stereotype.Repository

@Repository
open class TransactionRepositoryImpl(
    private val jpa: JpaTransactionRepository
) : TransactionRepository {

    override fun save(transaction: Transaction): Transaction {
        val entity = TransactionEntity().apply {
            id = transaction.id
            partnerId = transaction.partnerId
            currencyFrom = transaction.currencyFrom
            currencyTo = transaction.currencyTo
            amountFiat = transaction.amountFiat
            amountToPay = transaction.amountToPay
            partnerFee = transaction.partnerFee
            platformFee = transaction.platformFee
            totalCommission = transaction.totalCommission
            amountAfterFee = transaction.amountAfterFee
            nbpRate = transaction.nbpRate
            convertedAmount = transaction.convertedAmount
            status = transaction.status
            userEmail = transaction.userEmail
            verificationCode = transaction.verificationCode
            verificationExpires = transaction.verificationExpires
            paymentMethod = transaction.paymentMethod
            resendCount = transaction.resendCount
            externalPaymentId = transaction.externalPaymentId
            createdAt = transaction.createdAt
            updatedAt = transaction.updatedAt
        }

        val saved = jpa.save(entity)
        return saved.toDomain()
    }



    override fun findById(id: Long): Transaction? =
        jpa.findById(id).orElse(null)?.toDomain()

    override fun findByExternalPaymentId(externalPaymentId: String): Transaction? =
        jpa.findByExternalPaymentId(externalPaymentId)?.toDomain()

    private fun TransactionEntity.toDomain() = Transaction(
        id = id,
        partnerId = partnerId,
        currencyFrom = currencyFrom,
        currencyTo = currencyTo,
        amountFiat = amountFiat,
        amountToPay = amountToPay,
        partnerFee = partnerFee,
        platformFee = platformFee,
        amountAfterFee = amountAfterFee,
        nbpRate = nbpRate,
        convertedAmount = convertedAmount,
        totalCommission = totalCommission,
        status = status,
        userEmail = userEmail,
        verificationCode = verificationCode,
        verificationExpires = verificationExpires,
        paymentMethod = paymentMethod,
        createdAt = createdAt,
        updatedAt = updatedAt,
        resendCount = resendCount,
        externalPaymentId = externalPaymentId,
    )

}
