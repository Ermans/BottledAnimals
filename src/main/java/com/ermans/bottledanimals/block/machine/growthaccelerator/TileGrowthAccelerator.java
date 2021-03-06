package com.ermans.bottledanimals.block.machine.growthaccelerator;

import com.ermans.bottledanimals.block.machine.TileMachine;
import com.ermans.bottledanimals.init.ModItems;
import com.ermans.bottledanimals.recipe.GrowthAcceleratorManager;
import com.ermans.bottledanimals.recipe.IRecipe;
import com.ermans.bottledanimals.reference.Animals;
import com.ermans.repackage.cofh.lib.util.helpers.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileGrowthAccelerator extends TileMachine {

    private static final GrowthAcceleratorManager recManager = GrowthAcceleratorManager.INSTANCE;


    private static final int animal = 0;
    private static final int multi = 1;
    private static final int output = 2;

    private byte multLevel = 1;

    public int multiplier = 1;

    @Override
    public void initTile() {
        super.initTile();
        this.storage.setCapacity(400000);
        this.storage.setMaxReceive(400);
        this.storage.setMaxExtract(400000);
        this.RFTick = 2;
    }

    @Override
    protected IRecipe getRecipeIfCanStart() {
        if (inventory[animal] == null) {
            return null;
        }
        GrowthAcceleratorManager.GrowthAcceleratorRecipe recipe = recManager.getRecipeIfValid(inventory[animal]);

        if (recipe == null) {
            return null;
        }
        if (inventory[output] == null) {
            return recipe;
        }
        if (!inventory[output].isItemEqual(recipe.getOutput())) {
            return null;
        }
        if (recipe.getOutputStackSize() + inventory[output].stackSize <= inventory[output].getMaxStackSize()){
            return recipe;
        }
        return null;
    }

    @Override
    protected void startProcess() {
        this.multLevel = 1;
    }

    @Override
    protected boolean canStillProcess(int recipeCode) {
        if (inventory[animal] == null) {
            return false;
        }

        GrowthAcceleratorManager.GrowthAcceleratorRecipe recipe = recManager.getRecipeIfValid(inventory[animal]);
        return recipe != null && recipe.getCode() == recipeCode;
    }

    @Override
    protected boolean updateMachine() {
        if (multLevel < 5) {
            if (inventory[multi] == null) {
                return false;
            }
            Animals an = Animals.getAnimalsFromID(inventory[animal].getItemDamage());
            if (an == null) {
                return false;
            }
            for (ItemStack food : an.getValidFoods()) {
                if (food.isItemEqual(inventory[multi])) {
                    remaining = MathHelper.ceil(remaining * 0.85);
                    decrStackSize(multi, 1);
                    multLevel++;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void operationStopped() {
        multLevel = 1;
    }

    @Override
    protected boolean shouldStopIfChangeSlot(int slotIndex, int mode) {
        if (slotIndex == multi) return false;
        return super.shouldStopIfChangeSlot(slotIndex, mode);
    }

    @Override
    protected void finishProcess() {
        GrowthAcceleratorManager.GrowthAcceleratorRecipe recipe = recManager.getRecipeIfValid(inventory[animal]);
        increaseStackSize(output, recipe.getOutput());
        decrStackSize(animal, recipe.getInputStackSize());
        multLevel = 1;
    }



    @Override
    protected int getNumInput() {
        return 2;
    }

    @Override
    protected int getNumOutput() {
        return 1;
    }

    @SideOnly(Side.CLIENT)
    public int getMultiplierLevelScaled(int scale) {
        return multLevel * scale / 5;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
        if (!invHelper.isInput(slot)) {
            return false;
        }
        if (slot == animal) {
            return itemstack.getItem() == ModItems.itemDigitalizedBabyAnimal;
        }
        if (slot == multi) {
            return Animals.getFoodsSet().contains(itemstack.getItem());
        }
        return false;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        multLevel = buf.readByte();

    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        buf.writeByte(multLevel);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setByte("multLevel", multLevel);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        multLevel = nbtTagCompound.getByte("multLevel");
    }


}
