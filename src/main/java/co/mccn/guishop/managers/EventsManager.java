package co.mccn.guishop.managers;

import co.mccn.guishop.GUIShop;
import co.mccn.guishop.objects.CategoryItem;
import co.mccn.guishop.objects.ShopItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class EventsManager implements Listener {
    private final GUIShop _plugin;

    public EventsManager(GUIShop plugin) {
        _plugin = plugin;
    }

    @EventHandler
    private void inventoryClickEvent(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if ((event.getCurrentItem() != null) && (event.getCurrentItem().getType() != Material.AIR) && (event.getClick() != null)) {
            ItemStack item = event.getCurrentItem();
            if (event.getInventory().getTitle().equals(_plugin.getInventoryManager().getMainInventory().getTitle())) {
                if (event.getClick().isLeftClick()) {
                    if (event.getSlotType().equals(InventoryType.SlotType.CONTAINER)) {
                        int slot = event.getSlot() + 1;
                        event.setCancelled(true);
                        Inventory inventory = _plugin.getInventoryManager().createInventory(slot);
                        player.closeInventory();
                        player.openInventory(inventory);
                        return;
                    } else {
                        event.setCancelled(true);
                    }
                } else {
                    event.setCancelled(true);
                }
                event.setCancelled(true);
            }

            for (Integer i : _plugin.getConfigManager().getCategoryInformationMap().keySet()) {
                CategoryItem categoryItem = _plugin.getConfigManager().getCategoryInformationMap().get(i);
                if (ChatColor.stripColor(event.getInventory().getTitle()).equals(ChatColor.stripColor(_plugin.getUtilsManager().colorizeString(_plugin.getConfigManager().getLanguageInformationMap().get("CategoryName").replace("%cat%", categoryItem.getName()))))) {
                    int categoryNumber = categoryItem.getPosition();
                    int slot = event.getSlot() + 1;
                    if (slot == event.getInventory().getSize()) {
                        player.closeInventory();
                        player.openInventory(_plugin.getInventoryManager().getMainInventory());
                        event.setCancelled(true);
                        return;
                    } else {
                        ShopItem shopItem = _plugin.getConfigManager().getCategoryItemInformationMap().get(categoryNumber).get(slot);
                        if (event.getClick() != null) {
                            if (event.getSlotType().equals(InventoryType.SlotType.CONTAINER)) {
                                if (event.getClick().isLeftClick()) {
                                    event.setCancelled(true);
                                    //Buy Item
                                    player.closeInventory();
                                    this.buyItemMessages(player, shopItem);
                                } else if (event.getClick().isRightClick()) {
                                    event.setCancelled(true);
                                    //Sell Item
                                    player.closeInventory();
                                    this.sellItemMessages(player, shopItem);
                                }
                            }
                        }
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Economy eco = _plugin.getEconomy();
        Double playerBalance = eco.getBalance(player.getName());
        if (_plugin.getBuyMap() != null) {
            if (_plugin.getBuyMap().get(player.getName()) != null) {
                try {
                    Integer count = Integer.valueOf(event.getMessage());
                    if (count > 0) {
                        ShopItem shopItem = _plugin.getBuyMap().get(player.getName());
                        Double itemPrice = shopItem.getBuyPrice();
                        if ((count * itemPrice) <= playerBalance) {
                            eco.withdrawPlayer(player.getName(), (count * itemPrice));
                            ItemStack item = shopItem.getItem();
                            ItemMeta itemMeta = item.getItemMeta();
                            itemMeta.setLore(null);
                            item.setItemMeta(itemMeta);
                            item.setAmount(count);
                            HashMap<Integer, ItemStack> extraItems = player.getInventory().addItem(item);
                            for (Integer i : extraItems.keySet()) {
                                if (extraItems.get(i) != null) {
                                    player.getLocation().getWorld().dropItem(player.getLocation(), extraItems.get(i));
                                    _plugin.getChatManager().sendMessage(player, "&cThere was not enough space in your inventory.\n&cDropping items at your feet.");
                                } else {
                                }
                            }
                            _plugin.getChatManager().sendMessage(player, "&bTransaction was successful!\n&bNew balance: &e" + eco.getBalance(player.getName()));
                            _plugin.getBuyMap().remove(player.getName());
                            player.playSound(player.getLocation(), Sound.valueOf(_plugin.getConfigManager().getGeneralSettingsMap().get("BuySound")), 1, 0);
                            event.setCancelled(true);
                            return;
                        } else {
                            _plugin.getChatManager().sendMessage(player, "&cYou do not have enough money to fulfill that transaction.");
                            player.playSound(player.getLocation(), Sound.valueOf(_plugin.getConfigManager().getGeneralSettingsMap().get("DenySound")), 1, 0);

                            _plugin.getBuyMap().remove(player.getName());
                            event.setCancelled(true);
                            return;
                        }
                    } else {
                        _plugin.getChatManager().sendMessage(player, "&cEnter a positive number!");
                        player.playSound(player.getLocation(), Sound.valueOf(_plugin.getConfigManager().getGeneralSettingsMap().get("DenySound")), 1, 0);

                        player.openInventory(_plugin.getInventoryManager().getMainInventory());
                        _plugin.getBuyMap().remove(player.getName());
                        event.setCancelled(true);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    _plugin.getChatManager().sendMessage(player, "&cEnter a number only!");
                    player.openInventory(_plugin.getInventoryManager().getMainInventory());
                    player.playSound(player.getLocation(), Sound.valueOf(_plugin.getConfigManager().getGeneralSettingsMap().get("DenySound")), 1, 0);

                    _plugin.getBuyMap().remove(player.getName());
                    event.setCancelled(true);
                    return;

                }
            }
        }
        if (_plugin.getSellMap() != null) {
            if (_plugin.getSellMap().get(player.getName()) != null) {
                ShopItem shopItem = _plugin.getSellMap().get(player.getName());
                try {
                    Integer count = Integer.valueOf(event.getMessage());
                    int inInventory = this.getAmountInInventory(player, shopItem);
                    if (count > 0) {
                        if (inInventory >= count) {
                            double finalPrice = (shopItem.getSellPrice() * count);
                            eco.depositPlayer(player.getName(), finalPrice);
                            int a = 0;
                            for (ItemStack item : player.getInventory().getContents()) {
                                if (a < count) {
                                    if (item != null) {
                                        if (item.getType().equals(shopItem.getItem())) ;
                                        if (item.getAmount() == count) {
                                            player.getInventory().remove(item);
                                            _plugin.getSellMap().remove(player.getName());
                                            _plugin.getChatManager().sendMessage(player, "&bTransaction was successful!\n&bNew balance: &e" + eco.getBalance(player.getName()));
                                            player.playSound(player.getLocation(), Sound.valueOf(_plugin.getConfigManager().getGeneralSettingsMap().get("SellSound")), 1, 0);
                                            event.setCancelled(true);
                                            return;
                                        } else if (item.getAmount() > count) {
                                            item.setAmount(item.getAmount() - count);
                                            _plugin.getSellMap().remove(player.getName());
                                            _plugin.getChatManager().sendMessage(player, "&bTransaction was successful!\n&bNew balance: &e" + eco.getBalance(player.getName()));
                                            player.playSound(player.getLocation(), Sound.valueOf(_plugin.getConfigManager().getGeneralSettingsMap().get("SellSound")), 1, 0);
                                            event.setCancelled(true);
                                            return;
                                        } else {
                                            count = count - item.getAmount();
                                            player.getInventory().remove(item);
                                            a++;
                                        }
                                    }
                                }
                            }
                            _plugin.getSellMap().remove(player.getName());
                            _plugin.getChatManager().sendMessage(player, "&bTransaction was successful!\n&bNew balance: &e" + eco.getBalance(player.getName()));
                            player.playSound(player.getLocation(), Sound.valueOf(_plugin.getConfigManager().getGeneralSettingsMap().get("SellSound")), 1, 0);
                            event.setCancelled(true);
                            return;
                        } else {
                            _plugin.getChatManager().sendMessage(player, "&cYou don't have that many of the Item.\n&cTransaction failed.");
                            player.playSound(player.getLocation(), Sound.valueOf(_plugin.getConfigManager().getGeneralSettingsMap().get("DenySound")), 1, 0);
                            _plugin.getSellMap().remove(player.getName());
                            player.openInventory(_plugin.getInventoryManager().getMainInventory());
                        }

                    } else {
                        _plugin.getChatManager().sendMessage(player, "&cEnter a positive number!");
                        player.playSound(player.getLocation(), Sound.valueOf(_plugin.getConfigManager().getGeneralSettingsMap().get("DenySound")), 1, 0);

                        player.openInventory(_plugin.getInventoryManager().getMainInventory());
                        _plugin.getSellMap().remove(player.getName());
                        event.setCancelled(true);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    _plugin.getChatManager().sendMessage(player, "&cEnter a number only!");
                    player.playSound(player.getLocation(), Sound.valueOf(_plugin.getConfigManager().getGeneralSettingsMap().get("DenySound")), 1, 0);

                    player.openInventory(_plugin.getInventoryManager().getMainInventory());
                    _plugin.getSellMap().remove(player.getName());
                    event.setCancelled(true);
                    return;
                }
            }
        }

    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&5***************&6GUIShop&5***************\n&6This server is running GUIShop " + _plugin.getDescription().getVersion() + "\n&6Developed by &c@aaomidi\n&6Bukkit: &ahttp://stuff.com/\n&6Test Server: &aMCCN.co\n&5**************************************"));
            }
        };
        runnable.runTaskLater(_plugin, 60L);
    }

    private void buyItemMessages(Player player, ShopItem shopItem) {
        Economy eco = _plugin.getEconomy();
        Double playerBalance = eco.getBalance(player.getName());
        Double itemPrice = shopItem.getBuyPrice();
        Integer buyable = (int) (playerBalance / itemPrice);
        _plugin.getChatManager().sendMessage(player, "&bPlease enter the amount you want to buy:\n&bRemeber each is priced at: " + shopItem.getBuyPrice() + "$");
        _plugin.getChatManager().sendMessage(player, "&bYour balance is: &e" + playerBalance + "$ &byou may only buy &e" + buyable + "&b.");
        _plugin.getBuyMap().put(player.getName(), shopItem);
        _plugin.getSellMap().remove(player.getName());
    }

    private void sellItemMessages(Player player, ShopItem shopItem) {
        int amount = this.getAmountInInventory(player, shopItem);
        if (amount != 0) {
            _plugin.getChatManager().sendMessage(player, "&bPlease enter the amount you want to sell:\n&bRemeber each is priced at: " + shopItem.getSellPrice() + "$");
            _plugin.getBuyMap().remove(player.getName());
            _plugin.getSellMap().put(player.getName(), shopItem);
            _plugin.getChatManager().sendMessage(player, "&bYou may only sell &e" + amount + " &bof that Item.");

        } else {
            _plugin.getBuyMap().remove(player.getName());
            _plugin.getChatManager().sendMessage(player, "&cYou don't have any of that item.");
            return;

        }

    }

    private int getAmountInInventory(Player player, ShopItem shopItem) {
        int x = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                if (item.getType().equals(shopItem.getItem().getType())) {
                    x += item.getAmount();
                }
            }
        }
        return x;
    }
}
