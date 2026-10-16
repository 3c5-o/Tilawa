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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.ui.components.TilawaTopBar
import com.example.ui.theme.EmeraldDarkBg
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldSurfaceCard
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.TilawaTextMuted
import com.example.ui.theme.TilawaTextPrimary
import com.example.ui.theme.TilawaTextSecondary

@Composable
fun RecitersScreen(
    reciters: List<Reciter>,
    selectedReciter: Reciter?,
    reciterFilter: String,
    onFilterChange: (String) -> Unit,
    onSelectReciter: (Reciter) -> Unit,
    onToggleFavorite: (Reciter) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val filterTabs = listOf("الكل", "الأكثر استماعاً", "الأكثر تسجيلاً", "ترتيل")

    val filteredList = reciters.filter { reciter ->
        val matchesSearch = reciter.name.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (reciterFilter) {
            "الأكثر استماعاً" -> reciter.isPopular
            "الأكثر تسجيلاً" -> reciter.recitationsCount >= 100
            "ترتيل" -> reciter.rewaya.contains("مرتل", ignoreCase = true) || reciter.isPopular
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDarkBg)
    ) {
        TilawaTopBar(
            title = "مكتبة القراء",
            showBack = true,
            onBackClick = onBack
        )

        // Search Input
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text("ابحث عن قارئ...", color = TilawaTextMuted, fontSize = 14.sp)
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
                    .height(52.dp)
                    .testTag("reciters_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = EmeraldSurfaceCard,
                    unfocusedContainerColor = EmeraldSurfaceCard,
                    focusedBorderColor = EmeraldPrimary,
                    unfocusedBorderColor = Color(0x3310B981),
                    focusedTextColor = TilawaTextPrimary,
                    unfocusedTextColor = TilawaTextPrimary
                ),
                shape = RoundedCornerShape(26.dp)
            )
        }

        // Filter Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterTabs) { tab ->
                val isSelected = reciterFilter == tab
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) EmeraldPrimary else EmeraldSurfaceCard)
                        .border(
                            1.dp,
                            if (isSelected) EmeraldPrimary else Color(0x2210B981),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onFilterChange(tab) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("filter_tab_$tab"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.White else TilawaTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Reciters List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList) { reciter ->
                val isSelected = selectedReciter?.id == reciter.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onSelectReciter(reciter) }
                        .testTag("reciter_card_${reciter.id}"),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceCard),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(
                            if (isSelected) EmeraldPrimary else Color(0x2210B981)
                        ),
                        width = if (isSelected) 1.5.dp else 1.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF07211C))
                                    .border(1.5.dp, if (isSelected) EmeraldPrimary else IslamicGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = reciter.name.firstOrNull()?.toString() ?: "ق",
                                    color = IslamicGold,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = reciter.name,
                                    color = TilawaTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = "(${reciter.recitationsCount} تلاوة) • ${reciter.rewaya}",
                                    color = TilawaTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { onToggleFavorite(reciter) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (reciter.isPopular) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "المفضلة",
                                    tint = if (reciter.isPopular) IslamicGold else TilawaTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "عرض",
                                tint = TilawaTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
