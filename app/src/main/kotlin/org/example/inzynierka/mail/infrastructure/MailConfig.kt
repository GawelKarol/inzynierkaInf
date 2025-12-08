package org.example.inzynierka.mail.infrastructure

import org.example.inzynierka.mail.domain.MailSenderAdapter
import org.example.inzynierka.mail.domain.TransactionMailSender
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring6.SpringTemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.springframework.mail.javamail.JavaMailSender
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver

@Configuration
open class MailConfig(
    private val applicationContext: ApplicationContext
) {

    @Bean
    open fun emailTemplateEngine(emailMessageSource: ResourceBundleMessageSource): SpringTemplateEngine =
        SpringTemplateEngine().apply {
            setTemplateResolver(htmlTemplateResolver())   // uwaga: NIE addTemplateResolver
            setTemplateEngineMessageSource(emailMessageSource)
        }

    private fun htmlTemplateResolver(): SpringResourceTemplateResolver =
        SpringResourceTemplateResolver().apply {
            setApplicationContext(applicationContext)
            prefix = "classpath:/templates/mail/"
            suffix = ".html"
            templateMode = TemplateMode.HTML
            characterEncoding = "UTF-8"
            isCacheable = false
        }

    @Bean
    open fun emailMessageSource(): ResourceBundleMessageSource =
        ResourceBundleMessageSource().apply {
            setBasename("templates/mail/messages")
            setDefaultEncoding("UTF-8")
            setFallbackToSystemLocale(false)
        }

    @Bean
    open fun mailSenderAdapter(javaMailSender: JavaMailSender): MailSenderAdapter =
        DefaultMailSenderAdapter(javaMailSender)

    @Bean
    open fun transactionMailSender(
        mailSenderAdapter: MailSenderAdapter,
        templateEngine: SpringTemplateEngine,
        @Value("\${spring.mail.from}") mailFrom: String
    ): TransactionMailSender =
        DefaultTransactionMailSender(
            mailSenderAdapter = mailSenderAdapter,
            templateEngine = templateEngine,
            mailFrom = mailFrom
        )
}

