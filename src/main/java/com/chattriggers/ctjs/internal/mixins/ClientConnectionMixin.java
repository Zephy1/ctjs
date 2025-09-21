package com.chattriggers.ctjs.internal.mixins;

import com.chattriggers.ctjs.internal.engine.CTEvents;
import com.chattriggers.ctjs.api.triggers.TriggerType;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC<=12105
//$$import net.minecraft.network.PacketCallbacks;
//$$import org.jetbrains.annotations.Nullable;
//#else
import io.netty.channel.ChannelFutureListener;
//#endif

@Mixin(ClientConnection.class)
public abstract class ClientConnectionMixin {
    @Shadow
    public abstract NetworkSide getSide();

    @Inject(
        method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/packet/Packet;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/ClientConnection;handlePacket(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;)V",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void injectHandlePacket(ChannelHandlerContext channelHandlerContext, Packet<?> packet, CallbackInfo ci) {
        if (getSide() == NetworkSide.CLIENTBOUND) {
            CTEvents.PACKET_RECEIVED.invoker().receive(packet, ci);
        }
    }

    @Inject(
        //#if MC<=12105
        //$$method = "send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V",
        //#else
        method = "send(Lnet/minecraft/network/packet/Packet;Lio/netty/channel/ChannelFutureListener;)V",
        //#endif
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    //#if MC<=12105
    //$$private void injectSendPacket(Packet<?> packet, @Nullable PacketCallbacks callbacks, CallbackInfo ci) {
    //#else
    private void injectSendPacket(Packet<?> packet, ChannelFutureListener channelFutureListener, CallbackInfo ci) {
    //#endif
        TriggerType.PACKET_SENT.triggerAll(packet, ci);
    }
}
