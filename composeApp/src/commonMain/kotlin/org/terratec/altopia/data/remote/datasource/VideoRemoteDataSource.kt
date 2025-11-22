package org.terratec.altopia.data.remote.datasource

import org.terratec.altopia.data.remote.dto.response.VideoResponse

/**
 * Remote Data Source for Video data.
 * Coordinates data fetching from remote sources (API).
 */
interface VideoRemoteDataSource {

    /**
     * Get all videos from remote source
     * @return Result with list of VideoResponse or error
     */
    suspend fun getVideos(): Result<List<VideoResponse>>
}
