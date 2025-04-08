package com.akedev7.pos.adapters.grpc.validation

import com.akedev7.pos.adapters.grpc.payment.validation.PaymentRequestValidator
import com.akedev7.pos.adapters.grpc.protobuf.Payment
import com.google.protobuf.Struct
import com.google.protobuf.Timestamp
import com.google.type.Decimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.time.Instant

@ExtendWith(MockitoExtension::class)
class PaymentRequestValidatorTest {

    private lateinit var validator: PaymentRequestValidator

    @BeforeEach
    fun beforeEach() {
        validator = PaymentRequestValidator()
    }

    @Test
    fun `validate returns Valid for correct request`() {
        val request = createValidPaymentRequest()
        val result = validator.validate(request)
        assertTrue(result is PaymentRequestValidator.ValidationResult.Valid)
    }

    @Test
    fun `validate returns Invalid when customerId is blank`() {
        val request = createValidPaymentRequest().toBuilder().setCustomerId(" ").build()
        val result = validator.validate(request)
        assertValidationError(result, "Customer ID cannot be blank")
    }

    @Test
    fun `validate returns Invalid when customerId contains non-digits`() {
        val request = createValidPaymentRequest().toBuilder().setCustomerId("abc123").build()
        val result = validator.validate(request)
        assertValidationError(result, "Customer ID must contain only digits")
    }

    @Test
    fun `validate returns Invalid when price is empty`() {
        val request = createValidPaymentRequest().toBuilder()
            .setPrice(Decimal.getDefaultInstance())
            .build()
        val result = validator.validate(request)
        assertValidationError(result, "Price must be specified")
    }

    @Test
    fun `validate returns Invalid when price is zero or negative`() {
        val requestZero = createValidPaymentRequest().toBuilder()
            .setPrice(Decimal.newBuilder().setValue("0").build())
            .build()
        val resultZero = validator.validate(requestZero)
        assertValidationError(resultZero, "Price must be positive")

        val requestNegative = createValidPaymentRequest().toBuilder()
            .setPrice(Decimal.newBuilder().setValue("-10").build())
            .build()
        val resultNegative = validator.validate(requestNegative)
        assertValidationError(resultNegative, "Price must be positive")
    }

    @Test
    fun `validate returns Invalid when priceModifier is empty`() {
        val request = createValidPaymentRequest().toBuilder()
            .setPriceModifier(Decimal.getDefaultInstance())
            .build()
        val result = validator.validate(request)
        assertValidationError(result, "Price modifier must be specified")
    }

    @Test
    fun `validate returns Invalid when priceModifier is zero or negative`() {
        val requestZero = createValidPaymentRequest().toBuilder()
            .setPriceModifier(Decimal.newBuilder().setValue("0").build())
            .build()
        val resultZero = validator.validate(requestZero)
        assertValidationError(resultZero, "Price modifier must be positive")

        val requestNegative = createValidPaymentRequest().toBuilder()
            .setPriceModifier(Decimal.newBuilder().setValue("-0.5").build())
            .build()
        val resultNegative = validator.validate(requestNegative)
        assertValidationError(resultNegative, "Price modifier must be positive")
    }

    @Test
    fun `validate returns Invalid when paymentMethod is blank`() {
        val request = createValidPaymentRequest().toBuilder().setPaymentMethod(" ").build()
        val result = validator.validate(request)
        assertValidationError(result, "Payment method cannot be blank")
    }

    @Test
    fun `validate returns Invalid when datetime is not set`() {
        val request = createValidPaymentRequest().toBuilder().clearDatetime().build()
        val result = validator.validate(request)
        assertValidationError(result, "Datetime must be specified")
    }

    @Test
    fun `validate is successful when additionalItem is present`() {
        val request = createValidPaymentRequest().toBuilder()
            .setAdditionalItem(Struct.newBuilder().build())
            .build()
        val result = validator.validate(request)
        assertTrue(result is PaymentRequestValidator.ValidationResult.Valid)
    }

    @Test
    fun `validate is successful when additionalItem is not present`() {
        val request = createValidPaymentRequest().toBuilder()
            .clearAdditionalItem()
            .build()
        val result = validator.validate(request)
        assertTrue(result is PaymentRequestValidator.ValidationResult.Valid)
    }

    @Test
    fun `validate returns multiple errors when multiple fields are invalid`() {
        val request = createValidPaymentRequest().toBuilder()
            .setCustomerId("")
            .setPrice(Decimal.getDefaultInstance())
            .setPaymentMethod(" ")
            .build()
        val result = validator.validate(request)
        assertTrue(result is PaymentRequestValidator.ValidationResult.Invalid)
        val errors = (result as PaymentRequestValidator.ValidationResult.Invalid).errors
        assertEquals(3, errors.size)
        assertTrue(errors.contains("Customer ID cannot be blank"))
        assertTrue(errors.contains("Price must be specified"))
        assertTrue(errors.contains("Payment method cannot be blank"))
    }

    private fun createValidPaymentRequest(): Payment.PaymentRequest {
        return Payment.PaymentRequest.newBuilder()
            .setCustomerId("12345")
            .setPrice(Decimal.newBuilder().setValue("100.00").build())
            .setPriceModifier(Decimal.newBuilder().setValue("1.2").build())
            .setPaymentMethod("CARD")
            .setDatetime(
                Timestamp.newBuilder()
                    .setSeconds(Instant.now().epochSecond)
                    .build()
            )
            .build()
    }

    private fun assertValidationError(
        result: PaymentRequestValidator.ValidationResult,
        expectedError: String
    ) {
        assertTrue(result is PaymentRequestValidator.ValidationResult.Invalid)
        val errors = (result as PaymentRequestValidator.ValidationResult.Invalid).errors
        assertTrue(errors.contains(expectedError), "Expected error '$expectedError' not found in $errors")
    }
}
