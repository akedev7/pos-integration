package com.akedev7.pos.domain.port

import com.akedev7.pos.domain.model.Payment

interface IPaymentRepository {
    fun save(payment: Payment)
}