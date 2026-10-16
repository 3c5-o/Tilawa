package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val revelationType: String, // "مكية" or "مدنية"
    val versesCount: Int,
    val pageNumber: Int = 1
)

data class Reciter(
    val id: Int,
    val name: String,
    val rewaya: String = "حفص عن عاصم",
    val recitationsCount: Int = 114,
    val serverUrl: String,
    val avatarUrl: String = "",
    val isPopular: Boolean = false,
    val letter: String = ""
)

data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val textArabic: String,
    val audioUrl: String = ""
)

data class AyahComparisonItem(
    val reciterId: Int,
    val reciterName: String,
    val rewaya: String,
    val audioUrl: String,
    val avatarUrl: String = ""
)

enum class DownloadStatus {
    DOWNLOADING,
    COMPLETED,
    PAUSED,
    FAILED
}

data class DownloadItem(
    val id: String,
    val surahNumber: Int,
    val surahName: String,
    val reciterName: String,
    val progress: Int,
    val status: DownloadStatus,
    val localFilePath: String? = null,
    val fileSizeFormatted: String = "18.4 MB"
)

data class PlaylistItem(
    val id: Long,
    val title: String,
    val count: Int,
    val iconType: String = "star"
)

data class NotificationItem(
    val id: Long,
    val title: String,
    val timeAgo: String,
    val isRead: Boolean = false
)

// DTOs for Mp3Quran API
@JsonClass(generateAdapter = true)
data class Mp3QuranRecitersResponse(
    @Json(name = "reciters") val reciters: List<Mp3QuranReciterDto>?
)

@JsonClass(generateAdapter = true)
data class Mp3QuranReciterDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "letter") val letter: String?,
    @Json(name = "moshaf") val moshaf: List<Mp3QuranMoshafDto>?
)

@JsonClass(generateAdapter = true)
data class Mp3QuranMoshafDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "server") val server: String,
    @Json(name = "surah_total") val surahTotal: Int?,
    @Json(name = "surah_list") val surahList: String?
)
