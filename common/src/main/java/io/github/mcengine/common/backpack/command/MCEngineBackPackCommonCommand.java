package io.github.mcengine.common.backpack.command;

import io.github.mcengine.common.backpack.command.MCEngineBackPackCommonCommandUtil;
import io.github.mcengine.api.MCEngineBackPackApi;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
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
                player.sendMessage("§eUsage: /backpack <create> <hdb id> [size]");
                return true;
            }

            String action = args[0].toLowerCase();
            String texture = args[1];

            switch (action) {
                case "create":
                    if (!player.hasPermission("mcengine.backpack.create")) {
                        player.sendMessage("§cYou do not have permission to create backpacks.");
                        return true;
                    }

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
                    if (!player.hasPermission("mcengine.backpack.get")) {
                        player.sendMessage("§cYou do not have permission to get backpacks.");
                        return true;
                    }
                
                    if (args.length < 2) {
                        player.sendMessage("§eUsage: /backpack get <name>");
                        return true;
                    }
                
                    String name = args[1];
                    YamlConfiguration config = MCEngineBackPackCommonCommandUtil.loadBackpackConfig(name);
                
                    if (config == null) {
                        player.sendMessage("§cBackpack data for '" + name + "' does not exist.");
                        return true;
                    }
                
                    String headId = config.getString(name + ".head_id");
                    int rows = config.getInt(name + ".size");
                
                    if (headId == null || rows <= 0 || rows > 6) {
                        player.sendMessage("§cInvalid backpack data for '" + name + "'.");
                        return true;
                    }
                
                    int sizeInSlots = rows * 9;
                    ItemStack retrievedBackpack = backpackApi.createBackpack(headId, sizeInSlots);
                
                    if (retrievedBackpack == null) {
                        player.sendMessage("§cFailed to retrieve backpack. Please check the data.");
                        return true;
                    }
                
                    player.getInventory().addItem(retrievedBackpack);
                    player.sendMessage("§aBackpack '" + name + "' retrieved and added to your inventory!");
                    break;

                default:
                    player.sendMessage("§eUsage: /backpack <create> <hdb id> [size]");
                    break;
            }
            return true;
        }

        return false;
    }
}
