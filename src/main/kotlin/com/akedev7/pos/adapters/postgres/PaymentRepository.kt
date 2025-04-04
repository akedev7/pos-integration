package com.akedev7.pos.adapters.postgres

import com.akedev7.pos.application.utils.structToString
import com.akedev7.pos.domain.model.Payment
import com.akedev7.pos.domain.port.IPaymentRepository
import com.akedev7.tables.CustomerPayments.Companion.CUSTOMER_PAYMENTS
import org.jooq.DSLContext
import org.jooq.JSONB
import org.springframework.stereotype.Repository


@Repository
class PaymentRepository(private val dsl: DSLContext) : IPaymentRepository {
    override fun save(payment: Payment) {
        dsl.insertInto(CUSTOMER_PAYMENTS)
            .set(CUSTOMER_PAYMENTS.CUSTOMER_ID, payment.customerId)
            .set(CUSTOMER_PAYMENTS.PRICE, payment.price)
            .set(CUSTOMER_PAYMENTS.PRICE_MODIFIER, payment.priceModifier)
            .set(CUSTOMER_PAYMENTS.PAYMENT_METHOD, payment.paymentMethod)
            .set(CUSTOMER_PAYMENTS.DATETIME, payment.datetime)
            .set(CUSTOMER_PAYMENTS.METADATA, JSONB.valueOf(structToString(payment.additionalItem)))
            .execute()
    }

}