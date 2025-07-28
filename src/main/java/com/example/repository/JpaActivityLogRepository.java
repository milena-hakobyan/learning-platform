package com.example.repository;

import com.example.model.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findAllByUserId(Long userId);
}
