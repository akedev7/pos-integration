package com.akedev7.pos.application.ruleengine

import org.slf4j.LoggerFactory
import org.springframework.expression.EvaluationContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.stereotype.Component

@Component
class SpelRuleParser {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private val parser = SpelExpressionParser()
    }

    fun parse(expression: String, context: EvaluationContext): Boolean {
        return try {
            parser.parseExpression(expression).getValue(context, Boolean::class.java)!!
        } catch (ex: Exception) {
            log.error("Error when parsing payment rule $expression", ex)
            false
        }
    }
}
