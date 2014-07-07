package com.aaomidi.dev.guishop.engine.objects;


import com.aaomidi.dev.guishop.GUIShop;
import com.aaomidi.dev.guishop.engine.Caching;
import com.aaomidi.dev.guishop.engine.modules.MenuBehaviour;
import com.aaomidi.dev.guishop.utils.ConfigReader;
import com.aaomidi.dev.guishop.utils.StringManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class GUIStock extends MenuBehaviour {
    @Getter
    private final String fqn;
    @Getter
    private final String name;
    @Getter
    private final List<String> lore;
    @Getter
    private final int buyPrice;
    @Getter
    private final int sellPrice;
    @Getter
    private final String command;

    public ItemStack toItemStack() {
        try {
            String[] fqnArray = fqn.split(":");

            ItemStack item = GUIShop.getEss().getItemDb().get(fqnArray[0]);
            if (fqnArray.length == 2) {
                item.setDurability(Short.parseShort(fqnArray[1]));
            }
            ItemMeta itemMeta = item.getItemMeta();
            if (!name.isEmpty()) {
                itemMeta.setDisplayName(StringManager.colorize(name));
            }
            if (!lore.isEmpty()) {
                itemMeta.setLore(StringManager.colorize(lore, this));
            } else {
                List<String> localLore = new ArrayList<>();
                localLore.add("&eBuy at &b%bprice% &eeach!");
                if (sellPrice == -1) {
                    localLore.add("&eThis item is not sellable.");
                } else {
                    localLore.add("&eSell at &b%sprice% &eeach!");
                }
                localLore.add("&eLeft click to buy!");
                localLore.add("3Right click to sell!");
                itemMeta.setLore(StringManager.colorize(localLore, this));
            }
            item.setItemMeta(itemMeta);
            item.setAmount(1);
            return item;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean isItem() {
        return command == null || command.isEmpty();
    }

    @Override
    public void onClose(Player player) {

    }

    @Override
    public void onLeftClick(Player player) {
        player.closeInventory();
        double playerBalance = GUIShop.getEconomy().getBalance(player);
        int buyableAmount = (int) Math.floor(playerBalance / buyPrice);
        StringManager.sendMessage(player, String.format("&bPlease type in the amount you want to buy.\n&bThe max you can buy is &e%d&b.", buyableAmount));
        Caching.getBuyInventoryMap().put(player, this);
    }

    @Override
    public void onRightClick(Player player) {
        player.closeInventory();
        if (sellPrice == -1) {
            StringManager.sendMessage(player, String.format("&3This item is not sellable."));
        } else {
            ItemStack item = this.toItemStack();
            int count = 0;
            for (ItemStack itemStack : player.getInventory().getContents()) {
                if (itemStack != null && itemStack.getType() == item.getType()) {
                    count += itemStack.getAmount();
                }
            }
            StringManager.sendMessage(player, String.format("&bPlease enter the amount you want to sell.\n&bThe max you can sell is &e%d&b.", count));
            Caching.getSellInventoryMap().put(player, this);
        }
    }

    public void buy(Player player, int amount) {
        ItemStack item = toItemStack();
        double playerBalance = GUIShop.getEconomy().getBalance(player.getName());
        int buyableAmount = (int) Math.floor(playerBalance / buyPrice);
        EconomyResponse response = GUIShop.getEconomy().withdrawPlayer(player, buyPrice * amount);
        if (!response.type.equals(EconomyResponse.ResponseType.SUCCESS)) {
            StringManager.sendMessage(player, String.format("&cThere was a problem buying &e%d %s&c.", amount, GUIShop.getEss().getItemDb().name(item) + "(s)"));
            player.playSound(player.getLocation(), ConfigReader.getDenySound(), 1, 0);
            return;
        }
        if (amount <= buyableAmount) {
            if (this.isItem()) {
                while (amount-- != 0) {
                    player.getInventory().addItem(item);
                }
            } else {
                while (amount-- != 0) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%p%", player.getName()));
                }
            }
            StringManager.sendMessage(player, "&bYou successfully bought that item.");
            player.playSound(player.getLocation(), ConfigReader.getBuySound(), 1, 0);
        } else {
            StringManager.sendMessage(player, "&3You cannot buy that much of that item.");
            player.playSound(player.getLocation(), ConfigReader.getDenySound(), 1, 0);

        }
    }

    public void sell(Player player, int amount) {
        ItemStack item = toItemStack();
        item.setAmount(1);
        int count = 0;
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() == item.getType()) {
                count += itemStack.getAmount();
            }
        }
        if (amount > count) {
            StringManager.sendMessage(player, String.format("&bYou do not have enough &e%s.", GUIShop.getEss().getItemDb().name(item)));
            player.playSound(player.getLocation(), ConfigReader.getDenySound(), 1, 0);

            return;
        }
        StringManager.sendMessage(player, String.format("&bYou have successfully sold &e%d %s&b.", amount, GUIShop.getEss().getItemDb().name(item)));
        player.playSound(player.getLocation(), ConfigReader.getSellSound(), 1, 0);
        GUIShop.getEconomy().depositPlayer(player, amount * sellPrice);
        while (amount-- != 0) {
            player.getInventory().removeItem(item);
        }
    }
}
