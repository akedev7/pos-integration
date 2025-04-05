package com.akedev7.pos.domain.port

import com.akedev7.pos.domain.model.Payment
import com.akedev7.pos.domain.model.PaymentResult

interface IPaymentService {
    fun getPaymentResult(payment: Payment): PaymentResult
}