package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CommentEntity
import com.example.data.model.VideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Query("SELECT * FROM videos ORDER BY uploadTimestamp DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE category = :category ORDER BY uploadTimestamp DESC")
    fun getVideosByCategory(category: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR authorName LIKE '%' || :query || '%' ORDER BY uploadTimestamp DESC")
    fun searchVideos(query: String): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    fun getVideoById(id: String): Flow<VideoEntity?>

    @Query("SELECT * FROM videos WHERE id = :id LIMIT 1")
    suspend fun getVideoByIdDirect(id: String): VideoEntity?

    @Query("SELECT * FROM videos WHERE isUserUploaded = 1 ORDER BY uploadTimestamp DESC")
    fun getUserUploadedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isDownloaded = 1 ORDER BY uploadTimestamp DESC")
    fun getDownloadedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT * FROM videos WHERE isLiked = 1 ORDER BY uploadTimestamp DESC")
    fun getLikedVideos(): Flow<List<VideoEntity>>

    @Query("SELECT COUNT(*) FROM videos")
    suspend fun getVideoCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Update
    suspend fun updateVideo(video: VideoEntity)

    @Query("DELETE FROM videos WHERE id = :id")
    suspend fun deleteVideoById(id: String)

    @Query("UPDATE videos SET isLiked = :isLiked, likesCount = :likesCount WHERE id = :id")
    suspend fun updateLikeStatus(id: String, isLiked: Boolean, likesCount: Int)

    @Query("UPDATE videos SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: String)

    @Query("UPDATE videos SET isDownloaded = :isDownloaded, localDownloadPath = :localPath WHERE id = :id")
    suspend fun updateDownloadStatus(id: String, isDownloaded: Boolean, localPath: String?)

    // Comments
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY timestamp DESC")
    fun getCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("DELETE FROM comments WHERE id = :id")
    suspend fun deleteComment(id: String)
}
