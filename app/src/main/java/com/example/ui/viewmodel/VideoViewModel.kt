package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CommentEntity
import com.example.data.model.VideoEntity
import com.example.data.repository.VideoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: VideoRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = VideoRepository(database.videoDao())
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
    }

    // Navigation and screen state
    val selectedTab = MutableStateFlow(0) // 0: Feed, 1: Upload, 2: Downloads, 3: Profile

    // Currently playing video (null if on feed/other tabs)
    private val _activeVideoId = MutableStateFlow<String?>(null)
    val activeVideoId: StateFlow<String?> = _activeVideoId.asStateFlow()

    // Filters
    val selectedCategory = MutableStateFlow("All")
    val searchQuery = MutableStateFlow("")

    // Category list
    val categories = listOf("All", "Trending", "Nature", "Animation", "Tech", "Sports", "Comedy")

    // Reactive list of videos according to category & search
    val feedVideos: StateFlow<List<VideoEntity>> = combine(
        selectedCategory,
        searchQuery
    ) { category, query ->
        Pair(category, query)
    }.flatMapLatest { (category, query) ->
        if (query.isNotBlank()) {
            repository.searchVideos(query)
        } else {
            repository.getVideosByCategory(category)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // User's own uploads
    val userUploads: StateFlow<List<VideoEntity>> = repository.userUploadedVideos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Downloaded offline videos
    val downloadedVideos: StateFlow<List<VideoEntity>> = repository.downloadedVideos.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Active video entity flow
    val activeVideo: StateFlow<VideoEntity?> = _activeVideoId.flatMapLatest { id ->
        if (id == null) {
            MutableStateFlow<VideoEntity?>(null)
        } else {
            repository.getVideoById(id)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Comments for active video
    val activeVideoComments: StateFlow<List<CommentEntity>> = _activeVideoId.flatMapLatest { id ->
        if (id == null) {
            MutableStateFlow(emptyList())
        } else {
            repository.getComments(id)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Upload notification/feedback state
    private val _uploadSuccessMessage = MutableStateFlow<String?>(null)
    val uploadSuccessMessage: StateFlow<String?> = _uploadSuccessMessage.asStateFlow()

    fun playVideo(videoId: String) {
        _activeVideoId.value = videoId
        viewModelScope.launch {
            repository.incrementViews(videoId)
        }
    }

    fun closePlayer() {
        _activeVideoId.value = null
    }

    fun setCategory(category: String) {
        selectedCategory.value = category
    }

    fun setSearch(query: String) {
        searchQuery.value = query
    }

    fun toggleLike(videoId: String) {
        viewModelScope.launch {
            repository.toggleLike(videoId)
        }
    }

    fun toggleDownload(videoId: String, isCurrentlyDownloaded: Boolean) {
        viewModelScope.launch {
            repository.setDownloaded(videoId, !isCurrentlyDownloaded)
        }
    }

    fun deleteVideo(videoId: String) {
        viewModelScope.launch {
            if (_activeVideoId.value == videoId) {
                _activeVideoId.value = null
            }
            repository.deleteVideo(videoId)
        }
    }

    fun addComment(videoId: String, commentText: String) {
        if (commentText.isBlank()) return
        viewModelScope.launch {
            repository.addComment(
                videoId = videoId,
                authorName = "You",
                content = commentText
            )
        }
    }

    fun uploadVideo(
        title: String,
        description: String,
        videoUri: String,
        thumbnailUri: String,
        category: String,
        authorName: String
    ) {
        viewModelScope.launch {
            val newVideo = VideoEntity(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                description = description.trim(),
                videoUri = videoUri.trim(),
                thumbnailUri = thumbnailUri.trim().ifBlank {
                    // Pick attractive default thumbnail based on category
                    when (category) {
                        "Nature" -> "https://images.unsplash.com/photo-1470071459604-3b5ec3a7fe05?w=800"
                        "Tech" -> "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800"
                        "Gaming" -> "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=800"
                        "Comedy" -> "https://images.unsplash.com/photo-1514306191717-452ec28c7814?w=800"
                        else -> "https://images.unsplash.com/photo-1536240478700-b869070f9279?w=800"
                    }
                },
                authorName = authorName.trim().ifBlank { "Creator" },
                authorAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200",
                uploadTimestamp = System.currentTimeMillis(),
                durationSeconds = 60,
                viewsCount = 1,
                likesCount = 0,
                category = category,
                isUserUploaded = true
            )
            repository.insertVideo(newVideo)
            _uploadSuccessMessage.value = "Video successfully uploaded to VidShare community!"
            // Switch to Home feed so user immediately sees their video at the top
            selectedTab.value = 0
            selectedCategory.value = "All"
            searchQuery.value = ""
        }
    }

    fun clearUploadSuccessMessage() {
        _uploadSuccessMessage.value = null
    }
}
