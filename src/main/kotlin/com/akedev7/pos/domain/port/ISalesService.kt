package com.akedev7.pos.domain.port

import com.akedev7.pos.domain.model.SalesSummary
import java.time.OffsetDateTime

interface ISalesService {
    fun getSalesSummary(startDateTime: OffsetDateTime, endDateTime: OffsetDateTime): SalesSummary
}
