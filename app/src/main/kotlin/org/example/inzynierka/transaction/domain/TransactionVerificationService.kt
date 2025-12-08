package org.example.inzynierka.transaction.domain

import kotlin.random.Random

open class TransactionVerificationService {

    fun generateCode(): String =
        Random.nextInt(100000, 999999).toString()

    fun validateCode(transaction: Transaction, code: String): Boolean =
        transaction.verificationCode == code
}
