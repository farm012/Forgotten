package com.example.echoesofthepast.item;

import com.example.echoesofthepast.block.ModBlocks;
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
            DeferredRegister.createItems("echoesofthepast");
    //creation of items is important
    public static final DeferredItem<Item> COMRADES_REMAINS = ITEMS.registerItem(
            "comrades_remains", Item::new, props -> props
                    .component(DataComponents.LORE,
                            new ItemLore(List.of(
                               Component.literal("The last hope to give your friends a place to rest").withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                            ))


    ));

    public static final DeferredItem<net.minecraft.world.item.BlockItem> SCATTERED_REMAINS_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.SCATTERED_REMAINS);

    public static final DeferredItem<Item> GUARDIAN_ESSENCE = ITEMS.registerItem(
            "guardian_essence", Item::new, props -> props
                    .component(DataComponents.LORE,
                            new ItemLore(List.of(
                                    Component.literal("It seems that the world is trying to protect something that it pulled figures such as").withStyle(ChatFormatting.DARK_BLUE, ChatFormatting.BOLD),
                                    Component.literal("Minotaur, Zeus and Anubis").withStyle(ChatFormatting.RED, ChatFormatting.BOLD)

                            ))
                            )
    );

    public static final DeferredItem<Item> GRIEF_EMBLEM = ITEMS.registerItem(//4AM CODDING MY BRAIN ISN4T WORKING SO MAYBE  I WILL CHANGE THE LORE LATER
            "grief_emblem",
            Item::new,
            props -> props
                    .stacksTo(1)
                    .component(
                            DataComponents.LORE,
                            new ItemLore(List.of(
                                    Component.literal("The Emblem").withStyle(ChatFormatting.BLACK),

                                    Component.literal("Keeps you safe from your own dreams slowly storing your grief turning it into power.")
                                            .withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD),
                                    Component.empty(),
                                    Component.literal("The dead do not rest quietly here.")
                                            .withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC),
                                    Component.empty(),
                                    Component.literal("+ Speed").withStyle(ChatFormatting.BLUE, ChatFormatting.BOLD),
                                    Component.literal("+ Strength").withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                            ))
                    )
    );




    //last second thoguht
    public static final DeferredItem<ZeusLastOathItem> ZEUS_LAST_OATH = ITEMS.registerItem(
            "zeus_last_oath",
            props -> new ZeusLastOathItem(
                    props.sword(net.minecraft.world.item.ToolMaterial.IRON, 3, -2.4f)
                            .component(
                                    net.minecraft.core.component.DataComponents.LORE,
                                    new net.minecraft.world.item.component.ItemLore(List.of(
                                            Component.literal("Wrath from the sky")
                                                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                                            Component.literal("Every second strike calls down judgment upon your foe.")
                                                    .withStyle(ChatFormatting.AQUA)
                                    ))
                            )
            ),
            props -> props
    );




}