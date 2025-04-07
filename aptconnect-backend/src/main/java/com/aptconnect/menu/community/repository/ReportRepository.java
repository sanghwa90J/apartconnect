package com.aptconnect.menu.community.repository;

import com.aptconnect.menu.auth.entity.User;
import com.aptconnect.menu.community.entity.Report;
import com.aptconnect.menu.community.entity.ReportStatus;
import com.aptconnect.menu.community.entity.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    boolean existsByReporterAndTargetIdAndType(User reporter, Long targetId, ReportType type);

    List<Report> findByStatus(ReportStatus status);
    List<Report> findByReporter(User reporter);
    List<Report> findByType(ReportType type);
    List<Report> findByStatusAndType(ReportStatus status, ReportType type);
}
