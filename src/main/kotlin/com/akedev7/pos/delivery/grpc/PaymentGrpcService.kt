package com.akedev7.pos.delivery.grpc

import com.akedev7.pos.controller.PaymentRequest
import com.akedev7.pos.controller.PaymentResponse
import com.akedev7.pos.controller.PaymentServiceGrpcKt
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class PaymentGrpcService : PaymentServiceGrpcKt.PaymentServiceCoroutineImplBase() {
    override suspend fun processPayment(request: PaymentRequest): PaymentResponse {
        val finalPrice = request.price * request.priceModifier
        val points = (finalPrice * 0.05).toInt()

        val response = PaymentResponse.newBuilder()
            .setFinalPrice(finalPrice)
            .setPoints(points)
            .build()

      return response
    }

}
