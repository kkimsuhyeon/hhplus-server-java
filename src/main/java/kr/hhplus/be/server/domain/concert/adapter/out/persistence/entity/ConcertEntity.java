package kr.hhplus.be.server.domain.concert.adapter.out.persistence.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.concert.model.Concert;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "concerts")
public class ConcertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "concert_id")
    @Comment("콘서트 아이디")
    private String id;

    @Column(name = "title", nullable = false)
    @Comment("콘서트 제목")
    private String title;

    @Column(name = "description")
    @Comment("콘서트 설명")
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("등록 일시")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 일시")
    private LocalDateTime updatedAt;

    public static ConcertEntity create(Concert concert) {
        return ConcertEntity.builder()
                .title(concert.getTitle())
                .description(concert.getDescription())
                .build();
    }

    public Concert toModel() {
        return Concert.builder()
                .id(this.id)
                .title(this.title)
                .description(this.description)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    public void update(Concert concert) {
        this.title = concert.getTitle();
        this.description = concert.getDescription();
    }
}
