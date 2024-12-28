package io.github.mcengine.api.backpack.command;

import io.github.mcengine.api.backpack.listener.MCEngineBackPackApiUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MCEngineBackPackApiCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("givebackpack")) {
            if (args.length < 2) {
                player.sendMessage("§eUsage: /givebackpack <head id || texture> <size>");
                return true;
            }

            String texture = args[0];
            int rows;
            try {
                rows = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid size. Please provide a valid number.");
                return true;
            }

            if (rows < 1 || rows > 6) {
                player.sendMessage("§cSize must be between 1 and 6 rows.");
                return true;
            }

            int size = rows * 9;
            ItemStack backpack = MCEngineBackPackApiUtil.getBackpack(texture, size);
            if (backpack == null) {
                player.sendMessage("§cFailed to create the backpack. Please check the texture ID.");
                return true;
            }

            player.getInventory().addItem(backpack);
            player.sendMessage("§aBackpack given!");
            return true;
        }

        return false;
    }
}
