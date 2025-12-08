package org.example.inzynierka.rate.infrastructure


import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.example.inzynierka.rate.application.RateRefreshService

@Component
class RateScheduler(
    private val rateRefreshService: RateRefreshService
) {
    @Scheduled(cron = "0 0 */2 * * *")
    fun refresh() {
        println("Running scheduler...")
        rateRefreshService.refreshNbpRates()
    }
}
