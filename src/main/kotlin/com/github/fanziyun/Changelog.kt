package com.github.fanziyun

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Changelog : ModInitializer {

    companion object {
        const val MOD_ID = "changelog363"
        val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    }

    override fun onInitialize() {
        LOGGER.info("363Changelog initialized")
    }
}
