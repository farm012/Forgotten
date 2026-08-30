package com.example.echoesofthepast.event;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.List;

@EventBusSubscriber(modid = "echoesofthepast")
public class StarterItemHandler {

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var tag = player.getPersistentData();
        boolean alreadyGiven = tag.getBoolean("echoesofthepast_journal_given").orElse(false);
        if (alreadyGiven) return;

        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
                Filterable.passThrough("Fading Memory"),
                "The Past",
                0,
                List.of(
                        Filterable.passThrough(
                                Component.literal("Your memory seems fuzzy as you enter this place.\n\nPast events have blurred, slipping just out of reach.")
                        ),
                        Filterable.passThrough(
                                Component.literal("Yet something uneasy dawns at the back of your mind.")
                        ),
                        Filterable.passThrough(
                                Component.literal("Sleep will not be kind to you here.")
                        )
                ),
                true
        ));
        player.getInventory().add(book);
        tag.putBoolean("echoesofthepast_journal_given", true);
    }
}