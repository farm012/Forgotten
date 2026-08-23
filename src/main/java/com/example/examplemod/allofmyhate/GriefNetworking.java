package com.example.examplemod.allofmyhate;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@EventBusSubscriber(modid = "examplemod")
public class GriefNetworking {

    private static int clientGrief = 0;

    public static int getClientGrief() {
        return clientGrief;
    }

    public record Packet(int grief) implements CustomPacketPayload {
        public static final Type<Packet> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath("examplemod", "grief_sync"));

        public static final StreamCodec<RegistryFriendlyByteBuf, Packet> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, Packet::grief,
                        Packet::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    // registers the payload TYPE, ;-;
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToClient(Packet.TYPE, Packet.STREAM_CODEC);
    }

    //what happens when the payload arrives on the client
    @SubscribeEvent
    public static void registerClientHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(Packet.TYPE, (packet, context) -> clientGrief = packet.grief());
    }

    public static void sendToPlayer(ServerPlayer player, int grief) {
        PacketDistributor.sendToPlayer(player, new Packet(grief));
    }
}