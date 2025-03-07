package com.aptconnect.repository;

import com.aptconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // 특정 아파트의 가입 대기자 조회 (자신 제외)
    List<User> findByApartmentNameAndEmailNotAndApartmentAccess(String apartmentName, String apartmentAccess, String email);

    // 특정 아파트의 전체 사용자 조회 (자신 제외)
    List<User> findByApartmentNameAndEmailNot(String apartmentName, String email);

    // 모든 가입 대기자 조회 (자신 제외)
    List<User> findByEmailNotAndApartmentAccess(String apartmentAccess, String email);

    // 전체 사용자 조회 (자신 제외)
    List<User> findByEmailNot(String email);

}
