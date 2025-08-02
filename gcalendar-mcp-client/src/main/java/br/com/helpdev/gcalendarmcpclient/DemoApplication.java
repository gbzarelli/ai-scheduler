package br.com.helpdev.gcalendarmcpclient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;
import java.util.HashMap;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public int runAfterStartup(ChatClient chatClient) {
        final var toClient = "16988056630";
        final var fromRequester = "16988030791";
        final var chatId = toClient + "-" + fromRequester;
        final var userPromptTemplate = new PromptTemplate("""
                    "toClient": "{toClient}",
                    "fromRequester": "{fromRequester}",
                    "dataHora": "{dataHora}",
                    "message": "{message}",
                """);

        final var userPrompt = userPromptTemplate.create(
                new HashMap<>() {{
                    put("toClient", toClient);
                    put("fromRequester", fromRequester);
                    put("dataHora", LocalDateTime.now());
                    put("message", "Qual meu próximo compromisso?");
                }}
        );
        ChatClient.CallResponseSpec res = chatClient
                .prompt()
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId))
                .user(userPrompt.getContents())
                .call();
        System.out.println(res.content());
        return 1;
    }

}
