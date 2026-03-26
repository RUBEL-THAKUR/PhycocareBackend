package com.psychocare.backend.service;

import com.psychocare.backend.dto.ComposeMessageRequest;
import com.psychocare.backend.dto.MessageResponse;
import com.psychocare.backend.exception.ResourceNotFoundException;
import com.psychocare.backend.model.AppUser;
import com.psychocare.backend.model.Message;
import com.psychocare.backend.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserService userService;

    @Transactional
    public Message compose(Long userId, ComposeMessageRequest request) {
        AppUser user = userService.getById(userId);
        Message message = Message.builder()
                .sender(user)
                .recipientEmail(request.getRecipientEmail())
                .subject(request.getSubject())
                .body(request.getBody())
                .folder("SENT")
                .isRead(true)
                .build();
        return messageRepository.save(message);
    }

    public Page<Message> getInbox(Long userId, Pageable pageable) {
        return messageRepository.findBySenderUserIdAndFolderAndIsDeletedFalse(userId, "INBOX", pageable);
    }

    public Page<Message> getSent(Long userId, Pageable pageable) {
        return messageRepository.findBySenderUserIdAndFolderAndIsDeletedFalse(userId, "SENT", pageable);
    }

    public Page<Message> getStarred(Long userId, Pageable pageable) {
        return messageRepository.findBySenderUserIdAndIsStarredTrueAndIsDeletedFalse(userId, pageable);
    }

    public Page<Message> getImportant(Long userId, Pageable pageable) {
        return messageRepository.findBySenderUserIdAndIsImportantTrueAndIsDeletedFalse(userId, pageable);
    }

    public Page<Message> getTrash(Long userId, Pageable pageable) {
        return messageRepository.findBySenderUserIdAndIsDeletedTrue(userId, pageable);
    }

    @Transactional
    public Message toggleStar(Long messageId, Long userId) {
        Message msg = getAndValidate(messageId, userId);
        msg.setIsStarred(!msg.getIsStarred());
        return messageRepository.save(msg);
    }

    @Transactional
    public Message toggleImportant(Long messageId, Long userId) {
        Message msg = getAndValidate(messageId, userId);
        msg.setIsImportant(!msg.getIsImportant());
        return messageRepository.save(msg);
    }

    @Transactional
    public void moveToTrash(Long messageId, Long userId) {
        Message msg = getAndValidate(messageId, userId);
        msg.setIsDeleted(true);
        messageRepository.save(msg);
    }

    @Transactional
    public Message markRead(Long messageId, Long userId) {
        Message msg = getAndValidate(messageId, userId);
        msg.setIsRead(true);
        return messageRepository.save(msg);
    }

    private Message getAndValidate(Long messageId, Long userId) {
        Message msg = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
        if (!msg.getSender().getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Message not found");
        }
        return msg;
    }

    public MessageResponse toResponse(Message m) {
        return MessageResponse.builder()
                .id(m.getId())
                .senderName(m.getSender() != null
                        ? m.getSender().getFirstName() + " " + m.getSender().getLastName()
                        : "System")
                .recipientEmail(m.getRecipientEmail())
                .subject(m.getSubject())
                .body(m.getBody())
                .folder(m.getFolder())
                .isRead(m.getIsRead())
                .isStarred(m.getIsStarred())
                .isImportant(m.getIsImportant())
                .createdAt(m.getCreatedAt())
                .build();
    }
}
