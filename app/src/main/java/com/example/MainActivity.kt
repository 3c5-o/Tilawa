package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.Screen
import com.example.ui.TilawaViewModel
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.TilawaBottomNav
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.DeviceSyncScreen
import com.example.ui.screens.DownloadsScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MushafScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OfflineHubScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.PlaylistsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReciterCompareScreen
import com.example.ui.screens.RecitersScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.SurahsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.EmeraldDarkBg
import com.example.ui.theme.TilawaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TilawaTheme {
                // Ensure RTL layout for authentic Arabic Quran experience
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    TilawaApp()
                }
            }
        }
    }
}

@Composable
fun TilawaApp(viewModel: TilawaViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val reciters by viewModel.reciters.collectAsStateWithLifecycle()
    val selectedReciter by viewModel.selectedReciter.collectAsStateWithLifecycle()
    val selectedSurah by viewModel.selectedSurah.collectAsStateWithLifecycle()
    val audioState by viewModel.audioState.collectAsStateWithLifecycle()
    val activeComparisonUrl by viewModel.activeComparisonUrl.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val reciterFilter by viewModel.reciterFilter.collectAsStateWithLifecycle()
    val surahTab by viewModel.surahTab.collectAsStateWithLifecycle()
    val downloadTab by viewModel.downloadTab.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val downloads by viewModel.downloads.collectAsStateWithLifecycle()
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val userEmail by viewModel.userEmail.collectAsStateWithLifecycle()

    val showBottomNav = currentScreen in listOf(
        Screen.HOME,
        Screen.RECITERS,
        Screen.SURAHS,
        Screen.FAVORITES,
        Screen.PROFILE
    )

    val showMiniPlayer = (audioState.isPlaying || audioState.currentUrl.isNotEmpty()) &&
            currentScreen != Screen.PLAYER &&
            currentScreen != Screen.SPLASH &&
            currentScreen != Screen.WELCOME

    // Hardware and gesture Back Button handler
    BackHandler(enabled = currentScreen != Screen.SPLASH && currentScreen != Screen.HOME) {
        if (!viewModel.navigateBack()) {
            viewModel.navigateTo(Screen.HOME)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(EmeraldDarkBg),
        containerColor = EmeraldDarkBg,
        bottomBar = {
            Column(modifier = Modifier.fillMaxWidth()) {
                AnimatedVisibility(
                    visible = showMiniPlayer,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    MiniPlayerBar(
                        surah = selectedSurah,
                        reciter = selectedReciter,
                        audioState = audioState,
                        onPlayPause = { viewModel.togglePlayPause() },
                        onNext = { viewModel.playNextSurah() },
                        onExpand = { viewModel.navigateTo(Screen.PLAYER) }
                    )
                }

                if (showBottomNav) {
                    TilawaBottomNav(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(EmeraldDarkBg)
        ) {
            when (currentScreen) {
                Screen.SPLASH -> {
                    SplashScreen(
                        onStartClick = { viewModel.navigateTo(Screen.WELCOME) }
                    )
                }

                Screen.WELCOME -> {
                    WelcomeScreen(
                        onLoginClick = { viewModel.navigateTo(Screen.HOME) },
                        onRegisterClick = { viewModel.navigateTo(Screen.HOME) },
                        onSkipClick = { viewModel.navigateTo(Screen.HOME) }
                    )
                }

                Screen.HOME -> {
                    HomeScreen(
                        reciters = reciters,
                        surahs = viewModel.surahs,
                        selectedSurah = selectedSurah,
                        selectedReciter = selectedReciter,
                        onPlaySurah = { surah, reciter ->
                            viewModel.playSurah(surah, reciter)
                        },
                        onSelectReciter = { reciter ->
                            viewModel.selectReciter(reciter)
                        },
                        onNavigate = { viewModel.navigateTo(it) }
                    )
                }

                Screen.RECITERS -> {
                    RecitersScreen(
                        reciters = reciters,
                        selectedReciter = selectedReciter,
                        reciterFilter = reciterFilter,
                        onFilterChange = { viewModel.setReciterFilter(it) },
                        onSelectReciter = { reciter ->
                            viewModel.selectReciter(reciter)
                            val surah = selectedSurah ?: viewModel.surahs.first()
                            viewModel.playSurah(surah, reciter)
                            viewModel.navigateTo(Screen.PLAYER)
                        },
                        onToggleFavorite = { reciter ->
                            viewModel.toggleFavoriteReciter(reciter)
                        },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.SURAHS -> {
                    SurahsScreen(
                        surahs = viewModel.surahs,
                        selectedReciter = selectedReciter,
                        surahTab = surahTab,
                        onTabChange = { viewModel.setSurahTab(it) },
                        isSurahFavorite = { viewModel.isSurahFavorite(it) },
                        onToggleFavorite = { viewModel.toggleFavoriteSurah(it) },
                        onDownloadSurah = { viewModel.startDownload(it) },
                        onPlaySurah = { surah ->
                            viewModel.playSurah(surah, selectedReciter)
                            viewModel.navigateTo(Screen.PLAYER)
                        },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.PLAYER -> {
                    val isFav = selectedSurah?.let { viewModel.isSurahFavorite(it.number) } ?: false
                    PlayerScreen(
                        surah = selectedSurah,
                        reciter = selectedReciter,
                        audioState = audioState,
                        isFavorite = isFav,
                        onToggleFavorite = {
                            selectedSurah?.let { viewModel.toggleFavoriteSurah(it) }
                        },
                        onPlayPause = { viewModel.togglePlayPause() },
                        onSeekTo = { viewModel.seekTo(it) },
                        onNextSurah = { viewModel.playNextSurah() },
                        onPrevSurah = { viewModel.playPrevSurah() },
                        onForward10 = { viewModel.forward10() },
                        onReplay10 = { viewModel.replay10() },
                        onToggleRepeat = { viewModel.toggleRepeat() },
                        onCycleSpeed = { viewModel.cycleSpeed() },
                        onOpenCompare = { viewModel.navigateTo(Screen.RECITER_COMPARE) },
                        onOpenMushaf = { viewModel.navigateTo(Screen.MUSHAF_VIEWER) },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.DOWNLOADS -> {
                    DownloadsScreen(
                        downloads = downloads,
                        downloadTab = downloadTab,
                        onTabChange = { viewModel.setDownloadTab(it) },
                        onRemoveDownload = { viewModel.removeDownload(it) },
                        onPlayDownload = { item ->
                            val surah = viewModel.surahs.find { it.number == item.surahNumber }
                            if (surah != null) {
                                viewModel.playSurah(surah, selectedReciter)
                                viewModel.navigateTo(Screen.PLAYER)
                            }
                        },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.FAVORITES -> {
                    FavoritesScreen(
                        favorites = favorites,
                        onPlayFavorite = { item ->
                            val surah = viewModel.surahs.find { it.number == item.targetId }
                            if (surah != null) {
                                viewModel.playSurah(surah, selectedReciter)
                                viewModel.navigateTo(Screen.PLAYER)
                            }
                        },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.PLAYLISTS -> {
                    PlaylistsScreen(
                        playlists = playlists,
                        onCreatePlaylist = { viewModel.createPlaylist(it) },
                        onSelectPlaylist = {
                            val surah = viewModel.surahs.firstOrNull() ?: return@PlaylistsScreen
                            viewModel.playSurah(surah, selectedReciter)
                            viewModel.navigateTo(Screen.PLAYER)
                        },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.SEARCH -> {
                    SearchScreen(
                        searchQuery = searchQuery,
                        onQueryChange = { viewModel.setSearchQuery(it) },
                        reciters = reciters,
                        surahs = viewModel.surahs,
                        onSelectReciter = { reciter ->
                            viewModel.selectReciter(reciter)
                            val surah = selectedSurah ?: viewModel.surahs.first()
                            viewModel.playSurah(surah, reciter)
                            viewModel.navigateTo(Screen.PLAYER)
                        },
                        onSelectSurah = { surah ->
                            viewModel.playSurah(surah, selectedReciter)
                            viewModel.navigateTo(Screen.PLAYER)
                        },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.SETTINGS -> {
                    SettingsScreen(
                        onNavigateAbout = { viewModel.navigateTo(Screen.ABOUT) },
                        onNavigateOffline = { viewModel.navigateTo(Screen.OFFLINE_HUB) },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.OFFLINE_HUB -> {
                    OfflineHubScreen(
                        onNavigateDownloads = { viewModel.navigateTo(Screen.DOWNLOADS) },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.PROFILE -> {
                    ProfileScreen(
                        userName = userName,
                        userEmail = userEmail,
                        onNavigate = { viewModel.navigateTo(it) },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.DEVICE_SYNC -> {
                    DeviceSyncScreen(
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.RECITER_COMPARE -> {
                    ReciterCompareScreen(
                        comparisons = viewModel.ayahComparisons,
                        audioState = audioState,
                        activeComparisonUrl = activeComparisonUrl,
                        onPlayComparison = { item ->
                            viewModel.playAyahComparison(item)
                        },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.MUSHAF_VIEWER -> {
                    MushafScreen(
                        surah = selectedSurah,
                        onPlaySurah = { surah ->
                            viewModel.playSurah(surah, selectedReciter)
                            viewModel.navigateTo(Screen.PLAYER)
                        },
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }

                Screen.ABOUT -> {
                    AboutScreen(
                        onBack = {
                            if (!viewModel.navigateBack()) viewModel.navigateTo(Screen.HOME)
                        }
                    )
                }
            }
        }
    }
}
