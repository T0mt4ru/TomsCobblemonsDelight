package com.tomtaru.tmtcobfarm.client;

import com.tomtaru.tmtcobfarm.block.ModBlocks;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;

public class StoveCampfireItemProperties {

    public static void register() {
        ItemProperties.register(
                ModBlocks.STOVE_CAMPFIRE.get().asItem(),
                ResourceLocation.fromNamespaceAndPath("tmtcobfarm", "color"),
                (stack, level, entity, seed) -> {
                    if (stack.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA)) {
                        var customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
                        if (customData != null && customData.contains("Color")) {
                            String color = customData.copyTag().getString("Color");
                            return switch (color) {
                                case "red" -> 0.0f;
                                case "blue" -> 1.0f;
                                case "green" -> 2.0f;
                                case "yellow" -> 3.0f;
                                case "pink" -> 4.0f;
                                case "white" -> 5.0f;
                                case "black" -> 6.0f;
                                default -> 0.0f;
                            };
                        }
                    }
                    return 0.0f;
                }
        );
    }
}