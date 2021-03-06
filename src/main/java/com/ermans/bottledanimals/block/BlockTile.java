package com.ermans.bottledanimals.block;

import com.ermans.api.IEnergyInfoBA;
import com.ermans.bottledanimals.BottledAnimals;
import com.ermans.bottledanimals.BottledAnimalsTab;
import com.ermans.bottledanimals.reference.Reference;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockTile extends BlockBase implements IGuiHandler, ITileEntityProvider {


    @SideOnly(Side.CLIENT)
    protected IIcon[][] iconBuffer;

    protected int guiId;


    public BlockTile(String machineName) {
        super(machineName);
        setHardness(3.0F);
        setStepSound(soundTypeMetal);
        setHarvestLevel("pickaxe", 1);
        setBlockName(machineName);
        setCreativeTab(BottledAnimalsTab.tabBottledAnimals);
        this.guiId = BottledAnimals.guiHandler.addGuiHandler(this);

    }

    @Override
    public void onNeighborChange(IBlockAccess world, int x, int y, int z, int tileX, int tileY, int tileZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileBottledAnimals) {
            ((TileBottledAnimals) te).onNeighborChange();
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block tileZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileBottledAnimals) {
            ((TileBottledAnimals) te).onNeighborChange();
        }
    }

    private boolean openGui(World world, int x, int y, int z, EntityPlayer entityPlayer) {
        entityPlayer.openGui(BottledAnimals.INSTANCE, this.guiId, world, x, y, z);
        return true;
    }


    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int metadata, float xClicked, float yClicked, float zClicked) {

        if (!world.isRemote) {
            ItemStack equipped = player.getCurrentEquippedItem();
            TileEntity tileEntity = world.getTileEntity(x, y, z);

            if (tileEntity != null) {
                if (tileEntity instanceof TileBase) {

                    if (((TileBase) tileEntity).handleRightClick(player, equipped, xClicked, yClicked, zClicked)) {
                        return true;
                    }
                }
                if (player.isSneaking()) {
                    return false;
                }
                openGui(world, x, y, z, player);
                return true;

            }
        }
        return true;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block block, int par6) {
        IInventory inventory = (worldIn.getTileEntity(x, y, z) instanceof IInventory) ? (IInventory) worldIn.getTileEntity(x, y, z) : null;
        if (inventory != null) {
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                if (inventory.getStackInSlot(i) != null) {
                    EntityItem item = new EntityItem(worldIn, x + 0.5D, y + 0.5D, z + 0.5D, inventory.getStackInSlot(i));
                    float multiplier = 0.1F;
                    float motionX = worldIn.rand.nextFloat() - 0.5F;
                    float motionY = worldIn.rand.nextFloat() - 0.5F;
                    float motionZ = worldIn.rand.nextFloat() - 0.5F;

                    item.motionX = (motionX * multiplier);
                    item.motionY = (motionY * multiplier);
                    item.motionZ = (motionZ * multiplier);

                    worldIn.spawnEntityInWorld(item);
                }
            }
        }

        TileEntity tile = worldIn.getTileEntity(x, y, z);
        if (tile != null) {
            worldIn.removeTileEntity(x, y, z);
        }
    }


    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, player, stack);

        TileBottledAnimals te = (TileBottledAnimals) world.getTileEntity(x, y, z);
        if (te != null) {
            te.setFacing(player.rotationYaw);
            te.setTileName(getBlockName());
        }
        world.markBlockForUpdate(x, y, z);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iIconRegister) {

        this.blockIcon = iIconRegister.registerIcon(getTileSideTexture());

        this.iconBuffer = new IIcon[2][6];

        iconBuffer[0][0] = iIconRegister.registerIcon(getTileBottomTexture());
        iconBuffer[0][1] = iIconRegister.registerIcon(getTileTopTexture());
        iconBuffer[0][2] = iIconRegister.registerIcon(getTileSideTexture());
        iconBuffer[0][3] = iIconRegister.registerIcon(getTileFrontTexture());
        iconBuffer[0][4] = iIconRegister.registerIcon(getTileSideTexture());
        iconBuffer[0][5] = iIconRegister.registerIcon(getTileSideTexture());

        iconBuffer[1][0] = iIconRegister.registerIcon(getTileBottomTexture());
        iconBuffer[1][1] = iIconRegister.registerIcon(getTileTopTexture());
        iconBuffer[1][2] = iIconRegister.registerIcon(getTileSideTexture());
        iconBuffer[1][3] = iIconRegister.registerIcon(getTileFrontTexture() + "On");
        iconBuffer[1][4] = iIconRegister.registerIcon(getTileSideTexture());
        iconBuffer[1][5] = iIconRegister.registerIcon(getTileSideTexture());
    }

    protected abstract String getTileSideTexture();

    protected abstract String getTileTopTexture();

    protected abstract String getTileBottomTexture();

    protected abstract String getTileFrontTexture();


    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconBuffer[0][side];
    }


    private static final int[][] sideAndFacingToSpriteOffset = {
            {3, 2, 0, 0, 0, 0},
            {2, 3, 1, 1, 1, 1},
            {1, 1, 3, 2, 5, 4},
            {0, 0, 2, 3, 4, 5},
            {4, 5, 4, 5, 3, 2},
            {5, 4, 5, 4, 2, 3}
    };

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof TileBottledAnimals) {
            int iconIndex = sideAndFacingToSpriteOffset[side][((TileBottledAnimals) te).getFacing()];
            if (te instanceof IEnergyInfoBA) {
                return iconBuffer[((IEnergyInfoBA) te).isActive() ? 1 : 0][iconIndex];
            } else {
                return iconBuffer[0][iconIndex];
            }
        }
        return null;
    }



    @Override
    public String getUnlocalizedName() {
        return String.format("tile.%s%s", Reference.MOD_ID_LOWERCASE + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }

    private String getUnwrappedUnlocalizedName(String unlocalizedName) {
        return unlocalizedName.substring(unlocalizedName.indexOf(":") + 1);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof IEnergyInfoBA) {
            if (((IEnergyInfoBA) tile).isActive()) {
                return 14;
            } else {
                return 0;
            }
        }
        return 0;
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int event, int value) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null) {
            return tile.receiveClientEvent(event, value);
        }
        return super.onBlockEventReceived(world, x, y, z, event, value);
    }
}

