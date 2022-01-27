package me.blurmit.skin.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class messagesUtil
{

    public void sendColoredMessage(CommandSender sender, String message)
    {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
