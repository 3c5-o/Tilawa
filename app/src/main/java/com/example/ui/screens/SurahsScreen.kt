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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.ui.components.AyahNumberBadge
import com.example.ui.components.TilawaTopBar
import com.example.ui.theme.EmeraldDarkBg
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldSurfaceCard
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.TilawaTextMuted
import com.example.ui.theme.TilawaTextPrimary
import com.example.ui.theme.TilawaTextSecondary

@Composable
fun SurahsScreen(
    surahs: List<Surah>,
    selectedReciter: Reciter?,
    surahTab: String,
    onTabChange: (String) -> Unit,
    isSurahFavorite: (Int) -> Boolean,
    onToggleFavorite: (Surah) -> Unit,
    onDownloadSurah: (Surah) -> Unit,
    onPlaySurah: (Surah) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("الكل", "المفضلة", "المشروعة")

    val filteredSurahs = surahs.filter { surah ->
        val matchesSearch = surah.nameArabic.contains(searchQuery) ||
                surah.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                "${surah.number}".contains(searchQuery)

        val matchesTab = when (surahTab) {
            "المفضلة" -> isSurahFavorite(surah.number)
            "المشروعة" -> surah.number in listOf(1, 2, 18, 36, 55, 56, 67, 112, 113, 114)
            else -> true
        }
        matchesSearch && matchesTab
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDarkBg)
    ) {
        TilawaTopBar(
            title = "قائمة السور",
            showBack = true,
            onBackClick = onBack
        )

        // Search Input
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("ابحث عن سورة...", color = TilawaTextMuted, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "بحث",
                        tint = TilawaTextSecondary
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("surahs_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = EmeraldSurfaceCard,
                    unfocusedContainerColor = EmeraldSurfaceCard,
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = Color(0x3310B981),
                    focusedTextColor = TilawaTextPrimary,
                    unfocusedTextColor = TilawaTextPrimary
                ),
                shape = RoundedCornerShape(25.dp)
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = tabs.indexOf(surahTab).coerceAtLeast(0),
            containerColor = EmeraldSurface,
            contentColor = EmeraldPrimary,
            indicator = { tabPositions ->
                val index = tabs.indexOf(surahTab).coerceAtLeast(0)
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                    color = EmeraldPrimary,
                    height = 3.dp
                )
            },
            divider = {}
        ) {
            tabs.forEach { tab ->
                val selected = surahTab == tab
                Tab(
                    selected = selected,
                    onClick = { onTabChange(tab) },
                    text = {
                        Text(
                            text = tab,
                            color = if (selected) EmeraldPrimary else TilawaTextSecondary,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }
        }

        // Surahs List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredSurahs) { surah ->
                val isFav = isSurahFavorite(surah.number)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onPlaySurah(surah) }
                        .testTag("surah_card_${surah.number}"),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceCard),
                    shape = RoundedCornerShape(14.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0x2210B981)),
                        width = 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AyahNumberBadge(number = surah.number, size = 38.dp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "سورة ${surah.nameArabic}",
                                    color = TilawaTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${surah.revelationType} - ${surah.versesCount} آية",
                                    color = TilawaTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Favorite Button
                            IconButton(
                                onClick = { onToggleFavorite(surah) },
                                modifier = Modifier.size(36.dp).testTag("fav_surah_${surah.number}")
                            ) {
                                Icon(
                                    imageVector = if (isFav) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "المفضلة",
                                    tint = if (isFav) IslamicGold else TilawaTextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            // Download Button
                            IconButton(
                                onClick = { onDownloadSurah(surah) },
                                modifier = Modifier.size(36.dp).testTag("download_surah_${surah.number}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = "تحميل",
                                    tint = TilawaTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Play Button
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "تشغيل",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
