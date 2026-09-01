package com.example.echoesofthepast.item;

import com.example.echoesofthepast.block.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
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



    public static final DeferredItem<LabyrinthFangItem> LABYRINTH_FANG = ITEMS.registerItem(
            "labyrinth_fang",
            props -> new LabyrinthFangItem(
                    props.sword(ToolMaterial.GOLD, 9, -3.0f)
                            .component(
                                    net.minecraft.core.component.DataComponents.LORE,
                                    new net.minecraft.world.item.component.ItemLore(List.of(
                                            Component.literal(""),
                                            Component.literal(""),
                                            Component.literal("Echo of Asterion")
                                                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                                            Component.literal(""),
                                            Component.literal("The echo of Asterion's grief").withStyle(ChatFormatting.DARK_BLUE),
                                            Component.literal("floods your mind, awakening").withStyle(ChatFormatting.DARK_BLUE),
                                            Component.literal("a rage long buried.").withStyle(ChatFormatting.DARK_BLUE),
                                            Component.literal(""),
                                            Component.literal("For 100 seconds,").withStyle(ChatFormatting.RED),
                                            Component.literal("+damage")
                                                    .withStyle(ChatFormatting.RED)
                                                    .append(Component.literal(" and resistance are")
                                                            .withStyle(ChatFormatting.BLUE)),
                                            Component.literal("greatly increased.").withStyle(ChatFormatting.DARK_BLUE)
                                    ))
                            )
            ),
            props -> props
    );



    public static final DeferredItem<Item> ANKH = ITEMS.registerItem(
            "ankh",
            props -> new Item(
                    props
                            .stacksTo(1)
                            .component(
                                    net.minecraft.core.component.DataComponents.LORE,
                                    new net.minecraft.world.item.component.ItemLore(List.of(
                                            Component.literal(""),
                                            Component.literal(""),
                                            Component.literal("Echo of the Eternal")
                                                    .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                                            Component.literal(""),
                                            Component.literal("An ancient echo clings to your soul,")
                                                    .withStyle(ChatFormatting.DARK_BLUE),
                                            Component.literal("shielding you from the afflictions")
                                                    .withStyle(ChatFormatting.DARK_BLUE),
                                            Component.literal("that once claimed the lives of others.")
                                                    .withStyle(ChatFormatting.DARK_BLUE),
                                            Component.literal(""),
                                            Component.literal("Immunity to:")
                                                    .withStyle(ChatFormatting.BLUE),
                                            Component.literal("Fire, Poison & Slowness")
                                                    .withStyle(ChatFormatting.RED),
                                            Component.literal(""),
                                            Component.literal("Revives you upon death.")
                                                    .withStyle(ChatFormatting.GOLD),
                                            Component.literal("Cooldown: 90 seconds")
                                                    .withStyle(ChatFormatting.DARK_BLUE)
                                    ))
                            )
            ),
            props -> props
    );






}