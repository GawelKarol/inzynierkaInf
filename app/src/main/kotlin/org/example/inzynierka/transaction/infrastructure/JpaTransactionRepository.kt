package org.example.inzynierka.transaction.infrastructure

import org.springframework.data.jpa.repository.JpaRepository

interface JpaTransactionRepository : JpaRepository<TransactionEntity, Long> {

    fun findByExternalPaymentId(externalPaymentId: String): TransactionEntity?
}
