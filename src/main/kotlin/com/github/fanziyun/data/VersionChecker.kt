package com.github.fanziyun.data

import com.github.fanziyun.Changelog
import java.util.concurrent.CompletableFuture

/**
 * 版本检测服务
 */
object VersionChecker {

    @Volatile
    var isChecking: Boolean = false
        private set

    @Volatile
    var isDone: Boolean = false
        private set

    @Volatile
    var hasUpdate: Boolean = false
        private set

    @Volatile
    var latestVersion: String = ""
        private set

    @Volatile
    var currentVersion: String = ""
        private set

    /** 异步检查更新（复用 ChangelogLoader 已加载的数据，避免重复 HTTP 请求） */
    fun checkAsync(modpackVersion: String) {
        if (isChecking || modpackVersion.isBlank()) {
            isDone = true
            return
        }

        currentVersion = modpackVersion
        isChecking = true

        CompletableFuture.runAsync {
            try {
                val latest = ChangelogLoader.latestVersionFromData
                latestVersion = latest
                hasUpdate = latest.isNotBlank() && latest != modpackVersion
                Changelog.LOGGER.info(
                    "Version check: current={}, latest={}, hasUpdate={}",
                    modpackVersion, latest, hasUpdate
                )
            } catch (e: Exception) {
                Changelog.LOGGER.error("Version check failed", e)
            } finally {
                isChecking = false
                isDone = true
            }
        }
    }

}