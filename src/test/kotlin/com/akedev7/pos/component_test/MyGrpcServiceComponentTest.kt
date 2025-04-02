package com.akedev7.pos.component_test

import com.akedev7.pos.controller.Payment.PaymentRequest
import com.akedev7.pos.controller.PaymentServiceGrpcKt
import com.akedev7.tables.references.CUSTOMER_PAYMENTS
import com.google.protobuf.Timestamp
import com.google.type.Decimal
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import kotlin.test.assertEquals


@SpringBootTest
@DirtiesContext
class MyGrpcServiceComponentTest : BaseTest() {

    @Autowired
    lateinit var dsl: DSLContext

    @Test
    fun should_create_payment_in_db_when_call_process_payment_given_valid_payment_request(): Unit = runBlocking {
        val channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build()
        val client = PaymentServiceGrpcKt.PaymentServiceCoroutineStub(channel)
        val response = client.processPayment(
            PaymentRequest.newBuilder()
                .setCustomerId("1")  // Set customer ID
                .setPrice(Decimal.newBuilder().setValue("100.00").build())  // Set price
                .setPriceModifier(Decimal.newBuilder().setValue("1").build())  // 10% discount
                .setPaymentMethod("CASH")  // Set payment method
                .setDatetime(
                    Timestamp.newBuilder()
                        .setSeconds(System.currentTimeMillis() / 1000)
                        .build()
                )  // Set current timestamp
                .putAllAdditionalItem(
                    mapOf(
                        "item1" to "value1",
                        "item2" to "value2"
                    )
                )  // Set additional items
                .build()
        )
        val actual = dsl.selectFrom(CUSTOMER_PAYMENTS)
            .orderBy(CUSTOMER_PAYMENTS.ID.desc()) // Replace with a timestamp column if needed
            .limit(1)
            .fetchOne()
        assertEquals(5, response.points)
        assertEquals(100.0, response.finalPrice)
        assertEquals("CASH", actual!!.paymentMethod)


    }
}
