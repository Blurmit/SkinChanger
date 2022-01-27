package me.blurmit.skin.commands;

import me.blurmit.skin.SkinChanger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkinCommand implements CommandExecutor
{

    SkinChanger plugin = SkinChanger.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {

        if (sender.hasPermission("skinchanger.use"))
        {

            if (sender instanceof Player)
            {

                if (args.length == 0 || args.length > 2)
                {

                    Player player = (Player) sender;

                    try {

                        plugin.playerUtil.setSkin(player, player.getName());
                        plugin.messagesUtil.sendColoredMessage(sender, plugin.messages.getString("reset-skin"));

                    } catch (Exception e) {
                        plugin.messagesUtil.sendColoredMessage(sender, "&cFailed to reset your skin.");
                    }

                }
                if (args.length == 1)
                {

                    Player player = (Player) sender;

                    try {
                        plugin.playerUtil.setSkin(player, args[0]);
                        plugin.messagesUtil.sendColoredMessage(sender, plugin.messages.getString("skin-changed")
                                .replace("{player}", player.getName())
                                .replace("{skin}", args[0]));
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed to get the skin of: &f" + args[0]));
                    }


                }

            }
            else
            {
                plugin.messagesUtil.sendColoredMessage(sender, plugin.messages.getString("invalid-arguments")
                        .replace("{command}", label)
                        .replace("{args}", "<skin> <player>"));
            }

            if (args.length == 2)
            {

                Player player = plugin.getServer().getPlayer(args[1]);

                if (player != null)
                {

                    try {
                        plugin.playerUtil.setSkin(player, args[0]);
                        plugin.messagesUtil.sendColoredMessage(sender, plugin.messages.getString("skin-changed")
                                .replace("{player}", player.getName())
                                .replace("{skin}", args[0]));
                    } catch (Exception e) {
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cFailed to get the skin of: &f" + args[0]));
                    }

                }
                else
                {
                    plugin.messagesUtil.sendColoredMessage(sender, plugin.messages.getString("player-not-found"));
                }

            }

        }
        else
        {
            plugin.messagesUtil.sendColoredMessage(sender, plugin.messages.getString("no-permission"));
        }

        return false;
    }

}
