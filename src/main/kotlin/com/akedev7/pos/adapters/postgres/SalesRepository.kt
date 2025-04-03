package com.akedev7.pos.adapters.postgres

import com.akedev7.pos.domain.model.Sale
import com.akedev7.pos.domain.model.SalesSummary
import com.akedev7.pos.domain.port.ISalesRepository
import org.jooq.DSLContext
import org.jooq.DatePart
import org.jooq.Record3
import org.jooq.Result
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.OffsetDateTime

@Repository
class SalesRepository(private val dsl: DSLContext) : ISalesRepository {

    override fun getSales(startDateTime: OffsetDateTime, endDateTime: OffsetDateTime): SalesSummary {
        val pointsExpression = DSL.floor(
            DSL.sum(DSL.field("price", BigDecimal::class.java))
                .cast(Int::class.java)
        )
        return dsl.select(
            DSL.trunc(DSL.field("datetime", OffsetDateTime::class.java), DatePart.HOUR).`as`("hour"),
            DSL.sum(DSL.field("price", BigDecimal::class.java)).`as`("total_sales"),
            pointsExpression.`as`("total_points")
        )
            .from(DSL.table("customer_payments"))
            .where(
                DSL.field("datetime", OffsetDateTime::class.java).between(
                    startDateTime,
                    endDateTime
                )
            )
            .groupBy(DSL.trunc(DSL.field("datetime", OffsetDateTime::class.java), DatePart.HOUR))
            .orderBy(DSL.field("hour"))
            .fetch().toSalesSummary()
    }

    fun Result<Record3<OffsetDateTime, BigDecimal, Int>>.toSalesSummary() =
        SalesSummary(this.map { Sale(it.value1(), it.value2(), it.value3()) })

}