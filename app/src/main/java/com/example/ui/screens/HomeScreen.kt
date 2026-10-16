package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.ui.Screen
import com.example.ui.components.AyahNumberBadge
import com.example.ui.components.TilawaTopBar
import com.example.ui.theme.EmeraldDarkBg
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSurfaceCard
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldBorder
import com.example.ui.theme.TilawaTextMuted
import com.example.ui.theme.TilawaTextPrimary
import com.example.ui.theme.TilawaTextSecondary

@Composable
fun HomeScreen(
    reciters: List<Reciter>,
    surahs: List<Surah>,
    selectedSurah: Surah?,
    selectedReciter: Reciter?,
    onPlaySurah: (Surah, Reciter?) -> Unit,
    onSelectReciter: (Reciter) -> Unit,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDarkBg)
    ) {
        // App Top Bar
        TilawaTopBar(
            title = "تلاوة",
            showSearch = true,
            onSearchClick = { onNavigate(Screen.SEARCH) },
            showNotifications = true,
            onNotificationClick = { onNavigate(Screen.NOTIFICATIONS) }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Fake Search Input Bar leading to Search Screen
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(25.dp))
                        .background(EmeraldSurfaceCard)
                        .border(1.dp, Color(0x3310B981), RoundedCornerShape(25.dp))
                        .clickable { onNavigate(Screen.SEARCH) }
                        .padding(horizontal = 18.dp)
                        .testTag("home_search_bar"),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "بحث",
                            tint = TilawaTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "ابحث عن قارئ أو سورة أو تلاوة...",
                            color = TilawaTextMuted,
                            fontSize = 14.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
            }

            // Quick Features Hub (Comparison, Mushaf, Playlists, Downloads)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    QuickActionCard(
                        title = "مقارنة القراء",
                        icon = Icons.Default.CompareArrows,
                        accentColor = IslamicGold,
                        onClick = { onNavigate(Screen.RECITER_COMPARE) },
                        testTag = "home_quick_compare"
                    )
                    QuickActionCard(
                        title = "قراءة المصحف",
                        icon = Icons.Default.MenuBook,
                        accentColor = EmeraldPrimary,
                        onClick = { onNavigate(Screen.MUSHAF_VIEWER) },
                        testTag = "home_quick_mushaf"
                    )
                    QuickActionCard(
                        title = "قوائم التشغيل",
                        icon = Icons.Default.PlaylistPlay,
                        accentColor = Color(0xFF60A5FA),
                        onClick = { onNavigate(Screen.PLAYLISTS) },
                        testTag = "home_quick_playlists"
                    )
                    QuickActionCard(
                        title = "التنزيلات",
                        icon = Icons.Default.Download,
                        accentColor = Color(0xFFA78BFA),
                        onClick = { onNavigate(Screen.DOWNLOADS) },
                        testTag = "home_quick_downloads"
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Banner "آخر ما استمعته"
            item {
                Text(
                    text = "آخر ما استمعته",
                    color = TilawaTextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            val surah = selectedSurah ?: surahs.getOrNull(1) ?: surahs.first()
                            onPlaySurah(surah, selectedReciter)
                            onNavigate(Screen.PLAYER)
                        }
                        .testTag("home_last_listened_banner"),
                    colors = CardDefaults.cardColors(
                        containerColor = EmeraldSurfaceCard
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0x55F59E0B), Color(0x3310B981))
                        ),
                        width = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF092922))
                                    .border(1.5.dp, IslamicGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_quran_book),
                                    contentDescription = "المصحف الشريف",
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "سورة ${selectedSurah?.nameArabic ?: "البقرة"}",
                                    color = TilawaTextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = selectedReciter?.name ?: "عبد الباسط عبد الصمد",
                                    color = IslamicGold,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Play Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(EmeraldPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "تشغيل",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Carousel "القراء المفضلون"
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "القراء المفضلون",
                        color = TilawaTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "عرض الكل",
                        color = EmeraldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { onNavigate(Screen.RECITERS) }
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp)
                ) {
                    items(reciters.take(8)) { reciter ->
                        Column(
                            modifier = Modifier
                                .width(80.dp)
                                .clickable {
                                    onSelectReciter(reciter)
                                    onNavigate(Screen.RECITERS)
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldSurfaceCard)
                                    .border(2.dp, if (reciter.id == selectedReciter?.id) EmeraldPrimary else Color(0x3310B981), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = reciter.name.firstOrNull()?.toString() ?: "ق",
                                    color = IslamicGold,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = reciter.name,
                                color = TilawaTextPrimary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Quick Surahs Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "سور القرآن الكريم",
                        color = TilawaTextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "قائمة السور",
                        color = EmeraldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { onNavigate(Screen.SURAHS) }
                            .padding(4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // First few surahs quick list
            items(surahs.take(6)) { surah ->
                SurahItemCard(
                    surah = surah,
                    reciter = selectedReciter,
                    onPlay = {
                        onPlaySurah(surah, selectedReciter)
                        onNavigate(Screen.PLAYER)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .width(76.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(EmeraldSurfaceCard)
            .border(1.dp, Color(0x2210B981), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            color = TilawaTextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SurahItemCard(
    surah: Surah,
    reciter: Reciter?,
    onPlay: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onPlay() },
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(Color(0x2210B981), Color(0x11F59E0B))),
            width = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AyahNumberBadge(number = surah.number, size = 36.dp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "سورة ${surah.nameArabic}",
                        color = TilawaTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${surah.revelationType} • ${surah.versesCount} آيات",
                        color = TilawaTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF092922))
                    .border(1.dp, Color(0x4410B981), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "تشغيل",
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
