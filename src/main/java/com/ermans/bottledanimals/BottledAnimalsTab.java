package com.ermans.bottledanimals;

import com.ermans.bottledanimals.init.ModItems;
import com.ermans.bottledanimals.reference.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BottledAnimalsTab extends CreativeTabs {
    public static final CreativeTabs tabBottledAnimals = new BottledAnimalsTab();

    private BottledAnimalsTab() {
        super(Reference.MOD_ID_LOWERCASE);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getTabIconItem() {
        return ModItems.itemModIcon;
    }
}
