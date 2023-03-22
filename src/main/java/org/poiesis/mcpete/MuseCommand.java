package org.poiesis.mcpete;

import com.theokanning.openai.completion.chat.ChatMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MuseCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (command.getName().equalsIgnoreCase("muse")) {
                if(args.length == 0) {
                    return false;
                }
                if (args.length >= 1) {
                    String message = "";
                    for (int i = 0; i < args.length; i++) {
                        message += args[i] + " ";
                    }

                    if (message.length() == 0) {
                        return false;
                    }

                    String output = processInput(message, sender.getName());
                    sender.sendMessage(output);
                    return true;
                }
            }
        }
        return false;
    }

    private String processInput(String input, String playerName) {

        List<ChatMessage> messages = List.of(new ChatMessage("system", "You are a helpful minecraft assistant"), new ChatMessage("user", playerName + ": " + input));
        OpenAiClient openAiClient = new OpenAiClient(PluginMain.OPENAIAPIKEY);
        ChatMessage completion = openAiClient.getChatCompletion("gpt-3.5-turbo", messages);
        return completion.getContent();
    }
}

