package com.akedev7.pos.application.rule_engine

import com.akedev7.pos.application.rule_engine.model.PaymentCalculationResult
import com.akedev7.pos.domain.model.Payment
import com.akedev7.tables.PaymentRule.Companion.PAYMENT_RULE
import org.jooq.DSLContext
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class PaymentRuleEngine(ctx: DSLContext) {
    data class PaymentRuleDTO(
        val condition: String,
        val pointModifier: BigDecimal
    )

    private final val parser: ExpressionParser = SpelExpressionParser()
    private final val paymentRule: Map<String, PaymentRuleDTO>

    init {
        val rules = ctx.selectFrom(PAYMENT_RULE).fetch()
        paymentRule = rules.map { rule ->
            rule.paymentMethod!! to
                    PaymentRuleDTO(
                        rule.conditions!!, rule.pointsPercentage ?: BigDecimal.ONE
                    )
        }.toMap()
    }

    fun getPaymentCalculationResult(payment: Payment): PaymentCalculationResult {
        if (isRuleMatched(payment)) {
            val point = payment.price.multiply(paymentRule[payment.paymentMethod]?.pointModifier)
            val price = payment.price.multiply(payment.priceModifier)
            return PaymentCalculationResult(price, point)
        }
        throw IllegalArgumentException("No matching rule found for your payment")
    }

    private fun isRuleMatched(payment: Payment): Boolean =
        parser.parseExpression(paymentRule[payment.paymentMethod]!!.condition)
            .getValue(StandardEvaluationContext(payment), Boolean::class.java) ?: false
}

