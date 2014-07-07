package com.aaomidi.dev.guishop;


import com.aaomidi.dev.guishop.engine.Caching;
import com.aaomidi.dev.guishop.engine.CommandsManager;
import com.aaomidi.dev.guishop.engine.EventsManager;
import com.aaomidi.dev.guishop.engine.InventoryManager;
import com.aaomidi.dev.guishop.utils.ConfigReader;
import com.aaomidi.dev.guishop.utils.StringManager;
import com.earth2me.essentials.Essentials;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class GUIShop extends JavaPlugin {
    @Getter
    private static Essentials ess;
    @Getter
    private static Economy economy = null;
    @Getter
    private ConfigReader configReader;
    @Getter
    private StringManager stringManager;
    @Getter
    private CommandsManager commandsManager;
    @Getter
    private InventoryManager inventoryManager;
    @Getter
    private EventsManager eventsManager;
    @Getter
    private Caching caching;

    @Override
    public void onLoad() {
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.saveDefaultConfig();
        }
    }

    @Override
    public void onEnable() {
        this.setupEssentials();
        this.setupEconomy();
        caching = new Caching(this);
        configReader = new ConfigReader(this, this.getConfig());
        stringManager = new StringManager(this);
        inventoryManager = new InventoryManager(this);
        commandsManager = new CommandsManager(this);
        eventsManager = new EventsManager(this);
        this.getCommand("shop").setExecutor(commandsManager);
        this.getServer().getPluginManager().registerEvents(eventsManager, this);
    }

    @Override
    public void onDisable() {

    }

    private void setupEssentials() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Essentials");
        if (plugin == null) {
            this.getLogger().log(Level.SEVERE, "Could not find Essentials. Shutting down plugin.");
            this.getServer().getPluginManager().disablePlugin(this);
        } else {
            ess = (Essentials) plugin;
        }
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public void reloadPlugin() {
        this.reloadConfig();
        configReader = null;
        configReader = new ConfigReader(this, this.getConfig());
        inventoryManager.createCategoryInventory();
    }

}
