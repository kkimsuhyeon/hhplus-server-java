package kr.hhplus.be.server.domain.reservation.adapter.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * JPA Entity - Reservation
 * 순수하게 데이터베이스 매핑만 담당하며 비즈니스 로직은 없음
 */
@Getter
@Builder(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reservations")
public class ReservationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reservation_id")
    @Comment("예약 아이디")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("예약 상태")
    private ReservationStatus status;

    @Column(name = "expired_at")
    @Comment("예약 만료 시간")
    private LocalDateTime expiredAt;
    
    @Column(name = "seat_price", nullable = false)
    @Comment("예약 당시 좌석 가격")
    private BigInteger seatPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private SeatEntity seat;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("등록 일시")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 일시")
    private LocalDateTime updatedAt;
    
    /**
     * Adapter에서 사용할 업데이트 메서드
     * 도메인 모델의 변경사항을 Entity에 반영
     */
    void updateFromDomain(ReservationStatus status, LocalDateTime expiredAt) {
        this.status = status;
        this.expiredAt = expiredAt;
    }
}
