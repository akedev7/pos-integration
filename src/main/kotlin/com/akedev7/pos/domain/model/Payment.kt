package com.akedev7.pos.domain.model


import com.google.protobuf.Struct
import com.google.protobuf.Timestamp
import java.math.BigDecimal

data class Payment(
    val customerId: Long,
    val price: BigDecimal,
    val priceModifier: BigDecimal,
    val paymentMethod: String,
    val datetime: Timestamp,
    val additionalItem: Struct
)

data class PaymentResult(
    val finalPrice: BigDecimal,
    val point: BigDecimal
)

