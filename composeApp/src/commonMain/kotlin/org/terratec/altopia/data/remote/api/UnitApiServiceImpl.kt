package org.terratec.altopia.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.terratec.altopia.data.remote.dto.BlockDto
import org.terratec.altopia.data.remote.dto.CreateUnitRequest
import org.terratec.altopia.data.remote.dto.UnitDto
import org.terratec.altopia.data.remote.util.safeApiCall

class UnitApiServiceImpl(
    private val httpClient: HttpClient
) : UnitApiService {

    override suspend fun getUnits(): List<UnitDto> {
        return safeApiCall {
            httpClient.post("rpc/get_units_with_details") {
                contentType(ContentType.Application.Json)
            }.body()
        }
    }

    override suspend fun getBlocks(): List<BlockDto> {
        return safeApiCall {
            httpClient.post("rpc/get_blocks_selector") {
                 contentType(ContentType.Application.Json)
            }.body()
        }
    }

    override suspend fun createUnit(request: CreateUnitRequest) {
        return safeApiCall {
            httpClient.post("unidades") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        }
    }

    override suspend fun updateUnit(id: String, request: CreateUnitRequest) {
        return safeApiCall {
            httpClient.patch("unidades?id=eq.$id") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        }
    }

    override suspend fun deleteUnit(id: String) {
        return safeApiCall {
            httpClient.delete("unidades?id=eq.$id") {
                contentType(ContentType.Application.Json)
            }.body()
        }
    }
}
