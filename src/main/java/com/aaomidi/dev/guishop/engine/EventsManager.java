package com.aaomidi.dev.guishop.engine;


import com.aaomidi.dev.guishop.GUIShop;
import com.aaomidi.dev.guishop.engine.objects.GUICategory;
import com.aaomidi.dev.guishop.engine.objects.GUIStock;
import com.aaomidi.dev.guishop.utils.ConfigReader;
import com.aaomidi.dev.guishop.utils.StringManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.regex.Pattern;

public class EventsManager implements Listener {
    private final GUIShop _plugin;
    Pattern pattern;

    public EventsManager(GUIShop plugin) {
        _plugin = plugin;
        pattern = Pattern.compile("^[-+]?\\d+$");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryInteract(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        int clickedSlot = event.getRawSlot();
        if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
            if (inventory.getHolder() instanceof InventoryManager) {
                GUICategory guiCategory = ConfigReader.getGuiCategories().get(clickedSlot);
                guiCategory.onLeftClick(player);
                event.setCancelled(true);
                return;
            }
            if (Caching.getOpenInventoryMap().containsKey(player)) {
                GUICategory guiCategory = Caching.getOpenInventoryMap().get(player);
                GUIStock guiStock = guiCategory.getStock().get(clickedSlot);
                if (event.getClick().isLeftClick()) {
                    guiStock.onLeftClick(player);
                } else if (event.getClick().isRightClick()) {
                    guiStock.onRightClick(player);
                }
                Caching.getOpenInventoryMap().remove(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        if (Caching.getBuyInventoryMap().containsKey(player)) {
            final GUIStock guiStock = Caching.getBuyInventoryMap().get(player);
            if (pattern.matcher(event.getMessage()).matches()) {
                final Integer amount = Integer.valueOf(event.getMessage());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        guiStock.buy(player, amount);

                    }
                }.runTask(_plugin);
            } else {
                StringManager.sendMessage(player, "&3Please enter a number.");
            }
            Caching.getBuyInventoryMap().remove(player);
            event.setCancelled(true);
            return;
        }
        if (Caching.getSellInventoryMap().containsKey(player)) {
            final GUIStock guiStock = Caching.getSellInventoryMap().get(player);
            if (pattern.matcher(event.getMessage()).matches()) {
                final Integer amount = Integer.valueOf(event.getMessage());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        guiStock.sell(player, amount);

                    }
                }.runTask(_plugin);
            } else {
                StringManager.sendMessage(player, "&3Please enter a number.");
            }
            Caching.getSellInventoryMap().remove(player);
            event.setCancelled(true);
            return;
        }
    }
}
