package com.akedev7.pos.adapters.postgres

import com.akedev7.pos.adapters.postgres.jooq.tables.references.CUSTOMER_PAYMENTS
import com.akedev7.pos.domain.model.Sale
import com.akedev7.pos.domain.port.ISalesRepository
import org.jooq.DSLContext
import org.jooq.DatePart
import org.jooq.impl.DSL
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime

@Repository
class SalesRepository(private val dsl: DSLContext) : ISalesRepository {
    override fun getSales(startDateTime: OffsetDateTime, endDateTime: OffsetDateTime): List<Sale> {
        return dsl.select(
            DSL.trunc(CUSTOMER_PAYMENTS.DATETIME, DatePart.HOUR).`as`(CUSTOMER_PAYMENTS.DATETIME),
            DSL.sum(CUSTOMER_PAYMENTS.PRICE),
            DSL.sum(CUSTOMER_PAYMENTS.POINT)
        )
            .from(CUSTOMER_PAYMENTS)
            .where(CUSTOMER_PAYMENTS.DATETIME.between(startDateTime, endDateTime))
            .groupBy(DSL.trunc(CUSTOMER_PAYMENTS.DATETIME, DatePart.HOUR))
            .fetch { Sale(it.value1()!!, it.value2()!!, it.value3()!!) }
    }
}
