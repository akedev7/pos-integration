package com.akedev7.pos.application.service

import com.akedev7.pos.adapters.postgres.SalesRepository
import com.akedev7.pos.domain.model.SalesSummary
import com.akedev7.pos.domain.port.ISalesService
import org.springframework.stereotype.Service
import java.time.OffsetDateTime


@Service
class SalesService(private val salesRepository: SalesRepository) : ISalesService {
    override fun getSalesSummary(startDateTime: OffsetDateTime, endDateTime: OffsetDateTime): SalesSummary {
        return salesRepository.getSales(startDateTime, endDateTime)
    }

}