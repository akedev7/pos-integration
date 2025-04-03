package com.akedev7.pos.application.service

import com.akedev7.pos.adapters.postgres.PaymentRepository
import com.akedev7.pos.application.rule_engine.PaymentRuleEngine
import com.akedev7.pos.domain.model.Payment
import com.akedev7.pos.domain.model.PaymentResult
import com.akedev7.pos.domain.port.IPaymentService
import org.springframework.stereotype.Service

@Service
class PaymentService(private val paymentRuleEngine: PaymentRuleEngine, private val paymentRepository: PaymentRepository) :
    IPaymentService {
    override fun getPaymentResult(payment: Payment): PaymentResult {
        val paymentCalculationResult = paymentRuleEngine.getPaymentCalculationResult(payment)
        paymentRepository.save(payment)
        return PaymentResult(paymentCalculationResult.finalPrice, paymentCalculationResult.calculatedPoints)
    }
}