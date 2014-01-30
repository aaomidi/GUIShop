package co.mccn.guishop.managers;

import co.mccn.guishop.GUIShop;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created with IntelliJ IDEA.
 * User: Amir
 * Date: 1/29/14
 * Time: 5:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommandsManager implements CommandExecutor {
    private final GUIShop _plugin;

    public CommandsManager(GUIShop plugin) {
        _plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("shop")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                player.openInventory(_plugin.getInventoryManager().getMainInventory());
                return true;
            }
        }
        return false;
    }
}
