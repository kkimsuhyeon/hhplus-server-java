package kr.hhplus.be.server.domain.payment.adapter.out.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import kr.hhplus.be.server.domain.reservation.adapter.out.persistence.ReservationEntity;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;

@Getter
@Builder(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "payments")
public class PaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", nullable = false)
    @Comment("결제 아이디")
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Comment("결제 상태")
    private PaymentStatus status;

    @Column(name = "amount", nullable = false)
    @Comment("가격")
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private ReservationEntity reservation;

    public static PaymentEntity create(Payment payment, ReservationEntity reservation) {
        return PaymentEntity.builder()
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .reservation(reservation)
                .build();
    }

    public Payment toModel() {
        return Payment.builder()
                .id(this.id)
                .status(this.status)
                .amount(this.amount)
                .reservationId(this.reservation.getId())
                .build();
    }

    public void update(Payment payment) {
        this.status = payment.getStatus();
        this.amount = payment.getAmount();
    }
}
