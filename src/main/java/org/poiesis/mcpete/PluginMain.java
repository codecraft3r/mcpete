package org.poiesis.mcpete;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginMain extends JavaPlugin implements Listener {

    public static String OPENAIAPIKEY;
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        OPENAIAPIKEY = getConfig().getString("openaiapikey");


        getCommand("muse").setExecutor(new MuseCommand());

    }




}