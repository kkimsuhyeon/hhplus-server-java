package kr.hhplus.be.server.domain.concert.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SeatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "seat_id")
    @Comment("좌석 아이디")
    private String id;

    @Column(name = "seat_number", nullable = false)
    @Comment("좌석 번호")
    private int seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Comment("좌석 상태")
    private SeatStatus status;

    @Column(name = "price", nullable = false)
    @Comment("가격")
    private Long price;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private ConcertScheduleEntity schedule;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    /**
     * 좌석을 예약합니다.
     *
     * @param userId 사용자 ID
     * @throws IllegalStateException 이미 예약된 좌석인 경우
     */
    public void reserve(String userId) {
        if (this.status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("예약 가능한 좌석이 아닙니다.");
        }

        this.status = SeatStatus.RESERVING;
        this.user = UserEntity.builder().id(userId).build();
    }

    /**
     * 좌석이 예약 가능한지 확인합니다.
     *
     * @return 예약 가능 여부
     */
    public boolean isAvailable() {
        return this.status == SeatStatus.AVAILABLE;
    }
}
