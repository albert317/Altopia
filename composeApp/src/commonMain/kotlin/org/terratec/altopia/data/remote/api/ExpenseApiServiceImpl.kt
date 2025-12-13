package org.terratec.altopia.data.remote.api

import kotlinx.coroutines.delay

class ExpenseApiServiceImpl : ExpenseApiService {
    override suspend fun getBuildingExpenses(): List<ExpenseResponse> {
        delay(1500)
        return listOf(
            ExpenseResponse("1", "Seguridad y Vigilancia", 3500.00),
            ExpenseResponse("2", "Jardinería y Limpieza", 1200.00),
            ExpenseResponse("3", "Luz de Áreas Comunes", 850.50),
            ExpenseResponse("4", "Mantenimiento Ascensores", 600.00)
        )
    }
}
