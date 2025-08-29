package br.com.helpdev.gcalendarmcpclient.controller;

import br.com.helpdev.gcalendarmcpclient.controller.dto.RequestChat;
import br.com.helpdev.gcalendarmcpclient.controller.dto.ResponseChat;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    private static final String PROMPT_TEMPLATE = """
            "toClient": "{toClient}",
            "fromRequester": "{fromRequester}",
            "dataHora": "{dataHora}",
            "message": "{message}",
        """;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/chat")
    public ResponseChat chat(@RequestBody RequestChat message) {
        final var chatId = message.toClient() + "-" + message.fromRequester();
        final var userPromptTemplate = new PromptTemplate(PROMPT_TEMPLATE);

        final var userPrompt = userPromptTemplate.create(
                new HashMap<>() {{
                    put("toClient", message.toClient());
                    put("fromRequester", message.fromRequester());
                    put("dataHora", LocalDateTime.now());
                    put("message", message.message());
                }}
        );
        ChatClient.CallResponseSpec res = chatClient
                .prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(userPrompt.getContents())
                .call();

        return new ResponseChat(message.toClient(), message.fromRequester(), res.content());
    }
}




