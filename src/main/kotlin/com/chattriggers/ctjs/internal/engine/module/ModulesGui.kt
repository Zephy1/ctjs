package com.chattriggers.ctjs.internal.engine.module

import com.chattriggers.ctjs.api.client.Player
import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.api.render.GUIRenderer
import com.chattriggers.ctjs.api.render.RenderUtils
import com.chattriggers.ctjs.api.render.Text
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen

object ModulesGui : Screen(net.minecraft.text.Text.literal("Modules")) {
    private val window = object {
        val title = Text("Modules").setScale(2f).setShadow(true)
        val exit = Text(ChatLib.addColor("&cx")).setScale(2f)
        var height = 0f
        var scroll = 0f
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        super.render(context, mouseX, mouseY, deltaTicks)

        RenderUtils.pushMatrix()

        val middle = GUIRenderer.screen.getWidth() / 2f
        val width = (GUIRenderer.screen.getWidth() - 100f).coerceAtMost(500f)

        GUIRenderer.drawRect(
            0f,
            0f,
            GUIRenderer.screen.getWidth().toFloat(),
            GUIRenderer.screen.getHeight().toFloat(),
            0x50000000,
        )

        if (-window.scroll > window.height - GUIRenderer.screen.getHeight() + 20)
            window.scroll = -window.height + GUIRenderer.screen.getHeight() - 20
        if (-window.scroll < 0) window.scroll = 0f

        if (-window.scroll > 0) {
            GUIRenderer.drawRect(GUIRenderer.screen.getWidth() - 20f, GUIRenderer.screen.getHeight() - 20f, 20f, 20f, 0xAA000000)
            GUIRenderer.drawString("^", GUIRenderer.screen.getWidth() - 12f, GUIRenderer.screen.getHeight() - 12f)
        }

        GUIRenderer.drawRect(middle - width / 2f, window.scroll + 95f, width, window.height - 90, 0x50000000)

        GUIRenderer.drawRect(middle - width / 2f, window.scroll + 95f, width, 25f, 0xAA000000)
        window.title.draw((middle - width / 2f + 5) / 2f, (window.scroll + 100f) / 2f)
        window.exit.draw((middle + width / 2f - 17) / 2f, (window.scroll + 99f) / 2f)

        window.height = 125f
        ModuleManager.cachedModules.sortedBy { it.name }.forEach {
            window.height += it.draw(middle - width / 2f, window.scroll + window.height, width)
        }

        RenderUtils.popMatrix()
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        super.mouseClicked(mouseX, mouseY, button)

        var width = GUIRenderer.screen.getWidth() - 100f
        if (width > 500) width = 500f

        if (mouseX > GUIRenderer.screen.getWidth() - 20 && mouseY > GUIRenderer.screen.getHeight() - 20) {
            window.scroll = 0f
            return false
        }

        if (mouseX > GUIRenderer.screen.getWidth() / 2f + width / 2f - 25 &&
            mouseX < GUIRenderer.screen.getWidth() / 2f + width / 2f &&
            mouseY > window.scroll + 95 &&
            mouseY < window.scroll + 120
        ) {
            Player.toMC()?.closeScreen()
            return false
        }

        ModuleManager.cachedModules.toList().forEach {
            it.click(mouseX, mouseY, width)
        }

        return false
    }

    override fun mouseScrolled(
        mouseX: Double,
        mouseY: Double,
        horizontalAmount: Double,
        delta: Double
    ): Boolean {
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, delta)
        window.scroll += delta.toFloat()
        return false
    }
}
