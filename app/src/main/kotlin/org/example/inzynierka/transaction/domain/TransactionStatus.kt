package org.example.inzynierka.transaction.domain

enum class TransactionStatus {
    CREATED,             // transakcja utworzona
    VERIFICATION_SENT,   // wysłano kod weryfikacyjny
    VERIFIED,            // kod poprawnie zweryfikowany
    SUBMITTED,           // dane potwierdzone, wysłano do płatności
    WAITING_FOR_PAYMENT, // czekamy na wynik płatności
    COMPLETED,           // płatność przeszła, wszystko zakończone
    CANCELED             // coś poszło nie tak / anulowano
}
