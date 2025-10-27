package com.chattriggers.ctjs.api.render

import net.minecraft.util.Identifier

//#if MC>=12106
    //$$import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
    //$$enum class HudRenderLayer(val mcValue: Identifier) {
    //$$MISC_OVERLAYS(VanillaHudElements.MISC_OVERLAYS),
    //$$CROSSHAIR(VanillaHudElements.CROSSHAIR),
    //$$SPECTATOR_MENU(VanillaHudElements.SPECTATOR_MENU),
    //$$HOTBAR(VanillaHudElements.HOTBAR),
    //$$ARMOR_BAR(VanillaHudElements.ARMOR_BAR),
    //$$HEALTH_BAR(VanillaHudElements.HEALTH_BAR),
    //$$FOOD_BAR(VanillaHudElements.FOOD_BAR),
    //$$AIR_BAR(VanillaHudElements.AIR_BAR),
    //$$MOUNT_HEALTH(VanillaHudElements.MOUNT_HEALTH),
    //$$INFO_BAR(VanillaHudElements.INFO_BAR),
    //$$EXPERIENCE_LEVEL(VanillaHudElements.EXPERIENCE_LEVEL),
    //$$HELD_ITEM_TOOLTIP(VanillaHudElements.HELD_ITEM_TOOLTIP),
    //$$SPECTATOR_TOOLTIP(VanillaHudElements.SPECTATOR_TOOLTIP),
    //$$STATUS_EFFECTS(VanillaHudElements.STATUS_EFFECTS),
    //$$BOSS_BAR(VanillaHudElements.BOSS_BAR),
    //$$SLEEP(VanillaHudElements.SLEEP),
    //$$DEMO_TIMER(VanillaHudElements.DEMO_TIMER),
    //$$DEBUG(VanillaHudElements.DEBUG),
    //$$SCOREBOARD(VanillaHudElements.SCOREBOARD),
    //$$OVERLAY_MESSAGE(VanillaHudElements.OVERLAY_MESSAGE),
    //$$TITLE_AND_SUBTITLE(VanillaHudElements.TITLE_AND_SUBTITLE),
    //$$CHAT(VanillaHudElements.CHAT),
    //$$PLAYER_LIST(VanillaHudElements.PLAYER_LIST),
    //$$SUBTITLES(VanillaHudElements.SUBTITLES);
//#else
import net.fabricmc.fabric.api.client.rendering.v1.IdentifiedLayer
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
//#endif
    fun toMC() = mcValue
}
