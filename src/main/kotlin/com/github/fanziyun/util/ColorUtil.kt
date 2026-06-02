package com.github.fanziyun.util
object ColorUtil {
    fun parseColor(colorStr: String, defaultColor: Int = 0xFFFFFFFF.toInt()): Int {
        return try {
            when {
                colorStr.startsWith("0x", ignoreCase = true) -> {
                    val hex = colorStr.removePrefix("0x").removePrefix("0X")
                    when (hex.length) {
                        6 -> (0xFF000000.toInt() or Integer.parseInt(hex, 16))
                        8 -> Integer.parseUnsignedInt(hex, 16)
                        else -> defaultColor
                    }
                }
                colorStr.startsWith("#") -> {
                    val hex = colorStr.removePrefix("#")
                    when (hex.length) {
                        6 -> (0xFF000000.toInt() or Integer.parseInt(hex, 16))
                        8 -> Integer.parseUnsignedInt(hex, 16)
                        else -> defaultColor
                    }
                }
                else -> {
                    val intVal = colorStr.toInt()
                    if (intVal in 0x000000..0xFFFFFF) 0xFF000000.toInt() or intVal
                    else intVal
                }
            }
        } catch (_: Exception) {
            defaultColor
        }
    }

    fun getTypeColor(type: String): Int = when (type.lowercase()) {
        "major", "重大更新" -> 0xFF5555FF.toInt()
        "minor", "功能更新" -> 0xFF55FF55.toInt()
        "patch", "修复补丁" -> 0xFF_FF_FF_55.toInt()
        "hotfix", "热修复" -> 0xFFFF5555.toInt()
        "danger", "危险更新" -> 0xFFFF5555.toInt()
        else -> 0xFF888888.toInt()
    }

    fun getTypeDisplayName(type: String, chinese: Boolean = true): String = when (type.lowercase()) {
        "major", "重大更新" -> if (chinese) "重大更新" else "Major Update"
        "minor", "功能更新" -> if (chinese) "功能更新" else "Feature Update"
        "patch", "修复补丁" -> if (chinese) "修复补丁" else "Patch"
        "hotfix", "热修复" -> if (chinese) "热修复" else "Hotfix"
        "danger", "危险更新" -> if (chinese) "危险更新" else "Danger"
        else -> type
    }

    fun getTypeIcon(type: String): String = when (type) {
        "major" -> "\u2605"       // ★
        "minor" -> "\u25CF"       // ●
        "patch" -> "\u25CB"       // ○
        "hotfix" -> "\u25C6"      // ◆
        "danger" -> "\u26A0"      // ⚠
        else -> "\u2022"          // •
    }
    fun isBright(color: Int): Boolean {
        val r = (color shr 16) and 0xFF
        val g = (color shr 8) and 0xFF
        val b = color and 0xFF
        val luma = 0.299 * r + 0.587 * g + 0.114 * b
        return luma > 160
    }
}