package com.ermans.bottledanimals.block.machine.wirelessfeeder;

import com.ermans.bottledanimals.client.gui.GuiBaseAdv;
import com.ermans.bottledanimals.client.gui.button.ElementButtonOptionWF;
import com.ermans.bottledanimals.client.gui.tab.TabInfo;
import com.ermans.bottledanimals.reference.Textures;
import com.ermans.repackage.cofh.lib.gui.element.ElementButtonOption;
import com.ermans.repackage.cofh.lib.gui.element.ElementEnergyStored;
import com.ermans.repackage.cofh.lib.gui.element.ElementFluidTank;
import com.ermans.repackage.cofh.lib.gui.element.TabBase;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiWirelessFeeder extends GuiBaseAdv {

    private final TileWirelessFeeder tile;
    private ElementButtonOption buttonHeal;

    public GuiWirelessFeeder(InventoryPlayer invPlayer, TileWirelessFeeder entity) {
        super(new ContainerWirelessFeeder(invPlayer, entity), Textures.Gui.WIRELESS_FEEDER, entity);
        this.tile = entity;

    }


    @Override
    public void initGui() {
        super.initGui();
        addTab(new TabInfo(this, TabBase.LEFT, "Heals and feeds player in a 5x5x5 area"));

        addElement(new ElementEnergyStored(this, tile.getEnergyStorage()));
        addElement(new ElementFluidTank(this, tile.getFluidTank()).setGauge(1));

        buttonHeal = (ElementButtonOption) addElement(new ElementButtonOptionWF(this, 131, 54, 16, 16).setTexture(Textures.WIDGET_TEXTURE_STRING, 256, 256));
        buttonHeal.setDefaultValue(tile.getMode().ordinal());
    }


    public TileWirelessFeeder getTile() {
        return tile;
    }


}