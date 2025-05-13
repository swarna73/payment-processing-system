// com.swarna.paymentsystem.repository.AuditLogRepository.java

package com.swarna.paymentsystem.repository;

import com.swarna.paymentsystem.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUserEmailOrderByTimestampDesc(String email);
}