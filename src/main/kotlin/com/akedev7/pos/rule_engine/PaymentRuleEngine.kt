package com.akedev7.pos.rule_engine

import com.akedev7.pos.domain.Payment
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
    private final val paymentRule: List<PaymentRuleDTO>

    init {
        val rules = ctx.selectFrom(PAYMENT_RULE).fetch()
        paymentRule = rules.map { rule ->
            PaymentRuleDTO(
                rule.conditions!!,
                rule.pointsPercentage!!
            )
        }.toList()
    }

    fun calculatePoint(payment: Payment): BigDecimal {
        val context = StandardEvaluationContext(payment)

        val (_, pointModifier) = paymentRule.first {
            parser.parseExpression(it.condition).getValue(context, Boolean::class.java) == true
        }
        return payment.price * pointModifier
    }

}