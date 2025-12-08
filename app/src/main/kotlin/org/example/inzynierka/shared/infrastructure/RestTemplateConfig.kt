package org.example.inzynierka.shared.infrastructure

import org.example.inzynierka.rate.infrastructure.RestNBPClientRateProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
open class RestTemplateConfig {

    @Bean
    open fun restTemplate(): RestTemplate = RestTemplate()
}

