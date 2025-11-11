package kr.hhplus.be.server.domain.reservation.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kr.hhplus.be.server.config.exception.exceptions.BusinessException;
import kr.hhplus.be.server.config.exception.exceptions.CommonErrorCode;
import kr.hhplus.be.server.config.exception.exceptions.ServerErrorException;
import kr.hhplus.be.server.domain.concert.model.entity.SeatEntity;
import kr.hhplus.be.server.domain.reservation.exception.ReservationErrorCode;
import kr.hhplus.be.server.domain.user.model.entity.UserEntity;
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

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "expired_at")
    @Comment("예약 만료 시간")
    private LocalDateTime expiredAt;

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

    public static ReservationEntity create(UserEntity user, SeatEntity seat) {
        ReservationEntity reservation = ReservationEntity.builder()
                .status(ReservationStatus.PENDING_PAYMENT)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .user(user)
                .seat(seat)
                .build();

        seat.addReservation(reservation);

        return reservation;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public boolean isPayable() {
        return this.status == ReservationStatus.PENDING_PAYMENT;
    }

    public boolean isOwnedBy(String userId) {
        return this.getId().equals(userId);
    }

    public boolean isOwnedBy(UserEntity user) {
        return this.user.getId().equals(user.getId());
    }

    public void validateForPayment(String userId) {
        if (!isOwnedBy(userId)) throw new BusinessException(CommonErrorCode.FORBIDDEN_ERROR);
        if (isExpired()) throw new BusinessException(ReservationErrorCode.EXPIRED); // todo check, 에러 타입 변경
        if (!isPayable()) {
            if (this.status == ReservationStatus.CONFIRMED) {
                throw new BusinessException(ReservationErrorCode.ALREADY_PAID);
            }
            if (this.status == ReservationStatus.CANCELLED) {
                throw new BusinessException(ReservationErrorCode.CANCELED);
            }

            throw new ServerErrorException("validateForPayment : 이상한 에러");
        }
    }

    public void completePayment() {
        this.status = ReservationStatus.CONFIRMED;
    }
}
