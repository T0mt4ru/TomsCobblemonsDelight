package com.tomtaru.tmtcobfarm.mixin;

import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.block.entity.CampfireBlockEntity;
import com.cobblemon.mod.common.item.CampfirePotItem;
import com.tomtaru.tmtcobfarm.block.ModBlocks;
import com.tomtaru.tmtcobfarm.block.StoveCampfireBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vectorwing.farmersdelight.common.block.StoveBlock;

@Mixin(CampfirePotItem.class)
public class CampfirePotItemMixin {

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true, remap = false)
    private void onUseOnStove(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        Level world = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = world.getBlockState(blockPos);
        var player = context.getPlayer();

        if (player != null && blockState.getBlock() instanceof StoveBlock) {
            if (blockState.getValue(StoveBlock.LIT)) {
                Direction stoveFacing = blockState.getValue(HorizontalDirectionalBlock.FACING);
                Direction itemFacing = Direction.fromYRot(player.getYHeadRot());

                // Determine pot color from item
                ItemStack potItem = context.getItemInHand();
                StoveCampfireBlock.PotColor color = StoveCampfireBlock.PotColor.RED; // default
                String itemId = potItem.toString().toLowerCase();

                if (itemId.contains("blue")) color = StoveCampfireBlock.PotColor.BLUE;
                else if (itemId.contains("green")) color = StoveCampfireBlock.PotColor.GREEN;
                else if (itemId.contains("yellow")) color = StoveCampfireBlock.PotColor.YELLOW;
                else if (itemId.contains("black")) color = StoveCampfireBlock.PotColor.BLACK;
                else if (itemId.contains("white")) color = StoveCampfireBlock.PotColor.WHITE;
                else if (itemId.contains("pink")) color = StoveCampfireBlock.PotColor.PINK;

                // Replace stove with our custom stove campfire block
                BlockState newBlockState = ModBlocks.STOVE_CAMPFIRE.get()
                        .defaultBlockState()
                        .setValue(HorizontalDirectionalBlock.FACING, stoveFacing)
                        .setValue(StoveCampfireBlock.ITEM_DIRECTION, stoveFacing)
                        .setValue(StoveCampfireBlock.COLOR, color);

                world.setBlockAndUpdate(blockPos, newBlockState);

                var newBlockEntity = world.getBlockEntity(blockPos);
                if (newBlockEntity instanceof CampfireBlockEntity campfireBlockEntity) {
                    if (campfireBlockEntity.getPotItem() == null || campfireBlockEntity.getPotItem().isEmpty()) {
                        campfireBlockEntity.setPotItem(new ItemStack(context.getItemInHand().getItem(), 1));
                        context.getItemInHand().shrink(1);
                        world.playSound(null, blockPos, CobblemonSounds.CAMPFIRE_POT_SET, SoundSource.BLOCKS, 1.0F, 1.0F);
                        cir.setReturnValue(InteractionResult.SUCCESS);
                        return;
                    }
                }
            } else if (!player.isCrouching()) {
                cir.setReturnValue(InteractionResult.FAIL);
                return;
            }
        }
    }
}