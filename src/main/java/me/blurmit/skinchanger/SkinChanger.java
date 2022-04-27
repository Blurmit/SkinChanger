package me.blurmit.skinchanger;

import me.blurmit.skinchanger.commands.SkinCommand;
import me.blurmit.skinchanger.utils.SkinManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class SkinChanger extends JavaPlugin {

    private SkinManager skinManager;

    @Override
    public void onEnable() {
        Bukkit.getLogger().log(Level.INFO, "The plugin has been successfully enabled!");

        getCommand("skin").setExecutor(new SkinCommand(this));
        this.skinManager = new SkinManager(this);
    }

    public SkinManager getSkinManager() {
        return skinManager;
    }
    
}
