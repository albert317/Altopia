package org.terratec.altopia.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import org.terratec.altopia.data.mapper.UserMapper
import org.terratec.altopia.data.remote.UserDto
import org.terratec.altopia.data.remote.VideoDto
import org.terratec.altopia.domain.model.User
import org.terratec.altopia.domain.model.Video
import org.terratec.altopia.domain.repository.UserRepository

class UserRepositoryImpl(
    private val httpClient: HttpClient
) : UserRepository {
    
    private val baseUrl = "https://jsonplaceholder.typicode.com" // Example URL

    override suspend fun getUser(userId: Long): Result<User> {
        return try {
            val dto = httpClient.get("$baseUrl/users/$userId").body<UserDto>()
            Result.success(UserMapper.dtoToDomain(dto))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUsers(): Result<List<User>> {
        return try {
            val dtos = httpClient.get("$baseUrl/users").body<List<UserDto>>()
            Result.success(dtos.map { UserMapper.dtoToDomain(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVideos(): Result<List<Video>> {
        return try {
            val dtos = httpClient.get("https://thvijyqaigfbfbbrmknx.supabase.co/rest/v1/Videos?select=*") {
                headers {
                    append("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRodmlqeXFhaWdmYmZiYnJta254Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjMxMzM4ODgsImV4cCI6MjA3ODcwOTg4OH0.CNeEfs62T930qQGsgXUzkdfjdRMV57V8aa59j1vVR4c")
                    append("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRodmlqeXFhaWdmYmZiYnJta254Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjMxMzM4ODgsImV4cCI6MjA3ODcwOTg4OH0.CNeEfs62T930qQGsgXUzkdfjdRMV57V8aa59j1vVR4c")
                }
            }.body<List<VideoDto>>()
            Result.success(dtos.map { UserMapper.dtoToDomain(it) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
