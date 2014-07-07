package com.aaomidi.dev.guishop.engine;

import com.aaomidi.dev.guishop.GUIShop;
import com.aaomidi.dev.guishop.engine.objects.GUICategory;
import com.aaomidi.dev.guishop.engine.objects.GUIStock;
import com.aaomidi.dev.guishop.utils.ConfigReader;
import com.aaomidi.dev.guishop.utils.StringManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public class InventoryManager implements InventoryHolder {
    private final GUIShop _plugin;
    @Getter
    @Setter
    private Inventory inventory;
    private List<GUICategory> guiCategories;

    public InventoryManager(GUIShop plugin) {
        _plugin = plugin;
        guiCategories = ConfigReader.getGuiCategories();
        this.createCategoryInventory();
    }

    public static Inventory createStockInventory(GUICategory guiCategory) {
        String inventoryName = guiCategory.getName();
        int inventorySize = guiCategory.getSize() * 9;
        InventoryHolder holder = new InventoryHolder() {
            @Override
            public Inventory getInventory() {
                return null;
            }
        };
        Inventory stockInventory = Bukkit.createInventory(holder, inventorySize, StringManager.colorize(inventoryName));
        for (GUIStock guiStock : guiCategory.getStock()) {
            stockInventory.addItem(guiStock.toItemStack());
        }
        return stockInventory;
    }

    public void createCategoryInventory() {
        String inventoryName = _plugin.getConfigReader().getInventoryName();
        int inventorySize = _plugin.getConfigReader().getInventorySize();
        Inventory stockInventory = _plugin.getServer().createInventory(this, inventorySize, StringManager.colorize(inventoryName));
        for (GUICategory guiCategory : guiCategories) {
            stockInventory.addItem(guiCategory.toItemStack());
        }
        inventory = stockInventory;
    }

}
