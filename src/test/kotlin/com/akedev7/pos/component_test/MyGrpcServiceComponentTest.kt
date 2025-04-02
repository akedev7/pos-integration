package com.akedev7.pos.component_test

import com.akedev7.pos.controller.Payment.PaymentRequest
import com.akedev7.pos.controller.PaymentServiceGrpcKt
import com.akedev7.tables.references.CUSTOMER_PAYMENTS
import com.google.protobuf.Struct
import com.google.protobuf.Timestamp
import com.google.protobuf.Value
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
    fun should_create_payment_in_db_when_call_process_payment_given_valid_master_card_payment_request(): Unit = runBlocking {
        val channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build()
        val client = PaymentServiceGrpcKt.PaymentServiceCoroutineStub(channel)
        val response = client.processPayment(
            PaymentRequest.newBuilder()
                .setCustomerId("1")  // Set customer ID
                .setPrice(Decimal.newBuilder().setValue("100.00").build())  // Set price
                .setPriceModifier(Decimal.newBuilder().setValue("1").build())  // 10% discount
                .setPaymentMethod("MASTERCARD")  // Set payment method
                .setDatetime(
                    Timestamp.newBuilder()
                        .setSeconds(System.currentTimeMillis() / 1000)
                        .build()
                )  // Set current timestamp
                .setAdditionalItem(
                    Struct.newBuilder()
                        .putFields("last4", Value.newBuilder()
                            .setStringValue("1234")
                            .build())
                        .build()
                )  // Set additional items
                .build()
        )
        val actual = dsl.selectFrom(CUSTOMER_PAYMENTS)
            .orderBy(CUSTOMER_PAYMENTS.ID.desc()) // Replace with a timestamp column if needed
            .limit(1)
            .fetchOne()
        assertEquals(3, response.points)
        assertEquals(100.0, response.finalPrice)
        assertEquals("MASTERCARD", actual!!.paymentMethod)
    }

    @Test
    fun should_create_payment_in_db_when_call_process_payment_given_valid_bank_transfer_payment_request(): Unit = runBlocking {
        val channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build()
        val client = PaymentServiceGrpcKt.PaymentServiceCoroutineStub(channel)
        val response = client.processPayment(
            PaymentRequest.newBuilder()
                .setCustomerId("1")  // Set customer ID
                .setPrice(Decimal.newBuilder().setValue("100.00").build())  // Set price
                .setPriceModifier(Decimal.newBuilder().setValue("1").build())  // 10% discount
                .setPaymentMethod("BANK_TRANSFER")  // Set payment method
                .setDatetime(
                    Timestamp.newBuilder()
                        .setSeconds(System.currentTimeMillis() / 1000)
                        .build()
                )  // Set current timestamp
                .setAdditionalItem(
                    Struct.newBuilder()
                        .putFields("bankName", Value.newBuilder().setStringValue("Bangkok Bank").build())
                        .putFields("accountNumber", Value.newBuilder().setStringValue("Bangkok Bank").build())
                        .build()
                )  // Set additional items
                .build()
        )
        val actual = dsl.selectFrom(CUSTOMER_PAYMENTS)
            .orderBy(CUSTOMER_PAYMENTS.ID.desc()) // Replace with a timestamp column if needed
            .limit(1)
            .fetchOne()
        assertEquals(3, response.points)
        assertEquals(100.0, response.finalPrice)
        assertEquals("MASTERCARD", actual!!.paymentMethod)
    }
}
