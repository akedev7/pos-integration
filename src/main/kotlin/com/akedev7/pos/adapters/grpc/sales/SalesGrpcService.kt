package com.akedev7.pos.adapters.grpc.sales

import com.akedev7.pos.adapters.grpc.protobuf.Payment
import com.akedev7.pos.adapters.grpc.protobuf.SalesServiceGrpcKt
import com.akedev7.pos.application.service.SalesService
import com.akedev7.pos.application.utils.toOffsetDateTime
import com.akedev7.pos.domain.model.SalesSummary
import com.google.protobuf.Timestamp
import com.google.type.Decimal
import net.devh.boot.grpc.server.service.GrpcService
import java.time.Instant
import java.time.OffsetDateTime


@GrpcService
class SalesGrpcService(private val salesService: SalesService) : SalesServiceGrpcKt.SalesServiceCoroutineImplBase() {
    override suspend fun getSalesData(request: Payment.SalesDataRequest): Payment.SalesDataResponse {
        val salesSummary: SalesSummary = salesService.getSalesSummary(
            request.startDateTime.toOffsetDateTime(),
            request.endDateTime.toOffsetDateTime()
        )
        return getGrpcResponse(salesSummary)
    }

    fun getGrpcResponse(salesSummary: SalesSummary): Payment.SalesDataResponse {
        val sales = salesSummary.sales.map {
            Payment.SalesRecord.newBuilder()
                .setDatetime(it.datetime.toProtoTimestamp())
                .setSales(Decimal.newBuilder().setValue(it.sales.toPlainString()).build())
                .setPoints(Decimal.newBuilder().setValue(it.points.toPlainString()).build())
                .build()
        }
        return Payment.SalesDataResponse.newBuilder().addAllSales(sales).build()
    }

    fun OffsetDateTime.toProtoTimestamp(): Timestamp {
        val instant: Instant = this.toInstant()
        return Timestamp.newBuilder()
            .setSeconds(instant.epochSecond)
            .setNanos(instant.nano)
            .build()
    }
}
