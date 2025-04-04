package com.akedev7.pos.adapters.grpc.payment.validation

import com.akedev7.pos.adapters.grpc.protobuf.Payment
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class PaymentRequestValidator(
    @Value("\${payment.max-price-modifier}") private val maxPriceModifier: BigDecimal
) {
    fun validate(request: Payment.PaymentRequest): ValidationResult {
        val errors = mutableListOf<String>()

        if (request.customerId.isBlank()) {
            errors.add("Customer ID cannot be blank")
        } else if (!request.customerId.matches(Regex("\\d+"))) {
            errors.add("Customer ID must contain only digits")
        }

        if (request.price.value.isEmpty()) {
            errors.add("Price must be specified")
        } else if (request.price.value.toBigDecimal() <= BigDecimal.ZERO) {
            errors.add("Price must be positive")
        }

        if (request.priceModifier.value.isEmpty()) {
            errors.add("Price modifier must be specified")
        } else if (request.priceModifier.value.toBigDecimal() <= BigDecimal.ZERO) {
            errors.add("Price modifier must be positive")
        } else if (request.priceModifier.value.toBigDecimal() > maxPriceModifier) {
            errors.add("Price modifier cannot be greater than $maxPriceModifier")
        }

        if (request.paymentMethod.isBlank()) {
            errors.add("Payment method cannot be blank")
        }

        if (request.datetime.toString().isEmpty()) {
            errors.add("Datetime must be specified")
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }

    sealed class ValidationResult {
        data object Valid : ValidationResult()
        data class Invalid(val errors: List<String>) : ValidationResult()
    }
}