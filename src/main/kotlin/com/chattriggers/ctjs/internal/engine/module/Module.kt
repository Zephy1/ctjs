package com.chattriggers.ctjs.internal.engine.module

import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.api.render.GUIRenderer
import com.chattriggers.ctjs.api.render.RenderUtils
import com.chattriggers.ctjs.api.render.Text
import com.fasterxml.jackson.core.Version
import java.io.File

class Module(val name: String, var metadata: ModuleMetadata, val folder: File) {
    var targetModVersion: Version? = null
    var requiredBy = mutableSetOf<String>()

    private val gui = object {
        var collapsed = true
        var x = 0f
        var y = 0f
        var description = Text(metadata.description ?: "No description provided in the metadata")
    }

    fun draw(
        x: Float,
        y: Float,
        width: Float,
    ): Float {
        gui.x = x
        gui.y = y

        RenderUtils.pushMatrix()
        GUIRenderer.drawRect(
            x,
            y,
            width,
            13f,
            0xAA000000,
        )
        GUIRenderer.drawStringWithShadow(
            metadata.name ?: name,
            x + 3,
            y + 3,
        )

        return if (gui.collapsed) {
            RenderUtils
                .translate(x + width - 5, y + 8)
                .rotate(180f)
            GUIRenderer.drawString("^", 0f, 0f)
            RenderUtils.popMatrix()
            15f
        } else {
            gui.description.setMaxWidth(width.toInt() - 5)

            GUIRenderer.drawRect(x, y + 13, width, gui.description.getHeight() + 12, 0x50000000)
            GUIRenderer.drawString("^", x + width - 10, y + 5)

            gui.description.draw(x + 3, y + 15)

            if (metadata.version != null) {
                GUIRenderer.drawStringWithShadow(
                    ChatLib.addColor("&8v${metadata.version}"),
                    x + width - RenderUtils.getStringWidth(ChatLib.addColor("&8v${metadata.version}")),
                    y + gui.description.getHeight() + 15,
                )
            }

            GUIRenderer.drawStringWithShadow(
                ChatLib.addColor(
                    if (metadata.isRequired && requiredBy.isNotEmpty()) {
                        "&8required by $requiredBy"
                    } else {
                        "&4[delete]"
                    },
                ),
                x + 3,
                y + gui.description.getHeight() + 15,
            )

            RenderUtils.popMatrix()
            gui.description.getHeight() + 27
        }
    }

    fun click(x: Double, y: Double, width: Float) {
        if (x > gui.x &&
            x < gui.x + width &&
            y > gui.y &&
            y < gui.y + 13
        ) {
            gui.collapsed = !gui.collapsed
            return
        }

        if (gui.collapsed || (metadata.isRequired && requiredBy.isNotEmpty())) return

        if (x > gui.x &&
            x < gui.x + 45 &&
            y > gui.y + gui.description.getHeight() + 15 &&
            y < gui.y + gui.description.getHeight() + 25
        ) {
            ModuleManager.deleteModule(name)
        }
    }

    override fun toString() = "Module{name=$name,version=${metadata.version}}"
}
