package com.chattriggers.ctjs.internal.listeners

import com.chattriggers.ctjs.api.render.GUIRenderer
import com.chattriggers.ctjs.api.triggers.CancellableEvent
import com.chattriggers.ctjs.api.triggers.TriggerType
import com.chattriggers.ctjs.internal.utils.Initializer
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.util.math.BlockPos

object WorldListener : Initializer {
    override fun init() {
        WorldRenderEvents.BLOCK_OUTLINE.register { _, ctx ->
            val event = CancellableEvent()
            TriggerType.RENDER_BLOCK_HIGHLIGHT.triggerAll(BlockPos(ctx.blockPos()), event)
            !event.isCancelled()
        }

        WorldRenderEvents.START.register { ctx ->
            val deltaTicks = ctx.tickCounter().dynamicDeltaTicks
            GUIRenderer.withMatrix(ctx.matrixStack(), deltaTicks) {
                TriggerType.PRE_RENDER_WORLD.triggerAll(deltaTicks)
            }
        }

        WorldRenderEvents.LAST.register { ctx ->
            val deltaTicks = ctx.tickCounter().dynamicDeltaTicks
            GUIRenderer.withMatrix(ctx.matrixStack(), deltaTicks) {
                TriggerType.POST_RENDER_WORLD.triggerAll(deltaTicks)
            }
        }
    }
}
