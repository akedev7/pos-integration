package com.akedev7.pos.repository

import com.akedev7.pos.domain.Sales
import com.akedev7.pos.utils.structToString
import com.akedev7.pos.utils.toOffsetDateTime
import com.akedev7.tables.CustomerPayments.Companion.CUSTOMER_PAYMENTS
import org.jooq.DSLContext
import org.jooq.DatePart
import org.jooq.JSONB
import org.jooq.Record3
import org.jooq.Result
import org.jooq.impl.DSL.*
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.OffsetDateTime


@Repository
class PaymentRepository(val dsl: DSLContext) {
    fun insertPayment(payment: PaymentDAO) {
        dsl.insertInto(CUSTOMER_PAYMENTS)
            .set(CUSTOMER_PAYMENTS.CUSTOMER_ID, payment.customerId)
            .set(CUSTOMER_PAYMENTS.PRICE, payment.price)
            .set(CUSTOMER_PAYMENTS.PRICE_MODIFIER, payment.priceModifier)
            .set(CUSTOMER_PAYMENTS.PAYMENT_METHOD, payment.paymentMethod)
            .set(CUSTOMER_PAYMENTS.DATETIME, payment.datetime)
            .set(CUSTOMER_PAYMENTS.METADATA, JSONB.valueOf(structToString(payment.metadata)))
            .execute()
    }

    fun getSales(sales: Sales): Result<Record3<OffsetDateTime, BigDecimal, Int>> {
        val pointsExpression = floor(
            sum(field("price", BigDecimal::class.java))
                .cast(Int::class.java)
        )
        return dsl.select(
            trunc(field("datetime", OffsetDateTime::class.java), DatePart.HOUR).`as`("hour"),
            sum(field("price", BigDecimal::class.java)).`as`("total_sales"),
            pointsExpression.`as`("total_points")
        )
            .from(table("customer_payments"))
            .where(
                field("datetime", OffsetDateTime::class.java).between(
                    sales.startDateTime.toOffsetDateTime(),
                    sales.endDateTime.toOffsetDateTime()
                )
            )
            .groupBy(trunc(field("datetime", OffsetDateTime::class.java), DatePart.HOUR))
            .orderBy(field("hour"))
            .fetch()
    }

}