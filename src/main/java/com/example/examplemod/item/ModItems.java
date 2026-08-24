package com.example.examplemod.item;

import com.example.examplemod.block.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemLore;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems("examplemod");
    //creation of items is important
    public static final DeferredItem<Item> COMRADES_REMAINS = ITEMS.registerSimpleItem("comrades_remains");

    public static final DeferredItem<net.minecraft.world.item.BlockItem> SCATTERED_REMAINS_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.SCATTERED_REMAINS);

    public static final DeferredItem<Item> GUARDIAN_ESSENCE = ITEMS.registerItem(
            "guardian_essence", Item::new, props -> props
    );

    public static final DeferredItem<Item> GRIEF_EMBLEM = ITEMS.registerItem(//4AM CODDING MY BRAIN ISN4T WORKING SO MAYBE  I WILL CHANGE THE LORE LATER
            "grief_emblem",
            Item::new,
            props -> props
                    .stacksTo(1)
                    .component(
                            DataComponents.LORE,
                            new ItemLore(List.of(
                                    Component.literal("It remembers what you tried to forget.")
                                            .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.ITALIC),
                                    Component.literal("The dead do not rest quietly here.")
                                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC),
                                    Component.empty(),
                                    Component.literal("+ Speed").withStyle(ChatFormatting.BLUE),
                                    Component.literal("+ Strength").withStyle(ChatFormatting.RED)
                            ))
                    )
    );
}