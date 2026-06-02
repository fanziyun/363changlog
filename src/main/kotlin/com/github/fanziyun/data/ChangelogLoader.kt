package com.github.fanziyun.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.github.fanziyun.Changelog
import net.fabricmc.loader.api.FabricLoader
import java.io.*
import java.net.HttpURLConnection
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import com.github.fanziyun.util.ColorUtil

/**
 * 更新日志加载器
 * 支持: 本地资源加载、远程 URL 加载、ETag 缓存
 */
object ChangelogLoader {

    private val gson: Gson = GsonBuilder().create()
    private val cacheDir: Path = FabricLoader.getInstance().getGameDir().resolve(".cache")

    @Volatile
    var isLoaded: Boolean = false
        private set

    @Volatile
    var isError: Boolean = false
        private set

    @Volatile
    var errorMessage: String = ""
        private set

    private val _data = AtomicReference(createDefaultData())
    val data: ChangelogData get() = _data.get()

    val latestVersionFromData: String
        get() = data.entriesOrEmpty.firstOrNull()?.versionOrEmpty ?: ""

    private val loading = AtomicBoolean(false)
    private var cachedEtag: String = ""

    /** 加载数据（优先远程，失败回退本地） */
    fun load(remoteUrl: String, forceRefresh: Boolean = false): CompletableFuture<Boolean> {
        if (!loading.compareAndSet(false, true)) {
            return CompletableFuture.completedFuture(true)
        }
        return CompletableFuture.supplyAsync {
            try {
                val success = if (remoteUrl.isNotBlank() && !forceRefresh) {
                    loadFromRemote(remoteUrl)
                } else if (remoteUrl.isNotBlank() && forceRefresh) {
                    loadFromRemote(remoteUrl, forceRefresh = true)
                } else {
                    false
                }

                if (!success) {
                    // 尝试缓存
                    if (!tryLoadCache()) {
                        // 回退本地资源
                        loadFromResources()
                    }
                }

                isLoaded = true
                isError = false
                true
            } catch (e: Exception) {
                isError = true
                errorMessage = e.message ?: "Unknown error"
                Changelog.LOGGER.error("Failed to load changelog", e)
                // 最后尝试缓存
                if (!tryLoadCache()) {
                    _data.set(createDefaultData())
                }
                isLoaded = true
                false
            } finally {
                loading.set(false)
            }
        }
    }

    /** 从远程加载 */
    private fun loadFromRemote(urlStr: String, forceRefresh: Boolean = false): Boolean {
        val url = URI.create(urlStr).toURL()
        val conn = url.openConnection() as HttpURLConnection
        return try {
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 10000
            conn.setRequestProperty("User-Agent", "363Changelog/1.0")

            // ETag 缓存检查
            if (!forceRefresh && cachedEtag.isNotBlank()) {
                conn.setRequestProperty("If-None-Match", "\"$cachedEtag\"")
            }

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                Changelog.LOGGER.info("Changelog cache valid (304)")
                return tryLoadCache()
            }

            if (responseCode != HttpURLConnection.HTTP_OK) return false

            // 读取新 ETag
            conn.headerFields["ETag"]?.firstOrNull()?.let { etag ->
                cachedEtag = etag.replace("\"", "").replace("W/", "").trim()
            }

            // 解析数据
            val data = conn.inputStream.use { stream ->
                parseJson(stream.readAllBytes())
            } ?: return false

            _data.set(data)

            // 写缓存
            saveCache(data)

            Changelog.LOGGER.info(" Changelog loaded from remote, ${data.entriesOrEmpty.size} entries")
            true
        } finally {
            conn.disconnect()
        }
    }

    /** 从 JAR 资源文件加载 */
    private fun loadFromResources(): Boolean {
        val stream = ChangelogLoader::class.java.getResourceAsStream("/changelog.json")
            ?: return false

        return try {
            val data = parseJson(stream.readAllBytes()) ?: return false
            _data.set(data)
            Changelog.LOGGER.info(" Changelog loaded from resources, ${data.entriesOrEmpty.size} entries")
            true
        } catch (e: Exception) {
            Changelog.LOGGER.error("Failed to load changelog from resources", e)
            false
        }
    }

    /** 从缓存加载 */
    private fun tryLoadCache(): Boolean {
        return try {
            val cacheFile = cacheDir.resolve("changelog_cache.json")
            if (Files.exists(cacheFile)) {
                val data = parseJson(Files.readAllBytes(cacheFile)) ?: return false
                _data.set(data)
                Changelog.LOGGER.info("Changelog loaded from cache")
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    /** 保存缓存 */
    private fun saveCache(data: ChangelogData) {
        try {
            Files.createDirectories(cacheDir)
            val cacheFile = cacheDir.resolve("changelog_cache.json")
            Files.write(cacheFile, gson.toJson(data).toByteArray(StandardCharsets.UTF_8))

            // 保存 ETag
            if (cachedEtag.isNotBlank()) {
                Files.writeString(cacheDir.resolve("changelog_cache.etag"), cachedEtag)
            }
        } catch (e: Exception) {
            Changelog.LOGGER.warn("Failed to write changelog cache", e)
        }
    }

    /** 解析 JSON 字节数组 */
    private fun parseJson(bytes: ByteArray): ChangelogData? {
        return try {
            val jsonStr = String(bytes, StandardCharsets.UTF_8)
            gson.fromJson(jsonStr, ChangelogData::class.java)
        } catch (e: Exception) {
            Changelog.LOGGER.error("Failed to parse changelog JSON", e)
            null
        }
    }

    /** 清空缓存并强制重新加载 */
    fun clearCache() {
        try {
            Files.deleteIfExists(cacheDir.resolve("changelog_cache.json"))
            Files.deleteIfExists(cacheDir.resolve("changelog_cache.etag"))
        } catch (_: Exception) {}
        cachedEtag = ""
    }

    private fun createDefaultData() = ChangelogData(
        footer = "",
        tagColors = emptyMap(),
        entries = emptyList()
    )
}