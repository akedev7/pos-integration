package com.akedev7.pos.domain.model


import com.google.protobuf.Struct
import java.math.BigDecimal
import java.time.OffsetDateTime

data class Payment(
    val customerId: Long,
    val price: BigDecimal,
    val priceModifier: BigDecimal,
    val paymentMethod: String,
    val datetime: OffsetDateTime,
    val additionalItem: Struct
)

data class PaymentResult(
    val finalPrice: BigDecimal,
    val point: BigDecimal
)

