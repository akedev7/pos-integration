package com.akedev7.pos.component_test

import com.akedev7.pos.adapters.grpc.protobuf.Payment
import com.akedev7.pos.adapters.grpc.protobuf.PaymentServiceGrpcKt
import com.akedev7.pos.adapters.postgres.jooq.tables.CustomerPayments.Companion.CUSTOMER_PAYMENTS
import com.google.protobuf.Struct
import com.google.protobuf.Timestamp
import com.google.protobuf.Value
import com.google.rpc.ErrorInfo
import com.google.type.Decimal
import io.grpc.StatusException
import io.grpc.protobuf.StatusProto
import kotlinx.coroutines.runBlocking
import net.devh.boot.grpc.client.inject.GrpcClient
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import java.util.stream.Stream
import kotlin.test.assertEquals


class PaymentGrpcServiceComponentTest() : ComponentTestBase() {

    @Autowired
    lateinit var dsl: DSLContext

    @GrpcClient("inProcess")
    private lateinit var grpcStub: PaymentServiceGrpcKt.PaymentServiceCoroutineStub

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
                additionalFields = mapOf("bankName" to "Bangkok", "chequeNumber" to "123456"),
                expectedPoints = 0,
                expectedFinalPrice = 100.0
            ),
            PaymentTestData(
                paymentMethod = "BANK_TRANSFER",
                priceModifier = "1",
                additionalFields = mapOf("bankName" to "Bangkok", "bankAccount" to "123456"),
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
    fun `should create payment in db when call process payment given valid payment request`(
        testData: PaymentTestData
    ): Unit = runBlocking {
        val additionalItemBuilder = Struct.newBuilder().apply {
            testData.additionalFields.forEach { (key, value) ->
                putFields(key, Value.newBuilder().setStringValue(value).build())
            }
        }

        val response = grpcStub.processPayment(
            Payment.PaymentRequest.newBuilder()
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

    @Test
    fun `should return invalid argument error given invalid condition`(): Unit = runBlocking {
        val ex = assertThrows<StatusException> {
            grpcStub.processPayment(
                Payment.PaymentRequest.newBuilder()
                    .setCustomerId("1")
                    .setPrice(Decimal.newBuilder().setValue("100.00").build())
                    .setPriceModifier(Decimal.newBuilder().setValue("2").build())
                    .setPaymentMethod("MASTERCARD")
                    .setDatetime(
                        Timestamp.newBuilder()
                            .setSeconds(System.currentTimeMillis() / 1000)
                            .build()
                    )
                    .setAdditionalItem(
                        Struct.newBuilder()
                            .putFields("last4", Value.newBuilder().setStringValue("1234").build())
                            .build()
                    )
                    .build()
            )

        }
        assertThat(ex.status.code.name).isEqualTo("INVALID_ARGUMENT")

        val status = StatusProto.fromThrowable(ex)
        val errorInfo = status?.detailsList?.find { it.`is`(ErrorInfo::class.java) }?.unpack(com.google.rpc.ErrorInfo::class.java)
        assertThat(errorInfo?.reason).isEqualTo("INVALID_ARGUMENT")
        assertThat(errorInfo?.metadataMap?.get("details")).contains("No matching rule found for your payment")
    }

    @Test
    fun `should return invalid argument error given invalid argument`(): Unit = runBlocking {
        val ex = assertThrows<StatusException> {
            grpcStub.processPayment(
                Payment.PaymentRequest.newBuilder()
                    .build()
            )

        }
        assertThat(ex.status.code.name).isEqualTo("INVALID_ARGUMENT")

        val status = StatusProto.fromThrowable(ex)
        val errorInfo = status?.detailsList?.find { it.`is`(ErrorInfo::class.java) }?.unpack(com.google.rpc.ErrorInfo::class.java)
        assertThat(errorInfo?.reason).isEqualTo("INVALID_ARGUMENT")
        assertThat(errorInfo?.metadataMap?.get("details")).isEqualTo("{\"error\": [\"Customer ID cannot be blank\", \"Price must be specified\", \"Price modifier must be specified\", \"Payment method cannot be blank\", \"Datetime must be specified\"]}")
    }
}
