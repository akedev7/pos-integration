package com.akedev7.pos.delivery.grpc


import com.akedev7.pos.controller.Payment
import com.akedev7.pos.controller.PaymentServiceGrpcKt
import com.akedev7.pos.domain.toDomainObject
import com.akedev7.pos.repository.PaymentDAO
import com.akedev7.pos.repository.PaymentRepository
import com.akedev7.pos.rule_engine.PaymentRuleEngine
import org.springframework.grpc.server.service.GrpcService
import java.time.OffsetDateTime

@GrpcService
class PaymentGrpcService(val paymentRuleEngine: PaymentRuleEngine, val paymentRepository: PaymentRepository) : PaymentServiceGrpcKt.PaymentServiceCoroutineImplBase() {
    override suspend fun processPayment(request: Payment.PaymentRequest): Payment.PaymentResponse {
        val toDomainObject = request.toDomainObject()
        val paymentCalculationResult = paymentRuleEngine.getPaymentCalculationResult(toDomainObject)

        val paymentDAO = PaymentDAO(
            toDomainObject.customerId,
            toDomainObject.price,
            toDomainObject.priceModifier,
            toDomainObject.paymentMethod,
            OffsetDateTime.now(),
            toDomainObject.additionalItem
        )
        paymentRepository.insertPayment(paymentDAO)
        val response = Payment.PaymentResponse.newBuilder()
            .setFinalPrice(paymentCalculationResult.defaultPoints.toDouble())
            .setPoints(paymentCalculationResult.ruleAdjustedPoints.toInt())
            .build()

      return response
    }
}
