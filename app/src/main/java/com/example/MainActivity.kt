package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.FeedScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.UploadScreen
import com.example.ui.screens.WatchScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.VideoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: VideoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                VidShareApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun VidShareApp(viewModel: VideoViewModel) {
    val activeVideoId by viewModel.activeVideoId.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    // Back button handling when on a secondary tab
    BackHandler(enabled = activeVideoId == null && selectedTab != 0) {
        viewModel.selectedTab.value = 0
    }

    if (activeVideoId != null) {
        // Watch Screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            WatchScreen(
                viewModel = viewModel,
                onBack = { viewModel.closePlayer() }
            )
        }
    } else {
        // Main tabs screen with bottom navigation bar
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.selectedTab.value = 0 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 0) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Feed"
                            )
                        },
                        label = { Text("Feed", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_feed_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.selectedTab.value = 1 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 1) Icons.Filled.CloudUpload else Icons.Outlined.CloudUpload,
                                contentDescription = "Upload"
                            )
                        },
                        label = { Text("Upload", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_upload_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.selectedTab.value = 2 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 2) Icons.Filled.DownloadDone else Icons.Outlined.DownloadDone,
                                contentDescription = "Downloads"
                            )
                        },
                        label = { Text("Offline", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        ),
                        modifier = Modifier.testTag("nav_downloads_tab")
                    )

                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { viewModel.selectedTab.value = 3 },
                        icon = {
                            Icon(
                                imageVector = if (selectedTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Channel"
                            )
                        },
                        label = { Text("Channel", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_profile_tab")
                    )
                }
            }
        ) { innerPadding ->
            Crossfade(
                targetState = selectedTab,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { tab ->
                when (tab) {
                    0 -> FeedScreen(
                        viewModel = viewModel,
                        onNavigateToUpload = { viewModel.selectedTab.value = 1 }
                    )
                    1 -> UploadScreen(
                        viewModel = viewModel
                    )
                    2 -> DownloadsScreen(
                        viewModel = viewModel,
                        onNavigateToFeed = { viewModel.selectedTab.value = 0 }
                    )
                    3 -> ProfileScreen(
                        viewModel = viewModel,
                        onNavigateToUpload = { viewModel.selectedTab.value = 1 }
                    )
                }
            }
        }
    }
}
