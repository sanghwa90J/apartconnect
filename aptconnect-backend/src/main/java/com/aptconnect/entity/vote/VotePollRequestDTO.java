package com.aptconnect.entity.vote;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VotePollRequestDTO {
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isAnonymous;
    private boolean isMultipleChoice;

    @Size(min = 2, message = "투표 선택지는 최소 2개 이상이어야 합니다.")
    private List<String> options; // 선택지 리스트

}
