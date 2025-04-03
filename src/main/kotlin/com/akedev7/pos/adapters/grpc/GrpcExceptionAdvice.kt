package com.akedev7.pos.adapters.grpc

import io.grpc.Status
import io.grpc.StatusException
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler

@GrpcAdvice
class GrpcExceptionAdvice {
    @GrpcExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): StatusException {
        val status = Status.INTERNAL.withDescription(ex.message).withCause(ex)
        return status.asException()
    }
}