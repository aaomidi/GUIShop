package co.mccn.guishop.managers;

import co.mccn.guishop.GUIShop;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class ChatManager {
    private final GUIShop _plugin;
    private String prefix;

    public ChatManager(GUIShop plugin) {
        _plugin = plugin;
        prefix = _plugin.getConfigManager().getGeneralSettingsMap().get("Name") + " ";
    }

    public void sendMessage(Player player, String message) {
        String send = _plugin.getUtilsManager().colorizeString(prefix + message);
        player.sendMessage(send);
    }

    public void sendMessage(CommandSender commandSender, String message) {
        String send = _plugin.getUtilsManager().colorizeString(prefix + message);
        commandSender.sendMessage(send);
    }

}
