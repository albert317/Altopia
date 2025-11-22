package org.terratec.altopia.data.remote.api

import org.terratec.altopia.data.remote.dto.response.VideoResponse

/**
 * API Service for Video-related endpoints.
 * Defines the contract for HTTP requests/responses with the backend API.
 */
interface VideoApiService {

    /**
     * Get all videos from Supabase
     * @return List of VideoResponse DTOs
     */
    suspend fun getVideos(): List<VideoResponse>
}
