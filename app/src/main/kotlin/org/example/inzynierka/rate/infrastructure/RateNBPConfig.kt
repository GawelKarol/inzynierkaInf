package org.example.inzynierka.rate.infrastructure

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
open class RateNBPConfig {

    @Bean
    open fun restNBPClientRateProvider(restTemplate: RestTemplate) = RestNBPClientRateProvider(restTemplate)
}

