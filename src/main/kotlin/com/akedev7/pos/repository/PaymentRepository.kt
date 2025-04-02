package com.akedev7.pos.repository

import com.akedev7.tables.CustomerPayments.Companion.CUSTOMER_PAYMENTS
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.jooq.DSLContext
import org.jooq.JSONB
import org.springframework.stereotype.Repository

@Repository
class PaymentRepository(val dsl: DSLContext) {
    fun insertPayment(payment: PaymentDAO) {
        dsl.insertInto(CUSTOMER_PAYMENTS)
            .set(CUSTOMER_PAYMENTS.CUSTOMER_ID, payment.customerId)
            .set(CUSTOMER_PAYMENTS.PRICE, payment.price)
            .set(CUSTOMER_PAYMENTS.PRICE_MODIFIER, payment.priceModifier)
            .set(CUSTOMER_PAYMENTS.PAYMENT_METHOD, payment.paymentMethod)
            .set(CUSTOMER_PAYMENTS.DATETIME, payment.datetime)
            .set(CUSTOMER_PAYMENTS.METADATA, JSONB.valueOf(jacksonObjectMapper().writeValueAsString(payment.metadata)))
            .execute()
    }

}