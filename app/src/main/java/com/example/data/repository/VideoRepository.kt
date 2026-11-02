package com.example.data.repository

import com.example.data.local.VideoDao
import com.example.data.model.CommentEntity
import com.example.data.model.VideoEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class VideoRepository(private val videoDao: VideoDao) {

    val allVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()
    val userUploadedVideos: Flow<List<VideoEntity>> = videoDao.getUserUploadedVideos()
    val downloadedVideos: Flow<List<VideoEntity>> = videoDao.getDownloadedVideos()
    val likedVideos: Flow<List<VideoEntity>> = videoDao.getLikedVideos()

    fun getVideosByCategory(category: String): Flow<List<VideoEntity>> {
        return if (category == "All") {
            videoDao.getAllVideos()
        } else {
            videoDao.getVideosByCategory(category)
        }
    }

    fun searchVideos(query: String): Flow<List<VideoEntity>> {
        return if (query.isBlank()) {
            videoDao.getAllVideos()
        } else {
            videoDao.searchVideos(query.trim())
        }
    }

    fun getVideoById(id: String): Flow<VideoEntity?> = videoDao.getVideoById(id)

    suspend fun getVideoByIdDirect(id: String): VideoEntity? = videoDao.getVideoByIdDirect(id)

    fun getComments(videoId: String): Flow<List<CommentEntity>> = videoDao.getCommentsForVideo(videoId)

    suspend fun insertVideo(video: VideoEntity) {
        videoDao.insertVideo(video)
    }

    suspend fun deleteVideo(id: String) {
        videoDao.deleteVideoById(id)
    }

    suspend fun toggleLike(id: String) {
        val video = videoDao.getVideoByIdDirect(id) ?: return
        val newLiked = !video.isLiked
        val newLikesCount = if (newLiked) video.likesCount + 1 else (video.likesCount - 1).coerceAtLeast(0)
        videoDao.updateLikeStatus(id, newLiked, newLikesCount)
    }

    suspend fun incrementViews(id: String) {
        videoDao.incrementViews(id)
    }

    suspend fun setDownloaded(id: String, downloaded: Boolean, path: String? = null) {
        videoDao.updateDownloadStatus(id, downloaded, path)
    }

    suspend fun addComment(videoId: String, authorName: String, content: String) {
        val comment = CommentEntity(
            id = UUID.randomUUID().toString(),
            videoId = videoId,
            authorName = authorName.ifBlank { "Guest User" },
            content = content.trim(),
            timestamp = System.currentTimeMillis(),
            likesCount = 0
        )
        videoDao.insertComment(comment)
    }

    suspend fun initializeSeedDataIfNeeded() {
        val count = videoDao.getVideoCount()
        if (count > 0) return

        val seedVideos = listOf(
            VideoEntity(
                id = "seed_1",
                title = "Cinematic Forest & Mountain Flight 4K",
                description = "Breathtaking aerial drone shots over snowy peaks and misty evergreen valleys. Shot with ultra-wide cinema lenses.",
                videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                thumbnailUri = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=800",
                authorName = "AeroCinematics",
                authorAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200",
                uploadTimestamp = System.currentTimeMillis() - 7200000,
                durationSeconds = 15,
                viewsCount = 14200,
                likesCount = 3890,
                category = "Nature",
                isUserUploaded = false
            ),
            VideoEntity(
                id = "seed_2",
                title = "Big Buck Bunny - Animated Comedy Adventure",
                description = "Classic open-source 3D animation classic featuring a kind giant bunny standing up against mischievous forest bullies.",
                videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUri = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800",
                authorName = "Blender Studio",
                authorAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200",
                uploadTimestamp = System.currentTimeMillis() - 14400000,
                durationSeconds = 596,
                viewsCount = 89400,
                likesCount = 21400,
                category = "Animation",
                isUserUploaded = false
            ),
            VideoEntity(
                id = "seed_3",
                title = "Tears of Steel - Sci-Fi VFX Showcase",
                description = "Exploring futuristic robotics and cyberpunk visual effects set in an alternate dystopian future in Amsterdam.",
                videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                thumbnailUri = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=800",
                authorName = "CyberLab FX",
                authorAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200",
                uploadTimestamp = System.currentTimeMillis() - 86400000,
                durationSeconds = 734,
                viewsCount = 45200,
                likesCount = 12900,
                category = "Tech",
                isUserUploaded = false
            ),
            VideoEntity(
                id = "seed_4",
                title = "Extreme Sports: High Octane Stunts",
                description = "Adrenaline-fueled skate and bike tricks captured with high frame rate action cameras.",
                videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                thumbnailUri = "https://images.unsplash.com/photo-1492691527719-9d1e07e534b4?w=800",
                authorName = "ActionPulse",
                authorAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200",
                uploadTimestamp = System.currentTimeMillis() - 172800000,
                durationSeconds = 15,
                viewsCount = 31000,
                likesCount = 8450,
                category = "Sports",
                isUserUploaded = false
            ),
            VideoEntity(
                id = "seed_5",
                title = "Future of Creative AI & Android Coding",
                description = "A deep dive into crafting mobile applications, high performance media architectures, and Jetpack Compose UIs.",
                videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/WeAreGoingOnBullrun.mp4",
                thumbnailUri = "https://images.unsplash.com/photo-1498050108023-c5249f4df085?w=800",
                authorName = "CodeCrafters",
                authorAvatarUrl = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=200",
                uploadTimestamp = System.currentTimeMillis() - 259200000,
                durationSeconds = 15,
                viewsCount = 18900,
                likesCount = 5120,
                category = "Tech",
                isUserUploaded = false
            ),
            VideoEntity(
                id = "seed_6",
                title = "Weekend Comedy & Hilarious Fails",
                description = "Laugh-out-loud funny moments, hilarious pet reactions, and comedy sketches to brighten your day.",
                videoUri = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
                thumbnailUri = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800",
                authorName = "GiggleBox",
                authorAvatarUrl = "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200",
                uploadTimestamp = System.currentTimeMillis() - 345600000,
                durationSeconds = 60,
                viewsCount = 52300,
                likesCount = 17800,
                category = "Comedy",
                isUserUploaded = false
            )
        )

        videoDao.insertVideos(seedVideos)

        // Seed some sample comments
        videoDao.insertComment(
            CommentEntity(
                id = "c_1",
                videoId = "seed_1",
                authorName = "Liam Vance",
                content = "The lighting in the second shot is pure magic! What camera did you use?",
                timestamp = System.currentTimeMillis() - 3600000,
                likesCount = 24
            )
        )
        videoDao.insertComment(
            CommentEntity(
                id = "c_2",
                videoId = "seed_1",
                authorName = "Elena R.",
                content = "Downloaded this to watch on my flight, super clear 4K quality!",
                timestamp = System.currentTimeMillis() - 1800000,
                likesCount = 12
            )
        )
        videoDao.insertComment(
            CommentEntity(
                id = "c_3",
                videoId = "seed_2",
                authorName = "Maya_Tech",
                content = "Timeless open movie! Big Buck Bunny is legendary.",
                timestamp = System.currentTimeMillis() - 7200000,
                likesCount = 45
            )
        )
    }
}
