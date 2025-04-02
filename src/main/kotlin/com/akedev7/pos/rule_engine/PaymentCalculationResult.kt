package com.akedev7.pos.rule_engine

import java.math.BigDecimal

data class PaymentCalculationResult(
    val ruleAdjustedPoints: BigDecimal,  // null if no rule matched
    val defaultPoints: BigDecimal        // always payment.price * payment.priceModifier
)