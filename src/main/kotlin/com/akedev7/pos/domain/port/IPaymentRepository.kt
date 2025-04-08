package com.akedev7.pos.domain.port

import com.akedev7.pos.domain.model.PaymentDAO

interface IPaymentRepository {
    fun save(payment: PaymentDAO)
}
