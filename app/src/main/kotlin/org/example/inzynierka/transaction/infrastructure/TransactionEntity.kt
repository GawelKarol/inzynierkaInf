package org.example.inzynierka.transaction.infrastructure

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import org.example.inzynierka.transaction.domain.TransactionStatus

@Entity
@Table(name = "transaction")
class TransactionEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "partner_id", nullable = false, length = 255)
    lateinit var partnerId: String

    @Column(name = "currency_from", nullable = false, length = 3)
    lateinit var currencyFrom: String

    @Column(name = "currency_to", nullable = false, length = 3)
    lateinit var currencyTo: String

    @Column(name = "amount_fiat", nullable = false, precision = 18, scale = 6)
    lateinit var amountFiat: BigDecimal

    @Column(name = "amount_to_pay", nullable = false, precision = 18, scale = 6)
    lateinit var amountToPay: BigDecimal

    @Column(name = "partner_fee", nullable = false, precision = 18, scale = 6)
    lateinit var partnerFee: BigDecimal

    @Column(name = "platform_fee", nullable = false, precision = 18, scale = 6)
    lateinit var platformFee: BigDecimal

    @Column(name = "total_commission", nullable = false, precision = 18, scale = 6)
    lateinit var totalCommission: BigDecimal

    @Column(name = "amount_after_fee", nullable = false, precision = 18, scale = 6)
    lateinit var amountAfterFee: BigDecimal

    @Column(name = "nbp_rate", nullable = false, precision = 18, scale = 6)
    lateinit var nbpRate: BigDecimal

    @Column(name = "converted_amount", nullable = false, precision = 18, scale = 6)
    lateinit var convertedAmount: BigDecimal

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    lateinit var status: TransactionStatus

    @Column(name = "user_email", length = 255)
    var userEmail: String? = null

    @Column(name = "verification_code", length = 6)
    var verificationCode: String? = null

    @Column(name = "verification_expires")
    var verificationExpires: LocalDateTime? = null

    @Column(name = "payment_method", length = 50)
    var paymentMethod: String? = null

    @Column(name = "resend_count", nullable = false)
    var resendCount: Int = 0

    @Column(name = "external_payment_id", length = 255)
    var externalPaymentId: String? = null

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
}
