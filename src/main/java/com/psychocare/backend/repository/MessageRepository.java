package com.psychocare.backend.repository;

import com.psychocare.backend.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findBySenderUserIdAndFolderAndIsDeletedFalse(Long userId, String folder, Pageable pageable);
    Page<Message> findBySenderUserIdAndIsStarredTrueAndIsDeletedFalse(Long userId, Pageable pageable);
    Page<Message> findBySenderUserIdAndIsImportantTrueAndIsDeletedFalse(Long userId, Pageable pageable);
    Page<Message> findBySenderUserIdAndIsDeletedTrue(Long userId, Pageable pageable);
}
