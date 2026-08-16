package com.tomtaru.tmtcobfarm.block;

import com.tomtaru.tmtcobfarm.Tmtcobfarm;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, Tmtcobfarm.MODID);

    public static final DeferredHolder<Block, StoveCampfireBlock> STOVE_CAMPFIRE = BLOCKS.register(
            "stove_campfire",
            () -> new StoveCampfireBlock(
                    BlockBehaviour.Properties.of()
                            .strength(2.0F)
                            .sound(SoundType.LANTERN)
                            .lightLevel(state -> 13)
                            .noOcclusion()
            )
    );
}