package org.poiesis.mcpete.helpers;

import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;

import java.util.List;

// This class is a wrapper for the OpenAiService class.
public class OpenAiHelper {
    private String openAiApiKey;
    public OpenAiHelper(String apiKey) {
        openAiApiKey = apiKey;
    } // constructor, sets the API key
    public ChatMessage getChatCompletion(String model, List<ChatMessage> messages) {
        // create a new OpenAiService with the API key
        OpenAiService service = new OpenAiService(openAiApiKey);
        // create a new ChatCompletionRequest with the model and messages
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(model);
        request.setMessages(messages);
        // get the completion from the OpenAiService
        ChatCompletionResult completion = service.createChatCompletion(request);
        // return the first ChatMessage from the completion
        return completion.getChoices().get(0).getMessage();
    }


}
