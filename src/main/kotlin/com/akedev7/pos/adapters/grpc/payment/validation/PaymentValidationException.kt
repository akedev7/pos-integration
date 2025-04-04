package com.akedev7.pos.adapters.grpc.payment.validation

class PaymentValidationException(
    errors: List<String>
) : RuntimeException(buildErrorMessage(errors)) {
    companion object {
        private fun buildErrorMessage(errors: List<String>): String {
            val errorJson = errors.joinToString(
                separator = ", ",
                prefix = "[",
                postfix = "]"
            ) { "\"$it\"" }
            return """{"error": $errorJson}"""
        }
    }
}