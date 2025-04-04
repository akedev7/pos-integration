package com.akedev7.pos.adapters.grpc

import com.akedev7.pos.adapters.grpc.payment.validation.PaymentValidationException
import com.google.rpc.Code
import com.google.rpc.ErrorInfo
import com.google.rpc.Status as RpcStatus
import com.google.protobuf.Any
import io.grpc.StatusRuntimeException
import io.grpc.protobuf.StatusProto
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler
import org.slf4j.LoggerFactory
import java.util.logging.Logger

@GrpcAdvice
class GlobalGrpcExceptionHandler {

    data class ErrorResponse(val error: String)

    @GrpcExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): StatusRuntimeException {
        log.error(ex.stackTrace.toString())
        val errorInfo = ErrorInfo.newBuilder()
            .setReason("INVALID_ARGUMENT")
            .putMetadata("details", ex.message)
            .build()

        val status = RpcStatus.newBuilder()
            .setCode(Code.INVALID_ARGUMENT.number)
            .setMessage("Invalid input")
            .addDetails(Any.pack(errorInfo))
            .build()

        return StatusProto.toStatusRuntimeException(status)
    }

    @GrpcExceptionHandler(PaymentValidationException::class)
    fun handlePaymentValidation(ex: PaymentValidationException): StatusRuntimeException {
        log.error(ex.stackTrace.toString())
        val errorInfo = ErrorInfo.newBuilder()
            .setReason("INVALID_ARGUMENT")
            .putMetadata("details", ex.message)
            .build()

        val status = RpcStatus.newBuilder()
            .setCode(Code.INVALID_ARGUMENT.number)
            .setMessage("Invalid input")
            .addDetails(Any.pack(errorInfo))
            .build()

        return StatusProto.toStatusRuntimeException(status)
    }

    @GrpcExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): StatusRuntimeException {
        log.error(ex.stackTrace.toString())
        val errorMessage = ErrorResponse(error = "Unexpected server error: ${ex.message}")

        val errorInfo = ErrorInfo.newBuilder()
            .setReason("INTERNAL")
            .putMetadata("details", errorMessage.error)
            .build()

        val status = RpcStatus.newBuilder()
            .setCode(Code.INTERNAL.number)
            .setMessage("Unexpected error")
            .addDetails(Any.pack(errorInfo))
            .build()

        return StatusProto.toStatusRuntimeException(status)
    }
    companion object {
        private val log = LoggerFactory.getLogger(GlobalGrpcExceptionHandler::class.java)
    }
}