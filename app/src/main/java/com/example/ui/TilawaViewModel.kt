package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlayerState
import com.example.audio.TilawaAudioPlayer
import com.example.data.local.FavoriteEntity
import com.example.data.model.AyahComparisonItem
import com.example.data.model.DownloadItem
import com.example.data.model.NotificationItem
import com.example.data.model.PlaylistItem
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.repository.TilawaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    SPLASH,
    WELCOME,
    HOME,
    RECITERS,
    SURAHS,
    PLAYER,
    DOWNLOADS,
    FAVORITES,
    PLAYLISTS,
    SEARCH,
    SETTINGS,
    OFFLINE_HUB,
    PROFILE,
    DEVICE_SYNC,
    RECITER_COMPARE,
    MUSHAF_VIEWER,
    NOTIFICATIONS,
    ABOUT
}

class TilawaViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TilawaRepository(application)
    private val audioPlayer = TilawaAudioPlayer(application)

    // Navigation Stack
    private val _currentScreen = MutableStateFlow(Screen.SPLASH)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<Screen>()

    // Reciters & Surahs
    private val _reciters = MutableStateFlow<List<Reciter>>(emptyList())
    val reciters: StateFlow<List<Reciter>> = _reciters.asStateFlow()

    val surahs: List<Surah> = repository.surahs

    private val _selectedReciter = MutableStateFlow<Reciter?>(null)
    val selectedReciter: StateFlow<Reciter?> = _selectedReciter.asStateFlow()

    private val _selectedSurah = MutableStateFlow<Surah?>(surahs[1]) // Default Al-Baqarah
    val selectedSurah: StateFlow<Surah?> = _selectedSurah.asStateFlow()

    // Audio Playback
    val audioState: StateFlow<AudioPlayerState> = audioPlayer.playerState

    // Ayah Comparison
    val ayahComparisons: List<AyahComparisonItem> = repository.getAyahComparisons()
    private val _activeComparisonUrl = MutableStateFlow<String?>(null)
    val activeComparisonUrl: StateFlow<String?> = _activeComparisonUrl.asStateFlow()

    // Search Query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Reciters Filter (الكل, الأكثر استماعاً, ترتيل)
    private val _reciterFilter = MutableStateFlow("الكل")
    val reciterFilter: StateFlow<String> = _reciterFilter.asStateFlow()

    // Surahs Tab (الكل, المفضلة, المشروعة)
    private val _surahTab = MutableStateFlow("الكل")
    val surahTab: StateFlow<String> = _surahTab.asStateFlow()

    // Downloads Tab (الكل, المكتملة, جارية التحميل)
    private val _downloadTab = MutableStateFlow("الكل")
    val downloadTab: StateFlow<String> = _downloadTab.asStateFlow()

    // Database flows
    val favorites: StateFlow<List<FavoriteEntity>> = repository.allFavorites.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val downloads: StateFlow<List<DownloadItem>> = repository.allDownloads.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        listOf(
            DownloadItem("d1", 2, "سورة البقرة", "عبد الباسط عبد الصمد", 45, com.example.data.model.DownloadStatus.DOWNLOADING),
            DownloadItem("d2", 18, "سورة الكهف", "مشاري العفاسي", 100, com.example.data.model.DownloadStatus.COMPLETED),
            DownloadItem("d3", 36, "سورة يس", "عبد الباسط عبد الصمد", 100, com.example.data.model.DownloadStatus.COMPLETED),
            DownloadItem("d4", 67, "سورة الملك", "محمود خليل الحصري", 30, com.example.data.model.DownloadStatus.DOWNLOADING)
        )
    )

    val playlists: StateFlow<List<PlaylistItem>> = repository.allPlaylists.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Notifications
    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem(1, "تم إكتمال تنزيل سورة الكهف", "منذ ساعة"),
            NotificationItem(2, "تم إضافة تلاوة جديدة للشيخ مشاري العفاسي", "منذ 5 ساعات"),
            NotificationItem(3, "مزامنة ناجحة لكافة قوائم التشغيل والمفضلة", "منذ 6 ساعات"),
            NotificationItem(4, "تذكير يومي: استمع اليوم إلى سورة الكهف", "منذ 8 ساعات")
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Settings
    val isDarkMode = MutableStateFlow(true)
    val audioQuality = MutableStateFlow("عالية (192 kbps)")
    val wifiOnlyDownload = MutableStateFlow(true)
    val syncEnabled = MutableStateFlow(true)
    val userEmail = MutableStateFlow("ffkyyr@gmail.com")
    val userName = MutableStateFlow("أحمد محمد")

    init {
        loadReciters()
        audioPlayer.onCompletionListener = {
            playNextSurah()
        }
    }

    private fun loadReciters() {
        viewModelScope.launch {
            val list = repository.getReciters()
            _reciters.value = list
            if (_selectedReciter.value == null && list.isNotEmpty()) {
                _selectedReciter.value = list.first()
            }
        }
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
            return true
        }
        return false
    }

    fun selectReciter(reciter: Reciter) {
        _selectedReciter.value = reciter
    }

    fun selectSurah(surah: Surah) {
        _selectedSurah.value = surah
    }

    fun playSurah(surah: Surah, reciter: Reciter? = null) {
        _selectedSurah.value = surah
        val activeReciter = reciter ?: _selectedReciter.value ?: _reciters.value.firstOrNull()
        if (activeReciter != null) {
            _selectedReciter.value = activeReciter
            val url = repository.getAudioUrl(activeReciter, surah.number)
            _activeComparisonUrl.value = null
            audioPlayer.playUrl(url)
        }
    }

    fun playAyahComparison(item: AyahComparisonItem) {
        _activeComparisonUrl.value = item.audioUrl
        audioPlayer.playUrl(item.audioUrl)
    }

    fun togglePlayPause() {
        audioPlayer.togglePlayPause()
    }

    fun seekTo(positionMs: Int) {
        audioPlayer.seekTo(positionMs)
    }

    fun forward10() {
        audioPlayer.forward10Seconds()
    }

    fun replay10() {
        audioPlayer.replay10Seconds()
    }

    fun toggleRepeat() {
        audioPlayer.toggleRepeat()
    }

    fun cycleSpeed() {
        audioPlayer.cycleSpeed()
    }

    fun playNextSurah() {
        val current = _selectedSurah.value ?: return
        val nextNumber = if (current.number >= 114) 1 else current.number + 1
        val nextSurah = surahs.find { it.number == nextNumber } ?: surahs.first()
        playSurah(nextSurah)
    }

    fun playPrevSurah() {
        val current = _selectedSurah.value ?: return
        val prevNumber = if (current.number <= 1) 114 else current.number - 1
        val prevSurah = surahs.find { it.number == prevNumber } ?: surahs.last()
        playSurah(prevSurah)
    }

    fun toggleFavoriteSurah(surah: Surah) {
        viewModelScope.launch {
            val isFav = favorites.value.any { it.type == "SURAH" && it.targetId == surah.number }
            repository.toggleFavorite(
                type = "SURAH",
                targetId = surah.number,
                title = "سورة ${surah.nameArabic}",
                subtitle = "${surah.revelationType} - ${surah.versesCount} آية",
                current = isFav
            )
        }
    }

    fun toggleFavoriteReciter(reciter: Reciter) {
        viewModelScope.launch {
            val isFav = favorites.value.any { it.type == "RECITER" && it.targetId == reciter.id }
            repository.toggleFavorite(
                type = "RECITER",
                targetId = reciter.id,
                title = reciter.name,
                subtitle = "${reciter.rewaya} (${reciter.recitationsCount} تلاوة)",
                current = isFav
            )
        }
    }

    fun isSurahFavorite(surahNumber: Int): Boolean {
        return favorites.value.any { it.type == "SURAH" && it.targetId == surahNumber }
    }

    fun startDownload(surah: Surah) {
        viewModelScope.launch {
            val reciterName = _selectedReciter.value?.name ?: "القارئ"
            repository.addDownload(surah.number, "سورة ${surah.nameArabic}", reciterName)
        }
    }

    fun removeDownload(id: String) {
        viewModelScope.launch {
            repository.removeDownload(id)
        }
    }

    fun createPlaylist(title: String) {
        viewModelScope.launch {
            if (title.isNotBlank()) {
                repository.createPlaylist(title)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setReciterFilter(filter: String) {
        _reciterFilter.value = filter
    }

    fun setSurahTab(tab: String) {
        _surahTab.value = tab
    }

    fun setDownloadTab(tab: String) {
        _downloadTab.value = tab
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
