package com.midnightdoggo19.openheart;

//import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
//import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class LocateBlockCommand {
    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("locateblock")
                    .then(ClientCommandManager.argument("tag", StringArgumentType.word())
                            .executes(ctx -> {

                                String input = StringArgumentType.getString(ctx, "tag");
                                var client = MinecraftClient.getInstance();
                                var player = client.player;
                                if (player == null || client.world == null) return 0;

                                BlockPos origin = player.getBlockPos();
                                int maxRadius = 64;

                                if (input.startsWith("#")) {
                                    // tag search
                                    String tagId = input.substring(1); // remove #
                                    var tag = BlockLocator.getTag(tagId);

                                    player.sendMessage(Text.literal("Searching for tag: #" + tagId + "..."), false);
                                    BlockLocator.findNearestBlockByTagAsync(client.world, origin, tag, maxRadius)
                                            .thenAccept(result -> {
                                                if (result != null) {
                                                    player.sendMessage(Text.literal("Found block in #" + tagId + " at " + result.toShortString()), false);
                                                    
                                                } else {
                                                    player.sendMessage(Text.literal("No matching block in #" + tagId + " found."), false);
                                                }
                                            });
                                } else {
                                    // block search
                                    Identifier id = Identifier.tryParse(input);
                                    if (id == null || !Registries.BLOCK.containsId(id)) {
                                        player.sendMessage(Text.literal("Unknown block: " + input), false);
                                        return 0;
                                    }

                                    Block block = Registries.BLOCK.get(id);

                                    player.sendMessage(Text.literal("Searching for block: " + id + "..."), false);
                                    BlockLocator.findNearestBlockByTypeAsync(client.world, origin, block, maxRadius)
                                            .thenAccept(result -> {
                                                if (result != null) {
                                                    player.sendMessage(Text.literal("Found block " + id + " at " + result.toShortString()), false);
                                                } else {
                                                    player.sendMessage(Text.literal("No block " + id + " found nearby."), false);
                                                }
                                            });
                                }

                                return 1;
                            })));
        });
    }
}