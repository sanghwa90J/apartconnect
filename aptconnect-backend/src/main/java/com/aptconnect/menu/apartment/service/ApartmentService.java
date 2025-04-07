package com.aptconnect.menu.apartment.service;

import com.aptconnect.menu.apartment.entity.Apartment;
import com.aptconnect.menu.apartment.repository.ApartmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class ApartmentService {
    private final ApartmentRepository apartmentRepository;

    public ApartmentService(ApartmentRepository apartmentRepository, ApartmentRepository apartmentRepository1) {
        this.apartmentRepository = apartmentRepository1;
    }


    public Map<String, Long> getApartmentStatus() {
        Map<String, Long> apartmentStatus = new LinkedHashMap<>();

        List<Object[]> result = apartmentRepository.countApartmentByName();

        for (Object[] row : result) {
            String name = (String) row[0];  // 아파트 이름
            Long count = (Long) row[1];     // 해당 아파트 개수
            apartmentStatus.put(name, count);
        }

        return apartmentStatus;
    }

    public Map<String, Long> getMonthlyApartmentRegistration() {
        Map<String, Long> monthlyRegistrations = new LinkedHashMap<>();
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(5).withDayOfMonth(1);

        List<Object[]> result = apartmentRepository.countMonthlyApartmentRegistrations(sixMonthsAgo);

        // 기본 값 세팅 (데이터가 없을 경우 0으로 표시)
        YearMonth currentMonth = YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            monthlyRegistrations.put(month.getMonthValue() + "월", 0L);
        }

        // 조회된 데이터를 매핑
        for (Object[] row : result) {
            Integer month = (Integer) row[0];  // 월
            Long count = (Long) row[1];        // 개수
            monthlyRegistrations.put(month + "월", count);
        }

        return monthlyRegistrations;
    }

    public List<Apartment> searchApartments(String query) {
        return apartmentRepository.findByNameContainingIgnoreCase(query);
    }

    public Apartment getApartmentById(Long id) {
        return apartmentRepository.findById(id).orElse(null);
    }
}
