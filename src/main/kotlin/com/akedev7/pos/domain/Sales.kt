package com.akedev7.pos.domain

import com.akedev7.pos.controller.Payment
import com.google.protobuf.Timestamp

data class Sales(val startDateTime: Timestamp, val endDateTime: Timestamp)

fun Payment.SalesDataRequest.toDomainObject(): Sales {
    return Sales(this.startDateTime, this.endDateTime)
}