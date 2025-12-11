package kr.hhplus.be.server.domain.reservation.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.reservation.model.Reservation;
import kr.hhplus.be.server.domain.reservation.model.ReservationStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reservation_id")
    @Comment("예약 아이디")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("예약 상태")
    private ReservationStatus status;

    @Column(name = "payment_amount", nullable = false)
    @Comment("결제 금액")
    private BigDecimal paymentAmount;

    @Column(name = "expire_at")
    @Comment("예약 만료 시간")
    private LocalDateTime expireAt;

    @Column(name = "user_id", nullable = false)
    @Comment("유저 아이디")
    private String userId;

    @Column(name = "seat_id", nullable = false)
    @Comment("좌석 아이디")
    private String seatId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Comment("등록 일시")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Comment("수정 일시")
    private LocalDateTime updatedAt;

    public static ReservationEntity create(Reservation reservation) {
        return ReservationEntity.builder()
                .status(reservation.getStatus())
                .expireAt(reservation.getExpireAt())
                .paymentAmount(reservation.getPaymentAmount())
                .userId(reservation.getUserId())
                .seatId(reservation.getSeatId())
                .build();
    }

    public Reservation toModel() {
        return Reservation.builder()
                .id(this.id)
                .status(this.status)
                .expireAt(this.expireAt)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .paymentAmount(this.paymentAmount)
                .userId(this.userId)
                .seatId(this.seatId)
                .build();
    }

    public void update(Reservation reservation) {
        this.status = reservation.getStatus();
        this.expireAt = reservation.getExpireAt();
        this.userId = reservation.getUserId();
        this.seatId = reservation.getSeatId();
    }
}
