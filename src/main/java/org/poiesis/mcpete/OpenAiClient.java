package org.poiesis.mcpete;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;

import java.util.List;

public class OpenAiClient {
    private String openAiApiKey;
    public OpenAiClient(String apiKey) {
        openAiApiKey = apiKey;
    }
    public ChatMessage getChatCompletion(String model, List<ChatMessage> messages) {
        OpenAiService service = new OpenAiService(openAiApiKey);
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(model);
        request.setMessages(messages);
        ChatCompletionResult completion = service.createChatCompletion(request);
        return completion.getChoices().get(0).getMessage();
    }

}
