/*
 *  Prison is a Minecraft plugin for the prison game mode.
 *  Copyright (C) 2016 The Prison Team
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.prison.spigot;

import io.github.prison.gui.Button;
import io.github.prison.gui.GUI;
import io.github.prison.internal.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SirFaizdat
 */
public class SpigotGUI implements GUI {

    private Map<Integer, Button> buttons;
    private String title;
    private int numRows;

    private Inventory bukkitInventory;

    public SpigotGUI(String title, int numRows) {
        this.buttons = new HashMap<>();
        this.title = title;
        this.numRows = numRows;
    }

    @Override
    public void show(Player... players) {
        for (Player player : players) {
            org.bukkit.entity.Player bPlayer = Bukkit.getServer().getPlayer(player.getName());
            bPlayer.openInventory(bukkitInventory);
        }
        GUIListener.getInstance().registerInventory(this);
    }

    @Override
    public GUI build() {
        bukkitInventory = Bukkit.getServer().createInventory(null, numRows * 9, ChatColor.translateAlternateColorCodes('&', title));
        for (Map.Entry<Integer, Button> button : buttons.entrySet())
            bukkitInventory.setItem(button.getKey(), buttonToItemStack(button.getValue()));

        return this;
    }

    private ItemStack buttonToItemStack(Button button) {
        ItemStack stack = new ItemStack(button.getItem().getLegacyId(), 1, button.getItem().getData());
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&r" + button.getName()));
        meta.setLore(button.getLore());
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getNumRows() {
        return numRows;
    }

    @Override
    public Map<Integer, Button> getButtons() {
        return buttons;
    }

    @Override
    public GUI addButton(int slot, Button button) {
        buttons.put(slot, button);
        return this;
    }

}
