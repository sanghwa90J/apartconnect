package com.aptconnect.menu.complaint.entity;

import lombok.Getter;

import java.util.List;

// ComplaintDetailDto.java
@Getter
public class ComplaintDetailDto {
    // getter
    private Complaint complaint;
    private List<ComplaintFile> files;

    // 생성자
    public ComplaintDetailDto(Complaint complaint, List<ComplaintFile> files) {
        this.complaint = complaint;
        this.files = files;
    }

}