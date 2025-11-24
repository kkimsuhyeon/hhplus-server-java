package kr.hhplus.be.server.domain.payment.adapter.persistence;

import jakarta.persistence.*;
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

    /**
     * 도메인 Model의 변경사항을 Entity에 반영 (Adapter용)
     */
    void updateFromDomain(PaymentStatus status) {
        this.status = status;
    }
}
