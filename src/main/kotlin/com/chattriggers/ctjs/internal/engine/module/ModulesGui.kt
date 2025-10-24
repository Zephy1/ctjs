package com.chattriggers.ctjs.internal.engine.module

import com.chattriggers.ctjs.api.client.Player
import com.chattriggers.ctjs.api.message.ChatLib
import com.chattriggers.ctjs.api.render.GUIRenderer
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

    override fun render(ctx: DrawContext?, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        //#if MC>12105
        //$$ctx!!.matrices.pushMatrix()
        //#else
        ctx!!.matrices.push()
        //#endif

        ctx.fill(0, 0, ctx.scaledWindowWidth, ctx.scaledWindowHeight, 0x50000000)
        val middle = GUIRenderer.screen.getWidth() / 2
        val width = (GUIRenderer.screen.getWidth() - 100).coerceAtMost(500)

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

        val ox = middle - width / 2
        val oy = window.scroll.toInt() + 95

        ctx.fill(ox, oy, ox + width, oy + (window.height.toInt() - 90), 0x50000000)
        ctx.fill(ox, oy, ox + width, oy + 25, 0xaa000000.toInt())

        window.title.draw(ctx, (middle - width / 2 + 5) / 2, (window.scroll.toInt() + 100) / 2)
        window.exit.draw(ctx, (middle + width / 2 - 17) / 2, (window.scroll.toInt() + 99) / 2)

        window.height = 125f
        ModuleManager.cachedModules.sortedBy { it.name }.forEach {
            window.height += it.draw(ctx, middle - width / 2, (window.scroll + window.height).toInt(), width)
        }

        //#if MC>12105
        //$$ctx.matrices.popMatrix()
        //#else
        ctx.matrices.pop()
        //#endif
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
