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
                hasUpdate = latest.isNotBlank() && compareSemver(latest, modpackVersion) > 0
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

    // 语义化版本比较，返回正数代表 a > b，负数 a < b，0 相等
    private fun compareSemver(a: String, b: String): Int {
        val aParts = a.split('.').mapNotNull { it.toIntOrNull() }
        val bParts = b.split('.').mapNotNull { it.toIntOrNull() }
        val maxLen = maxOf(aParts.size, bParts.size)
        for (i in 0 until maxLen) {
            val aNum = aParts.getOrElse(i) { 0 }
            val bNum = bParts.getOrElse(i) { 0 }
            if (aNum != bNum) return aNum - bNum
        }
        return 0
    }

}