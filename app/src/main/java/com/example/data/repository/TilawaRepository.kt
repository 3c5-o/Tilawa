package com.example.data.repository

import android.content.Context
import com.example.data.local.DownloadEntity
import com.example.data.local.FavoriteEntity
import com.example.data.local.PlaylistEntity
import com.example.data.local.TilawaDatabase
import com.example.data.model.AyahComparisonItem
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.PlaylistItem
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.remote.TilawaApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TilawaRepository(context: Context) {
    private val database = TilawaDatabase.getDatabase(context)
    private val favoriteDao = database.favoriteDao()
    private val downloadDao = database.downloadDao()
    private val playlistDao = database.playlistDao()
    private val apiService = TilawaApiService.create()

    // 114 Holy Quran Surahs with exact metadata
    val surahs: List<Surah> = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", "مكية", 7, 1),
        Surah(2, "البقرة", "Al-Baqarah", "مدنية", 286, 2),
        Surah(3, "آل عمران", "Ali 'Imran", "مدنية", 200, 50),
        Surah(4, "النساء", "An-Nisa", "مدنية", 176, 77),
        Surah(5, "المائدة", "Al-Ma'idah", "مدنية", 120, 106),
        Surah(6, "الأنعام", "Al-An'am", "مكية", 165, 128),
        Surah(7, "الأعراف", "Al-A'raf", "مكية", 206, 151),
        Surah(8, "الأنفال", "Al-Anfal", "مدنية", 75, 177),
        Surah(9, "التوبة", "At-Tawbah", "مدنية", 129, 187),
        Surah(10, "يونس", "Yunus", "مكية", 109, 208),
        Surah(11, "هود", "Hud", "مكية", 123, 221),
        Surah(12, "يوسف", "Yusuf", "مكية", 111, 235),
        Surah(13, "الرعد", "Ar-Ra'd", "مدنية", 43, 249),
        Surah(14, "إبراهيم", "Ibrahim", "مكية", 52, 255),
        Surah(15, "الحجر", "Al-Hijr", "مكية", 99, 262),
        Surah(16, "النحل", "An-Nahl", "مكية", 128, 267),
        Surah(17, "الإسراء", "Al-Isra", "مكية", 111, 282),
        Surah(18, "الكهف", "Al-Kahf", "مكية", 110, 293),
        Surah(19, "مريم", "Maryam", "مكية", 98, 305),
        Surah(20, "طه", "Taha", "مكية", 135, 312),
        Surah(21, "الأنبياء", "Al-Anbiya", "مكية", 112, 322),
        Surah(22, "الحج", "Al-Hajj", "مدنية", 78, 332),
        Surah(23, "المؤمنون", "Al-Mu'minun", "مكية", 118, 342),
        Surah(24, "النور", "An-Nur", "مدنية", 64, 350),
        Surah(25, "الفرقان", "Al-Furqan", "مكية", 77, 359),
        Surah(26, "الشعراء", "Ash-Shu'ara", "مكية", 227, 367),
        Surah(27, "النمل", "An-Naml", "مكية", 93, 377),
        Surah(28, "القصص", "Al-Qasas", "مكية", 88, 385),
        Surah(29, "العنكبوت", "Al-'Ankabut", "مكية", 69, 396),
        Surah(30, "الروم", "Ar-Rum", "مكية", 60, 404),
        Surah(31, "لقمان", "Luqman", "مكية", 34, 411),
        Surah(32, "السجدة", "As-Sajdah", "مكية", 30, 415),
        Surah(33, "الأحزاب", "Al-Ahzab", "مدنية", 73, 418),
        Surah(34, "سبأ", "Saba", "مكية", 54, 428),
        Surah(35, "فاطر", "Fatir", "مكية", 45, 434),
        Surah(36, "يس", "Ya-Sin", "مكية", 83, 440),
        Surah(37, "الصافات", "As-Saffat", "مكية", 182, 446),
        Surah(38, "ص", "Sad", "مكية", 88, 453),
        Surah(39, "الزمر", "Az-Zumar", "مكية", 75, 458),
        Surah(40, "غافر", "Ghafir", "مكية", 85, 467),
        Surah(41, "فصلت", "Fussilat", "مكية", 54, 477),
        Surah(42, "الشورى", "Ash-Shura", "مكية", 53, 483),
        Surah(43, "الزخرف", "Az-Zukhruf", "مكية", 89, 489),
        Surah(44, "الدخان", "Ad-Dukhan", "مكية", 59, 496),
        Surah(45, "الجاثية", "Al-Jathiyah", "مكية", 37, 499),
        Surah(46, "الأحقاف", "Al-Ahqaf", "مكية", 35, 502),
        Surah(47, "محمد", "Muhammad", "مدنية", 38, 507),
        Surah(48, "الفتح", "Al-Fath", "مدنية", 29, 511),
        Surah(49, "الحجرات", "Al-Hujurat", "مدنية", 18, 515),
        Surah(50, "ق", "Qaf", "مكية", 45, 518),
        Surah(51, "الذاريات", "Adh-Dhariyat", "مكية", 60, 520),
        Surah(52, "الطور", "At-Tur", "مكية", 49, 523),
        Surah(53, "النجم", "An-Najm", "مكية", 62, 526),
        Surah(54, "القمر", "Al-Qamar", "مكية", 55, 528),
        Surah(55, "الرحمن", "Ar-Rahman", "مدنية", 78, 531),
        Surah(56, "الواقعة", "Al-Waqi'ah", "مكية", 96, 534),
        Surah(57, "الحديد", "Al-Hadid", "مدنية", 29, 537),
        Surah(58, "المجادلة", "Al-Mujadila", "مدنية", 22, 542),
        Surah(59, "الحشر", "Al-Hashr", "مدنية", 24, 545),
        Surah(60, "الممتحنة", "Al-Mumtahanah", "مدنية", 13, 549),
        Surah(61, "الصف", "As-Saff", "مدنية", 14, 551),
        Surah(62, "الجمعة", "Al-Jumu'ah", "مدنية", 11, 553),
        Surah(63, "المنافقون", "Al-Munafiqun", "مدنية", 11, 554),
        Surah(64, "التغابن", "At-Taghabun", "مدنية", 18, 556),
        Surah(65, "الطلاق", "At-Talaq", "مدنية", 12, 558),
        Surah(66, "التحريم", "At-Tahrim", "مدنية", 12, 560),
        Surah(67, "الملك", "Al-Mulk", "مكية", 30, 562),
        Surah(68, "القلم", "Al-Qalam", "مكية", 52, 564),
        Surah(69, "الحاقة", "Al-Haqqah", "مكية", 52, 566),
        Surah(70, "المعارج", "Al-Ma'arij", "مكية", 44, 568),
        Surah(71, "نوح", "Nuh", "مكية", 28, 570),
        Surah(72, "الجن", "Al-Jinn", "مكية", 28, 572),
        Surah(73, "المزمل", "Al-Muzzammil", "مكية", 20, 574),
        Surah(74, "المدثر", "Al-Muddaththir", "مكية", 56, 575),
        Surah(75, "القيامة", "Al-Qiyamah", "مكية", 40, 577),
        Surah(76, "الإنسان", "Al-Insan", "مدنية", 31, 578),
        Surah(77, "المرسلات", "Al-Mursalat", "مكية", 50, 580),
        Surah(78, "النبأ", "An-Naba", "مكية", 40, 582),
        Surah(79, "النازعات", "An-Nazi'at", "مكية", 46, 583),
        Surah(80, "عبس", "'Abasa", "مكية", 42, 585),
        Surah(81, "التكوير", "At-Takwir", "مكية", 29, 586),
        Surah(82, "الانفطار", "Al-Infitar", "مكية", 19, 587),
        Surah(83, "المطففين", "Al-Mutaffifin", "مكية", 36, 587),
        Surah(84, "الانشقاق", "Al-Inshiqaq", "مكية", 25, 589),
        Surah(85, "البروج", "Al-Buruj", "مكية", 22, 590),
        Surah(86, "الطارق", "At-Tariq", "مكية", 17, 591),
        Surah(87, "الأعلى", "Al-A'la", "مكية", 19, 591),
        Surah(88, "الغاشية", "Al-Ghashiyah", "مكية", 26, 592),
        Surah(89, "الفجر", "Al-Fajr", "مكية", 30, 593),
        Surah(90, "البلد", "Al-Balad", "مكية", 20, 594),
        Surah(91, "الشمس", "Ash-Shams", "مكية", 15, 595),
        Surah(92, "الليل", "Al-Layl", "مكية", 21, 595),
        Surah(93, "الضحى", "Ad-Duha", "مكية", 11, 596),
        Surah(94, "الشرح", "Ash-Sharh", "مكية", 8, 596),
        Surah(95, "التين", "At-Tin", "مكية", 8, 597),
        Surah(96, "العلق", "Al-'Alaq", "مكية", 19, 597),
        Surah(97, "القدر", "Al-Qadr", "مكية", 5, 598),
        Surah(98, "البينة", "Al-Bayyinah", "مدنية", 8, 598),
        Surah(99, "الزلزلة", "Az-Zalzalah", "مدنية", 8, 599),
        Surah(100, "العاديات", "Al-'Adiyat", "مكية", 11, 599),
        Surah(101, "القارعة", "Al-Qari'ah", "مكية", 11, 600),
        Surah(102, "التكاثر", "At-Takathur", "مكية", 8, 600),
        Surah(103, "العصر", "Al-'Asr", "مكية", 3, 601),
        Surah(104, "الهمزة", "Al-Humazah", "مكية", 9, 601),
        Surah(105, "الفيل", "Al-Fil", "مكية", 5, 601),
        Surah(106, "قريش", "Quraysh", "مكية", 4, 602),
        Surah(107, "الماعون", "Al-Ma'un", "مكية", 7, 602),
        Surah(108, "الكوثر", "Al-Kawthar", "مكية", 3, 602),
        Surah(109, "الكافرون", "Al-Kafirun", "مكية", 6, 603),
        Surah(110, "النصر", "An-Nasr", "مدنية", 3, 603),
        Surah(111, "المسد", "Al-Masad", "مكية", 5, 603),
        Surah(112, "الإخلاص", "Al-Ikhlas", "مكية", 4, 604),
        Surah(113, "الفلق", "Al-Falaq", "مكية", 5, 604),
        Surah(114, "الناس", "An-Nas", "مكية", 6, 604)
    )

    // Preloaded Top Reciters matching design image
    private val defaultReciters = listOf(
        Reciter(
            id = 54,
            name = "عبد الباسط عبد الصمد",
            rewaya = "حفص عن عاصم - مرتل",
            recitationsCount = 120,
            serverUrl = "https://server7.mp3quran.net/basit",
            isPopular = true,
            letter = "ع"
        ),
        Reciter(
            id = 112,
            name = "مشاري العفاسي",
            rewaya = "حفص عن عاصم",
            recitationsCount = 114,
            serverUrl = "https://server8.mp3quran.net/afs",
            isPopular = true,
            letter = "م"
        ),
        Reciter(
            id = 89,
            name = "محمود خليل الحصري",
            rewaya = "حفص عن عاصم",
            recitationsCount = 105,
            serverUrl = "https://server13.mp3quran.net/husr",
            isPopular = true,
            letter = "م"
        ),
        Reciter(
            id = 96,
            name = "الشيخ محمد صديق المنشاوي",
            rewaya = "حفص عن عاصم - مجود",
            recitationsCount = 98,
            serverUrl = "https://server10.mp3quran.net/minsh",
            isPopular = true,
            letter = "م"
        ),
        Reciter(
            id = 37,
            name = "سعد الغامدي",
            rewaya = "حفص عن عاصم",
            recitationsCount = 90,
            serverUrl = "https://server7.mp3quran.net/s_gmd",
            isPopular = true,
            letter = "س"
        ),
        Reciter(
            id = 104,
            name = "ماهر المعيقلي",
            rewaya = "حفص عن عاصم",
            recitationsCount = 114,
            serverUrl = "https://server12.mp3quran.net/maher",
            isPopular = true,
            letter = "م"
        ),
        Reciter(
            id = 125,
            name = "ياسر الدوسري",
            rewaya = "حفص عن عاصم",
            recitationsCount = 114,
            serverUrl = "https://server11.mp3quran.net/yasser",
            isPopular = true,
            letter = "ي"
        ),
        Reciter(
            id = 66,
            name = "عبد الله المطلق",
            rewaya = "حفص عن عاصم",
            recitationsCount = 45,
            serverUrl = "https://server14.mp3quran.net/mtlq",
            isPopular = false,
            letter = "ع"
        ),
        Reciter(
            id = 67,
            name = "عبد الله الموسى",
            rewaya = "حفص عن عاصم",
            recitationsCount = 38,
            serverUrl = "https://server14.mp3quran.net/mousa",
            isPopular = false,
            letter = "ع"
        ),
        Reciter(
            id = 68,
            name = "عبد الله العبيطي",
            rewaya = "حفص عن عاصم",
            recitationsCount = 22,
            serverUrl = "https://server14.mp3quran.net/abiti",
            isPopular = false,
            letter = "ع"
        ),
        Reciter(
            id = 69,
            name = "عبد الله الجهني",
            rewaya = "حفص عن عاصم",
            recitationsCount = 18,
            serverUrl = "https://server13.mp3quran.net/jhn",
            isPopular = false,
            letter = "ع"
        ),
        Reciter(
            id = 18,
            name = "أبو بكر الشاطري",
            rewaya = "حفص عن عاصم",
            recitationsCount = 114,
            serverUrl = "https://server11.mp3quran.net/shatri",
            isPopular = true,
            letter = "أ"
        )
    )

    suspend fun getReciters(): List<Reciter> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getReciters("ar")
            val remoteReciters = response.reciters?.mapNotNull { dto ->
                val primaryMoshaf = dto.moshaf?.firstOrNull()
                if (primaryMoshaf != null && primaryMoshaf.server.isNotBlank()) {
                    Reciter(
                        id = dto.id,
                        name = dto.name,
                        rewaya = primaryMoshaf.name,
                        recitationsCount = primaryMoshaf.surahTotal ?: 114,
                        serverUrl = primaryMoshaf.server.trimEnd('/'),
                        isPopular = dto.id in listOf(54, 112, 89, 96, 37, 104, 125),
                        letter = dto.letter ?: dto.name.firstOrNull()?.toString() ?: ""
                    )
                } else null
            }

            if (!remoteReciters.isNullOrEmpty()) {
                // Ensure default popular reciters stay at top
                val combined = (defaultReciters + remoteReciters).distinctBy { it.id }
                return@withContext combined
            }
        } catch (_: Exception) {
            // Network failure fallback
        }
        defaultReciters
    }

    fun getAudioUrl(reciter: Reciter, surahNumber: Int): String {
        val formattedNumber = String.format("%03d", surahNumber)
        return "${reciter.serverUrl}/$formattedNumber.mp3"
    }

    // Ayah comparison items for Ayah 255 of Surah Al-Baqarah (as shown in image screen 15)
    fun getAyahComparisons(): List<AyahComparisonItem> {
        return listOf(
            AyahComparisonItem(
                reciterId = 54,
                reciterName = "عبد الباسط عبد الصمد",
                rewaya = "تلاوة مرتلة خاشعة",
                audioUrl = "https://everyayah.com/data/Abdul_Basit_Murattal_192kbps/002255.mp3"
            ),
            AyahComparisonItem(
                reciterId = 112,
                reciterName = "مشاري العفاسي",
                rewaya = "تلاوة عذبة برواية حفص",
                audioUrl = "https://everyayah.com/data/Alafasy_128kbps/002255.mp3"
            ),
            AyahComparisonItem(
                reciterId = 89,
                reciterName = "محمود الحصري",
                rewaya = "تلاوة متقنة بالأحكام",
                audioUrl = "https://everyayah.com/data/Husary_128kbps/002255.mp3"
            ),
            AyahComparisonItem(
                reciterId = 96,
                reciterName = "محمد صديق المنشاوي",
                rewaya = "تلاوة باهرة الخشوع",
                audioUrl = "https://everyayah.com/data/Minshawy_Murattal_128kbps/002255.mp3"
            )
        )
    }

    // Favorites
    val allFavorites: Flow<List<FavoriteEntity>> = favoriteDao.getAllFavorites().flowOn(Dispatchers.IO)

    fun isFavorite(type: String, targetId: Int): Flow<Boolean> =
        favoriteDao.isFavorite(type, targetId).flowOn(Dispatchers.IO)

    suspend fun toggleFavorite(type: String, targetId: Int, title: String, subtitle: String, current: Boolean) =
        withContext(Dispatchers.IO) {
            if (current) {
                favoriteDao.deleteFavorite(type, targetId)
            } else {
                favoriteDao.insertFavorite(
                    FavoriteEntity(type = type, targetId = targetId, title = title, subtitle = subtitle)
                )
            }
        }

    // Downloads
    val allDownloads: Flow<List<DownloadItem>> = downloadDao.getAllDownloads().map { list ->
        list.map { entity ->
            val status = try {
                DownloadStatus.valueOf(entity.status)
            } catch (_: Exception) {
                DownloadStatus.COMPLETED
            }
            DownloadItem(
                id = entity.id,
                surahNumber = entity.surahNumber,
                surahName = entity.surahName,
                reciterName = entity.reciterName,
                progress = entity.progress,
                status = status,
                localFilePath = entity.localFilePath,
                fileSizeFormatted = entity.fileSizeFormatted
            )
        }
    }.flowOn(Dispatchers.IO)

    suspend fun addDownload(surahNumber: Int, surahName: String, reciterName: String) = withContext(Dispatchers.IO) {
        val id = "surah_${surahNumber}_${reciterName.hashCode()}"
        downloadDao.insertOrUpdate(
            DownloadEntity(
                id = id,
                surahNumber = surahNumber,
                surahName = surahName,
                reciterName = reciterName,
                progress = 100,
                status = "COMPLETED",
                localFilePath = null,
                fileSizeFormatted = "18.4 MB"
            )
        )
    }

    suspend fun removeDownload(id: String) = withContext(Dispatchers.IO) {
        downloadDao.deleteDownload(id)
    }

    // Playlists
    val allPlaylists: Flow<List<PlaylistItem>> = playlistDao.getAllPlaylists().map { list ->
        if (list.isEmpty()) {
            listOf(
                PlaylistItem(1, "قائمة العائلة", 12, "family"),
                PlaylistItem(2, "تلاوات مميزة", 8, "star"),
                PlaylistItem(3, "للاستماع لاحقاً", 5, "clock"),
                PlaylistItem(4, "سور قصيرة", 7, "book")
            )
        } else {
            list.map { PlaylistItem(it.id, it.title, it.count, it.iconType) }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun createPlaylist(title: String) = withContext(Dispatchers.IO) {
        playlistDao.insertPlaylist(
            PlaylistEntity(title = title, count = 1, iconType = "playlist")
        )
    }
}
