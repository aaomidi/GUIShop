package com.aaomidi.dev.guishop.utils;

import com.aaomidi.dev.guishop.GUIShop;
import com.aaomidi.dev.guishop.engine.objects.GUICategory;
import com.aaomidi.dev.guishop.engine.objects.GUIStock;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;


public class ConfigReader {
    private static ConfigurationSection soundSettings = null;
    @Getter
    @Setter
    private static List<GUICategory> guiCategories;
    private final GUIShop _plugin;
    private final Configuration configuration;
    private final ConfigurationSection inventorySettings;

    public ConfigReader(GUIShop plugin, Configuration configuration) {
        this._plugin = plugin;
        this.configuration = configuration;
        inventorySettings = configuration.getConfigurationSection("Inventory-Settings");
        soundSettings = configuration.getConfigurationSection("Sound-Settings");
        this.setupGUIShop();
    }

    public static Sound getBuySound() {
        return Sound.valueOf(soundSettings.getString("Buy-Sound"));
    }

    public static Sound getSellSound() {
        return Sound.valueOf(soundSettings.getString("Sell-Sound"));

    }

    public static Sound getDenySound() {
        return Sound.valueOf(soundSettings.getString("Deny-Sound"));

    }

    public String getPrefix() {
        return configuration.getString("General-Settings.Prefix");
    }

    public String getInventoryName() {
        return inventorySettings.getString("Name");
    }

    public int getInventorySize() {
        return inventorySettings.getInt("Size") * 9;
    }

    public void setupGUIShop() {
        final ConfigurationSection settings = configuration.getConfigurationSection("Category-Settings");

        List<GUICategory> guiCategories = new ArrayList<>();
        for (String category : settings.getKeys(false)) {
            ConfigurationSection catSettings = settings.getConfigurationSection(category);
            String name = catSettings.getString("Name");
            int size = catSettings.getInt("Size");
            String icon = catSettings.getString("Icon");
            List<String> lore = catSettings.getStringList("Lore");
            List<GUIStock> guiStocks = new ArrayList<>();
            for (String stock : catSettings.getConfigurationSection("Stock").getKeys(false)) {
                ConfigurationSection stockSettings = catSettings.getConfigurationSection("Stock." + stock);
                String stockName = "";
                List<String> stockLore;
                int buyPrice = -1;
                int sellPrice = -1;
                String stockCommand = "";
                stockName = stockSettings.getString("Name");
                stockLore = stockSettings.getStringList("Lore");
                buyPrice = stockSettings.getInt("Buy-Price");
                sellPrice = stockSettings.getInt("Sell-Price");
                stockCommand = stockSettings.getString("Command");
                GUIStock guiStock = new GUIStock(stock, stockName, stockLore, buyPrice, sellPrice, stockCommand);
                guiStocks.add(guiStock);
            }
            GUICategory guiCategory = new GUICategory(category, name, size, icon, lore, guiStocks);
            guiCategories.add(guiCategory);
            ConfigReader.setGuiCategories(guiCategories);

        }
    }
}
