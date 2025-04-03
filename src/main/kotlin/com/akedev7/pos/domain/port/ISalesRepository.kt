package com.akedev7.pos.domain.port


import com.akedev7.pos.domain.model.SalesSummary
import java.time.OffsetDateTime

interface ISalesRepository {
    fun getSales(startDateTime: OffsetDateTime, endDateTime: OffsetDateTime): SalesSummary
}