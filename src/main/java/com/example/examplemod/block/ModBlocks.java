package com.example.examplemod.block;

import com.example.examplemod.remains.ScatteredRemainsBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks("examplemod");
    public static final DeferredBlock<Block> SCATTERED_REMAINS = BLOCKS.registerBlock(
            "scattered_remains",
            ScatteredRemainsBlock::new,
            props -> props
                    .mapColor(MapColor.STONE)
                    .sound(SoundType.BONE_BLOCK)
                    .noCollision()
                    .instabreak()
    );

}