package org.poiesis.mcpete.listeners;

import com.theokanning.openai.assistants.*;
import com.theokanning.openai.messages.Message;
import com.theokanning.openai.messages.MessageContent;
import com.theokanning.openai.messages.MessageRequest;
import com.theokanning.openai.runs.*;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.threads.ThreadRequest;
import com.theokanning.openai.threads.Thread;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.poiesis.mcpete.PluginMain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class AssistantAgentListener implements Listener {
    private final Assistant assistant = new Assistant();
    private Thread thread;

    private final PluginMain plugin;
    private OpenAiService oaiService;

    public AssistantAgentListener(String assistantId, String apiKey, PluginMain p) {
        assistant.setId(assistantId);
        oaiService = new OpenAiService(apiKey);
        plugin = p;
    }
    private boolean executeCommand(String command) {
        return Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
    }
    private String generate(ThreadRequest request) {
        Logger.getAnonymousLogger().info("Generating");
        if (thread == null) {
            thread = oaiService.createThread(request);
        } else {
            oaiService.modifyThread(thread.getId(), request);
        }
        Run run = oaiService.createRun(thread.getId(), RunCreateRequest.builder().assistantId(assistant.getId()).build());
        boolean isComplete = false;
        while(!isComplete) {
            String runStatus = run.getStatus();
            if (Objects.equals(runStatus, "requires_action")) {
                ArrayList<SubmitToolOutputRequestItem> toolOutputRequestItems = new ArrayList<>();
                run.getRequiredAction().getSubmitToolOutputs().getToolCalls().forEach(toolCall -> {
                    if(toolCall.getType().equals("function")) {
                        String cmd = run.getTools().get(toolCall.hashCode()).getFunction().getParameters().get("command").toString();
                        boolean isSuccessful = executeCommand(cmd);
                        toolOutputRequestItems.add(SubmitToolOutputRequestItem.builder().output("{\"command\": " + isSuccessful + "}").toolCallId(toolCall.getId()).build());
                    }
                });
                oaiService.submitToolOutputs(run.getThreadId(), run.getId(), SubmitToolOutputsRequest.builder().toolOutputs(toolOutputRequestItems).build());

            } else if (Objects.equals(runStatus, "complete")) {
                List<Message> messageList = oaiService.listMessages(run.getThreadId()).getData();
                Logger.getAnonymousLogger().info(messageList.get(messageList.size() - 1).getContent().toString());
                return messageList.get(messageList.size() - 1).getContent().toString();
            } else if (Objects.equals(runStatus, "error")) {
                return "Error";
            }

            try {
                wait(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        return "Error";
    }

    @EventHandler
    public void onPlayerChat(PlayerChatEvent event) {
        Logger.getAnonymousLogger().info("Player chat event");
        // add the message to the list of messages
        String message = "Player Name: " + event.getPlayer().getName() + " Player Position (as of message): " + event.getPlayer().getLocation() + " Player Message: " + event.getMessage();
        ArrayList<MessageRequest> messages = new ArrayList<>();
        messages.add(MessageRequest.builder().content(message).build());
        ThreadRequest request = ThreadRequest.builder().messages(messages).build();

        CompletableFuture<String> response = CompletableFuture.supplyAsync(() -> generate(request));
        response.thenAccept(responseMessage -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.getServer().sendMessage(Component.text(responseMessage));
            });
        });
    }



}
