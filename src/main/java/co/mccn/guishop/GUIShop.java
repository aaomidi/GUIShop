package co.mccn.guishop;

import co.mccn.guishop.managers.*;
import co.mccn.guishop.objects.ShopItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

public class GUIShop extends JavaPlugin {
    private static Economy economy = null;
    private ConfigManager configManager;
    private UtilsManager utilsManager;
    private InventoryManager inventoryManager;
    private CommandsManager commandsManager;
    private ChatManager chatManager;
    private EventsManager eventsManager;
    private HashMap<String, ShopItem> buyMap;
    private HashMap<String, ShopItem> sellMap;

    public final void onLoad() {
        if (!new File(this.getDataFolder(), "config.yml").exists()) {
            this.saveDefaultConfig();
        }
    }

    public final void onEnable() {
        initPlugin();
    }

    public final void onDisable() {

    }

    private void initPlugin() {
        buyMap = new HashMap<>();
        sellMap = new HashMap<>();
        this.setupVault();
        utilsManager = new UtilsManager(this);
        configManager = new ConfigManager(this);
        inventoryManager = new InventoryManager(this);
        commandsManager = new CommandsManager(this);
        this.getCommand("shop").setExecutor(commandsManager);
        chatManager = new ChatManager(this);
        eventsManager = new EventsManager(this);
        this.getServer().getPluginManager().registerEvents(eventsManager, this);

    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public UtilsManager getUtilsManager() {
        return utilsManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    /**
     * Setup Vault;
     */
    private void setupVault() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Vault");
        if (plugin == null) {
            this.getLogger().log(Level.SEVERE, "Vault was not found, disabling plugin");
            this.getServer().getPluginManager().disablePlugin(this);
        } else {
            setupEconomy();
        }

    }

    /**
     * Setup Economy;
     *
     * @return
     */
    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            return (economy != null);
        } else {
            this.getLogger().log(Level.SEVERE, "Economy setup failed, do you have the latest version of Vault?");
            this.getServer().getPluginManager().disablePlugin(this);
            return (economy == null);
        }
    }

    public HashMap<String, ShopItem> getBuyMap() {
        return buyMap;
    }

    public HashMap<String, ShopItem> getSellMap() {
        return sellMap;
    }

    public Economy getEconomy() {
        return this.economy;
    }
}
