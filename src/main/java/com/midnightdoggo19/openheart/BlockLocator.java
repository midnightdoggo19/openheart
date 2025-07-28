package com.midnightdoggo19.openheart;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
//import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BlockLocator {
    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();

    // search by type (minecraft:air)
    public static CompletableFuture<BlockPos> findNearestBlockByTypeAsync(World world, BlockPos origin, Block block, int maxRadius) {
        return CompletableFuture.supplyAsync(() -> {
            for (int radius = 1; radius <= maxRadius; radius++) {
                for (int x = -radius; x <= radius; x++) {
                    for (int y = -radius; y <= radius; y++) {
                        for (int z = -radius; z <= radius; z++) {
                            if (Math.abs(x) != radius && Math.abs(y) != radius && Math.abs(z) != radius) continue;

                            BlockPos currentPos = origin.add(x, y, z);
                            if (!world.isChunkLoaded(currentPos)) continue;

                            BlockState state = world.getBlockState(currentPos);
                            if (state.getBlock() == block) {
                                return currentPos;
                            }
                        }
                    }
                }
            }
            return null;
        }, EXECUTOR);
    }

    public static TagKey<Block> getTag(String tagId) {
        return TagKey.of(Registries.BLOCK.getKey(), Identifier.tryParse(tagId));
    }
}