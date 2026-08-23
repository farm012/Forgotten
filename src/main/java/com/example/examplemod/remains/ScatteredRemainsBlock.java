package com.example.examplemod.remains;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;

public class ScatteredRemainsBlock extends Block {
    public ScatteredRemainsBlock(Properties properties) {
        super(properties);
    }

    // flat, walkable shape like a carpet not full cube
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Block.box(0, 0, 0, 16, 2, 16);
    } //just getting started
}