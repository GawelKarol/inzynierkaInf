package org.example.inzynierka.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
open class SecurityConfig {

    @Bean
    open fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors { } // UŻYJ konfiguracji z CorsConfig
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/api/widget/**",
                        "/transactions/**"
                    ).permitAll()     // nasze endpointy dla SDK
                    .anyRequest().permitAll()   // na razie otwórz wszystko
            }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }

        return http.build()
    }
}
