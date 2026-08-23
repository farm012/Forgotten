package com.example.examplemod.item;

import com.example.examplemod.block.ModBlocks;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems("examplemod");
    //creation of items is important
    public static final DeferredItem<Item> COMRADES_REMAINS = ITEMS.registerSimpleItem("comrades_remains");

    public static final DeferredItem<net.minecraft.world.item.BlockItem> SCATTERED_REMAINS_ITEM =
            ITEMS.registerSimpleBlockItem(ModBlocks.SCATTERED_REMAINS);
}