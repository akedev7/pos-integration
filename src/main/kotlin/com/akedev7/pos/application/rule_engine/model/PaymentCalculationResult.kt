package com.akedev7.pos.application.rule_engine.model

import java.math.BigDecimal

data class PaymentCalculationResult(
    val finalPrice: BigDecimal,
    val calculatedPoints: BigDecimal
)