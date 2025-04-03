package com.akedev7.pos.adapters.grpc


import com.akedev7.pos.controller.Payment
import com.akedev7.pos.controller.PaymentServiceGrpcKt
import com.akedev7.pos.application.service.PaymentService
import com.akedev7.pos.application.utils.toBigDecimal
import net.devh.boot.grpc.server.service.GrpcService

@GrpcService
class PaymentGrpcService(private val paymentService: PaymentService) : PaymentServiceGrpcKt.PaymentServiceCoroutineImplBase() {
    override suspend fun processPayment(request: Payment.PaymentRequest): Payment.PaymentResponse {
        val payment = request.toDomainObject()
        val paymentResult = paymentService.getPaymentResult(payment)
        return Payment.PaymentResponse.newBuilder()
            .setFinalPrice(paymentResult.finalPrice.toDouble())
            .setPoints(paymentResult.point.toInt())
            .build()
    }

    fun Payment.PaymentRequest.toDomainObject(): com.akedev7.pos.domain.model.Payment {
        return com.akedev7.pos.domain.model.Payment(
            customerId = this.customerId.toLong(),
            price = this.price.toBigDecimal(),
            priceModifier = this.priceModifier.toBigDecimal(),
            paymentMethod = this.paymentMethod,
            datetime = this.datetime,
            additionalItem = this.additionalItem
        )
    }
}
