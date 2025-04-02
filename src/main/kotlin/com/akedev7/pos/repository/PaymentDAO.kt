package com.akedev7.pos.repository

import com.google.protobuf.Struct
import java.math.BigDecimal
import java.time.OffsetDateTime

data class PaymentDAO(
    val customerId: Long,
    val price: BigDecimal,
    val priceModifier: BigDecimal,
    val paymentMethod: String,
    val datetime: OffsetDateTime,
    val metadata: Struct,
)