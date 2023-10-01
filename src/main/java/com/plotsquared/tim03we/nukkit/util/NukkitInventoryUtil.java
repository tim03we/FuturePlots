package com.plotsquared.tim03we.nukkit.util;

import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;
import com.intellectualcrafters.plot.PS;
import com.intellectualcrafters.plot.config.C;
import com.intellectualcrafters.plot.object.PlotInventory;
import com.intellectualcrafters.plot.object.PlotItemStack;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.intellectualcrafters.plot.util.InventoryUtil;
import com.plotsquared.tim03we.nukkit.object.NukkitPlayer;

public class NukkitInventoryUtil extends InventoryUtil {

    public NukkitInventoryUtil() {
        PS.debug("Not implemented: NukkitInventoryUtil");
    }

    public static Item getItem(PlotItemStack item) {
        if (item == null) {
            return null;
        }
        Item stack = new Item(item.id, item.amount, item.data);
        if (item.name != null) {
            stack.setCustomName(C.color(item.name));
        }
        if (item.lore != null) {
            // TODO not implemented
        }
        return stack;
    }

    @Override
    public void open(PlotInventory inv) {
        return; // TODO
    }

    @Override
    public void close(PlotInventory inv) {
        return; // TODO
    }

    @Override
    public void setItem(PlotInventory inv, int index, PlotItemStack item) {
        return; // TODO
    }

    public PlotItemStack getItem(Item item) {
        if (item == null) {
            return null;
        }
        int id = item.getId();
        int data = item.getDamage();
        int amount = item.count;
        String name = item.getCustomName();
        if (name.length() == 0) {
            name = null;
        }
        return new PlotItemStack(id, (short) data, amount, name);
    }

    @Override
    public PlotItemStack[] getItems(PlotPlayer player) {
        NukkitPlayer bp = (NukkitPlayer) player;
        PlayerInventory inv = bp.player.getInventory();
        PlotItemStack[] items = new PlotItemStack[36];
        for (int i = 0; i < 36; i++) {
            items[i] = getItem(inv.getItem(i));
        }
        return items;
    }

    @Override
    public boolean isOpen(PlotInventory inv) {
        return false; // TODO
    }
}
