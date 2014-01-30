package co.mccn.guishop.managers;

import co.mccn.guishop.GUIShop;
import co.mccn.guishop.objects.CategoryItem;
import co.mccn.guishop.objects.ShopItem;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Set;

public class ConfigManager {
    private final GUIShop _plugin;
    private HashMap<String, String> generalSettingsMap;
    private HashMap<Integer, CategoryItem> categoryInformationMap;
    private HashMap<Integer, HashMap<Integer, ShopItem>> categoryItemInformationMap;
    private HashMap<String, String> languageInformationMap;
    private FileConfiguration config;

    public ConfigManager(GUIShop plugin) {
        _plugin = plugin;
        config = _plugin.getConfig();
        generalSettingsMap = new HashMap<>();
        categoryInformationMap = new HashMap<>();
        categoryItemInformationMap = new HashMap<>();
        languageInformationMap = new HashMap<>();
        this.getGeneralSettings();
        this.getShopCategories();
        this.getLanguageInformation();
    }

    private void getGeneralSettings() {
        generalSettingsMap.put("Name", config.getString("Main.Name"));
        generalSettingsMap.put("InventorySize", String.valueOf(config.getInt("Main.Size") * 9));
        generalSettingsMap.put("BuySound", config.getString("Main.BuySound"));
        generalSettingsMap.put("SellSound", config.getString("Main.SellSound"));
        generalSettingsMap.put("DenySound", config.getString("Main.DenySound"));
    }

    private void getShopCategories() {
        Set<String> categoryNames = config.getConfigurationSection("List").getKeys(false);
        int x = 0;
        for (String categoryName : categoryNames) {
            x = x + 1;
            //Initialize the indexes.
            int size = config.getInt("List." + categoryName + ".Size") * 9;
            ItemStack item1 = new ItemStack(Material.matchMaterial(config.getString("List." + categoryName + ".Icon")), 1);
            String description = config.getString("List." + categoryName + ".Description");
            //Create custom object.
            CategoryItem categoryItem = new CategoryItem(categoryName, item1, size, description, x);
            //Add custom object to HashMap
            categoryInformationMap.put(x, categoryItem);
            //Create custom HashMap for ItemInformation map.
            HashMap<Integer, ShopItem> itemMap = new HashMap<>();
            //Add Items to HashMap
            int y = 0;
            for (String itemName : config.getConfigurationSection("List." + categoryName + ".Stock").getKeys(false)) {
                y++;
                String[] itemInfo = itemName.split(":");
                ItemStack item;
                if (itemInfo.length == 2) {
                    item = new ItemStack(Material.matchMaterial(itemInfo[0]), 1);
                    item.setDurability(Short.valueOf(itemInfo[1]));
                } else {
                    item = new ItemStack(Material.matchMaterial(itemInfo[0]), 1);
                }
                Double buyPrice = config.getDouble("List." + categoryName + ".Stock." + itemName + ".BuyPrice");
                Double sellPrice = config.getDouble("List." + categoryName + ".Stock." + itemName + ".SellPrice");
                ShopItem shopItem = new ShopItem(item, buyPrice, sellPrice, y);
                itemMap.put(y, shopItem);
            }
            categoryItemInformationMap.put(x, itemMap);
        }
    }

    private void getLanguageInformation() {
        String categoryDisplay = config.getString("Language.CategoryName");
        String buyString = config.getString("Language.Buy");
        String sellString = config.getString("Language.Sell");
        String buyPriceString = config.getString("Language.BuyPrice");
        String sellPriceString = config.getString("Language.SellPrice");
        String backString = config.getString("Language.Back");
        this.languageInformationMap.put("CategoryName", categoryDisplay);
        this.languageInformationMap.put("Buy", buyString);
        this.languageInformationMap.put("Sell", sellString);
        this.languageInformationMap.put("BuyPrice", buyPriceString);
        this.languageInformationMap.put("SellPrice", sellPriceString);
        this.languageInformationMap.put("Back", backString);
    }

    public HashMap<String, String> getGeneralSettingsMap() {
        return generalSettingsMap;
    }

    public HashMap<Integer, CategoryItem> getCategoryInformationMap() {
        return categoryInformationMap;
    }

    public HashMap<Integer, HashMap<Integer, ShopItem>> getCategoryItemInformationMap() {
        return categoryItemInformationMap;
    }

    public HashMap<String, String> getLanguageInformationMap() {
        return languageInformationMap;
    }

}
