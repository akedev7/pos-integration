package com.akedev7.pos.adapters.grpc


import com.akedev7.pos.controller.Payment
import com.akedev7.pos.controller.SalesServiceGrpcKt
import com.akedev7.pos.adapters.postgres.SalesRepository
import com.akedev7.pos.application.service.SalesService
import com.akedev7.pos.application.utils.toOffsetDateTime
import com.akedev7.pos.domain.model.SalesSummary
import com.google.protobuf.Timestamp
import com.google.type.Decimal
import org.springframework.grpc.server.service.GrpcService
import java.time.Instant
import java.time.OffsetDateTime

@GrpcService
class SalesGrpcService(private val salesService: SalesService) : SalesServiceGrpcKt.SalesServiceCoroutineImplBase() {
    override suspend fun getSalesData(request: Payment.SalesDataRequest): Payment.SalesDataResponse {
        val salesSummary: SalesSummary = salesService.getSalesSummary(
            request.startDateTime.toOffsetDateTime(),
            request.endDateTime.toOffsetDateTime()
        )
        return convertJooqResultToGrpcResponse(salesSummary)
    }

    fun convertJooqResultToGrpcResponse(salesSummary: SalesSummary): Payment.SalesDataResponse {
        val builder = Payment.SalesDataResponse.newBuilder()

        salesSummary.sales.forEach { record ->
            val salesRecord = Payment.SalesRecord.newBuilder().apply {
                datetime = record.datetime.toProtoTimestamp()
                sales = Decimal.newBuilder()
                    .setValue(record.sales.toPlainString())
                    .build()
                points = record.points
            }.build()
            builder.addSales(salesRecord)
        }
        return builder.build()
    }

    fun OffsetDateTime.toProtoTimestamp(): Timestamp {
        val instant: Instant = this.toInstant()
        return Timestamp.newBuilder()
            .setSeconds(instant.epochSecond)  // Seconds since Unix epoch
            .setNanos(instant.nano)           // Nanoseconds adjustment
            .build()
    }
}
