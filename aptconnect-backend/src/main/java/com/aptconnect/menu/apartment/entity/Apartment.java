package com.aptconnect.menu.apartment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Apartment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // 아파트 이름

    @Column(nullable = false)
    private String address;  // 대표 주소

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createDate;  // 생성 날짜

    @Column(nullable = false)
    private String useYn = "Y";

    @Column
    private LocalDateTime updateDate;

    // 추가할 생성자
    public Apartment(String name, String address) {
        this.name = name;
        this.address = address;
    }
}