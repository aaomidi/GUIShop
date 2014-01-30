package co.mccn.guishop.managers;

import co.mccn.guishop.GUIShop;
import co.mccn.guishop.objects.CategoryItem;
import co.mccn.guishop.objects.ShopItem;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryManager {
    private final GUIShop _plugin;
    private Inventory mainInventory;

    public InventoryManager(GUIShop plugin) {
        _plugin = plugin;
        this.createMainInventor();
    }

    private void createMainInventor() {
        //Name of the Main Inventory;
        String name = _plugin.getConfigManager().getGeneralSettingsMap().get("Name");
        String colorizedName = _plugin.getUtilsManager().colorizeString(name);
        //Size of the Inventory;
        int inventorySize = Integer.valueOf(_plugin.getConfigManager().getGeneralSettingsMap().get("InventorySize"));
        //Initialize Inventory;
        mainInventory = _plugin.getServer().createInventory(null, inventorySize, colorizedName);
        //Retrieve the HashMaps for the plugin;
        HashMap<Integer, CategoryItem> categoryInformationMap = _plugin.getConfigManager().getCategoryInformationMap();
        HashMap<String, String> languageInformation = _plugin.getConfigManager().getLanguageInformationMap();
        //Add items to Inventory;
        for (Integer i : categoryInformationMap.keySet()) {
            CategoryItem categoryItem = categoryInformationMap.get(i);
            ItemStack item = categoryItem.getItem();
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(_plugin.getUtilsManager().colorizeString(languageInformation.get("CategoryName").replace("%cat%", categoryItem.getName())));
            List<String> lore = new ArrayList<>();
            lore.add(_plugin.getUtilsManager().colorizeString(categoryItem.getDescription()));
            item.setItemMeta(itemMeta);
            mainInventory.addItem(item);
        }
    }

    public Inventory createInventory(Integer categoryNumber) {
        CategoryItem categoryItem = _plugin.getConfigManager().getCategoryInformationMap().get(categoryNumber);
        HashMap<Integer, ShopItem> shopItemHashMap = _plugin.getConfigManager().getCategoryItemInformationMap().get(categoryNumber);
        HashMap<String, String> languageInformation = _plugin.getConfigManager().getLanguageInformationMap();
        Inventory inventory = _plugin.getServer().createInventory(null, categoryItem.getSize(), _plugin.getUtilsManager().colorizeString(languageInformation.get("CategoryName").replace("%cat%", categoryItem.getName())));
        for (Integer i : shopItemHashMap.keySet()) {
            ShopItem shopItem = shopItemHashMap.get(i);
            ItemStack item = shopItem.getItem();
            String itemName = null;
            if (item.getType().equals(Material.MOB_SPAWNER)) {
                String spawnerType = this.getSpawnerName(item.getDurability());
                itemName = _plugin.getUtilsManager().colorizeString("&r&f" + spawnerType + " Spawner");
            }
            item.setAmount(1);
            ItemMeta itemMeta = item.getItemMeta();
            if (itemName != null) {
                itemMeta.setDisplayName(itemName + " Spawner");
            }
            List<String> lore = new ArrayList<>();
            lore.add(_plugin.getUtilsManager().colorizeString(languageInformation.get("BuyPrice").replace("%price%", shopItem.getBuyPrice() + "")));
            lore.add(_plugin.getUtilsManager().colorizeString(languageInformation.get("SellPrice").replace("%price%", shopItem.getSellPrice() + "")));
            lore.add(" ");
            lore.add(_plugin.getUtilsManager().colorizeString(languageInformation.get("Buy")));
            lore.add(_plugin.getUtilsManager().colorizeString(languageInformation.get("Sell")));
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            inventory.addItem(item);
        }
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(_plugin.getUtilsManager().colorizeString(languageInformation.get("Back")));
        itemMeta.setLore(null);
        item.setItemMeta(itemMeta);
        inventory.setItem(inventory.getSize() - 1, item);
        return inventory;
    }

    private String getSpawnerName(short dur) {
        switch (dur) {
            case 61:
                return "Blaze";
            case 59:
                return "Cave Spider";
            case 50:
                return "Creeper";
            case 63:
                return "EnderDragon";
            case 58:
                return "Enderman";
            case 56:
                return "Ghast";
            case 62:
                return "Magma Cube";
            case 60:
                return "SilverFish";
            case 51:
                return "Skeleton";
            case 55:
                return "Slime";
            case 52:
                return "Spider";
            case 66:
                return "Witch";
            case 54:
                return "Zombie";
            case 57:
                return "Zombie Pigman";
            case 65:
                return "Bat";
            case 93:
                return "Chicken";
            case 92:
                return "Cow";
            case 100:
                return "Horse";
            case 96:
                return "Mooshroom";
            case 98:
                return "Ocelot";
            case 90:
                return "Pig";
            case 91:
                return "Sheep";
            case 94:
                return "Squid";
            case 95:
                return "Wolf";
            case 120:
                return "Villager";
            case 99:
                return "Iron Golem";
            case 97:
                return "Snowman";
            default:
                return "Unkown";

        }
    }

    public Inventory getMainInventory() {
        return mainInventory;
    }
}