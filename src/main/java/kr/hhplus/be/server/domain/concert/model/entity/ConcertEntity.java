package kr.hhplus.be.server.domain.concert.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "concerts")
public class ConcertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "concert_id")
    @Comment("콘서드 아이디")
    private String id;

    @Column(name = "title", nullable = false)
    @Comment("콘서트 제목")
    private String title;

    @Column(name = "description", nullable = false)
    @Comment("콘서트 설명")
    private String description;

    @Builder.Default
    @OneToMany(mappedBy = "concert")
    @BatchSize(size = 10)
    private List<ConcertScheduleEntity> schedules = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("등록 일시")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 일시")
    private LocalDateTime updatedAt;

    public boolean isReservable() {
        return schedules.stream()
                .anyMatch(ConcertScheduleEntity::isReservable);
    }
}
