package eu.org.animevsub.data.remote

import eu.org.animevsub.data.model.*
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class AnimeApi @Inject constructor(
    private val client: OkHttpClient
) {
    companion object {
        val HOST_CURL: String = intArrayOf(
            97, 110, 105, 109, 101, 118, 105, 101, 116, 115, 117, 98, 46, 109, 120
        ).map { it.toChar() }.joinToString("")

        val BASE_URL: String = intArrayOf(
            104, 116, 116, 112, 115, 58, 47, 47
        ).map { it.toChar() }.joinToString("") + HOST_CURL

        private val USER_AGENT = intArrayOf(
            77, 111, 122, 105, 108, 108, 97, 47, 53, 46, 48, 32, 40, 87, 105, 110, 100,
            111, 119, 115, 32, 78, 84, 32, 49, 48, 46, 48, 59, 32, 87, 105, 110, 54, 52,
            59, 32, 120, 54, 52, 41, 32, 65, 112, 112, 108, 101, 87, 101, 98, 75, 105,
            116, 47, 53, 51, 55, 46, 51, 54, 32, 40, 75, 72, 84, 77, 76, 44, 32, 108,
            105, 107, 101, 32, 71, 101, 99, 107, 111, 41, 32, 67, 104, 114, 111, 109,
            101, 47, 49, 49, 53, 46, 48, 46, 48, 46, 48, 32, 83, 97, 102, 97, 114, 105,
            47, 53, 51, 55, 46, 51, 54
        ).map { it.toChar() }.joinToString("")
    }

    private fun buildRequest(path: String, cookie: String? = null): Request {
        val url = if (path.startsWith("http")) path else "$BASE_URL$path"
        val builder = Request.Builder()
            .url(url)
            .header("User-Agent", USER_AGENT)
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .header("Accept-Language", "vi-VN,vi;q=0.9,en;q=0.8")
            .header("Referer", BASE_URL)
        if (cookie != null) {
            builder.header("Cookie", cookie)
        }
        return builder.build()
    }

    private suspend fun fetchHtml(path: String, cookie: String? = null): String {
        return withContext(Dispatchers.IO) {
            val request = buildRequest(path, cookie)
            val response = client.newCall(request).execute()
            response.body?.string() ?: throw Exception("Empty response body")
        }
    }

    private suspend fun postForm(
        path: String,
        params: Map<String, String>,
        cookie: String? = null
    ): String {
        return withContext(Dispatchers.IO) {
            val formBuilder = FormBody.Builder()
            params.forEach { (key, value) -> formBuilder.add(key, value) }

            val url = if (path.startsWith("http")) path else "$BASE_URL$path"
            val builder = Request.Builder()
                .url(url)
                .post(formBuilder.build())
                .header("User-Agent", USER_AGENT)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Referer", BASE_URL)
            if (cookie != null) {
                builder.header("Cookie", cookie)
            }
            val response = client.newCall(builder.build()).execute()
            response.body?.string() ?: throw Exception("Empty response body")
        }
    }

    // ======== Parsers ========

    private fun parseNamePath(element: Element): NamePath {
        val href = element.attr("href")
        val path = getPathName(href)
        val name = element.text()
        return NamePath(name = name, path = path)
    }

    private fun getPathName(href: String): String {
        return try {
            val url = java.net.URL(href)
            url.path
        } catch (e: Exception) {
            href.removePrefix(BASE_URL)
                .let { if (it.startsWith("/")) it else "/$it" }
        }
    }

    private fun parseAnimeCard(element: Element, now: Long): AnimeCard {
        val anchorHref = element.selectFirst("a")?.attr("href") ?: ""
        val path = getPathName(anchorHref)

        val imgEl = element.selectFirst("img")
        val image = imgEl?.attr("data-cfsrc")
            ?: imgEl?.attr("src") ?: ""

        val name = element.selectFirst(".Title")?.text() ?: ""

        val chapText = element.selectFirst(".mli-eps > i")?.text() ?: ""
        val chap = if (chapText == "TẤT") "Full_Season" else chapText

        val rateText = element.selectFirst(".anime-avg-user-rating")?.text()?.trim()
            ?: element.selectFirst(".AAIco-star")?.text()?.trim() ?: ""
        val rate = rateText.toFloatOrNull() ?: 0f

        val viewsText = element.selectFirst(".Year")?.text()
            ?.let { Regex("[\\d,]+").find(it)?.value?.replace(",", "") }
        val views = viewsText?.toIntOrNull()

        val quality = element.selectFirst(".Qlty")?.text()
            ?: element.selectFirst(".mli-quality")?.text()

        val process = element.selectFirst(".AAIco-access_time")?.text()

        val yearText = element.selectFirst(".AAIco-date_range")?.text() ?: ""
        val year = yearText.filter { it.isDigit() }.toIntOrNull()

        val description = element.selectFirst(".Description > p")?.text()

        val studio = element.selectFirst(".Studio")?.text()
            ?.split(":", limit = 2)?.getOrNull(1)?.trim()

        val genre = element.select(".Genre > a").map { parseNamePath(it) }

        val timescheduleEl = element.selectFirst(".mli-timeschedule")
        val timeRelease = timescheduleEl?.text()?.trim()

        return AnimeCard(
            path = path,
            image = image,
            name = name,
            chap = chap.ifEmpty { null },
            rate = rate,
            views = views,
            quality = quality,
            process = process,
            year = year,
            description = description,
            studio = studio,
            genre = genre,
            timeRelease = timeRelease
        )
    }

    // ======== API Methods ========

    suspend fun getHomePage(): HomeData {
        val html = fetchHtml("/")
        val doc = Jsoup.parse(html)
        val now = System.currentTimeMillis()

        val thisSeason = doc.select(".MovieListTopCn .TPostMv").map { parseAnimeCard(it, now) }
        val carousel = doc.select(".MovieListSldCn .TPostMv").map { parseAnimeCard(it, now) }
        val lastUpdate = doc.select("#single-home .TPostMv").map { parseAnimeCard(it, now) }
        val preRelease = doc.select("#new-home .TPostMv").map { parseAnimeCard(it, now) }
        val nominate = doc.select("#hot-home .TPostMv").map { parseAnimeCard(it, now) }
        val hotUpdate = doc.select("#showTopPhim .TPost").map { parseAnimeCard(it, now) }

        return HomeData(
            thisSeason = thisSeason,
            carousel = carousel,
            lastUpdate = lastUpdate,
            preRelease = preRelease,
            nominate = nominate,
            hotUpdate = hotUpdate
        )
    }

    suspend fun getAnimeDetail(seasonId: String, cookie: String? = null): AnimeDetail {
        val html = fetchHtml("/phim/$seasonId/", cookie)
        val doc = Jsoup.parse(html)
        val now = System.currentTimeMillis()

        val name = doc.selectFirst(".Title")?.text() ?: ""
        val othername = doc.selectFirst(".SubTitle")?.text()
        val image = doc.selectFirst(".Image img")?.attr("src")
        val poster = doc.selectFirst(".TPostBg img")?.attr("src")

        val watchBtn = doc.selectFirst(".watch_button_more")
        val pathToView = watchBtn?.attr("href")?.let { getPathName(it) }

        val description = doc.selectFirst(".Description")?.text()?.trim() ?: ""
        val rate = doc.selectFirst("#average_score")?.text()?.toIntOrNull() ?: 0
        val countRate = doc.selectFirst(".num-rating")?.text()?.toIntOrNull() ?: 0
        val duration = doc.selectFirst(".AAIco-access_time")?.text()
        val yearOf = doc.selectFirst(".AAIco-date_range > a")?.text()?.toIntOrNull()

        val viewsText = doc.selectFirst(".AAIco-remove_red_eye")?.text()
            ?.let { Regex("[\\d,]+").find(it)?.value?.replace(",", "") }
        val views = viewsText?.toIntOrNull() ?: 0

        val season = doc.select(".season_item > a").map { parseNamePath(it) }
        val genre = doc.select(".breadcrumb > li > a").drop(1).dropLast(1).map { parseNamePath(it) }
        val quality = doc.selectFirst(".Qlty")?.text()

        // Info lists
        val infoListLeft = doc.select(".mvici-left > .InfoList > .AAIco-adjust")
        val infoListRight = doc.select(".mvici-right > .InfoList > .AAIco-adjust")

        fun findInfo(infoList: org.jsoup.select.Elements, query: String): Element? {
            return infoList.firstOrNull { el ->
                el.selectFirst("strong")?.text()?.lowercase()?.startsWith(query) == true
            }
        }

        val status = findInfo(infoListLeft, "trạng thái")?.text()
            ?.split(":", limit = 2)?.getOrNull(1)?.trim()
        val authors = findInfo(infoListLeft, "đạo diễn")
            ?.select("a")?.map { parseNamePath(it) } ?: emptyList()
        val countries = findInfo(infoListLeft, "quốc gia")
            ?.select("a")?.map { parseNamePath(it) } ?: emptyList()
        val followsText = findInfo(infoListLeft, "số người theo dõi")?.text()
            ?.split(":", limit = 2)?.getOrNull(1)?.trim()?.replace(",", "")
        val follows = followsText?.toIntOrNull() ?: 0

        val language = findInfo(infoListRight, "ngôn ngữ")?.text()
            ?.split(":", limit = 2)?.getOrNull(1)?.trim()
        val studio = findInfo(infoListRight, "studio")?.text()
            ?.split(":", limit = 2)?.getOrNull(1)?.trim()
        val seasonOfEl = findInfo(infoListRight, "season")?.selectFirst("a")
        val seasonOf = seasonOfEl?.let { parseNamePath(it) }

        val trailer = doc.selectFirst("#Opt1 iframe")?.attr("src")

        val related = doc.select(".MovieListRelated .TPostMv").map { parseAnimeCard(it, now) }

        return AnimeDetail(
            name = name,
            othername = othername,
            image = image,
            poster = poster,
            pathToView = pathToView,
            description = description,
            rate = rate,
            countRate = countRate,
            duration = duration,
            yearOf = yearOf,
            views = views,
            season = season,
            genre = genre,
            quality = quality,
            status = status,
            authors = authors,
            countries = countries,
            follows = follows,
            language = language,
            studio = studio,
            seasonOf = seasonOf,
            trailer = trailer,
            related = related
        )
    }

    suspend fun getChapters(seasonId: String, cookie: String? = null): ChapterData {
        val html = fetchHtml("/phim/$seasonId/", cookie)
        val doc = Jsoup.parse(html)

        val chaps = doc.select("#list-server .list-episode .episode a").map { item ->
            ChapterInfo(
                id = item.attr("data-id"),
                play = item.attr("data-play"),
                hash = item.attr("data-hash"),
                name = item.text().trim()
            )
        }

        val scheduleText = doc.selectFirst(".schedule-title-main > h4 > strong:nth-child(3)")?.text() ?: ""
        val matchResult = Regex("(Thứ [^\\s]+|chủ nhật) vào lúc (\\d+) giờ (\\d+) phút", RegexOption.IGNORE_CASE)
            .find(scheduleText)
        val update = matchResult?.let { match ->
            val dayText = match.groupValues[1].lowercase()
            val hour = match.groupValues[2].toInt()
            val minute = match.groupValues[3].toInt()
            Triple(dayTextToNum(dayText), hour, minute)
        }

        val image = doc.selectFirst(".Image img")?.attr("src") ?: ""
        val poster = doc.selectFirst(".TPostBg img")?.attr("src") ?: ""

        return ChapterData(
            chaps = chaps,
            update = update,
            image = image,
            poster = poster
        )
    }

    private fun dayTextToNum(day: String): Int {
        return when {
            day.contains("hai") -> 1
            day.contains("ba") -> 2
            day.contains("tư") || day.contains("tu") -> 3
            day.contains("năm") || day.contains("nam") -> 4
            day.contains("sáu") || day.contains("sau") -> 5
            day.contains("bảy") || day.contains("bay") -> 6
            day.contains("chủ nhật") || day.contains("chu nhat") -> 0
            else -> -1
        }
    }

    suspend fun getRankings(type: String = "day"): List<RankingItem> {
        val html = fetchHtml("/bang-xep-hang/$type")
        val doc = Jsoup.parse(html)

        return doc.select(".bxh-movie-phimletv > .group").map { item ->
            val imgEl = item.selectFirst("img")
            val image = imgEl?.attr("src")
                ?: extractBackgroundImage(item.attr("style"))

            val path = getPathName(item.selectFirst("a")?.attr("href") ?: "")
            val name = item.selectFirst("a")?.text() ?: ""
            val othername = item.selectFirst(".txt-info")?.text()
            val process = item.selectFirst(".score")?.text()

            RankingItem(
                image = image,
                path = path,
                name = name,
                othername = othername,
                process = process
            )
        }
    }

    private fun extractBackgroundImage(style: String): String {
        val match = Regex("background-image\\s*:\\s*url\\(['\"]?([^'\"\\)]+)['\"]?\\)")
            .find(style)
        return match?.groupValues?.getOrNull(1) ?: ""
    }

    suspend fun getSchedule(): List<ScheduleDay> {
        val html = fetchHtml("/lich-chieu-phim")
        val doc = Jsoup.parse(html)
        val now = System.currentTimeMillis()

        return doc.select("#sched-content > .Homeschedule").mapNotNull { item ->
            val day = item.selectFirst(".Top > h1 > b")?.text() ?: ""
            val topText = item.selectFirst(".Top > h1")?.text() ?: ""
            val numbers = Regex("\\d{1,2}").findAll(topText.split(",", limit = 2).getOrElse(1) { "" })
                .map { it.value }.toList()
            val date = numbers.getOrNull(0)
            val month = numbers.getOrNull(1)

            val items = item.select(".MovieList .TPostMv").map { parseAnimeCard(it, now) }

            if (items.isEmpty()) null
            else ScheduleDay(dayName = day, date = date, month = month, items = items)
        }
    }

    suspend fun preSearch(keyword: String): List<SearchSuggestion> {
        val html = postForm("/ajax/suggest", mapOf(
            "ajaxSearch" to "1",
            "keysearch" to keyword
        ))
        val doc = Jsoup.parse(html)

        return doc.select("li:not(.ss-bottom)").map { li ->
            val anchor = li.selectFirst("a")
            val image = extractBackgroundImage(anchor?.attr("style") ?: "")
            val path = getPathName(anchor?.attr("href") ?: "")
            val name = li.selectFirst(".ss-title")?.text() ?: "unknown"
            val status = li.selectFirst("p")?.text() ?: "unknown"
            SearchSuggestion(image = image, path = path, name = name, status = status)
        }
    }

    suspend fun getCategoryPage(typeNormal: String, value: String, page: Int = 1): CategoryPage {
        val path = if (page > 1) "/$typeNormal/$value/trang-$page" else "/$typeNormal/$value"
        val html = fetchHtml(path)
        val doc = Jsoup.parse(html)
        val now = System.currentTimeMillis()

        val items = doc.select(".TPostMv").map { parseAnimeCard(it, now) }

        val pageLinks = doc.select(".pagination li a")
        val totalPages = pageLinks.mapNotNull {
            it.text().toIntOrNull()
        }.maxOrNull() ?: 1

        val titleEl = doc.selectFirst(".Title") ?: doc.selectFirst("h1")
        val categoryName = titleEl?.text() ?: value

        return CategoryPage(
            items = items,
            totalPages = totalPages,
            currentPage = page,
            name = categoryName
        )
    }

    suspend fun getNotifications(cookie: String): NotificationData {
        val html = postForm("/ajax/notification", mapOf(
            "ShowNotify" to "true"
        ), cookie)
        val doc = Jsoup.parse(html)

        val items = doc.select(".notification-item, .noti-item, li").mapNotNull { item ->
            val anchor = item.selectFirst("a") ?: return@mapNotNull null
            val imgSrc = item.selectFirst("img")?.attr("src")
            val title = item.selectFirst(".noti-title, .title, strong")?.text() ?: ""
            val desc = item.selectFirst(".noti-desc, .description, p")?.text() ?: ""
            val path = getPathName(anchor.attr("href"))
            val time = item.selectFirst(".noti-time, .time, time")?.text()
            val id = item.attr("data-id").ifEmpty {
                anchor.attr("href").hashCode().toString()
            }

            NotificationItem(
                id = id,
                image = imgSrc,
                title = title,
                description = desc,
                path = path,
                time = time ?: ""
            )
        }

        val maxText = doc.selectFirst(".badge, .count")?.text()
        val max = maxText?.toIntOrNull() ?: items.size

        return NotificationData(items = items, max = max)
    }

    suspend fun login(email: String, password: String): Pair<User, String> {
        return withContext(Dispatchers.IO) {
            val formBody = FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .add("submit", "Đăng nhập")
                .build()

            val request = Request.Builder()
                .url("$BASE_URL/ajax/login")
                .post(formBody)
                .header("User-Agent", USER_AGENT)
                .header("Referer", BASE_URL)
                .build()

            val response = client.newCall(request).execute()
            val setCookie = response.headers("Set-Cookie")
                .firstOrNull { it.startsWith("token") }
                ?: throw Exception("Login failed")

            val body = response.body?.string() ?: throw Exception("Empty response")

            val profileHtml = fetchHtml("/account/info/", setCookie.split(";")[0])
            val profileDoc = Jsoup.parse(profileHtml)

            val user = User(
                avatar = profileDoc.selectFirst(".avatar img")?.attr("src"),
                email = email,
                name = profileDoc.selectFirst(".user-name")?.text() ?: email,
                sex = profileDoc.selectFirst("select[name='User[gender]'] option[selected]")?.attr("value") ?: "",
                username = profileDoc.selectFirst("input[name='User[hoten]']")?.attr("value") ?: ""
            )

            Pair(user, setCookie)
        }
    }

    suspend fun getPlayerLink(id: String, play: String, hash: String): String {
        val html = postForm("/ajax/player", mapOf(
            "id" to id,
            "play" to play,
            "hash" to hash
        ))

        val doc = Jsoup.parse(html)
        val link = doc.selectFirst("iframe")?.attr("src")
            ?: doc.selectFirst("source")?.attr("src")
            ?: ""

        if (link.contains(".m3u8")) return link

        // Try to extract from script
        val scriptContent = html
        val m3u8Match = Regex("(https?://[^\"'\\s]+\\.m3u8[^\"'\\s]*)").find(scriptContent)
        return m3u8Match?.value ?: link
    }
}
