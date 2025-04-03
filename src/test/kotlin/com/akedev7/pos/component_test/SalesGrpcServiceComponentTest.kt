package com.akedev7.pos.component_test

import com.akedev7.pos.controller.Payment.SalesDataRequest
import com.akedev7.pos.controller.PaymentServiceGrpcKt
import com.akedev7.pos.controller.SalesServiceGrpcKt
import com.akedev7.tables.CustomerPayments.Companion.CUSTOMER_PAYMENTS
import com.google.protobuf.Timestamp
import com.google.type.Decimal
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import net.devh.boot.grpc.client.inject.GrpcClient
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit
import kotlin.test.Test


@SpringBootTest
@DirtiesContext
class SalesGrpcServiceComponentTest : BaseTest() {

    @Autowired
    lateinit var dsl: DSLContext

    @GrpcClient("inProcess")
    private lateinit var grpcStub: SalesServiceGrpcKt.SalesServiceCoroutineStub

    private fun prepareData() {
        // Clear existing data
        dsl.deleteFrom(CUSTOMER_PAYMENTS).execute()

        // Prepare test data for 3 different hours with different customers
        val baseTime = LocalDateTime.of(2023, 1, 1, 10, 0) // 10:00 AM

        // Hour 1 - 3 payments (2 from customer 1, 1 from customer 2)
        insertPayment(1, BigDecimal("100.00"), BigDecimal("1.00"), "CREDIT_CARD", baseTime)
        insertPayment(1, BigDecimal("50.00"), BigDecimal("0.50"), "PAYPAL", baseTime.plusMinutes(15))
        insertPayment(2, BigDecimal("75.00"), BigDecimal("0.75"), "CREDIT_CARD", baseTime.plusMinutes(30))

        // Hour 2 - 2 payments (1 from customer 3, 1 from customer 1)
        insertPayment(3, BigDecimal("200.00"), BigDecimal("2.00"), "DEBIT_CARD", baseTime.plusHours(1))
        insertPayment(1, BigDecimal("30.00"), BigDecimal("0.30"), "CREDIT_CARD", baseTime.plusHours(1).plusMinutes(45))

        // Hour 3 - 1 payment from customer 4
        insertPayment(4, BigDecimal("150.00"), BigDecimal("1.50"), "PAYPAL", baseTime.plusHours(2))
    }
    private fun insertPayment(
        customerId: Long,
        price: BigDecimal,
        priceModifier: BigDecimal,
        paymentMethod: String,
        datetime: LocalDateTime
    ) {
        dsl.insertInto(CUSTOMER_PAYMENTS)
            .columns(
                CUSTOMER_PAYMENTS.CUSTOMER_ID,
                CUSTOMER_PAYMENTS.PRICE,
                CUSTOMER_PAYMENTS.PRICE_MODIFIER,
                CUSTOMER_PAYMENTS.PAYMENT_METHOD,
                CUSTOMER_PAYMENTS.DATETIME
            )
            .values(
                customerId,
                price,
                priceModifier,
                paymentMethod,
                datetime.toInstant(ZoneOffset.UTC).atOffset(ZoneOffset.UTC)
            )
            .execute()
    }

    @Test
    fun `should return sales data aggregated by hour`(): Unit = runBlocking {
        prepareData()
        val secondsThreeYearsAgo = System.currentTimeMillis() / 1000 - TimeUnit.DAYS.toSeconds((365 * 3).toLong())
        val response = grpcStub.getSalesData(
            SalesDataRequest.newBuilder()
                .setStartDateTime(
                    Timestamp.newBuilder()
                        .setSeconds(secondsThreeYearsAgo)
                        .build()
                ).setEndDateTime(
                    Timestamp.newBuilder()
                        .setSeconds(System.currentTimeMillis() / 1000)
                        .build()
                )
                .build()
        )
        assertThat(response.salesList).hasSize(3)

        // Verify hour 1 (10:00-11:00)
        val hour1 = response.salesList[0]
        assertThat(hour1.datetime).isEqualTo(
            LocalDateTime.of(2023, 1, 1, 10, 0)
                .toInstant(ZoneOffset.UTC)
                .toProtoTimestamp()
        )
        assertThat(hour1.sales).isEqualTo(Decimal.newBuilder().setValue("225.00").build()) // 100 + 50 + 75
        assertThat(hour1.points).isEqualTo(225) // Assuming 1 point per 1 currency unit

        // Verify hour 2 (11:00-12:00)
        val hour2 = response.salesList[1]
        assertThat(hour2.datetime).isEqualTo(
            LocalDateTime.of(2023, 1, 1, 11, 0)
                .toInstant(ZoneOffset.UTC)
                .toProtoTimestamp()
        )
        assertThat(hour2.sales).isEqualTo(Decimal.newBuilder().setValue("230.00").build()) // 200 + 30
        assertThat(hour2.points).isEqualTo(230)

        // Verify hour 3 (12:00-13:00)
        val hour3 = response.salesList[2]
        assertThat(hour3.datetime).isEqualTo(
            LocalDateTime.of(2023, 1, 1, 12, 0)
                .toInstant(ZoneOffset.UTC)
                .toProtoTimestamp()
        )
        assertThat(hour3.sales).isEqualTo(Decimal.newBuilder().setValue("150.00").build())
        assertThat(hour3.points).isEqualTo(150)

    }

    private fun Instant.toProtoTimestamp(): Timestamp {
        return Timestamp.newBuilder()
            .setSeconds(epochSecond)
            .setNanos(nano)
            .build()
    }
}
