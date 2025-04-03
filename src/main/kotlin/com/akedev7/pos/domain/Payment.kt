package com.akedev7.pos.domain


import com.akedev7.pos.controller.Payment.PaymentRequest
import com.akedev7.pos.utils.toBigDecimal
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

// Converts gRPC objects to domain objects
fun PaymentRequest.toDomainObject(): Payment {
    return Payment(
        customerId = this.customerId.toLong(),
        price = this.price.toBigDecimal(),
        priceModifier = this.priceModifier.toBigDecimal(),
        paymentMethod = this.paymentMethod,
        datetime = this.datetime,
        additionalItem = this.additionalItem
    )
}