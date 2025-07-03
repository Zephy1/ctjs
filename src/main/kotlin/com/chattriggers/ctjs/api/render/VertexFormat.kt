package com.chattriggers.ctjs.api.render

import com.chattriggers.ctjs.MCVertexFormat
import net.minecraft.client.render.VertexFormats

enum class VertexFormat(private val mcValue: MCVertexFormat) {
    BLIT_SCREEN(VertexFormats.BLIT_SCREEN),
    POSITION_COLOR_TEXTURE_LIGHT_NORMAL(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL),
    POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL),
    POSITION_TEXTURE_COLOR_LIGHT(VertexFormats.POSITION_TEXTURE_COLOR_LIGHT),
    POSITION(VertexFormats.POSITION),
    POSITION_COLOR(VertexFormats.POSITION_COLOR),
    POSITION_COLOR_NORMAL(VertexFormats.POSITION_COLOR_NORMAL),
    POSITION_COLOR_LIGHT(VertexFormats.POSITION_COLOR_LIGHT),
    POSITION_TEXTURE(VertexFormats.POSITION_TEXTURE),
    POSITION_TEXTURE_COLOR(VertexFormats.POSITION_TEXTURE_COLOR),
    POSITION_COLOR_TEXTURE_LIGHT(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT),
    POSITION_TEXTURE_LIGHT_COLOR(VertexFormats.POSITION_TEXTURE_LIGHT_COLOR),
    POSITION_TEXTURE_COLOR_NORMAL(VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

    fun toMC() = mcValue

    companion object {
        @JvmStatic
        fun fromMC(ucValue: MCVertexFormat) = entries.first { it.mcValue == ucValue }
    }
}
