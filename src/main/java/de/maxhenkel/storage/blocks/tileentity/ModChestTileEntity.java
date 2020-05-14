package de.maxhenkel.storage.blocks.tileentity;

import de.maxhenkel.storage.blocks.ModChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.WoodType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.minecraftforge.items.wrapper.InvWrapper;

@OnlyIn(value = Dist.CLIENT, _interface = IChestLid.class)
public class ModChestTileEntity extends LockableLootTileEntity implements IChestLid, ITickableTileEntity {

    protected NonNullList<ItemStack> chestContents;
    protected float lidAngle;
    protected float prevLidAngle;
    protected int numPlayersUsing;
    protected LazyOptional<IItemHandlerModifiable> chestHandler;
    protected WoodType woodType;

    public ModChestTileEntity(WoodType woodType) {
        super(ModTileEntities.CHEST);
        this.woodType = woodType;
        chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
    }

    public WoodType getWoodType() {
        if (woodType == null) {
            woodType = ((ModChestBlock) getBlockState().getBlock()).getWoodType();
        }
        return woodType;
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return ChestContainer.createGeneric9X3(id, player, this); //TODO
    }

    @Override
    protected ITextComponent getDefaultName() {
        return getBlockState().getBlock().getNameTextComponent();
    }

    @Override
    public int getSizeInventory() {
        return 27;
    }//TODO

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        chestContents = NonNullList.withSize(getSizeInventory(), ItemStack.EMPTY);
        if (!checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems(compound, chestContents);
        }

    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        if (!checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, chestContents);
        }

        return compound;
    }

    @Override
    public void tick() {
        prevLidAngle = lidAngle;
        if (numPlayersUsing > 0 && lidAngle == 0F) {
            playSound(SoundEvents.BLOCK_CHEST_OPEN);
        }

        if (numPlayersUsing == 0 && lidAngle > 0F || numPlayersUsing > 0 && lidAngle < 1F) {
            float oldAngle = lidAngle;
            if (numPlayersUsing > 0) {
                lidAngle += 0.1F;
            } else {
                lidAngle -= 0.1F;
            }

            if (lidAngle > 1F) {
                lidAngle = 1F;
            }

            if (lidAngle < 0.5F && oldAngle >= 0.5F) {
                playSound(SoundEvents.BLOCK_CHEST_CLOSE);
            }

            if (lidAngle < 0F) {
                lidAngle = 0F;
            }
        }

    }

    private void playSound(SoundEvent soundIn) {
        ChestType chesttype = getBlockState().get(ModChestBlock.TYPE);
        if (chesttype != ChestType.LEFT) {
            double x = (double) pos.getX() + 0.5D;
            double y = (double) pos.getY() + 0.5D;
            double z = (double) pos.getZ() + 0.5D;
            if (chesttype == ChestType.RIGHT) {
                Direction direction = ChestBlock.getDirectionToAttached(getBlockState());
                x += (double) direction.getXOffset() * 0.5D;
                z += (double) direction.getZOffset() * 0.5D;
            }

            world.playSound(null, x, y, z, soundIn, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    public boolean receiveClientEvent(int id, int value) {
        if (id == 1) {
            numPlayersUsing = value;
            return true;
        } else {
            return super.receiveClientEvent(id, value);
        }
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (numPlayersUsing < 0) {
                numPlayersUsing = 0;
            }

            numPlayersUsing++;
            onOpenOrClose();
        }

    }

    @Override
    public void closeInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            numPlayersUsing--;
            onOpenOrClose();
        }

    }

    protected void onOpenOrClose() {
        Block block = getBlockState().getBlock();
        if (block instanceof ModChestBlock) {
            world.addBlockEvent(pos, block, 1, numPlayersUsing);
            world.notifyNeighborsOfStateChange(pos, block);
        }
    }

    protected NonNullList<ItemStack> getItems() {
        return chestContents;
    }

    protected void setItems(NonNullList<ItemStack> items) {
        chestContents = items;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public float getLidAngle(float partialTicks) {
        return MathHelper.lerp(partialTicks, prevLidAngle, lidAngle);
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if (chestHandler != null) {
            chestHandler.invalidate();
            chestHandler = null;
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (!removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (chestHandler == null) {
                chestHandler = LazyOptional.of(this::createHandler);
            }
            return chestHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    private IItemHandlerModifiable createHandler() {
        BlockState state = this.getBlockState();
        if (!(state.getBlock() instanceof ChestBlock)) {
            return new InvWrapper(this);
        }
        ChestType type = state.get(ChestBlock.TYPE);
        if (type != ChestType.SINGLE) {
            BlockPos opos = this.getPos().offset(ChestBlock.getDirectionToAttached(state));
            BlockState ostate = this.getWorld().getBlockState(opos);
            if (state.getBlock() == ostate.getBlock()) {
                ChestType otype = ostate.get(ChestBlock.TYPE);
                if (otype != ChestType.SINGLE && type != otype && state.get(ChestBlock.FACING) == ostate.get(ChestBlock.FACING)) {
                    TileEntity ote = this.getWorld().getTileEntity(opos);
                    if (ote instanceof ChestTileEntity) {
                        IInventory top = type == ChestType.RIGHT ? this : (IInventory) ote;
                        IInventory bottom = type == ChestType.RIGHT ? (IInventory) ote : this;
                        return new CombinedInvWrapper(new InvWrapper(top), new InvWrapper(bottom));
                    }
                }
            }
        }
        return new InvWrapper(this);
    }

    @Override
    public void remove() {
        super.remove();
        if (chestHandler != null)
            chestHandler.invalidate();
    }
}
