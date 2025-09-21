package com.chattriggers.ctjs.api.render

import net.minecraft.client.network.AbstractClientPlayerEntity
import net.minecraft.client.render.entity.EntityRendererFactory
import net.minecraft.client.render.entity.PlayerEntityRenderer
import net.minecraft.client.render.entity.model.EntityModelLayers
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer
import net.minecraft.client.render.entity.state.PlayerEntityRenderState
import net.minecraft.client.util.math.MatrixStack

//#if MC<=12108
//$$import net.minecraft.client.render.VertexConsumerProvider
//$$import net.minecraft.client.render.entity.model.ArmorEntityModel
//$$import net.minecraft.client.render.entity.model.LoadedEntityModels
//$$import net.minecraft.text.Text
//#else
import net.minecraft.client.render.command.OrderedRenderCommandQueue
import net.minecraft.client.render.entity.model.EntityModelLayer
import net.minecraft.client.render.entity.model.EquipmentModelData
import net.minecraft.client.render.entity.model.PlayerEntityModel
import net.minecraft.client.render.state.CameraRenderState
//#endif

internal class CTPlayerRenderer(
    private val ctx: EntityRendererFactory.Context,
    private val slim: Boolean,
//#if MC<=12108
//$$) : PlayerEntityRenderer(ctx, slim) {
//#else
) : PlayerEntityRenderer<AbstractClientPlayerEntity>(ctx, slim) {
    private val PLAYER_SLIM: EquipmentModelData<EntityModelLayers>
        @Suppress("UNCHECKED_CAST")
        get() = EntityModelLayers::class.java.getField("PLAYER_SLIM").get(null) as EquipmentModelData<EntityModelLayers>
//#endif

    var showArmor = true
        set(value) {
            field = value
            reset()
        }
    var showHeldItem = true
        set(value) {
            field = value
            reset()
        }
    var showArrows = true
        set(value) {
            field = value
            reset()
        }
    var showCape = true
        set(value) {
            field = value
            reset()
        }
    var showElytra = true
        set(value) {
            field = value
            reset()
        }
    var showParrot = true
        set(value) {
            field = value
            reset()
        }
    var showStingers = true
        set(value) {
            field = value
            reset()
        }
    var showNametag = true
        set(value) {
            field = value
            reset()
        }

    fun setOptions(
        showNametag: Boolean = true,
        showArmor: Boolean = true,
        showCape: Boolean = true,
        showHeldItem: Boolean = true,
        showArrows: Boolean = true,
        showElytra: Boolean = true,
        showParrot: Boolean = true,
        showStingers: Boolean = true,
    ) {
        this.showNametag = showNametag
        this.showArmor = showArmor
        this.showCape = showCape
        this.showHeldItem = showHeldItem
        this.showArrows = showArrows
        this.showElytra = showElytra
        this.showParrot = showParrot
        this.showStingers = showStingers

        reset()
    }

    override fun renderLabelIfPresent(
        //#if MC<=12108
        //$$playerEntityRenderState: PlayerEntityRenderState,
        //$$text: Text,
        //$$matrixStack: MatrixStack,
        //$$vertexConsumerProvider: VertexConsumerProvider,
        //$$i: Int
        //#else
        playerEntityRenderState: PlayerEntityRenderState?,
        matrixStack: MatrixStack?,
        orderedRenderCommandQueue: OrderedRenderCommandQueue?,
        cameraRenderState: CameraRenderState?
        //#endif
    ) {
        if (showNametag) {
            //#if MC<=12108
            //$$super.renderLabelIfPresent(playerEntityRenderState, text, matrixStack, vertexConsumerProvider, i)
            //#else
            super.renderLabelIfPresent(playerEntityRenderState, matrixStack, orderedRenderCommandQueue, cameraRenderState)
            //#endif
        }
    }

    private fun reset() {
        features.clear()

        //#if MC<=12108
        //$$val entityModels = ctx.modelManager.entityModelsSupplier.get()
        //#else
        val entityModels = ctx.blockRenderManager.models.modelManager.entityModelsSupplier.get()
        //#endif


        if (showArmor) {
            //#if MC>=12109
            val layer = if (slim) PLAYER_SLIM else EntityModelLayers.PLAYER_EQUIPMENT
            //#endif

            addFeature(
                ArmorFeatureRenderer(
                    this,
                    //#if MC<=12108
                    //$$ArmorEntityModel(ctx.getPart(if (slim) EntityModelLayers.PLAYER_SLIM_INNER_ARMOR else EntityModelLayers.PLAYER_INNER_ARMOR)),
                    //$$ArmorEntityModel(ctx.getPart(if (slim) EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR else EntityModelLayers.PLAYER_OUTER_ARMOR)),
                    //#else
                    EquipmentModelData.mapToEntityModel(
                        layer as EquipmentModelData<EntityModelLayer>,
                        ctx.entityModels
                    ) { PlayerEntityModel(it, slim) },
                    //#endif
                    ctx.equipmentRenderer
                )
            )
        }
        if (showHeldItem) {
            addFeature(PlayerHeldItemFeatureRenderer(this))
        }
        if (showArrows) {
            addFeature(StuckArrowsFeatureRenderer(this, ctx))
        }
        addFeature(Deadmau5FeatureRenderer(this, entityModels))
        if (showCape) {
            addFeature(CapeFeatureRenderer(this, entityModels, ctx.equipmentModelLoader))
        }
        if (showArmor) {
            //#if MC<=12108
            //$$addFeature(HeadFeatureRenderer(this, entityModels))
            //#else
            addFeature(HeadFeatureRenderer(this, entityModels, ctx.playerSkinCache))
            //#endif
        }
        if (showElytra) {
            addFeature(ElytraFeatureRenderer(this, entityModels, ctx.equipmentRenderer))
        }
        if (showParrot) {
            addFeature(ShoulderParrotFeatureRenderer(this, entityModels))
        }
        addFeature(TridentRiptideFeatureRenderer(this, entityModels))
        if (showStingers) {
            addFeature(StuckStingersFeatureRenderer(this, ctx))
        }
    }
}
