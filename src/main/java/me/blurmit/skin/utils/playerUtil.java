package me.blurmit.skin.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.blurmit.skin.SkinChanger;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.InputStreamReader;
import java.net.URL;

public class playerUtil
{

    SkinChanger plugin;

    public playerUtil(SkinChanger plugin)
    {
        this.plugin = plugin;
    }

    public void setSkin(Player player, String name) throws Exception {

        GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        connection.sendPacket(new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                ((CraftPlayer) player).getHandle()
        ));

        profile.getProperties().removeAll("textures");
        profile.getProperties().put("textures", getSkin(name));

        connection.sendPacket(new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                ((CraftPlayer) player).getHandle()
        ));

        refreshSkin(player);

    }

    public Property getSkin(String name)
    {
        try {
            URL url_0 = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
            InputStreamReader reader_0 = new InputStreamReader(url_0.openStream());
            String uuid = new JsonParser().parse(reader_0).getAsJsonObject().get("id").getAsString();

            URL url_1 = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            InputStreamReader reader_1 = new InputStreamReader(url_1.openStream());
            JsonObject textureProperty = new JsonParser().parse(reader_1).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
            String texture = textureProperty.get("value").getAsString();
            String signature = textureProperty.get("signature").getAsString();

            return new Property("textures", texture, signature);
        } catch (Exception e) {
            return new Property("textures", "null", "null");
        }
    }

    public void refreshSkin(Player player) {

        for (Player online : Bukkit.getServer().getOnlinePlayers())
        {

            online.hidePlayer(plugin, player);
            online.showPlayer(plugin, player);

        }

        Location loc = player.getLocation();
        loc.setYaw(player.getLocation().getYaw());
        loc.setPitch(player.getLocation().getPitch());

        long seed = player.getWorld().getSeed();

        EntityPlayer p = ((CraftPlayer) player).getHandle();
        World world = p.getWorld();
        int slot = player.getInventory().getHeldItemSlot();

        new BukkitRunnable() {
            @Override
            public void run() {
                p.playerConnection.sendPacket(new PacketPlayOutRespawn(p.getWorld().getDimensionManager(), world.getDimensionKey(), seed,
                        p.playerInteractManager.getGameMode(), p.playerInteractManager.getGameMode(), false, false, true));
                if (player.isOp())
                {
                    player.setOp(true);
                }
                if (player.isFlying())
                {
                    player.setAllowFlight(true);
                }
                player.getInventory().setContents(player.getInventory().getContents());
                player.getInventory().setArmorContents(player.getInventory().getArmorContents());
                player.setExp(player.getExp());
                player.setFoodLevel(player.getFoodLevel());
                player.getInventory().setHeldItemSlot(slot);
                player.teleport(loc);
            }
        }.runTaskLater(plugin, 5);

    }

}
