package kr.hhplus.be.server.domain.payment.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.payment.model.Payment;
import kr.hhplus.be.server.domain.payment.model.PaymentStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(name = "rmk")
    @Comment("비고")
    private String rmk;

    @Column(name = "reservation_id", nullable = false)
    @Comment("예약 아이디")
    private String reservationId;

    public static PaymentEntity create(Payment payment) {
        return PaymentEntity.builder()
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .rmk(payment.getRmk())
                .reservationId(payment.getReservationId())
                .build();
    }

    public Payment toModel() {
        return Payment.builder()
                .id(this.id)
                .status(this.status)
                .amount(this.amount)
                .rmk(this.rmk)
                .reservationId(this.reservationId)
                .build();
    }

    public void update(Payment payment) {
        this.status = payment.getStatus();
    }
}
