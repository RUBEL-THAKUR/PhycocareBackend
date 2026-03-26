package com.psychocare.backend.controller;

import com.psychocare.backend.model.ChatMessage;
import com.psychocare.backend.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoint to fetch persisted chat history for a session.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatMessageRepository chatMessageRepository;

    @GetMapping("/history/{sessionId}")
    public List<ChatMessage> getHistory(@PathVariable Long sessionId) {
        return chatMessageRepository.findBySessionIdOrderBySentAtAsc(sessionId);
    }
}
