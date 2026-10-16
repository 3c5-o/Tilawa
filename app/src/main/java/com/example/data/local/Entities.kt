package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "SURAH" or "RECITER"
    val targetId: Int,
    val title: String,
    val subtitle: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String, // e.g. "surah_2_reciter_1"
    val surahNumber: Int,
    val surahName: String,
    val reciterName: String,
    val progress: Int,
    val status: String, // "DOWNLOADING", "COMPLETED", "PAUSED"
    val localFilePath: String?,
    val fileSizeFormatted: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val count: Int = 0,
    val iconType: String = "playlist",
    val surahIdsJson: String = "[]",
    val timestamp: Long = System.currentTimeMillis()
)
