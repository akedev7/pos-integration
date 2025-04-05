package com.akedev7.pos.adapters.grpc.payment


import com.akedev7.pos.adapters.grpc.payment.validation.PaymentRequestValidator
import com.akedev7.pos.adapters.grpc.payment.validation.PaymentValidationException
import com.akedev7.pos.adapters.grpc.protobuf.Payment
import com.akedev7.pos.adapters.grpc.protobuf.PaymentServiceGrpcKt
import com.akedev7.pos.application.service.PaymentService
import com.akedev7.pos.application.utils.toOffsetDateTime
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.google.protobuf.util.JsonFormat
import com.google.type.Decimal
import net.devh.boot.grpc.server.service.GrpcService
import java.math.BigDecimal

@GrpcService
class PaymentGrpcService(
    private val validator: PaymentRequestValidator,
    private val paymentService: PaymentService
) : PaymentServiceGrpcKt.PaymentServiceCoroutineImplBase() {

    companion object {
        val objectMapper = jacksonObjectMapper()
    }

    override suspend fun processPayment(request: Payment.PaymentRequest): Payment.PaymentResponse {
        val payment = request.toDomainObject()
        val paymentResult = paymentService.getPaymentResult(payment)
        return Payment.PaymentResponse.newBuilder()
            .setFinalPrice(paymentResult.finalPrice.toProtoDecimal())
            .setPoints(paymentResult.point.toProtoDecimal())
            .build()
    }

    fun Payment.PaymentRequest.toDomainObject(): com.akedev7.pos.domain.model.Payment {
        when (val validation = validator.validate(this)) {
            is PaymentRequestValidator.ValidationResult.Invalid -> {
                throw PaymentValidationException(validation.errors)
            }

            PaymentRequestValidator.ValidationResult.Valid -> {
                return com.akedev7.pos.domain.model.Payment(
                    customerId = this.customerId.toLong(),
                    price = this.price.toBigDecimal(),
                    priceModifier = this.priceModifier.toBigDecimal(),
                    paymentMethod = this.paymentMethod,
                    datetime = this.datetime.toOffsetDateTime(),
                    additionalItem = objectMapper.readTree(
                        JsonFormat.printer().print(this.additionalItem)
                    )
                )

            }
        }
    }

    fun BigDecimal.toProtoDecimal(): Decimal {
        return Decimal.newBuilder()
            .setValue(this.toPlainString())
            .build()
    }

    private fun Decimal.toBigDecimal(): BigDecimal {
        return BigDecimal(this.value)
    }
}
