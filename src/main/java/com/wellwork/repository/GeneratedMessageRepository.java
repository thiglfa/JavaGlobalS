package com.wellwork.repository;

import com.wellwork.model.entities.GeneratedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GeneratedMessageRepository extends JpaRepository<GeneratedMessage, Long> {
    Optional<GeneratedMessage> findByCheckInId(Long checkInId);
}
