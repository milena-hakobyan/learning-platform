package com.example.repository;

import com.example.model.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    Page<ActivityLog> findAllByUserId(Long userId, Pageable pageable);
}
