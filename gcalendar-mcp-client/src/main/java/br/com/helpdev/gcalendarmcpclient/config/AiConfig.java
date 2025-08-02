package br.com.helpdev.gcalendarmcpclient.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class AiConfig {

    /**
     * Initializes the JdbcChatMemoryRepository for storing chat memory in a database.
     *
     * @return A configured JdbcChatMemoryRepository instance.
     */
    @Bean
    public ChatMemory initialChatMemory(final JdbcChatMemoryRepository jdbcChatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(jdbcChatMemoryRepository)
                .maxMessages(10)
                .build();
    }

    /**
     * Initializes the ChatClient with the OpenAI chat model and a ToolCallbackProvider.
     *
     * @param openAiChatModel      The OpenAI chat model to be used for the ChatClient.
     * @param toolCallbackProvider You can be injected List<McpSyncClient> tools and build ToolCallbackProvider
     *                             using new SyncMcpToolCallbackProvider(tools);
     * @return A configured ChatClient instance.
     * @see OpenAiChatModel
     */
    @Bean
    public ChatClient initialChatClient(final OpenAiChatModel openAiChatModel,
                                        final ToolCallbackProvider toolCallbackProvider,
                                        final ChatMemory chatMemory) {
        return ChatClient.builder(openAiChatModel)
                .defaultSystem(getSystemPrompt())
                .defaultToolCallbacks(toolCallbackProvider)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }


    private String getSystemPrompt() {
        final var moc = new BeanOutputConverter<>(ResponseAI.class);

        return PromptTemplate
                .builder()
                .template("""
                        Seja um especialista em agendamento de eventos/consultas/agendas em um calendário.
                        ---
                        Voce sempre deve receber a mensagem do usuário com os seguintes campos para realizar as acoes:
                        - toClient: Nome do cliente ou usuário que possui o calendário (summary do calendario).
                        - fromRequester: Usar apenas para os eventos, nunca será o summary do calendário; sempre será o summary / keyword dos eventos.
                        - dataHora: Data e hora da mensagem.
                        - message: Mensagem que contém a ação a ser realizada no calendário.
                        ---
                        Regras:
                        - Leve em consideracao a data e hora da mensagem para realizar as acoes.
                        - Toda ação é realizada no summary do calendario do 'toClient'
                        - Caso não exista um calendário com o summary do 'toClient', crie-o.
                        - Você pode apenas consultar, criar, remover e buscar eventos/consultas/agendas que o summary for do valor do 'fromRequester'.
                        - Sempre confirme a acao antes de agendar ou remover um evento. Se o usuário já confirmou realize a ação.
                        - Nunca crie ou apague eventos no passado.
                        - Nao marque eventos em cima de eventos já agendados na mesma data e hora.
                        - Remova ou edite eventos apenas dos que o summary do evento seja o mesmo do 'fromRequester'.
                        - Nunca sugira alteracao ou exclusao de um evento no qual o summary não seja o mesmo do 'fromRequester'. Diga que já existe um evento agendado para esse horario.
                        - Sempre use datas com o timezone "-03:00" seguindo a Rfc3339 ex: "2025-10-10T16:00:00-03:00".
                        ---
                        Saida:
                        {format}
                        ---
                        - Ao responder no atributo 'action', informe o que você fez, mapeado entre: "SCHEDULED", "REMOVED", "FOUND", "CREATED", "LISTED", "DELETED", "NOT_FOUND" ou "TO_CONFIRM_ACTION".
                        """)
                .variables(
                        new HashMap<>() {{
                            put("format", moc.getFormat());
                        }}
                ).build()
                .create()
                .getContents();
    }

}
