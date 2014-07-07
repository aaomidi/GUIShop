package com.aaomidi.dev.guishop.utils;

import com.aaomidi.dev.guishop.GUIShop;
import com.aaomidi.dev.guishop.engine.objects.GUIStock;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Amir on 6/29/2014.
 */
public class StringManager {
    private final GUIShop _plugin;
    private String prefix;

    public StringManager(GUIShop plugin) {
        _plugin = plugin;
        prefix = _plugin.getConfigReader().getPrefix();
    }

    public static String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static StringManager getInstance() {
        return JavaPlugin.getPlugin(GUIShop.class).getStringManager();
    }

    public static List<String> colorize(List<String> strings, GUIStock guiStock) {
        List<String> newList = new ArrayList<>();
        for (String s : strings) {
            if (guiStock != null) {
                s = s.replace("%bprice%", String.valueOf(guiStock.getBuyPrice()));
                s = s.replace("%sprice%", String.valueOf(guiStock.getSellPrice()));
            }
            newList.add(colorize(s));
        }
        return newList;
    }

    public static void sendMessage(CommandSender player, String message) {
        message = getInstance().prefix + message;
        player.sendMessage(colorize(message));
    }
}
