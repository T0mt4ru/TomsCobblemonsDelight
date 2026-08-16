package com.tomtaru.tmtcobfarm.mixin;

import com.cobblemon.mod.common.block.entity.CampfireBlockEntity;
import com.cobblemon.mod.common.client.render.block.CampfireBlockEntityRenderer;
import com.tomtaru.tmtcobfarm.block.StoveCampfireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CampfireBlockEntityRenderer.class)
public class CampfireBlockEntityRendererMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true, remap = false)
    private void skipRenderForStoveCampfire(CampfireBlockEntity blockEntity,
                                            float partialTick,
                                            com.mojang.blaze3d.vertex.PoseStack poseStack,
                                            net.minecraft.client.renderer.MultiBufferSource buffer,
                                            int combinedLight,
                                            int combinedOverlay,
                                            CallbackInfo ci) {
        // Don't render Cobblemon's pot if it's on our custom stove block
        if (blockEntity.getBlockState().getBlock() instanceof StoveCampfireBlock) {
            ci.cancel();
        }
    }
}