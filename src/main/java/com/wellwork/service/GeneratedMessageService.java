package com.wellwork.service;

import com.wellwork.model.entities.CheckIn;
import com.wellwork.model.entities.GeneratedMessage;
import com.wellwork.repository.GeneratedMessageRepository;
import com.wellwork.repository.CheckInRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class GeneratedMessageService {

    private final GeneratedMessageRepository generatedMessageRepository;
    private final CheckInRepository checkInRepository;
    private final AIService aiService;

    public GeneratedMessageService(GeneratedMessageRepository generatedMessageRepository,
                                   CheckInRepository checkInRepository,
                                   AIService aiService) {
        this.generatedMessageRepository = generatedMessageRepository;
        this.checkInRepository = checkInRepository;
        this.aiService = aiService;
    }

    /**
     * Gera uma mensagem para o checkIn indicado, persiste e vincula a entidade.
     * Retorna a entidade persistida.
     */
    @Transactional
    public GeneratedMessage generateForCheckIn(Long checkInId) {
        CheckIn checkIn = checkInRepository.findById(checkInId)
                .orElseThrow(() -> new IllegalArgumentException("CheckIn não encontrado: " + checkInId));

        // Build prompt baseado nos dados do CheckIn
        String prompt = buildPrompt(checkIn);

        AIService.Result res = aiService.generateMessage(prompt);

        GeneratedMessage gm = new GeneratedMessage();
        gm.setCheckIn(checkIn);
        gm.setMessage(res.message());
        gm.setConfidence(res.confidence().orElse(null));
        gm.setGeneratedAt(Instant.now());

        GeneratedMessage saved = generatedMessageRepository.save(gm);

        // associação bidirecional
        checkIn.setGeneratedMessage(saved);
        checkInRepository.save(checkIn);

        return saved;
    }

    private String buildPrompt(CheckIn checkIn) {
        String notes = checkIn.getNotes() == null ? "" : checkIn.getNotes();
        return String.format("Você é um assistente de bem-estar. O usuário reportou humor: %s, nível de energia: %s. Notas: %s. Gere uma recomendação curta (1-2 frases) e informe a confiança da recomendação (0-1) se possível.",
                checkIn.getMood(), checkIn.getEnergyLevel(), notes);
    }
}
