package com.chattriggers.ctjs.api.render

import com.mojang.blaze3d.vertex.VertexFormat

enum class DrawMode(private val mcValue: VertexFormat.DrawMode) {
    LINES(VertexFormat.DrawMode.LINES),
    LINE_STRIP(VertexFormat.DrawMode.LINE_STRIP),
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
