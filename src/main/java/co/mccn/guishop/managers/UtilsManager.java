package co.mccn.guishop.managers;

import co.mccn.guishop.GUIShop;
import org.bukkit.ChatColor;

public class UtilsManager {
    private final GUIShop _plugin;

    public UtilsManager(GUIShop plugin) {
        _plugin = plugin;
    }

    /**
     * Colorize String using Bukkit's ChatColor method;
     *
     * @param string
     * @return
     */
    public String colorizeString(String string) {
        String color = ChatColor.translateAlternateColorCodes('&', string);
        return color;
    }
}
