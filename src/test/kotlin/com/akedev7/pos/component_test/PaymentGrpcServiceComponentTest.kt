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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.util.stream.Stream
import kotlin.test.assertEquals


@SpringBootTest
@DirtiesContext
class PaymentGrpcServiceComponentTest : BaseTest() {

    @Autowired
    lateinit var dsl: DSLContext

    companion object {
        @JvmStatic
        fun paymentMethodProvider(): Stream<PaymentTestData> = Stream.of(
            PaymentTestData(
                paymentMethod = "MASTERCARD",
                priceModifier = "1",
                additionalFields = mapOf("last4" to "1234"),
                expectedPoints = 3,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "VISA",
                priceModifier = "1",
                additionalFields = mapOf("last4" to "5678"),
                expectedPoints = 3,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "AMEX",
                priceModifier = "1",
                additionalFields = mapOf("last4" to "9012"),
                expectedPoints = 2,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "JCB",
                priceModifier = "1",
                additionalFields = mapOf("last4" to "3456"),
                expectedPoints = 5,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "LINE_PAY",
                priceModifier = "1",
                expectedPoints = 1,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "PAYPAY",
                priceModifier = "1",
                expectedPoints = 1,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "GRAB_PAY",
                priceModifier = "1",
                expectedPoints = 1,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "CASH",
                priceModifier = "1",
                expectedPoints = 5,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "CHEQUE",
                priceModifier = "1",
                expectedPoints = 0,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "BANK_TRANSFER",
                priceModifier = "1",
                expectedPoints = 0,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "POINTS",
                priceModifier = "1",
                expectedPoints = 0,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "CASH_ON_DELIVERY",
                priceModifier = "1",
                additionalFields = mapOf("courier" to "YAMATO"),
                expectedPoints = 3,
                expectedFinalPrice = 100.0
            )
        )
    }

    data class PaymentTestData(
        val paymentMethod: String,
        val priceModifier: String,
        val additionalFields: Map<String, String> = emptyMap(),
        val expectedPoints: Int,
        val expectedFinalPrice: Double
    )

    @ParameterizedTest
    @MethodSource("paymentMethodProvider")
    fun should_create_payment_in_db_when_call_process_payment_given_valid_payment_request(
        testData: PaymentTestData
    ): Unit = runBlocking {
        val channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext().build()
        val client = PaymentServiceGrpcKt.PaymentServiceCoroutineStub(channel)

        val additionalItemBuilder = Struct.newBuilder()
        testData.additionalFields.forEach { (key, value) ->
            additionalItemBuilder.putFields(key, Value.newBuilder().setStringValue(value).build())
        }

        val response = client.processPayment(
            PaymentRequest.newBuilder()
                .setCustomerId("1")
                .setPrice(Decimal.newBuilder().setValue("100.00").build())
                .setPriceModifier(Decimal.newBuilder().setValue(testData.priceModifier).build())
                .setPaymentMethod(testData.paymentMethod)
                .setDatetime(
                    Timestamp.newBuilder()
                        .setSeconds(System.currentTimeMillis() / 1000)
                        .build()
                )
                .setAdditionalItem(additionalItemBuilder.build())
                .build()
        )

        val actual = dsl.selectFrom(CUSTOMER_PAYMENTS)
            .orderBy(CUSTOMER_PAYMENTS.ID.desc())
            .limit(1)
            .fetchOne()

        assertEquals(testData.expectedPoints, response.points)
        assertEquals(testData.expectedFinalPrice, response.finalPrice)
        assertEquals(testData.paymentMethod, actual!!.paymentMethod)
    }
}
