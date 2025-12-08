package org.example.inzynierka.commission.application

import org.example.inzynierka.commission.domain.CommissionCalculatorFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class CommissionConfig {

    @Bean
    open fun commissionCalculatorFactory() = CommissionCalculatorFactory()
}