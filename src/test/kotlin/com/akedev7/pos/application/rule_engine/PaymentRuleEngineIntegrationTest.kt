import com.akedev7.pos.application.rule_engine.PaymentRuleEngine
import com.akedev7.pos.application.rule_engine.PaymentRuleRepository
import com.akedev7.pos.application.rule_engine.SpelRuleParser
import com.akedev7.pos.application.rule_engine.model.PaymentRule
import com.akedev7.pos.domain.model.Payment
import com.google.protobuf.Struct
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.OffsetDateTime

class PaymentRuleEngineIntegrationTest {

    private val mockRepository = mockk<PaymentRuleRepository>()
    private val realRuleParser = SpelRuleParser()
    private val paymentRuleEngine = PaymentRuleEngine(realRuleParser, mockRepository)

    @Test
    fun `getPaymentCalculationResult should return correct result when rule matches`() {
        // Given
        val paymentMethod = "CREDIT_CARD"
        val payment = Payment(
            customerId = 1L,
            price = BigDecimal("100.00"),
            priceModifier = BigDecimal("0.95"), // 5% discount
            paymentMethod = paymentMethod,
            datetime = OffsetDateTime.now(),
            additionalItem = Struct.newBuilder().build()
        )

        val ruleForMethod = PaymentRule(
            condition = "price > 50 && paymentMethod == 'CREDIT_CARD'",
            pointModifier = BigDecimal("0.02") // 2% points
        )

        val paymentRules = mapOf(
            paymentMethod to ruleForMethod,
            "DEBIT_CARD" to PaymentRule("price > 10", BigDecimal("0.01"))
        )

        every { mockRepository.getPaymentRule() } returns paymentRules

        // When
        val result = paymentRuleEngine.getPaymentCalculationResult(payment)

        // Then
        assertEquals(BigDecimal("95.0000"), result.finalPrice)
        assertEquals(BigDecimal("2.0000"), result.calculatedPoints)
    }

    @Test
    fun `getPaymentCalculationResult should throw exception when no matching rule found`() {
        // Given
        val payment = Payment(
            customerId = 1L,
            price = BigDecimal("100.00"),
            priceModifier = BigDecimal("1.00"),
            paymentMethod = "UNSUPPORTED_METHOD",
            datetime = OffsetDateTime.now(),
            additionalItem = Struct.newBuilder().build()
        )

        val paymentRules = mapOf(
            "CREDIT_CARD" to PaymentRule("price > 50", BigDecimal("0.02")),
            "DEBIT_CARD" to PaymentRule("price > 10", BigDecimal("0.01"))
        )

        every { mockRepository.getPaymentRule() } returns paymentRules

        // When / Then
        assertThrows<IllegalArgumentException> {
            paymentRuleEngine.getPaymentCalculationResult(payment)
        }
    }

    @Test
    fun `getPaymentCalculationResult should throw exception when condition evaluates to false`() {
        // Given
        val paymentMethod = "CREDIT_CARD"
        val payment = Payment(
            customerId = 1L,
            price = BigDecimal("30.00"), // Doesn't meet the >50 condition
            priceModifier = BigDecimal("0.95"),
            paymentMethod = paymentMethod,
            datetime = OffsetDateTime.now(),
            additionalItem = Struct.newBuilder().build()
        )

        val ruleForMethod = PaymentRule(
            condition = "price > 50", // Condition not met
            pointModifier = BigDecimal("0.02")
        )

        val paymentRules = mapOf(
            paymentMethod to ruleForMethod,
            "DEBIT_CARD" to PaymentRule("price > 10", BigDecimal("0.01"))
        )

        every { mockRepository.getPaymentRule() } returns paymentRules

        // When / Then
        assertThrows<IllegalArgumentException> {
            paymentRuleEngine.getPaymentCalculationResult(payment)
        }
    }

