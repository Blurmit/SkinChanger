package me.blurmit.skinchanger.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.blurmit.skinchanger.SkinChanger;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_16_R3.PlayerConnection;
import net.minecraft.server.v1_16_R3.PacketPlayOutRespawn;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SkinManager {

    private final SkinChanger plugin;
    private final Map<String, Property> propertyCache;

    public SkinManager(SkinChanger plugin) {
        this.plugin = plugin;
        this.propertyCache = new HashMap<>();
    }

    public void setSkin(Player player, String skin) {

        GameProfile profile = ((CraftPlayer) player).getHandle().getProfile();
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle()));
        profile.getProperties().removeAll("textures");

        if (!propertyCache.containsKey(skin)) {
            cacheSkinProperty(skin);
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            profile.getProperties().put("textures", propertyCache.get(skin));
            connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ((CraftPlayer) player).getHandle()));
            refreshPlayerSkin(player);
        }, 5L);
    }

    public void cacheSkinProperty(String skin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // UUID Getter
                URL apiServer = new URL("https://api.mojang.com/users/profiles/minecraft/" + skin);
                InputStreamReader uuidReader = new InputStreamReader(apiServer.openStream());
                String uuid = new JsonParser().parse(uuidReader).getAsJsonObject().get("id").getAsString();

                // Texture Property Getter
                URL sessionServer = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
                InputStreamReader textureReader = new InputStreamReader(sessionServer.openStream());
                JsonObject textureProperty = new JsonParser().parse(textureReader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

                String texture = textureProperty.get("value").getAsString();
                String signature = textureProperty.get("signature").getAsString();

                propertyCache.put(skin, new Property("textures", texture, signature));
            } catch (IOException | IllegalStateException e) {
                propertyCache.put(skin, new Property("textures", "null", "null"));
            }
        });
    }

    private void refreshPlayerSkin(Player player) {

        Bukkit.getOnlinePlayers().forEach(onlinePlayer -> {
            onlinePlayer.hidePlayer(plugin, player);
            onlinePlayer.showPlayer(plugin, player);
        });

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        entityPlayer.playerConnection.sendPacket(new PacketPlayOutRespawn(entityPlayer.getWorld().getDimensionManager(),
                entityPlayer.getWorld().getDimensionKey(),
                player.getWorld().getSeed(),
                entityPlayer.playerInteractManager.getGameMode(),
                entityPlayer.playerInteractManager.getGameMode(),
                false,
                false,
                true));

        player.getInventory().setItemInMainHand(player.getInventory().getItemInMainHand());
        player.getInventory().setArmorContents(player.getInventory().getArmorContents());
        player.getInventory().setContents(player.getInventory().getContents());
        player.setOp(player.isOp());
        player.setHealth(player.getHealth());
        player.setFoodLevel(player.getFoodLevel());
        player.setAllowFlight(player.isFlying());
        player.setExp(player.getExp());
        player.setLevel(player.getLevel());
        player.teleport(player.getLocation());

    }


}
