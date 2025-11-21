package com.wellwork.repository;

import com.wellwork.model.entities.CheckIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {
    Page<CheckIn> findByUserId(Long userId, Pageable pageable);
}
