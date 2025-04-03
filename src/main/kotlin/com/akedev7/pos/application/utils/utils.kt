package com.akedev7.pos.application.utils

import com.google.protobuf.Struct
import com.google.protobuf.Timestamp
import com.google.protobuf.util.JsonFormat
import com.google.type.Decimal
import java.math.BigDecimal
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun Decimal.toBigDecimal(): BigDecimal {
    return BigDecimal(this.value)
}

fun Timestamp.toOffsetDateTime(): OffsetDateTime {
    val instant = Instant.ofEpochSecond(seconds, nanos.toLong())
    return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC)
}

fun structToString(struct: Struct): String {
    return JsonFormat.printer().omittingInsignificantWhitespace().print(struct)
}
