package me.blurmit.skinchanger.commands;

import me.blurmit.skinchanger.SkinChanger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkinCommand implements CommandExecutor {

    private final SkinChanger plugin;

    public SkinCommand(SkinChanger plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Console cannot use the skin command!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("skinchanger.changeskin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if (args.length > 1) {
            player.sendMessage(ChatColor.RED + "Correct usage: /skin <skin>");
            return true;
        }

        if (args.length == 1) {
            plugin.getSkinManager().setSkin(player, args[0]);
            player.sendMessage(ChatColor.GREEN + "Successfully changed your skin to " + args[0] + "!");
            return true;
        }

        plugin.getSkinManager().setSkin(player, player.getName());
        player.sendMessage(ChatColor.GREEN + "Reset your current skin.");

        return true;
    }

}
