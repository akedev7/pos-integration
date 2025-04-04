package com.akedev7.pos.application.rule_engine

import com.akedev7.pos.application.rule_engine.model.PaymentCalculationResult
import com.akedev7.pos.application.rule_engine.model.PaymentRule
import com.akedev7.pos.domain.model.Payment
import org.slf4j.LoggerFactory
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component

@Component
class PaymentRuleEngine(val ruleParser: SpelRuleParser, val paymentRuleRepository: PaymentRuleRepository) {
    companion object {
        private val log = LoggerFactory.getLogger(PaymentRuleEngine::class.java)
    }
    fun getPaymentCalculationResult(payment: Payment): PaymentCalculationResult {
        val paymentRule = paymentRuleRepository.getPaymentRule()
        if (isRuleMatched(payment, paymentRule)) {
            val point = payment.price.multiply(paymentRule[payment.paymentMethod]?.pointModifier)
            val price = payment.price.multiply(payment.priceModifier)
            return PaymentCalculationResult(price, point)
        }
        throw IllegalArgumentException("No matching rule found for your payment")
    }

    private fun isRuleMatched(payment: Payment, paymentRule: Map<String, PaymentRule>): Boolean {
        val condition = paymentRule[payment.paymentMethod]?.condition
        return try {
            ruleParser.parse(condition!!, StandardEvaluationContext(payment))
        } catch (ex: Exception) {
            log.error("Error when parsing payment rule $condition", ex)
            false
        }
    }
}

