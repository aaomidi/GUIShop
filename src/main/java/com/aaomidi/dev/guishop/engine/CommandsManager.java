package com.aaomidi.dev.guishop.engine;


import com.aaomidi.dev.guishop.GUIShop;
import com.aaomidi.dev.guishop.utils.StringManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsManager implements CommandExecutor {
    private final GUIShop _plugin;

    public CommandsManager(GUIShop plugin) {
        _plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String commandLabel, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (cmd.getName().equalsIgnoreCase("shop")) {
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        if (commandSender.hasPermission("guishop.reload")) {
                            _plugin.reloadPlugin();
                            StringManager.sendMessage(player, "Reloaded plugin!");
                            return true;
                        }
                    }
                }
                player.closeInventory();
                player.openInventory(_plugin.getInventoryManager().getInventory());
            }
        }
        return true;
    }
}
