package org.poiesis.mcpete;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.poiesis.mcpete.commands.GenCommandExecutor;
import org.poiesis.mcpete.listeners.AssistantAgentListener;
import org.poiesis.mcpete.listeners.ConversationalAgentListener;

public class PluginMain extends JavaPlugin implements Listener {

    public static String OAI_API_KEY;
    public static String ASSISTANT_ID;
    @Override
    public void onEnable() {
        OAI_API_KEY = getConfig().getString("oai_api_key");
        ASSISTANT_ID = getConfig().getString("assistant_id");

        // check if the API key is null
        if (OAI_API_KEY == null) {
            getLogger().warning("OpenAI API key is null. Please set it in the config.yml file.");
            saveDefaultConfig();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        /* LISTENERS */
        Bukkit.getPluginManager().registerEvents(this, this);

        //Bukkit.getPluginManager().registerEvents(new ConversationalAgentListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AssistantAgentListener(ASSISTANT_ID, OAI_API_KEY, this), this);

        /* COMMANDS */

        // register the /genCommand command
        getCommand("genCommand").setExecutor(new GenCommandExecutor(this));

    }

}