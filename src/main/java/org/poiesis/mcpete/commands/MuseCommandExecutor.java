package org.poiesis.mcpete.commands;

import com.theokanning.openai.completion.chat.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.poiesis.mcpete.helpers.OpenAiHelper;
import org.poiesis.mcpete.PluginMain;

import java.util.List;

public class MuseCommandExecutor implements CommandExecutor {
    private final PluginMain plugin;

    public MuseCommandExecutor(PluginMain plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        //check if the sender is a player
        if (sender instanceof Player player) {
            //check if the command is /muse
            if (command.getName().equalsIgnoreCase("muse")) {
                // ensure that we've got at least one argument
                if(args.length == 0) {
                    return false;
                }
                else {
                    // construct the message from the arguments as each argument is separated by a space
                    StringBuilder messageBuilder = new StringBuilder();
                    for (String arg : args) {
                        messageBuilder.append(arg).append(" ");
                    }
                    String message = messageBuilder.toString();
                    // make sure the message isn't empty
                    if (message.length() == 0) {
                        return false;
                    }

                    // pipe the message through the OpenAI API and send the response to the player
                    player.sendMessage("Generating response, please wait...");
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        String response = processInput(message, player.getName());
                        player.sendMessage(response);
                    });
                    return true;
                }
            }
        }
        return false;
    }

    private String processInput(String input, String playerName) {
        //construct the prompt from a system message and a user message
        List<ChatMessage> messages = List.of(new ChatMessage("system", "You are a helpful minecraft assistant"), new ChatMessage("user", playerName + ": " + input));
        //create a new OpenAiClient with your API key
        OpenAiHelper openAiHelper = new OpenAiHelper(PluginMain.OPENAIAPIKEY);
        //get the completion from the OpenAiClient
        ChatMessage completion = openAiHelper.getChatCompletion("gpt-3.5-turbo", messages);
        //return completion.content
        return completion.getContent();
    }
}

