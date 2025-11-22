package org.terratec.altopia.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import org.terratec.altopia.data.remote.dto.response.VideoResponse

/**
 * Implementation of VideoApiService using Ktor HttpClient.
 * Handles HTTP communication with Supabase API.
 */
class VideoApiServiceImpl(
    private val httpClient: HttpClient
) : VideoApiService {
    
    private val baseUrl = "https://thvijyqaigfbfbbrmknx.supabase.co/rest/v1"
    private val apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRodmlqeXFhaWdmYmZiYnJta254Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjMxMzM4ODgsImV4cCI6MjA3ODcwOTg4OH0.CNeEfs62T930qQGsgXUzkdfjdRMV57V8aa59j1vVR4c"
    
    override suspend fun getVideos(): List<VideoResponse> {
        return httpClient.get("$baseUrl/Videos?select=*") {
            headers {
                append("apikey", apiKey)
                append("Authorization", "Bearer $apiKey")
            }
        }.body()
    }
}
