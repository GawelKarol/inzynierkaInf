package org.example.inzynierka.transaction.infrastructure

import org.example.inzynierka.transaction.application.WidgetInitService
import org.example.inzynierka.transaction.application.api.WidgetInitRequest
import org.example.inzynierka.transaction.application.api.WidgetInitResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/widget")
class WidgetController(
    private val widgetInitService: WidgetInitService,
) {

    @PostMapping("/init")
    fun initWidget(@RequestBody request: WidgetInitRequest): ResponseEntity<WidgetInitResponse> {
        val result = widgetInitService.initWidget(request)
        return ResponseEntity.ok(result)
    }
}
