package com.github.fanziyun.client

import me.shedaniel.autoconfig.ConfigData
import me.shedaniel.autoconfig.annotation.Config
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment

@Config(name = "changelog363")
class ModConfig : ConfigData {

    @Comment("URL of the remote changelog JSON file")
    var changelogUrl: String = "https://github.com/fanziyun/363changlog/blob/master/src/main/resources/changelog.json"

    @Comment("Modpack display name shown in the bottom-left of the title screen")
    var packName: String = "363 changlog"

    @Comment("Current modpack version number")
    var modpackVersion: String = "1.0.0"

    @Comment("Show changelog button on the title screen")
    var showOnTitle: Boolean = true

    @Comment("Enable automatic update checking")
    var enableVersionCheck: Boolean = true

    @Comment("Display name of the external link")
    var externalLinkName: String = "项目主页"

    @Comment("URL of the external link")
    var externalLinkUrl: String = "https://github.com/fanziyun/363changlog"
}