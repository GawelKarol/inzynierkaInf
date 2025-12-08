package org.example.inzynierka.transaction.domain

enum class WebhookEvent {
    PAYMENT_START, // transakcja zgłoszona do płatności
    COMPLETED,     // transakcja zakończona sukcesem
    CANCELLED      // transakcja anulowana / błąd
}
