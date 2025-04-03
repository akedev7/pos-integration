package com.akedev7.pos.delivery.grpc


import com.akedev7.pos.controller.Payment
import com.akedev7.pos.controller.SalesServiceGrpcKt
import com.akedev7.pos.domain.toDomainObject
import com.akedev7.pos.repository.PaymentRepository
import com.google.protobuf.Timestamp
import com.google.type.Decimal
import org.jooq.Record3
import org.springframework.grpc.server.service.GrpcService
import java.math.BigDecimal
import java.time.OffsetDateTime

@GrpcService
class SalesGrpcService(val paymentRepository: PaymentRepository) : SalesServiceGrpcKt.SalesServiceCoroutineImplBase() {
    override suspend fun getSalesData(request: Payment.SalesDataRequest): Payment.SalesDataResponse {
        val salesDomainObject = request.toDomainObject()
        val salesSummary: org.jooq.Result<Record3<OffsetDateTime, BigDecimal, Int>> = paymentRepository.getSales(salesDomainObject)
        return convertJooqResultToGrpcResponse(salesSummary)
    }

    fun convertJooqResultToGrpcResponse(jooqResult: org.jooq.Result<Record3<OffsetDateTime, BigDecimal, Int>>): Payment.SalesDataResponse {
        val builder = Payment.SalesDataResponse.newBuilder()

        jooqResult.forEach { record ->
            val salesRecord = Payment.SalesRecord.newBuilder().apply {
                datetime = Timestamp.newBuilder()
                    .setSeconds(record.value1().toEpochSecond())
                    .setNanos(record.value1().nano)
                    .build()
                sales = Decimal.newBuilder()
                    .setValue(record.value2().toString())
                    .build()
                points = record.value3()
            }.build()
            builder.addSales(salesRecord)
        }

        return builder.build()
    }
}
