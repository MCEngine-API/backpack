package io.github.mcengine.common.backpack.command;

import io.github.mcengine.api.MCEngineBackPackApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MCEngineBackPackCommonCommand implements CommandExecutor {
    private final MCEngineBackPackApi backpackApi;

    public MCEngineBackPackCommonCommand(MCEngineBackPackApi backpackApi) {
        this.backpackApi = backpackApi;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("backpack")) {
            if (args.length < 2) {
                player.sendMessage("§eUsage: /backpack <create || get> <hdb id> [size]");
                return true;
            }

            String action = args[0].toLowerCase();
            String texture = args[1];

            switch (action) {
                case "create":
                    if (args.length < 3) {
                        player.sendMessage("§eUsage: /backpack create <hdb id> <size>");
                        return true;
                    }

                    int size;
                    try {
                        size = Integer.parseInt(args[2]) * 9;
                    } catch (NumberFormatException e) {
                        player.sendMessage("§cInvalid size. Please provide a valid number.");
                        return true;
                    }

                    if (size < 9 || size > 54) {
                        player.sendMessage("§cSize must be between 1 and 6 rows (9-54 slots).");
                        return true;
                    }

                    ItemStack backpack = backpackApi.createBackpack(texture, size);
                    if (backpack == null) {
                        player.sendMessage("§cFailed to create backpack. Please check the head ID.");
                        return true;
                    }

                    player.getInventory().addItem(backpack);
                    player.sendMessage("§aBackpack created and added to your inventory!");
                    break;

                case "get":
                    ItemStack existingBackpack = backpackApi.getBackpack(texture);
                    if (existingBackpack == null) {
                        player.sendMessage("§cNo backpack found with the given head ID.");
                        return true;
                    }

                    player.getInventory().addItem(existingBackpack);
                    player.sendMessage("§aBackpack retrieved and added to your inventory!");
                    break;

                default:
                    player.sendMessage("§eUsage: /backpack <create || get> <hdb id> [size]");
                    break;
            }
            return true;
        }

        return false;
    }
}
