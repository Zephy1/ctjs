package com.chattriggers.ctjs.api.render

import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
import net.minecraft.util.Identifier

enum class HudRenderLayer(val mcValue: Identifier) {
    MISC_OVERLAYS(IdentifiedLayer.MISC_OVERLAYS),
    CROSSHAIR(IdentifiedLayer.CROSSHAIR),
    HOTBAR_AND_BARS(IdentifiedLayer.HOTBAR_AND_BARS),
    EXPERIENCE_LEVEL(IdentifiedLayer.EXPERIENCE_LEVEL),
    STATUS_EFFECTS(IdentifiedLayer.STATUS_EFFECTS),
    BOSS_BAR(IdentifiedLayer.BOSS_BAR),
    SLEEP(IdentifiedLayer.SLEEP),
    DEMO_TIMER(IdentifiedLayer.DEMO_TIMER),
    DEBUG(IdentifiedLayer.DEBUG),
    SCOREBOARD(IdentifiedLayer.SCOREBOARD),
    OVERLAY_MESSAGE(IdentifiedLayer.OVERLAY_MESSAGE),
    TITLE_AND_SUBTITLE(IdentifiedLayer.TITLE_AND_SUBTITLE),
    CHAT(IdentifiedLayer.CHAT),
    PLAYER_LIST(IdentifiedLayer.PLAYER_LIST),
    SUBTITLES(IdentifiedLayer.SUBTITLES);

    fun toMC() = mcValue
}
