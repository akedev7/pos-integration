package com.akedev7.pos.application.ruleengine

import com.akedev7.pos.application.ruleengine.model.PaymentCalculationResult
import com.akedev7.pos.application.ruleengine.model.PaymentRule
import com.akedev7.pos.domain.model.Payment
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

@Component
class PaymentRuleEngine(val ruleParser: SpelRuleParser, val paymentRuleRepository: PaymentRuleRepository) {

    fun getPaymentCalculationResult(payment: Payment): PaymentCalculationResult {
        val paymentRule = paymentRuleRepository.getPaymentRule()
        if (isRuleMatched(payment, paymentRule)) {
            val point = payment.price.multiply(paymentRule[payment.paymentMethod]!!.pointModifier)
            val price = payment.price.multiply(payment.priceModifier)
            return PaymentCalculationResult(price, point)
        }
        throw IllegalArgumentException("No matching rule found for your payment")
    }

    private fun isRuleMatched(payment: Payment, paymentRule: Map<String?, PaymentRule?>): Boolean {
        val condition = paymentRule[payment.paymentMethod]?.condition
            ?: throw IllegalArgumentException("The payment method ${payment.paymentMethod} is not supported")
        return ruleParser.parse(condition, StandardEvaluationContext(payment))
    }
}

