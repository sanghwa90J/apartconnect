package com.aptconnect.repository;

import com.aptconnect.entity.Role;
import com.aptconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Cacheable(value = "userCache", key = "#email")
    Optional<User> findByEmail(String email);

    // 특정 아파트의 가입 대기자 조회 (자신 제외)
    List<User> findByApartmentNameAndEmailNotAndApartmentAccess(String apartmentName, String apartmentAccess, String email);

    // 특정 아파트의 전체 사용자 조회 (자신 제외)
    List<User> findByApartmentNameAndEmailNot(String apartmentName, String email);

    // 모든 가입 대기자 조회 (자신 제외)
    List<User> findByEmailNotAndApartmentAccess(String apartmentAccess, String email);

    // 전체 사용자 조회 (자신 제외)
    List<User> findByEmailNot(String email);

    // 역할별 사용자 수 조회
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    long countByRole(Role role);

    // 월별 사용자 등록 수 조회
    @Query("SELECT COUNT(u) FROM User u WHERE YEAR(u.createDate) = :year AND MONTH(u.createDate) = :month")
    int countByRegistrationMonth(int year, int month);

    // 🔥 특정 아파트에 속하며 access 상태가 PENDING 또는 APPROVED인 유저 조회
    List<User> findByApartmentNameAndApartmentAccessIn(String apartmentName, List<String> statuses);
}
