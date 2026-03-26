package com.psychocare.backend.controller;

import com.psychocare.backend.model.ChatMessage;
import com.psychocare.backend.repository.ChatMessageRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

/**
 * Handles WebSocket STOMP messages for real-time session chat.
 * Client sends to: /app/chat/{sessionId}
 * Broadcasts to: /topic/chat/{sessionId}
 */
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat/{sessionId}")
    @SendTo("/topic/chat/{sessionId}")
    public ChatMessagePayload handleMessage(
            @DestinationVariable Long sessionId,
            ChatMessagePayload payload
    ) {
        ChatMessage entity = ChatMessage.builder()
                .sessionId(sessionId)
                .senderId(payload.getSenderId())
                .senderRole(payload.getSenderRole())
                .content(payload.getContent())
                .messageType(payload.getMessageType() != null ? payload.getMessageType() : "TEXT")
                .build();
        chatMessageRepository.save(entity);

        payload.setSentAt(LocalDateTime.now().toString());
        payload.setSessionId(sessionId);
        return payload;
    }

    @Data
    public static class ChatMessagePayload {
        private Long sessionId;
        private String senderId;
        private String senderRole;
        private String content;
        private String messageType;
        private String sentAt;
    }
}
