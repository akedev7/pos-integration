package com.akedev7.pos.application.rule_engine.model

import java.math.BigDecimal

data class PaymentRule(
    val condition: String,
    val pointModifier: BigDecimal
)