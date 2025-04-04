package com.akedev7.pos.application.rule_engine

import com.akedev7.pos.application.rule_engine.model.PaymentRule
import com.akedev7.tables.PaymentRule.Companion.PAYMENT_RULE
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class PaymentRuleRepository(ctx: DSLContext) {

    private final val paymentRule: Map<String, PaymentRule>

    init {
        val rules = ctx.selectFrom(PAYMENT_RULE).fetch()
        paymentRule = rules.map { rule ->
            rule.paymentMethod!! to
                    PaymentRule(
                        rule.conditions!!, rule.pointsPercentage ?: BigDecimal.ONE
                    )
        }.toMap()
    }

    fun getPaymentRule(): Map<String, PaymentRule> {
        return this.paymentRule
    }

}