package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.internal.engine.CTEvents;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Objects;

//#if MC<=12108
//$$import net.minecraft.block.entity.BlockEntity;
//$$import net.minecraft.client.render.VertexConsumerProvider;
//$$import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
//#else
import com.chattriggers.ctjs.api.client.Client;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//#endif

//#if MC<=12108
//$$@Mixin(BlockEntityRenderDispatcher.class)
//$$public class BlockEntityRenderDispatcherMixin {
//#else
@Mixin(BlockEntityRenderManager.class)
public abstract class BlockEntityRenderDispatcherMixin {
//#endif
    @Inject(
        //#if MC<=12108
        //$$method = "render(Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;)V",
        //#else
        method = "render",
        //#endif
        at = @At(
            value = "INVOKE",
            //#if MC<=12108
            //$$target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderDispatcher;render(Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;Lnet/minecraft/block/entity/BlockEntity;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/util/math/Vec3d;)V"
            //#else
            target = "Lnet/minecraft/client/render/block/entity/BlockEntityRenderer;render(Lnet/minecraft/client/render/block/entity/state/BlockEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"
            //#endif
        ),
        cancellable = true
        //#if MC>=12109
        , locals = LocalCapture.CAPTURE_FAILSOFT
        //#endif
    )
    //#if MC<=12108
    //$$private void injectRender(
    //$$    BlockEntity blockEntity,
    //$$    float tickDelta,
    //$$    MatrixStack matrices,
    //$$    VertexConsumerProvider vertexConsumers,
    //$$    CallbackInfo ci
    //$$) {
    //$$    if (blockEntity.hasWorld() && Objects.requireNonNull(blockEntity.getWorld()).isClient) {
    //$$        CTEvents.RENDER_BLOCK_ENTITY.invoker().render(matrices, blockEntity, tickDelta, ci);
    //$$    }
    //#else
    private <S extends BlockEntityRenderState> void injectRender(
        S renderState,
        MatrixStack matrixStack,
        OrderedRenderCommandQueue queue,
        CameraRenderState cameraRenderState,
        CallbackInfo ci,
        BlockEntityRenderer blockEntityRenderer
    ) {
//        fixme
//        if (blockEntity.getEntityWorld() != null && Objects.requireNonNull(blockEntity.getEntityWorld()).isClient()) {
//            CTEvents.RENDER_BLOCK_ENTITY.invoker().render(matrixStack, blockEntity, Client.getMinecraft().getRenderTickCounter().getDynamicDeltaTicks(), ci);
//        }
    //#endif
    }
}
