package com.github.fanziyun.data

import com.google.gson.annotations.SerializedName

data class ChangelogData(
    @SerializedName("footer")
    val footer: String? = null,

    @SerializedName("tagColors")
    val tagColors: Map<String, String>? = null,

    @SerializedName("entries")
    val entries: List<ChangelogEntry>? = null
) {
    val tagColorsOrEmpty: Map<String, String> get() = tagColors ?: emptyMap()
    val entriesOrEmpty: List<ChangelogEntry> get() = entries ?: emptyList()
}