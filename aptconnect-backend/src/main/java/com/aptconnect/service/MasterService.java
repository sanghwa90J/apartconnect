package com.aptconnect.service;

import com.aptconnect.repository.ErrorLogRepository;
import org.springframework.stereotype.Service;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class MasterService {
    private final ErrorLogRepository errorLogRepository;

    public MasterService(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
    }

    public Map<String, Integer> getDailyErrorLogCounts() {
        List<Object[]> results = errorLogRepository.countLogsPerDay();
        Map<String, Integer> logCounts = new LinkedHashMap<>();

        for (Object[] result : results) {
            String date = result[0].toString(); // 날짜 (yyyy-MM-dd)
            Integer count = ((Number) result[1]).intValue(); // 로그 개수
            logCounts.put(date, count);
        }

        return logCounts;
    }
}
