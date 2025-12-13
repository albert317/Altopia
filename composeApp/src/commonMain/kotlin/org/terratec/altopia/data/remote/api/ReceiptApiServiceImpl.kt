package org.terratec.altopia.data.remote.api

import kotlinx.coroutines.delay

class ReceiptApiServiceImpl : ReceiptApiService {
    override suspend fun getLastReceipt(): ReceiptResponse {
        delay(1000) // Simulate network delay
        return ReceiptResponse(
            id = "REC-2025-11",
            period = "Noviembre 2025",
            dueDate = "15/12/2025",
            status = "PENDIENTE",
            subtotalMaintenance = 150.00,
            subtotalServices = 30.00,
            total = 180.00
        )
    }

    override suspend fun getDebtSummary(): DebtSummaryResponse {
        delay(800)
        return DebtSummaryResponse(
            totalDebt = 180.00,
            isOverdue = false
        )
    }
}
