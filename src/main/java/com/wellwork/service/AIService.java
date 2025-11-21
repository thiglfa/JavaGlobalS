package com.wellwork.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AIService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String model;
    private final Duration timeout;

    public AIService(@Value("${groq.api.key}") String apiKey,
                     @Value("${groq.base-url:https://api.groq.com/openai/v1}") String baseUrl,
                     @Value("${groq.model:llama-3.1-8b-instant}") String model,
                     @Value("${groq.timeout-seconds:30}") long timeoutSeconds,
                     ObjectMapper objectMapper) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.objectMapper = objectMapper;
        this.model = model;
        this.timeout = Duration.ofSeconds(timeoutSeconds);
    }

    /**
     * Envia um prompt (mensagem do usuário) para o modelo e retorna um pair: (texto gerado, confidence opcional).
     * A estratégia: tenta extrair choices[0].message.content ou choices[0].text.
     * Se existir um campo 'confidence' em choices[0] ou em top-level, tenta extrair; caso contrário retorna Optional.empty() para confidence.
     */
    public Result generateMessage(String prompt) {
        try {
            // Monta payload conforme OpenAI Chat Completions
            Map<String, Object> payload = Map.of(
                    "model", model,
                    "messages", List.of(Map.of("role", "user", "content", prompt)),
                    "max_tokens", 200,
                    "temperature", 0.2
            );

            Mono<String> respMono = webClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(timeout);

            String respBody = respMono.block(timeout);

            JsonNode root = objectMapper.readTree(respBody);

            // Try chat-style: choices[0].message.content
            String messageText = null;
            Double confidence = null;

            if (root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0) {
                JsonNode first = root.get("choices").get(0);
                // message.content (chat)
                if (first.has("message") && first.get("message").has("content")) {
                    messageText = first.get("message").get("content").asText();
                }
                // fallback: text (older style)
                if ((messageText == null || messageText.isBlank()) && first.has("text")) {
                    messageText = first.get("text").asText();
                }
                // try to extract confidence if present in the choice
                if (first.has("confidence")) {
                    try { confidence = first.get("confidence").asDouble(); } catch (Exception ignored) {}
                } else if (root.has("confidence")) {
                    try { confidence = root.get("confidence").asDouble(); } catch (Exception ignored) {}
                }
            }

            if (messageText == null) messageText = "";

            return new Result(messageText.trim(), Optional.ofNullable(confidence));
        } catch (Exception ex) {
            // Em caso de erro, retornamos mensagem vazia e confidence null
            return new Result("", Optional.empty());
        }
    }

    public static record Result(String message, Optional<Double> confidence) { }
}
