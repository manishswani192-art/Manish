package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val videoUri: String,
    val thumbnailUri: String,
    val authorName: String,
    val authorAvatarUrl: String = "",
    val uploadTimestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 120,
    val viewsCount: Int = 0,
    val likesCount: Int = 0,
    val category: String = "Trending",
    val isUserUploaded: Boolean = false,
    val isLiked: Boolean = false,
    val isDownloaded: Boolean = false,
    val localDownloadPath: String? = null
)