    @Test
    fun `getPaymentCalculationResult should handle complex conditions correctly`() {
        // Given
        val paymentMethod = "MOBILE_PAY"
        val payment = Payment(
            customerId = 1L,
            price = BigDecimal("200.00"),
            priceModifier = BigDecimal("0.90"), // 10% discount
            paymentMethod = paymentMethod,
            datetime = OffsetDateTime.parse("2023-01-15T12:00:00Z"),
            additionalItem = Struct.newBuilder().build()
        )

        val ruleForMethod = PaymentRule(
            condition = """
                price > 100 && 
                (paymentMethod == 'MOBILE_PAY' || paymentMethod == 'E_WALLET') && 
                datetime.getHour() >= 10 && datetime.getHour() <= 14
            """.trimIndent(),
            pointModifier = BigDecimal("0.05") // 5% points during promotion hours
        )

        val paymentRules = mapOf(
            paymentMethod to ruleForMethod,
            "CREDIT_CARD" to PaymentRule("price > 50", BigDecimal("0.02"))
        )

        every { mockRepository.getPaymentRule() } returns paymentRules

        // When
        val result = paymentRuleEngine.getPaymentCalculationResult(payment)

        // Then
        assertEquals(BigDecimal("180.0000"), result.finalPrice) // 200 * 0.90
        assertEquals(BigDecimal("10.0000"), result.calculatedPoints) // 200 * 0.05
    }

    @Test
    fun `isRuleMatched should return false for invalid SpEL expressions`() {
        // Given
        val paymentMethod = "DEBIT_CARD"
        val payment = Payment(
            customerId = 1L,
            price = BigDecimal("20.00"),
            priceModifier = BigDecimal("1.00"),
            paymentMethod = paymentMethod,
            datetime = OffsetDateTime.now(),
            additionalItem = Struct.newBuilder().build()
        )

        val invalidRule = PaymentRule(
            condition = "this is not valid SpEL", // Invalid expression
            pointModifier = BigDecimal("0.01")
        )

        val paymentRules = mapOf(paymentMethod to invalidRule)
        every { mockRepository.getPaymentRule() } returns paymentRules

        // When / Then
        assertThrows<IllegalArgumentException> {
            paymentRuleEngine.getPaymentCalculationResult(payment)
        }
    }

    @Test
    fun `getPaymentCalculationResult should throw exception when payment rules are empty`() {
        val payment = Payment(
            customerId = 1L,
            price = BigDecimal("100.00"),
            priceModifier = BigDecimal("1.00"),
            paymentMethod = "CREDIT_CARD",
            datetime = OffsetDateTime.now(),
            additionalItem = Struct.newBuilder().build()
        )

        every { mockRepository.getPaymentRule() } returns emptyMap()

        assertThrows<IllegalArgumentException> {
            paymentRuleEngine.getPaymentCalculationResult(payment)
        }
    }

    @Test
    fun `getPaymentCalculationResult should handle zero price correctly`() {
        val paymentMethod = "CREDIT_CARD"
        val payment = Payment(
            customerId = 1L,
            price = BigDecimal("0.00"),
            priceModifier = BigDecimal("1.00"),
            paymentMethod = paymentMethod,
            datetime = OffsetDateTime.now(),
            additionalItem = Struct.newBuilder().build()
        )

        val ruleForMethod = PaymentRule(
            condition = "price >= 0", // Explicitly allow zero
            pointModifier = BigDecimal("0.01")
        )

        every { mockRepository.getPaymentRule() } returns mapOf(paymentMethod to ruleForMethod)

        val result = paymentRuleEngine.getPaymentCalculationResult(payment)
        assertEquals(BigDecimal("0.0000"), result.finalPrice)
        assertEquals(BigDecimal("0.0000"), result.calculatedPoints)
    }

    @Test
    fun `getPaymentCalculationResult should handle exactly matching condition values`() {
        val paymentMethod = "CREDIT_CARD"
        val payment = Payment(
            customerId = 1L,
            price = BigDecimal("50.00"), // Exactly matches condition
            priceModifier = BigDecimal("1.00"),
            paymentMethod = paymentMethod,
            datetime = OffsetDateTime.now(),
            additionalItem = Struct.newBuilder().build()
        )

        val ruleForMethod = PaymentRule(
            condition = "price >= 50", // Edge case condition
            pointModifier = BigDecimal("0.02")
        )

        every { mockRepository.getPaymentRule() } returns mapOf(paymentMethod to ruleForMethod)

        val result = paymentRuleEngine.getPaymentCalculationResult(payment)
        assertEquals(BigDecimal("50.0000"), result.finalPrice)
        assertEquals(BigDecimal("1.0000"), result.calculatedPoints)
    }

}