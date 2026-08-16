package com.tomtaru.tmtcobfarm.entity;

import com.cobblemon.mod.common.block.entity.CampfireBlockEntity;
import com.tomtaru.tmtcobfarm.Tmtcobfarm;
import com.tomtaru.tmtcobfarm.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Tmtcobfarm.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CampfireBlockEntity>> STOVE_CAMPFIRE =
            BLOCK_ENTITIES.register("stove_campfire", () ->
                    BlockEntityType.Builder.of(
                            CampfireBlockEntity::new,
                            ModBlocks.STOVE_CAMPFIRE.get()
                    ).build(null)
            );
}