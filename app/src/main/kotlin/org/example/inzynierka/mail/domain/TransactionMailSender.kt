package org.example.inzynierka.mail.domain

import org.example.inzynierka.mail.application.TransactionEmailData

interface TransactionMailSender {

    fun sendVerificationCode(email: String, code: String)

    fun sendPaymentStarted(data: TransactionEmailData)

    fun sendPaymentCompleted(data: TransactionEmailData)
}
