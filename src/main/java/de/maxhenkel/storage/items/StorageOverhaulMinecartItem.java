package de.maxhenkel.storage.items;

import de.maxhenkel.storage.entity.StorageOverhaulChestMinecartEntity;
import de.maxhenkel.storage.items.render.ChestMinecartItemRenderer;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class StorageOverhaulMinecartItem extends Item {
    private final IDispenseItemBehavior MINECART_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {
        private final DefaultDispenseItemBehavior behaviourDefaultDispenseItem = new DefaultDispenseItemBehavior();

        @Override
        public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
            Direction direction = source.getBlockState().get(DispenserBlock.FACING);
            World world = source.getWorld();
            double x = source.getX() + (double) direction.getXOffset() * 1.125D;
            double y = Math.floor(source.getY()) + (double) direction.getYOffset();
            double z = source.getZ() + (double) direction.getZOffset() * 1.125D;
            BlockPos blockpos = source.getBlockPos().offset(direction);
            BlockState blockstate = world.getBlockState(blockpos);
            RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
            double varY;
            if (blockstate.isIn(BlockTags.RAILS)) {
                if (railshape.isAscending()) {
                    varY = 0.6D;
                } else {
                    varY = 0.1D;
                }
            } else {
                if (!blockstate.isAir(world, blockpos) || !world.getBlockState(blockpos.down()).isIn(BlockTags.RAILS)) {
                    return this.behaviourDefaultDispenseItem.dispense(source, stack);
                }

                BlockState blockstate1 = world.getBlockState(blockpos.down());
                RailShape railshape1 = blockstate1.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) blockstate1.getBlock()).getRailDirection(blockstate1, world, blockpos.down(), null) : RailShape.NORTH_SOUTH;
                if (direction != Direction.DOWN && railshape1.isAscending()) {
                    varY = -0.4D;
                } else {
                    varY = -0.9D;
                }
            }
            StorageOverhaulChestMinecartEntity cart = StorageOverhaulMinecartItem.this.minecart.get().create(world);
            cart.setPosition(x, y + varY, z);
            if (stack.hasDisplayName()) {
                cart.setCustomName(stack.getDisplayName());
            }

            world.addEntity(cart);
            stack.shrink(1);
            return stack;
        }

        @Override
        protected void playDispenseSound(IBlockSource source) {
            source.getWorld().playEvent(1000, source.getBlockPos(), 0);
        }
    };
    private final Supplier<EntityType<StorageOverhaulChestMinecartEntity>> minecart;

    public StorageOverhaulMinecartItem(Supplier<EntityType<StorageOverhaulChestMinecartEntity>> minecart) {
        super(new Item.Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION).setISTER(() -> () -> new ChestMinecartItemRenderer(minecart)));
        this.minecart = minecart;
        DispenserBlock.registerDispenseBehavior(this, MINECART_DISPENSER_BEHAVIOR);
    }

    public ActionResultType onItemUse(ItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockpos = context.getPos();
        BlockState blockstate = world.getBlockState(blockpos);
        if (!blockstate.isIn(BlockTags.RAILS)) {
            return ActionResultType.FAIL;
        } else {
            ItemStack itemstack = context.getItem();
            if (!world.isRemote) {
                RailShape railshape = blockstate.getBlock() instanceof AbstractRailBlock ? ((AbstractRailBlock) blockstate.getBlock()).getRailDirection(blockstate, world, blockpos, null) : RailShape.NORTH_SOUTH;
                double height = 0.0D;
                if (railshape.isAscending()) {
                    height = 0.5D;
                }

                StorageOverhaulChestMinecartEntity cart = StorageOverhaulMinecartItem.this.minecart.get().create(world);
                cart.setPosition((double) blockpos.getX() + 0.5D, (double) blockpos.getY() + 0.0625D + height, (double) blockpos.getZ() + 0.5D);
                if (itemstack.hasDisplayName()) {
                    cart.setCustomName(itemstack.getDisplayName());
                }

                world.addEntity(cart);
            }

            itemstack.shrink(1);
            return ActionResultType.SUCCESS;
        }
    }
}
