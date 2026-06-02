package com.github.fanziyun.data

import com.google.gson.annotations.SerializedName
import com.github.fanziyun.util.ColorUtil

data class ChangelogEntry(
    @SerializedName("version")
    val version: String? = null,

    @SerializedName("date")
    val date: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("type")
    val type: List<String>? = null,

    @SerializedName("tags")
    val tags: List<String>? = null,

    @SerializedName("color")
    val color: String? = null,

    @SerializedName("changes")
    val changes: List<String>? = null
) {
    val versionOrEmpty: String get() = version ?: ""
    val dateOrEmpty: String get() = date ?: ""
    val titleOrEmpty: String get() = title ?: ""
    val typeOrEmpty: List<String> get() = type ?: listOf("patch")
    val tagsOrEmpty: List<String> get() = tags ?: emptyList()
    val colorOrArgb: String get() = color ?: "#FFFFFF"
    val changesOrEmpty: List<String> get() = changes ?: emptyList()

    val parsedColor: Int get() = ColorUtil.parseColor(colorOrArgb)

    val primaryType: String get() = typeOrEmpty.firstOrNull() ?: "patch"
}