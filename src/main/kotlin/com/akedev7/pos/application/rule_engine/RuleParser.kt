package com.akedev7.pos.application.rule_engine

import org.springframework.expression.EvaluationContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Component

@Component
class SpelRuleParser {
    private val parser = SpelExpressionParser()
    fun parse(expression: String, context: EvaluationContext): Boolean {
        return parser.parseExpression(expression)
            .getValue(context, Boolean::class.java) ?: false
    }
}