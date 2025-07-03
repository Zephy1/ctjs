package com.chattriggers.ctjs.api.render

import gg.essential.universal.UGraphics

enum class DrawMode(private val ucValue: UGraphics.DrawMode) {
    LINES(UGraphics.DrawMode.LINES),
    LINE_STRIP(UGraphics.DrawMode.LINE_STRIP),
    TRIANGLES(UGraphics.DrawMode.TRIANGLES),
    TRIANGLE_STRIP(UGraphics.DrawMode.TRIANGLE_STRIP),
    TRIANGLE_FAN(UGraphics.DrawMode.TRIANGLE_FAN),
    QUADS(UGraphics.DrawMode.QUADS);

    fun toUC() = ucValue

    companion object {
        @JvmStatic
        fun fromUC(ucValue: UGraphics.DrawMode) = entries.first { it.ucValue == ucValue }
    }
}
