package com.akedev7.pos.application.ruleengine

import com.akedev7.pos.adapters.postgres.jooq.tables.PaymentRule.Companion.PAYMENT_RULE
import com.akedev7.pos.application.ruleengine.model.PaymentRule
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class PaymentRuleRepository(ctx: DSLContext) {

    private final val paymentRule: Map<String?, PaymentRule?>

    init {
        val rules = ctx.selectFrom(PAYMENT_RULE).fetch()
        paymentRule = rules.map { rule ->
            rule.paymentMethod to PaymentRule(rule.conditions, rule.pointsPercentage!!)
        }.toMap()
    }

    fun getPaymentRule(): Map<String?, PaymentRule?> {
        return this.paymentRule
    }

}
