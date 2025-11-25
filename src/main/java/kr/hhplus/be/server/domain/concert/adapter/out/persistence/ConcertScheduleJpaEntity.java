package kr.hhplus.be.server.domain.concert.adapter.out.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.concert.model.ConcertSchedule;
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
@Table(name = "concert_schedules")
public class ConcertScheduleJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "schedule_id")
    @Comment("스케줄 아이디")
    private String id;

    @Column(name = "concert_date", nullable = false)
    @Comment("콘서트 날짜")
    private LocalDateTime concertDate;

    @Column(name = "concert_id", nullable = false)
    @Comment("콘서트 아이디")
    private String concertId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("등록 일시")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 일시")
    private LocalDateTime updatedAt;

    public static ConcertScheduleJpaEntity create(ConcertSchedule schedule) {
        return ConcertScheduleJpaEntity.builder()
                .concertDate(schedule.getConcertDate())
                .concertId(schedule.getConcertId())
                .build();
    }

    public ConcertSchedule toModel() {
        return ConcertSchedule.builder()
                .id(this.id)
                .concertDate(this.concertDate)
                .concertId(this.concertId)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    public void update(ConcertSchedule schedule) {
        this.concertDate = schedule.getConcertDate();
    }
}
