package com.aaomidi.dev.guishop.engine;


import com.aaomidi.dev.guishop.GUIShop;
import com.aaomidi.dev.guishop.engine.objects.GUICategory;
import com.aaomidi.dev.guishop.engine.objects.GUIStock;
import com.aaomidi.dev.guishop.utils.ConfigReader;
import com.aaomidi.dev.guishop.utils.StringManager;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class EventsManager implements Listener {
    @Getter
    private static Set<String> fix;
    private final GUIShop _plugin;
    Pattern pattern;

    public EventsManager(GUIShop plugin) {
        _plugin = plugin;
        pattern = Pattern.compile("^[+]?\\d+$");
        fix = new HashSet<>();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryInteract(InventoryClickEvent event) {
        try {

            Inventory inventory = event.getInventory();
            Player player = (Player) event.getWhoClicked();
            int clickedSlot = event.getRawSlot();
            if (inventory.getSize() <= clickedSlot) {
                return;
            }
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || inventory.getItem(clickedSlot) == null || inventory.getItem(clickedSlot).getType() == Material.AIR) {
                return;
            }
            if (inventory.getHolder() != null && inventory.getHolder() instanceof Player) {
                return;
            }
            if (!event.getSlotType().equals(InventoryType.SlotType.CONTAINER)) {
                return;
            }
            if (inventory.getHolder() instanceof InventoryManager) {
                event.setCancelled(true);
                GUICategory guiCategory = ConfigReader.getGuiCategories().get(clickedSlot);
                guiCategory.onLeftClick(player);
                fix.add(player.getName());
                return;
            }
            if (Caching.getOpenInventoryMap().containsKey(player)) {
                event.setCancelled(true);
                GUICategory guiCategory = Caching.getOpenInventoryMap().get(player);
                if (clickedSlot > guiCategory.getStock().size() || guiCategory.getStock().get(clickedSlot) == null) {
                    Caching.getOpenInventoryMap().remove(player);
                    return;
                }
                GUIStock guiStock = guiCategory.getStock().get(clickedSlot);
                if (event.getClick().isLeftClick()) {
                    guiStock.onLeftClick(player);
                } else if (event.getClick().isRightClick()) {
                    guiStock.onRightClick(player);
                }
                Caching.getOpenInventoryMap().remove(player);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (fix.contains(player.getName())) {
            event.getItemDrop().setItemStack(new ItemStack(Material.AIR));
            event.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
