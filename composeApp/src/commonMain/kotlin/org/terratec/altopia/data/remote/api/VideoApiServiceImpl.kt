package org.terratec.altopia.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.terratec.altopia.data.remote.dto.response.VideoResponse

/**
 * Implementation of VideoApiService using Ktor HttpClient.
 * Handles HTTP communication with Supabase API.
 * Uses a pre-configured HttpClient with base URL and authentication headers already set.
 */
class VideoApiServiceImpl(
    private val httpClient: HttpClient
) : VideoApiService {

    override suspend fun getVideos(): List<VideoResponse> {
        // Use relative URL without leading slash - base URL and headers are configured in the injected HttpClient
        return httpClient.get("Videos?select=*").body()
    }
}
