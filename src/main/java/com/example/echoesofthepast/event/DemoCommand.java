package com.example.echoesofthepast.event;

import com.example.echoesofthepast.dream.DreamManager;
import com.example.echoesofthepast.grief.GriefManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;


@EventBusSubscriber(modid = "echoesofthepast")
public class DemoCommand {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("TEST_SHOW_CASE")
                .requires(source ->
                        source.permissions().hasPermission(
                                new Permission.HasCommandLevel(PermissionLevel.MODERATORS)
                        )
                )                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();

                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                    "Starting showcase: journal, dream, then a guardian nearby.")
                            .withStyle(net.minecraft.ChatFormatting.GOLD));

                    // it gives the journal
                    var book = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.WRITTEN_BOOK);
                    player.getInventory().add(book);

                    // it immediately triggers a dream, bypassing sleep
                    DreamManager.enterDream(player);

                    return 1;
                }));
        dispatcher.register(Commands.literal("griefadd")
                .requires(source ->
                        source.permissions().hasPermission(
                                new Permission.HasCommandLevel(PermissionLevel.MODERATORS)
                        )
                )
                .then(Commands.argument("value", IntegerArgumentType.integer())
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();

                            int value = IntegerArgumentType.getInteger(context, "value");

                            // Add grief here
                            GriefManager.addGrief(player, value);

                            player.sendSystemMessage(
                                    net.minecraft.network.chat.Component.literal(
                                            "Added " + value + " grief."
                                    ).withStyle(net.minecraft.ChatFormatting.RED)
                            );

                            return 1;
                        })
                ));
    }
}