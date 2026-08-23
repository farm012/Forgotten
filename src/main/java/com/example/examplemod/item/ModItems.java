package com.example.examplemod.item;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems("examplemod");
    //creation of items is important
    public static final DeferredItem<Item> COMRADES_REMAINS = ITEMS.registerSimpleItem("comrades_remains");

}