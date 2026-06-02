package com.github.fanziyun.client

import com.github.fanziyun.data.ChangelogLoader
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Environment(EnvType.CLIENT)
class ChangelogClient : ClientModInitializer {

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger("changelog363")
        var config: ModConfig? = null
            private set
    }

    override fun onInitializeClient() {
        LOGGER.info("Initializing 363Changelog Client")

        AutoConfig.register(ModConfig::class.java) { config, clazz -> GsonConfigSerializer(config, clazz) }
        config = AutoConfig.getConfigHolder(ModConfig::class.java).config

        if (config?.changelogUrl?.isNotBlank() == true) {
            ChangelogLoader.load(config!!.changelogUrl)
        }
    }
}