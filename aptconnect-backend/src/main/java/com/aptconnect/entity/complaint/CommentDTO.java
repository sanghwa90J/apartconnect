package com.aptconnect.entity.complaint;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private String createdByName;
    private Long createdById;

    public CommentDTO(ComplaintComment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.createdByName = comment.getUser().getName();  // ✅ Lazy Loading 문제 해결
        this.createdById = comment.getUser().getId();  // ✅ 사용자 ID 추가
    }
}