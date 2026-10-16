package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioPlayerState
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.ui.components.OrnateMushafFrame
import com.example.ui.theme.EmeraldDarkBg
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSurfaceCard
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBorder
import com.example.ui.theme.TilawaTextMuted
import com.example.ui.theme.TilawaTextPrimary
import com.example.ui.theme.TilawaTextSecondary

@Composable
fun PlayerScreen(
    surah: Surah?,
    reciter: Reciter?,
    audioState: AudioPlayerState,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onPlayPause: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onNextSurah: () -> Unit,
    onPrevSurah: () -> Unit,
    onForward10: () -> Unit,
    onReplay10: () -> Unit,
    onToggleRepeat: () -> Unit,
    onCycleSpeed: () -> Unit,
    onOpenCompare: () -> Unit,
    onOpenMushaf: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val duration = audioState.durationMs
    val currentPos = audioState.currentPositionMs
    val sliderValue = if (duration > 0) (currentPos.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f

    fun formatTime(ms: Int): String {
        val totalSec = ms / 1000
        val min = totalSec / 60
        val sec = totalSec % 60
        return String.format("%02d:%02d", min, sec)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF041512),
                        Color(0xFF07241E),
                        Color(0xFF041512)
                    )
                )
            )
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("player_back_btn")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "الرجوع",
                        tint = TilawaTextPrimary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "سورة ${surah?.nameArabic ?: "البقرة"}",
                        color = TilawaTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = reciter?.name ?: "عبد الباسط عبد الصمد",
                        color = IslamicGold,
                        fontSize = 13.sp
                    )
                }

                IconButton(onClick = onToggleFavorite, modifier = Modifier.testTag("player_fav_btn")) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "المفضلة",
                        tint = if (isFavorite) IslamicGold else TilawaTextPrimary
                    )
                }
            }

            // Ornate Mushaf Center Frame
            OrnateMushafFrame(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Golden Surah Title Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF13362E))
                            .border(1.5.dp, IslamicGoldBorder, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "سُورَةُ ${surah?.nameArabic ?: "البقرة"}",
                            color = IslamicGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                        color = Color(0xFF1B2E28),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (surah?.number == 1) {
                            "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ ﴿١﴾ الرَّحْمَٰنِ الرَّحِيمِ ﴿٢﴾ مَالِكِ يَوْمِ الدِّينِ ﴿٣﴾ إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ ﴿٤﴾"
                        } else {
                            "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ"
                        },
                        color = Color(0xFF111827),
                        fontSize = 17.sp,
                        lineHeight = 32.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F362E))
                                .clickable { onOpenMushaf() }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "تصفح كامل المصحف",
                                color = IslamicGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Quick Comparison Callout Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(EmeraldSurfaceCard)
                    .border(1.dp, Color(0x3310B981), RoundedCornerShape(16.dp))
                    .clickable { onOpenCompare() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("player_compare_btn"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = "مقارنة القراء",
                        tint = IslamicGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مقارنة أصوات كبار القراء لهذه الآية",
                        color = TilawaTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Progress Slider and Times
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = sliderValue,
                    onValueChange = { frac ->
                        if (duration > 0) {
                            onSeekTo((frac * duration).toInt())
                        }
                    },
                    modifier = Modifier.fillMaxWidth().testTag("player_progress_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = IslamicGold,
                        activeTrackColor = EmeraldPrimary,
                        inactiveTrackColor = Color(0x3310B981)
                    )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(currentPos),
                        color = TilawaTextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = formatTime(duration),
                        color = TilawaTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            // Playback Controls Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Repeat Ayah
                IconButton(onClick = onToggleRepeat, modifier = Modifier.testTag("player_repeat_btn")) {
                    Icon(
                        imageVector = if (audioState.isRepeating) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "تكرار",
                        tint = if (audioState.isRepeating) IslamicGold else TilawaTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Skip Previous
                IconButton(onClick = onPrevSurah, modifier = Modifier.testTag("player_prev_btn")) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "السورة السابقة",
                        tint = TilawaTextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Replay 10s
                IconButton(onClick = onReplay10, modifier = Modifier.testTag("player_replay10_btn")) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "تأخير 10 ثواني",
                        tint = TilawaTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Main Play/Pause Button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(EmeraldPrimary)
                        .border(2.dp, IslamicGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (audioState.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        IconButton(
                            onClick = onPlayPause,
                            modifier = Modifier.size(68.dp).testTag("player_main_play_pause")
                        ) {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (audioState.isPlaying) "إيقاف مؤقت" else "تشغيل",
                                tint = Color.White,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                }

                // Forward 10s
                IconButton(onClick = onForward10, modifier = Modifier.testTag("player_forward10_btn")) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "تقديم 10 ثواني",
                        tint = TilawaTextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Skip Next
                IconButton(onClick = onNextSurah, modifier = Modifier.testTag("player_next_btn")) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "السورة التالية",
                        tint = TilawaTextPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Speed Cycle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF09241E))
                        .clickable { onCycleSpeed() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("player_speed_btn")
                ) {
                    Text(
                        text = "${audioState.playbackSpeed}x",
                        color = IslamicGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
