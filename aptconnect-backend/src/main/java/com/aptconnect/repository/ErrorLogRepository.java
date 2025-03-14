package com.aptconnect.repository;

import com.aptconnect.entity.ErrorLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ErrorLogRepository extends JpaRepository<ErrorLog, Long> {
    // 🔹 기본 로그 페이징 조회
    Page<ErrorLog> findAll(Pageable pageable);

    // 🔹 특정 레벨 필터링
    Page<ErrorLog> findByLevelIgnoreCase(String level, Pageable pageable);

    // 🔹 검색 기능 추가 (IP, Level, Message)
    @Query("SELECT e FROM ErrorLog e WHERE e.message LIKE CONCAT('%', :search, '%') OR e.level LIKE CONCAT('%', :search, '%')")
    Page<ErrorLog> searchLogs(@Param("search") String search, Pageable pageable);

    // 📌 날짜별 로그 발생 개수 조회
    @Query("SELECT FUNCTION('DATE', e.timestamp) as logDate, COUNT(e) FROM ErrorLog e GROUP BY FUNCTION('DATE', e.timestamp) ORDER BY logDate")
    List<Object[]> countLogsPerDay();
}
