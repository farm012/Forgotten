package com.example.echoesofthepast.allofmyhate;

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

@EventBusSubscriber(modid = "echoesofthepast")
public class GriefNetworking {

    private static int clientGrief = 0;

    public static int getClientGrief() {
        return clientGrief;
    }

    public record Packet(int grief) implements CustomPacketPayload {
        public static final Type<Packet> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath("echoesofthepast", "grief_sync"));

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
        registrar.playToClient(FizzlePacket.TYPE, FizzlePacket.STREAM_CODEC);//new fizzle packet, fuck packets


        registrar.playToClient(SleepPromptPacket.TYPE, SleepPromptPacket.STREAM_CODEC);

    }

    //tis happens when it arrives
    @SubscribeEvent
    public static void registerClientHandlers(RegisterClientPayloadHandlersEvent event) {
        event.register(Packet.TYPE, (packet, context) -> clientGrief = packet.grief());
        event.register(FizzlePacket.TYPE, (packet, context) -> clientFizzle = packet.progress());


        event.register(SleepPromptPacket.TYPE, (packet, context) ->
                sleepPromptShowUntil = System.currentTimeMillis() + 3000);
    }

    public static void sendToPlayer(ServerPlayer player, int grief) {
        PacketDistributor.sendToPlayer(player, new Packet(grief));
    }

    public record FizzlePacket(float progress) implements CustomPacketPayload {
        public static final Type<FizzlePacket> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath("echoesofthepast", "fizzle_sync"));

        public static final StreamCodec<RegistryFriendlyByteBuf, FizzlePacket> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.FLOAT, FizzlePacket::progress,
                        FizzlePacket::new
                );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private static float clientFizzle = 0f;
    public static float getClientFizzle() { return clientFizzle; }




    public record SleepPromptPacket() implements CustomPacketPayload {
        public static final Type<SleepPromptPacket> TYPE =
                new Type<>(Identifier.fromNamespaceAndPath("echoesofthepast", "sleep_prompt"));

        public static final StreamCodec<RegistryFriendlyByteBuf, SleepPromptPacket> STREAM_CODEC =
                StreamCodec.unit(new SleepPromptPacket());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private static long sleepPromptShowUntil = 0L;

    public static long getSleepPromptShowUntil() { return sleepPromptShowUntil; }
}