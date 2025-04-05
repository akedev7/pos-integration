package com.akedev7.pos.application.ruleengine.model

import java.math.BigDecimal

data class PaymentRule(
    val condition: String?,
    val pointModifier: BigDecimal
)
