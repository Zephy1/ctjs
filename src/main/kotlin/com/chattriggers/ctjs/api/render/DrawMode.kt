package com.chattriggers.ctjs.api.render

import com.mojang.blaze3d.vertex.VertexFormat

enum class DrawMode(private val mcValue: VertexFormat.DrawMode) {
    LINES(VertexFormat.DrawMode.LINES),
    //#if MC<=12110
    //$$LINE_STRIP(VertexFormat.DrawMode.LINE_STRIP),
    //#else
    LINE_STRIP(VertexFormat.DrawMode.DEBUG_LINE_STRIP),
    //#endif
    TRIANGLES(VertexFormat.DrawMode.TRIANGLES),
    TRIANGLE_STRIP(VertexFormat.DrawMode.TRIANGLE_STRIP),
    TRIANGLE_FAN(VertexFormat.DrawMode.TRIANGLE_FAN),
    QUADS(VertexFormat.DrawMode.QUADS);

    fun toMC() = mcValue

    companion object {
        @JvmStatic
        fun fromMC(mcValue: VertexFormat.DrawMode) = entries.first { it.mcValue == mcValue }
    }
}
