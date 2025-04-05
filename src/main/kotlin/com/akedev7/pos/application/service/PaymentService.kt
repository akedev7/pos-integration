package com.akedev7.pos.application.service

import com.akedev7.pos.adapters.postgres.PaymentRepository
import com.akedev7.pos.application.ruleengine.PaymentRuleEngine
import com.akedev7.pos.domain.model.Payment
import com.akedev7.pos.domain.model.PaymentDAO
import com.akedev7.pos.domain.model.PaymentResult
import com.akedev7.pos.domain.port.IPaymentService
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentRuleEngine: PaymentRuleEngine,
    private val paymentRepository: PaymentRepository
) : IPaymentService {
    override fun getPaymentResult(payment: Payment): PaymentResult {
        val paymentCalculationResult = paymentRuleEngine.getPaymentCalculationResult(payment)

        val paymentDAO = PaymentDAO(
            customerId = payment.customerId,
            price = payment.price,
            point = paymentCalculationResult.calculatedPoints,
            priceModifier = payment.priceModifier,
            paymentMethod =  payment.paymentMethod,
            datetime = payment.datetime,
            additionalItem = payment.additionalItem
        )

        paymentRepository.save(paymentDAO)
        return PaymentResult(paymentCalculationResult.finalPrice, paymentCalculationResult.calculatedPoints)
    }
}
