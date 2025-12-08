package org.example.inzynierka.transaction.application

import org.example.inzynierka.transaction.domain.TransactionVerificationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class TransactionConfig {

    @Bean
    open fun transactionVerificationService() = TransactionVerificationService()
}