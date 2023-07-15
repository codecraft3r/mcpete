package org.poiesis.mcpete.listeners;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.poiesis.mcpete.PluginMain;
import org.poiesis.mcpete.helpers.OpenAiHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ConversationalAgentListener implements Listener {
    List<ChatMessage> messages = new ArrayList<>(List.of(
            new ChatMessage("system", "Your name is Pete. Respond with a json object that looks like this: {\"message\": \"<your message here>\", \"command\": \"<command series here, separated with ^ characters>\"} (ex. {\"message\": \"Heyo\", \"command\": \"time set day^weather clear\"}) Only return this json object. Do not modify the json structure (i.e. do not add or remove fields). The command field only accepts Minecraft 1.20.1 console commands."),
            new ChatMessage("user", "Your name is Pete. Respond with a json object that looks like this: {\"message\": \"<your message here>\", \"command\": \"<command series here, separated with ^ characters>\"} (ex. {\"message\": \"Heyo\", \"command\": \"time set day^weather clear\"}) Only return this json object. Do not modify the json structure (i.e. do not add or remove fields). The command field only accepts Minecraft 1.20.1 console commands."),
            new ChatMessage( "user", "These are the commands you're allowed to use\n" +
                    "give <player name> <item id and NBT data> <amount: integer between 1-64>\n" +
                    "weather <clear | rain | thunder>\n" +
                    "gamemode <survival | creative | spectator> <player name>\n" +
                    "summon <entity> [pos] [nbt]\n" +
                    "setblock <pos> <block> [destroy¦keep¦replace]")
    ));
    private boolean isGenerating = false;

    private final PluginMain plugin;

    public ConversationalAgentListener(PluginMain plugin) {
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        if (!event.getMessage().toLowerCase().contains("pete")) {
            messages.add(new ChatMessage("user", event.getPlayer().getName() + ": " + event.getMessage()));
            return;
        }
        if (isGenerating) {
            event.getPlayer().sendMessage("Please wait for the previous message to finish generating.");
            return;
        }
        isGenerating = true;
        // add the message to the list of messages
        messages.add(new ChatMessage("user", "Current Position: " + event.getPlayer().getLocation() +" Name: " + event.getPlayer().getName() + " Message: " + event.getMessage()));
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> generate());

        future.thenAccept(response -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    // handle message
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(response, JsonObject.class);
                    String message = jsonObject.get("message").getAsString();
                    Bukkit.getServer().broadcastMessage(message);

                    //handle command
                    String commandString = jsonObject.get("command").getAsString();
                    String[] commands = commandString.split("^");
                    for (String command : commands) {
                        command = command.replace("^", "");
                        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.trim());
                    }
                } catch (JsonSyntaxException e) {
                    plugin.getLogger().log(Level.SEVERE, "Error parsing JSON response from OpenAI API", e);
                }

                isGenerating = false;
            });
        });
    }

    private String generate() {

        ArrayList<String> playerInfo = new ArrayList<>();
        Bukkit.getServer().getOnlinePlayers().forEach(
            player -> {
                String name = player.getName();
                Location location = player.getLocation();
                playerInfo.add("Player Name=\"" + name + "\" Player Location: x=" + location.getX() + " y=" + location.getY() + " z=" + location.getZ() + "\n");
            }
        );


        //construct the prompt from a system message and a user message

        Bukkit.getLogger().log(Level.INFO, "Chat Context: " + messages.toString());

        // make the call to the OpenAI API

        //create a new OpenAiClient with your API key
        OpenAiHelper openAiHelper = new OpenAiHelper(PluginMain.OPENAIAPIKEY);
        //get the completion from the OpenAiClient
        ChatMessage completion = openAiHelper.getChatCompletion("gpt-3.5-turbo", messages);
        if (completion == null) {
            return "Error generating response";
        }
        messages.add(completion);
        //return completion.content
        return completion.getContent();
    }

}
