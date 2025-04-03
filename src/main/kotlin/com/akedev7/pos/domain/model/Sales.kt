package com.akedev7.pos.domain.model

import java.math.BigDecimal
import java.time.OffsetDateTime

data class SalesSummary(
    val sales: List<Sale>
)

data class Sale(
    val datetime: OffsetDateTime,
    val sales: BigDecimal,
    val points: Int
)