package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.api.VideoApiService
import org.terratec.altopia.data.remote.dto.response.VideoResponse

/**
 * Implementation of VideoRemoteDataSource.
 * Handles remote data operations and error handling for videos.
 */
class VideoRemoteDataSourceImpl(
    private val apiService: VideoApiService
) : VideoRemoteDataSource {

    override suspend fun getVideos(): Result<List<VideoResponse>> {
        return try {
            val response = apiService.getVideos()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
