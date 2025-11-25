package kr.hhplus.be.server.domain.payment.adapter.out.persistence;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.math.BigInteger;

@Getter
@Builder(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Entity
@Table(name = "payments")
public class PaymentJpaEntity {

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
    private BigInteger amount;

    @Column(name = "reservation_id", nullable = false)
    @Comment("예약 아이디")
    private String reservationId;

    public static PaymentJpaEntity create(Payment payment) {
        return PaymentJpaEntity.builder()
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .reservationId(payment.getReservationId())
                .build();
    }

    public Payment toModel() {
        return Payment.builder()
                .id(this.id)
                .status(this.status)
                .amount(this.amount)
                .reservationId(this.reservationId)
                .build();
    }

    public void update(Payment payment) {
        this.status = payment.getStatus();
        this.amount = payment.getAmount();
    }
}
