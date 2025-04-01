package com.akedev7.pos.domain


import com.google.protobuf.Timestamp
import java.math.BigDecimal
import com.akedev7.pos.controller.Payment.PaymentRequest

data class Payment(
    val customerId: String,
    val price: BigDecimal,
    val priceModifier: Double,
    val paymentMethod: String,
    val datetime: Timestamp,
    val additionalItem: Map<String, String>
)

// Converts gRPC objects to domain objects
fun PaymentRequest.toDomainObject(): Payment {
    return Payment(
        customerId = this.customerId,
        price = BigDecimal(this.price),
        priceModifier = this.priceModifier,
        paymentMethod = this.paymentMethod,
        datetime = this.datetime,
        additionalItem = this.additionalItemMap
    )
}