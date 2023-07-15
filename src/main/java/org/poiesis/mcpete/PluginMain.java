package org.poiesis.mcpete;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.poiesis.mcpete.commands.GenCommandExecutor;
import org.poiesis.mcpete.listeners.ConversationalAgentListener;

public class PluginMain extends JavaPlugin implements Listener {

    public static String OPENAIAPIKEY;
    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new ConversationalAgentListener(this), this);
        // save the default config, if it doesn't exist already
        saveDefaultConfig();
        // get the API key from the config, and make it available to the rest of the plugin
        OPENAIAPIKEY = getConfig().getString("openaiapikey");

        /* COMMANDS */

        // register the /genCommand command
        getCommand("genCommand").setExecutor(new GenCommandExecutor(this));

    }

}