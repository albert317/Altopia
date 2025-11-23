package org.terratec.altopia.data.remote.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import org.terratec.altopia.data.remote.dto.error.SupabaseErrorResponse
import org.terratec.altopia.domain.exception.BusinessError

suspend inline fun <reified T> safeApiCall(
    apiCall: () -> HttpResponse
): T {
    try {
        val response = apiCall()
        if (response.status.isSuccess()) {
            return response.body()
        } else {
            // Try to parse Supabase error
            val errorMsg = try {
                val errorBody = response.body<SupabaseErrorResponse>()
                errorBody.msg ?: errorBody.errorDescription ?: response.status.description
            } catch (e: Exception) {
                response.status.description
            }
            
            throw BusinessError(
                message = errorMsg,
                httpCode = response.status.value,
                codeMessage = "API_ERROR",
                success = false
            )
        }
    } catch (e: BusinessError) {
        throw e
    } catch (e: Exception) {
        throw BusinessError(
            message = e.message ?: "Unknown error",
            codeMessage = "UNKNOWN_ERROR",
            success = false
        )
    }
}
