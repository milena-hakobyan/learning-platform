package com.example.repository;

import com.example.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findAllByUserId(Long userId);
}
