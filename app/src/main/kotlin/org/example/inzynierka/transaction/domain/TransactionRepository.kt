package org.example.inzynierka.transaction.domain

interface TransactionRepository {
    fun save(transaction: Transaction): Transaction
    fun findById(id: Long): Transaction?
    fun findByExternalPaymentId(externalPaymentId: String): Transaction?
}
