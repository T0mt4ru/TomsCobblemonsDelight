package com.tomtaru.tmtcobfarm.mixin;

import com.tomtaru.tmtcobfarm.block.StoveCampfireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityValidationMixin {

    @Shadow
    protected BlockPos worldPosition;

    @Inject(method = "validateBlockState", at = @At("HEAD"), cancellable = true)
    private void skipValidationForStoveCampfire(BlockState state, CallbackInfo ci) {
        // Skip validation if it's our custom stove campfire block
        if (state.getBlock() instanceof StoveCampfireBlock) {
            ci.cancel();
        }
    }
}