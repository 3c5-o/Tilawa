package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val isRepeating: Boolean = false,
    val playbackSpeed: Float = 1.0f,
    val currentUrl: String = "",
    val error: String? = null
)

class TilawaAudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    var onCompletionListener: (() -> Unit)? = null

    private val progressUpdater = object : Runnable {
        override fun run() {
            mediaPlayer?.let { player ->
                try {
                    if (player.isPlaying) {
                        _playerState.value = _playerState.value.copy(
                            currentPositionMs = player.currentPosition,
                            durationMs = player.duration.coerceAtLeast(1)
                        )
                    }
                } catch (_: Exception) {
                }
            }
            handler.postDelayed(this, 500)
        }
    }

    init {
        handler.post(progressUpdater)
    }

    fun playUrl(url: String) {
        if (url.isBlank()) return

        // If same URL and paused, just resume
        if (url == _playerState.value.currentUrl && mediaPlayer != null) {
            if (!_playerState.value.isPlaying) {
                resume()
                return
            }
        }

        releasePlayer()
        _playerState.value = _playerState.value.copy(
            isLoading = true,
            currentUrl = url,
            error = null
        )

        try {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(url)
                setOnPreparedListener { mp ->
                    _playerState.value = _playerState.value.copy(
                        isLoading = false,
                        isPlaying = true,
                        durationMs = mp.duration.coerceAtLeast(1),
                        currentPositionMs = 0
                    )
                    applySpeed(_playerState.value.playbackSpeed)
                    mp.start()
                }
                setOnCompletionListener {
                    if (_playerState.value.isRepeating) {
                        it.seekTo(0)
                        it.start()
                    } else {
                        _playerState.value = _playerState.value.copy(isPlaying = false, currentPositionMs = 0)
                        onCompletionListener?.invoke()
                    }
                }
                setOnErrorListener { _, what, extra ->
                    _playerState.value = _playerState.value.copy(
                        isLoading = false,
                        isPlaying = false,
                        error = "خطأ في تشغيل الملف الصوتي ($what, $extra)"
                    )
                    true
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            _playerState.value = _playerState.value.copy(
                isLoading = false,
                isPlaying = false,
                error = e.localizedMessage
            )
        }
    }

    fun togglePlayPause() {
        if (_playerState.value.isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _playerState.value = _playerState.value.copy(isPlaying = false)
            }
        }
    }

    fun resume() {
        mediaPlayer?.let {
            it.start()
            _playerState.value = _playerState.value.copy(isPlaying = true)
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.let {
            val target = positionMs.coerceIn(0, it.duration)
            it.seekTo(target)
            _playerState.value = _playerState.value.copy(currentPositionMs = target)
        }
    }

    fun forward10Seconds() {
        mediaPlayer?.let {
            val target = (it.currentPosition + 10000).coerceAtMost(it.duration)
            it.seekTo(target)
        }
    }

    fun replay10Seconds() {
        mediaPlayer?.let {
            val target = (it.currentPosition - 10000).coerceAtLeast(0)
            it.seekTo(target)
        }
    }

    fun toggleRepeat() {
        val newRepeat = !_playerState.value.isRepeating
        mediaPlayer?.isLooping = newRepeat
        _playerState.value = _playerState.value.copy(isRepeating = newRepeat)
    }

    fun cycleSpeed(): Float {
        val speeds = listOf(1.0f, 1.25f, 1.5f, 0.75f)
        val current = _playerState.value.playbackSpeed
        val nextIndex = (speeds.indexOf(current) + 1) % speeds.size
        val newSpeed = speeds[nextIndex]
        applySpeed(newSpeed)
        _playerState.value = _playerState.value.copy(playbackSpeed = newSpeed)
        return newSpeed
    }

    private fun applySpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mediaPlayer?.let {
                try {
                    val params = it.playbackParams
                    params.speed = speed
                    it.playbackParams = params
                } catch (_: Exception) {
                }
            }
        }
    }

    fun release() {
        handler.removeCallbacks(progressUpdater)
        releasePlayer()
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
    }
}
