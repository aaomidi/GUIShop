package co.mccn.guishop.managers;

import co.mccn.guishop.GUIShop;
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
        if (cmd.getName().equalsIgnoreCase("shop")) {
            if (args.length == 0) {
                if (commandSender instanceof Player) {
                    Player player = (Player) commandSender;
                    player.openInventory(_plugin.getInventoryManager().getMainInventory());
                    return true;
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {

                    if (commandSender instanceof Player) {
                        Player player = (Player) commandSender;
                        if (player.isOp()) {
                            String message=_plugin.getConfigManager().reloadConfig();
                            _plugin.getChatManager().sendMessage(commandSender, message);
                            return true;
                        } else {
                            _plugin.getChatManager().sendMessage(commandSender, "&cNo permissions");
                            return true;
                        }
                    } else {
                        _plugin.getConfigManager().reloadConfig();
                        _plugin.getChatManager().sendMessage(commandSender, "&aReloaded Config.");
                        return true;
                    }
                }
            }

        }
        return false;

    }
}