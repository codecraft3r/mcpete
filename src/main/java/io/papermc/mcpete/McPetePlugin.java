package io.papermc.mcpete;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class McPetePlugin extends JavaPlugin implements Listener {

    private static String opaenaiapikey;
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        opaenaiapikey = getConfig().getString("openaiapikey");
        getServer().getPluginManager().registerEvents(new ChatListener(opaenaiapikey), this);


    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(Component.text("Hello, " + event.getPlayer().getName() + "!"));
    }

}