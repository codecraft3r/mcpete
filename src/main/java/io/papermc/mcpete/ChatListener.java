package io.papermc.mcpete;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private String apiKey;
    public ChatListener(String opaenaiapikey) {
        apiKey = opaenaiapikey;
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // respond
        OpenAiService service = new OpenAiService(apiKey);
        CompletionRequest request = CompletionRequest.builder().prompt(message).model("ada").echo(true).build();
        service.createCompletion(request).getChoices().forEach(choice -> {
            player.sendMessage(choice.getText());
        });
    }
}
