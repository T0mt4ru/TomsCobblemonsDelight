package com.tomtaru.tmtcobfarm;

import com.cobblemon.mod.common.CobblemonBlockEntities;
import com.mojang.logging.LogUtils;
import com.tomtaru.tmtcobfarm.block.ModBlocks;
import com.tomtaru.tmtcobfarm.client.StoveCampfireItemProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Mod(Tmtcobfarm.MODID)
public class Tmtcobfarm {
    public static final String MODID = "tmtcobfarm";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Tmtcobfarm(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        ModBlocks.BLOCKS.register(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Loading Tomtaru's Cobblemon & Farmer's Delight Tweaks");

        event.enqueueWork(() -> {
            try {

                Field validBlocksField;
                try {
                    validBlocksField = BlockEntityType.class.getDeclaredField("validBlocks");
                } catch (NoSuchFieldException e) {
                    validBlocksField = BlockEntityType.class.getDeclaredField("f_58913_");
                }
                validBlocksField.setAccessible(true);


                Set<Block> validBlocks = (Set<Block>) validBlocksField.get(CobblemonBlockEntities.CAMPFIRE);


                try {
                    validBlocks.add(ModBlocks.STOVE_CAMPFIRE.get());
                } catch (UnsupportedOperationException e) {
                    Set<Block> newSet = new HashSet<>(validBlocks);
                    newSet.add(ModBlocks.STOVE_CAMPFIRE.get());
                    validBlocksField.set(CobblemonBlockEntities.CAMPFIRE, newSet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @EventBusSubscriber(modid = Tmtcobfarm.MODID, value = Dist.CLIENT)
    public class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                StoveCampfireItemProperties.register();
            });
        }
    }
}