package io.github.mcengine.common.backpack.command;

import io.github.mcengine.api.MCEngineBackPackApi;
import io.github.mcengine.common.backpack.command.MCEngineBackPackCommonCommandUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Handles common backpack-related commands for the MCEngine plugin.
 * 
 * Supported commands:
 * - /backpack create <hdb id> <size>: Creates a new backpack item.
 * - /backpack get <name>: Retrieves a predefined backpack by name.
 */
public class MCEngineBackPackCommonCommand implements CommandExecutor {
    private final MCEngineBackPackApi backpackApi;

    /**
     * Constructs a new command executor for backpack commands.
     *
     * @param backpackApi the API used for backpack creation and handling
     */
    public MCEngineBackPackCommonCommand(MCEngineBackPackApi backpackApi) {
        this.backpackApi = backpackApi;
    }

    /**
     * Handles the execution of the /backpack command.
     *
     * @param sender the source of the command
     * @param command the command which was executed
     * @param label the alias of the command used
     * @param args the arguments passed with the command
     * @return true if the command was handled, false otherwise
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can execute this command.");
            return true;
        }

        Player player = (Player) sender;

        if (!command.getName().equalsIgnoreCase("backpack")) return false;
        if (args.length < 1) {
            player.sendMessage("§eUsage: /backpack <create|get> <hdb id|name> [size]");
            return true;
        }

        String action = args[0].toLowerCase();
        switch (action) {
            case "create":
                handleCreateBackpack(player, args);
                break;
            case "get":
                handleGetBackpack(player, args);
                break;
            default:
                player.sendMessage("§eUsage: /backpack <create|get> <hdb id|name> [size]");
        }
        return true;
    }

    /**
     * Handles the creation of a new backpack item.
     *
     * @param player the player who executed the command
     * @param args the command arguments
     */
    private void handleCreateBackpack(Player player, String[] args) {
        if (!player.hasPermission("mcengine.backpack.create")) {
            player.sendMessage("§cYou do not have permission to create backpacks.");
            return;
        }

        if (args.length < 3) {
            player.sendMessage("§eUsage: /backpack create <hdb id> <size>");
            return;
        }

        String texture = args[1];
        int size = MCEngineBackPackCommonCommandUtil.parseBackpackSize(args[2], player);
        if (size == -1) return;

        ItemStack backpack = backpackApi.getBackpack("§6Backpack", texture, size);
        if (backpack == null) {
            player.sendMessage("§cFailed to create backpack. Please check the head ID.");
            return;
        }

        player.getInventory().addItem(backpack);
        player.sendMessage("§aBackpack created and added to your inventory!");
    }

    /**
     * Handles retrieving a predefined backpack configuration by name.
     *
     * @param player the player who executed the command
     * @param args the command arguments
     */
    private void handleGetBackpack(Player player, String[] args) {
        if (!player.hasPermission("mcengine.backpack.get")) {
            player.sendMessage("§cYou do not have permission to get backpacks.");
            return;
        }

        if (args.length < 2) {
            player.sendMessage("§eUsage: /backpack get <name>");
            return;
        }

        String name = args[1];
        YamlConfiguration config = MCEngineBackPackCommonCommandUtil.loadBackpackConfig(name);

        if (config == null) {
            player.sendMessage("§cBackpack data for '" + name + "' does not exist.");
            return;
        }

        String backpackName = config.getString("name", "Backpack").replace("&", "§");
        String headId = config.getString("head_id");
        int rows = config.getInt("size");

        if (headId == null || !MCEngineBackPackCommonCommandUtil.isValidSize(rows * 9)) {
            player.sendMessage("§cInvalid backpack data for '" + name + "'.");
            return;
        }

        int size = rows * 9;
        ItemStack retrievedBackpack = backpackApi.getBackpack(backpackName, headId, size);

        if (retrievedBackpack == null) {
            player.sendMessage("§cFailed to retrieve backpack. Please check the data.");
            return;
        }

        player.getInventory().addItem(retrievedBackpack);
        player.sendMessage("§aBackpack '" + name + "' retrieved and added to your inventory!");
    }
}
