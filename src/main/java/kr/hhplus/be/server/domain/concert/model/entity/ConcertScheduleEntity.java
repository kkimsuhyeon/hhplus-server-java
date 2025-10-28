package kr.hhplus.be.server.domain.concert.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "concert_schedules")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ConcertScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "concert_schedule_id", nullable = false)
    @Comment("콘서트 스케쥴 아이디")
    private String id;

    @Column(name = "concert_date", nullable = false)
    @Comment("콘서트 날짜")
    private LocalDateTime concertDate;

    @ManyToOne
    @JoinColumn(name = "concert_id", nullable = false)
    private ConcertEntity concert;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("등록 일시")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 일시")
    private LocalDateTime updatedAt;

}
