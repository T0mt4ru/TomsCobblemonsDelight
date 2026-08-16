package com.tomtaru.tmtcobfarm.block;

import com.cobblemon.mod.common.CobblemonBlockEntities;
import com.cobblemon.mod.common.CobblemonSounds;
import com.cobblemon.mod.common.block.campfirepot.CampfireBlock;
import com.cobblemon.mod.common.block.entity.CampfireBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.FarmersDelight;
import vectorwing.farmersdelight.common.block.StoveBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class StoveCampfireBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

    private static final MapCodec<StoveCampfireBlock> CODEC = simpleCodec(StoveCampfireBlock::new);

    public enum PotColor implements StringRepresentable {
        RED("red"),
        BLUE("blue"),
        GREEN("green"),
        YELLOW("yellow"),
        PINK("pink"),
        BLACK("black"),
        WHITE("white");

        private final String name;

        PotColor(String name) {
            this.name = name;
        }
        @Override
        public String getSerializedName() {
            return this.name;
        }
    }

    // Cobblemon Campfire Properties
    public static final DirectionProperty ITEM_DIRECTION = CampfireBlock.Companion.getITEM_DIRECTION();
    public static final BooleanProperty COOKING = CampfireBlock.Companion.getCOOKING();
    public static final BooleanProperty LID = CampfireBlock.Companion.getLID();
    public static final BooleanProperty POWERED = CampfireBlock.Companion.getPOWERED();

    // Standard Properties
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // Color Property
    public static final EnumProperty<PotColor> COLOR = EnumProperty.create("color", PotColor.class);

    private static final VoxelShape BASE_STOVE = Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    private static final VoxelShape POT_BOTTOM = Shapes.box(0.125, 1.0, 0.125, 0.875, 1.0625, 0.875);
    private static final VoxelShape SIDE_NORTH = Shapes.box(0.1875, 1.0625, 0.125, 0.875, 1.375, 0.1875);
    private static final VoxelShape SIDE_EAST = Shapes.box(0.8125, 1.0625, 0.1875, 0.875, 1.375, 0.875);
    private static final VoxelShape SIDE_WEST = Shapes.box(0.125, 1.0625, 0.125, 0.1875, 1.375, 0.8125);
    private static final VoxelShape SIDE_SOUTH = Shapes.box(0.125, 1.0625, 0.8125, 0.8125, 1.375, 0.875);

    private static final VoxelShape SHAPE = Shapes.or(
            BASE_STOVE,
            POT_BOTTOM,
            SIDE_NORTH,
            SIDE_EAST,
            SIDE_WEST,
            SIDE_SOUTH
    );

    public StoveCampfireBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ITEM_DIRECTION, Direction.NORTH)
                .setValue(POWERED, false)
                .setValue(COOKING, false)
                .setValue(LID, false)
                .setValue(WATERLOGGED, false)
                .setValue(COLOR, PotColor.RED));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() { return CODEC; }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ITEM_DIRECTION, POWERED, COOKING, LID, WATERLOGGED, COLOR);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(ITEM_DIRECTION, facing)
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == net.minecraft.world.level.material.Fluids.WATER);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.getBlockEntity(pos) instanceof CampfireBlockEntity campfire) {
            if (!level.isClientSide) {
                player.openMenu(campfire);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }


    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (blockEntityType == CobblemonBlockEntities.CAMPFIRE) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof CampfireBlockEntity campfire) {
                    if (level1.isClientSide) CampfireBlockEntity.Companion.clientTick(level1, pos, state1, campfire);
                    else CampfireBlockEntity.Companion.serverTick(level1, pos, state1, campfire);
                }
            };
        }
        return null;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CampfireBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof CampfireBlockEntity campfire) {
                if (level instanceof ServerLevel) {
                    Containers.dropContents(level, pos, campfire);
                    ItemStack potItem = campfire.getPotItem();
                    if (!newState.is(ModBlocks.STOVE.get())) {
                    ItemStack stoveItem = new ItemStack(ModBlocks.STOVE.get());
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stoveItem);
                    }

                    if (!potItem.isEmpty()) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), potItem);
                    }
                }
            }
            super.onRemove(state, level, pos, newState, movedByPiston);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit) {
        // Handle shift-right-click to remove pot and revert to stove
        if (player.isShiftKeyDown() && stack.isEmpty()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CampfireBlockEntity campfireBlockEntity) {
                // Get the pot item from the block entity
                ItemStack potItem = campfireBlockEntity.getPotItem();
                Containers.dropContents(level, pos, campfireBlockEntity);

                if (!potItem.isEmpty()) {
                    // Drop the pot item
                    if (!level.isClientSide && !player.isCreative()) {
                        player.setItemInHand(hand, potItem.copy());
                        level.playSound(null, pos, CobblemonSounds.CAMPFIRE_POT_BREAK,  SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    campfireBlockEntity.setPotItem(ItemStack.EMPTY);

                    // Replace with regular stove, preserving facing and lit state
                    BlockState stoveState = ModBlocks.STOVE.get().defaultBlockState()
                            .setValue(StoveBlock.FACING, state.getValue(FACING))
                            .setValue(StoveBlock.LIT, true);

                    level.setBlock(pos, stoveState, 3);

                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        // Otherwise, use the parent behavior (open GUI, etc.)
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, net.minecraft.util.RandomSource random) {
            Direction direction = state.getValue(FACING);
            double x = (double) pos.getX() + 0.5;
            double y =  pos.getY();
            double z = (double) pos.getZ() + 0.5;

            if (random.nextDouble() < 0.1) {
                level.playLocalSound(x, y, z, net.minecraft.sounds.SoundEvents.FURNACE_FIRE_CRACKLE,
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.4F, 0.4F, false);
            }

            // Offset for front face particles
            double horizontalOffset = 0.52;
            double verticalOffset = random.nextDouble() * 6.0 / 16.0;
            double sideOffset = random.nextDouble() * 0.6 - 0.3;

            switch (direction) {
                case NORTH:
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                            x + sideOffset, y + verticalOffset, z - horizontalOffset, 0.0, 0.0, 0.0);
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.FLAME,
                            x + sideOffset, y + verticalOffset, z - horizontalOffset, 0.0, 0.0, 0.0);
                    break;
                case SOUTH:
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                            x + sideOffset, y + verticalOffset, z + horizontalOffset, 0.0, 0.0, 0.0);
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.FLAME,
                            x + sideOffset, y + verticalOffset, z + horizontalOffset, 0.0, 0.0, 0.0);
                    break;
                case WEST:
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                            x - horizontalOffset, y + verticalOffset, z + sideOffset, 0.0, 0.0, 0.0);
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.FLAME,
                            x - horizontalOffset, y + verticalOffset, z + sideOffset, 0.0, 0.0, 0.0);
                    break;
                case EAST:
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                            x + horizontalOffset, y + verticalOffset, z + sideOffset, 0.0, 0.0, 0.0);
                    level.addParticle(net.minecraft.core.particles.ParticleTypes.FLAME,
                            x + horizontalOffset, y + verticalOffset, z + sideOffset, 0.0, 0.0, 0.0);
                    break;
            }
        }


    @Override
    protected RenderShape getRenderShape(BlockState state) { return RenderShape.MODEL; }

    @Override
    public net.minecraft.world.item.Item asItem() {
        // Return the stove item when this block is picked up
        return ModBlocks.STOVE.get().asItem();
    }

}