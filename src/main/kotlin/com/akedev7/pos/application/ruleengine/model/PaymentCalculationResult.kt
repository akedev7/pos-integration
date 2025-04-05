package com.akedev7.pos.application.ruleengine.model

import java.math.BigDecimal

data class PaymentCalculationResult(
    val finalPrice: BigDecimal,
    val calculatedPoints: BigDecimal
)
