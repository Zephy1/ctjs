package com.chattriggers.ctjs.internal.listeners

import com.chattriggers.ctjs.api.render.GUIRenderer
import com.chattriggers.ctjs.api.triggers.CancellableEvent
import com.chattriggers.ctjs.api.triggers.TriggerType
import net.minecraft.util.math.BlockPos

//#if MC<=12108
//$$import com.chattriggers.ctjs.internal.utils.Initializer
//$$import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
//$$object WorldListener : Initializer {
//$$    override fun init() {
//$$        WorldRenderEvents.BLOCK_OUTLINE.register { _, ctx ->
//$$            val event = CancellableEvent()
//$$            TriggerType.RENDER_BLOCK_HIGHLIGHT.triggerAll(BlockPos(ctx.blockPos()), event)
//$$            !event.isCancelled()
//$$        }
//$$        WorldRenderEvents.START.register { ctx ->
//$$            val deltaTicks = ctx.tickCounter().dynamicDeltaTicks
//$$            GUIRenderer.withMatrix(ctx.matrixStack(), deltaTicks) {
//$$                TriggerType.PRE_RENDER_WORLD.triggerAll(deltaTicks)
//$$            }
//$$        }
//$$        WorldRenderEvents.LAST.register { ctx ->
//$$            val deltaTicks = ctx.tickCounter().dynamicDeltaTicks
//$$            GUIRenderer.withMatrix(ctx.matrixStack(), deltaTicks) {
//$$                TriggerType.POST_RENDER_WORLD.triggerAll(deltaTicks)
//$$            }
//$$        }
//$$    }
//$$}
//#else
import com.chattriggers.ctjs.MCBlockPos
import net.minecraft.client.util.math.MatrixStack
object WorldListener {
    var matrixStack: MatrixStack? = null
    private var deltaTicks: Float = 1f

    fun triggerBlockOutline(bp: MCBlockPos): Boolean {
        val event = CancellableEvent()
        TriggerType.RENDER_BLOCK_HIGHLIGHT.triggerAll(BlockPos(bp), event)
        return event.isCanceled()
    }

    fun triggerRenderStart(ticks: Float) {
        deltaTicks = ticks
        if (matrixStack == null) return
        GUIRenderer.withMatrix(matrixStack, ticks) {
            TriggerType.PRE_RENDER_WORLD.triggerAll(ticks)
        }
    }

    fun triggerRenderLast() {
        if (matrixStack == null) return
        GUIRenderer.withMatrix(matrixStack, deltaTicks) {
            TriggerType.POST_RENDER_WORLD.triggerAll(deltaTicks)
        }
    }
}
//#endif
