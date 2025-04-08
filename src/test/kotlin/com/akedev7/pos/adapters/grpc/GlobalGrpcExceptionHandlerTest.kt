package com.akedev7.pos.adapters.grpc

import com.akedev7.pos.adapters.grpc.payment.validation.PaymentValidationException
import com.google.rpc.Code
import com.google.rpc.ErrorInfo
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class GlobalGrpcExceptionHandlerTest {

    @InjectMocks
    private lateinit var exceptionHandler: GlobalGrpcExceptionHandler

    @Test
    fun `handleIllegalArgument should return INVALID_ARGUMENT status`() {
        // Given
        val ex = IllegalArgumentException("Test illegal argument")

        // When
        val result = exceptionHandler.handleIllegalArgument(ex)

        // Then
        assertEquals(Code.INVALID_ARGUMENT.number, getStatusCode(result))
        assertTrue(getErrorDetails(result).contains("Test illegal argument"))
    }

    @Test
    fun `handlePaymentValidation should return INVALID_ARGUMENT status`() {
        // Given
        val ex = PaymentValidationException(listOf("erro1", "error2"))

        // When
        val result = exceptionHandler.handlePaymentValidation(ex)

        // Then
        assertEquals(Code.INVALID_ARGUMENT.number, getStatusCode(result))
        kotlin.test.assertEquals("{\"error\": [\"erro1\", \"error2\"]}", getErrorDetails(result))
    }

    @Test
    fun `handleGeneric should return INTERNAL status`() {
        // Given
        val ex = Exception("Test generic error")

        // When
        val result = exceptionHandler.handleGeneric(ex)

        // Then
        assertEquals(Code.INTERNAL.number, getStatusCode(result))
        kotlin.test.assertEquals("Test generic error", getErrorDetails(result))
    }

    private fun getStatusCode(exception: StatusRuntimeException): Int {
        val statusProto = StatusProto.fromThrowable(exception)
        return statusProto?.code ?: -1
    }

    private fun getErrorDetails(exception: StatusRuntimeException): String {
        val statusProto = StatusProto.fromThrowable(exception)
        val details = statusProto?.detailsList?.firstOrNull()?.unpack(ErrorInfo::class.java)
        return details?.metadataMap?.get("details") ?: ""
    }
}