package com.github.fanziyun.mixin

import com.github.fanziyun.client.ChangelogClient
import com.github.fanziyun.screen.ChangelogOverviewScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.PauseScreen
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(PauseScreen::class)
abstract class PauseScreenMixin : Screen(Component.literal("")) {

    @Inject(method = ["init"], at = [At("TAIL")])
    fun onInit(callback: CallbackInfo) {
        val config = ChangelogClient.config ?: return
        if (!config.showOnTitle) return

        val btnWidth = 204
        val fullscreen = minecraft.window.isFullscreen
        val btnY = if (fullscreen) height / 2 + 40 else height - 50
        addRenderableWidget(
            Button.builder(Component.translatable("menu.changelog363.button")) {
                Minecraft.getInstance().setScreen(ChangelogOverviewScreen(Minecraft.getInstance().screen))
            }.bounds((width - btnWidth) / 2, btnY, btnWidth, 20).build()
        )
    }
}