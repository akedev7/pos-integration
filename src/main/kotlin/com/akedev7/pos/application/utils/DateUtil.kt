package com.akedev7.pos.application.utils

import com.google.protobuf.Timestamp
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

fun Timestamp.toOffsetDateTime(): OffsetDateTime {
    val instant = Instant.ofEpochSecond(seconds, nanos.toLong())
    return OffsetDateTime.ofInstant(instant, ZoneOffset.UTC)
}
